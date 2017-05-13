/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wt.health;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class StepService extends Service {
	private static final String TAG = "hcj.StepsLife";
    private PedometerSettings mPedometerSettings;
    //private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    // private StepBuzzer mStepBuzzer; // used for debugging
    private StepDisplayer mStepDisplayer;
    private DistanceNotifier mDistanceNotifier;
    
    private PowerManager.WakeLock mWakeLock;
    //private NotificationManager mNM;

    private int mSteps;
    private float mDistance;
	private boolean mPedometerStart;

	private static final SimpleDateFormat mDateSdf = new SimpleDateFormat("yyyy-MM-dd");
	private Handler mHandler = new Handler();
	private Runnable mUpdateWidgetRunnable = new Runnable(){
		@Override
		public void run(){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(StepService.this);
			HealthWiget.performUpdate(StepService.this, appWidgetManager,appWidgetManager.getAppWidgetIds(new ComponentName(StepService.this, HealthWiget.class)),null);
		}
	};
	
	private Runnable mDateChangeRunnable = new Runnable(){
		@Override
		public void run(){
			String date = getCurrentDate();
			onDateChanged(date);
		}
	};

	private void onDateChanged(String date){
		Log.i(TAG,"onDateChanged date="+date);
		final ContentResolver cr = StepService.this.getContentResolver();
		Cursor cursor = cr.query(StepHealth.CONTENT_URI, StepHealth.ALL_PROJECTION, "date=?", new String[] { date }, null);
		if ((cursor != null) && (cursor.getCount() > 0)){
			ContentValues values = new ContentValues();
			values.put("steps", Integer.valueOf(mSteps));
			cursor.close();
			cr.update(StepHealth.CONTENT_URI, values, "date=?", new String[] { date });
			return;
		}
		insert(cr, date);
		resetValues();
	}
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        
        mPedometerSettings = PedometerSettings.getInstance(this);
        //acquireWakeLock();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //registerDetector();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction("android.intent.action.DATE_CHANGED");
        registerReceiver(mReceiver, filter);

	 String prevTodayTime = mPedometerSettings.getTodayTime();
	 String currTodayTime = getCurrentDate();
	 if (!currTodayTime.equals(prevTodayTime)) {
	 	mPedometerSettings.setTodayTime(currTodayTime);
		if(prevTodayTime != null){
			//save preiview day steps
			onDateChanged(prevTodayTime);
		}else{
			//first time step
		}
	 }

	 mPedometerStart = false;
	 if(mPedometerSettings.isPedometerStart()){
	 	startPedometer();
	 }

        mStepDisplayer = new StepDisplayer(mPedometerSettings);
        mStepDisplayer.setSteps(mSteps = mPedometerSettings.getTodaySteps());
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings);
        mDistanceNotifier.setDistance(mDistance = mPedometerSettings.getTodayDistance());
        mStepDetector.addStepListener(mDistanceNotifier);

        reloadSettings();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		super.onStartCommand(intent,flags,startId);
		return START_STICKY;  
	}   
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        //unregisterDetector();
        stopPedometer();
		
        mPedometerSettings.setTodayStepDistance(mSteps,mDistance);

        //wakeLock.release();
    }

    private void registerDetector() {
	 if(mPedometerStart){
	 	Log.i(TAG,"registerDetector already registered");
	 	return;
	 }
	 mPedometerStart = true;
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
	 if(!mPedometerStart){
	 	Log.i(TAG,"registerDetector already unregistered");
	 	return;
	 }
	 mPedometerStart = false;
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);
        public void paceChanged(int value);
        public void distanceChanged(float value);
        public void speedChanged(float value);
        public void caloriesChanged(float value);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
	 mCallback.stepsChanged(mSteps);
	 mCallback.distanceChanged(mDistance);
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }
        
    public void reloadSettings() {
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                    Float.valueOf(mPedometerSettings.getSensitivity())
            );
        }
        
        if (mStepDisplayer    != null) mStepDisplayer.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
    }

	public void startPedometer(){
		registerDetector();
		mWakeLock.acquire();
	}

	public void stopPedometer(){
		unregisterDetector();
		mWakeLock.release();

		mPedometerSettings.setTodayStepDistance(mSteps,mDistance);
	}
    
    public void resetValues() {
        mStepDisplayer.setSteps(mSteps = 0);
        mDistanceNotifier.setDistance(mDistance = 0f);
		
	 mPedometerSettings.setTodayStepDistance(0, 0f);
    }
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
	     saveSteps();
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
	     mHandler.post(mUpdateWidgetRunnable);
        }
    };
	
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
	     saveDistance();
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
            }
        }
    };
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
    	/*
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, Pedometer.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text,
                getText(R.string.notification_subtitle), contentIntent);

        mNM.notify(R.string.app_name, notification);
        */
    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
		  mPedometerSettings.setTodayStepDistance(mSteps,mDistance);
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
		/*		
                if (mPedometerSettings.wakeAggressively()) {
                    //wakeLock.release();
                    //acquireWakeLock();
                }*/
            }else if("android.intent.action.DATE_CHANGED".equals(action)){
            	  mHandler.post(mDateChangeRunnable);
            }
        }
    };
/*
    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }
        else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }
*/
    private void saveSteps(){
        Settings.System.putInt(this.getContentResolver(),"today_steps",mSteps);
    }

    private void saveDistance(){
        Settings.System.putFloat(this.getContentResolver(),"today_distance",mDistance);
    }

	public static String getCurrentDate(){
		String date = null;
		try{
			date = mDateSdf.format(new GregorianCalendar().getTime());
		}catch (Exception e){
		}
		return date;
	}

	public void insert(ContentResolver cr, String date){
		Log.i(TAG,"insert date="+date);
		ContentValues values = new ContentValues();
		values.put("date", date);
		values.put("steps", mSteps);
		values.put("diatance", mDistance);
		cr.insert(StepHealth.CONTENT_URI, values);
	}
}


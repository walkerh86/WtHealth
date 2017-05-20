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


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CurrentWalkActivity extends Activity {
	private static final String TAG = "hcj.StepsLife";
    private PedometerSettings mPedometerSettings;
    //private Utils mUtils;
    
    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
/*
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mRestartBtn;
*/
	private TextView mBtnControl;
    
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);
		
	 mPedometerSettings = PedometerSettings.getInstance(this);
	 
        mStepValue = 0;
        mPaceValue = 0;
        
        setContentView(R.layout.health_layout);
        
        mStepValueView     = (TextView) findViewById(R.id.steps_value);
        //mPaceValueView     = (TextView) findViewById(R.id.pace_value);
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        //mSpeedValueView    = (TextView) findViewById(R.id.speed_value);
        //mCaloriesValueView = (TextView) findViewById(R.id.calories_value);
        //mDesiredPaceView   = (TextView) findViewById(R.id.desired_pace_value);
        mStepValueView.setText("0");
        mDistanceValueView.setText("0");
    /*
        mStartBtn = (Button)findViewById(R.id.control_start);
        mStartBtn.setOnClickListener(mOnClickListener);
        mStopBtn = (Button)findViewById(R.id.control_stop);
        mStopBtn.setOnClickListener(mOnClickListener);
        mRestartBtn = (Button)findViewById(R.id.control_restart);
        mRestartBtn.setOnClickListener(mOnClickListener);
*/
	 mBtnControl = (TextView)findViewById(R.id.btn_control);
	 mBtnControl.setOnClickListener(mOnClickListener);
	 updateBtn();
	 
	 View btnHistory = findViewById(R.id.btn_history);
	 btnHistory.setOnClickListener(mOnClickListener);
        
        //mUtils = Utils.getInstance();
	 /*
	 if(mPedometerSettings.isPedometerStart()){
	 	startPedometer();
	 }*/
	 startStepService();
	 bindStepService();
    }

	private void startPedometer(){
		//startStepService();
	 	//bindStepService();
	 	mPedometerSettings.setPedometerState(true);
	 	if(mService != null){
			mService.startPedometer();
	 	}
	}

	private void stopPedometer(){
		//unbindStepService();
		//stopStepService();
		mPedometerSettings.setPedometerState(false);
	 	if(mService != null){
			mService.stopPedometer();
	 	}
	}
    
    private void updateBtn(){
	/*	
    	if(mPedometerSettings.isPedometerStart()){
    		mStopBtn.setVisibility(View.VISIBLE);
    		mStartBtn.setVisibility(View.GONE);
    	}else{
    		mStopBtn.setVisibility(View.GONE);
    		mStartBtn.setVisibility(View.VISIBLE);
    	}*/
    		mBtnControl.setText(mPedometerSettings.isPedometerStart() ? R.string.stop : R.string.start);
    }
    
    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
    	@Override
    	public void onClick(View view){
    		int id = view.getId();
		/*	
    		if(id == R.id.control_start){
    			startPedometer();
			updateBtn();
    		}else if(id == R.id.control_stop){
    			stopPedometer();
			updateBtn();
    		}else if(id == R.id.btn_history){
    			startHistoryActivity();
    		}*/
    		if(id == R.id.btn_history){
    			startHistoryActivity();
    		}else if(id == R.id.btn_control){
    			togglePedometerState();
    		}
    	}
    };
    
    @Override
    protected void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
		
        //if(mPedometerSettings.isPedometerStart()){
        	savePaceSetting();
        	mStepValueView.setText(String.valueOf(mPedometerSettings.getTodaySteps()));
        	mDistanceValueView.setText(mPedometerSettings.getFormatDistance());
        //}
        
        updateBtn();
    }
    
    @Override
    protected void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
/*
        if (mIsRunning) {
            unbindStepService();
        }
        mPedometerSettings.setPedometerState(mIsRunning);
*/
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
	 unbindStepService();
        super.onDestroy();
    }
    
    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }

	private void togglePedometerState(){
		if(mPedometerSettings.isPedometerStart()){
			stopPedometer();
		}else{
			startPedometer();
		}
		updateBtn();
	}

    private void savePaceSetting() {
        if(mService != null){
		mService.saveStepsAndDistance();
        }
    }

    private StepService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();
            android.util.Log.i(TAG, "onServiceConnected mService="+mService);

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
	     android.util.Log.i(TAG, "onServiceDisconnected");
            mService = null;
        }
    };
    

    private void startStepService() {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            mPedometerSettings.startPedometerService(this);
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(CurrentWalkActivity.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        if(mService == null){
        	return;
        }
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
            Log.i(TAG, "[SERVICE] stopService");
            mPedometerSettings.stopPedometerService(this);
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            //mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            //mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            //mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) { 
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) { 
                        mDistanceValueView.setText("0");
                    }else {
                        mDistanceValueView.setText(mPedometerSettings.getFormatDistance(mDistanceValue));
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) { 
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) { 
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
	private void startHistoryActivity(){
		Intent intent = new Intent(this,HistoryDataActivity.class);
		this.startActivity(intent);
	}
}

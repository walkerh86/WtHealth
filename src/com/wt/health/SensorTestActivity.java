package com.wt.health;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SensorTestActivity extends Activity{
	private TextView mDataXText;
	private TextView mDataYText;
	private TextView mDataZText;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SensorEventListener mListener;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		setContentView(R.layout.activity_test);
		
		mDataXText = (TextView)findViewById(R.id.data_x);
		mDataYText = (TextView)findViewById(R.id.data_y);
		mDataZText = (TextView)findViewById(R.id.data_z);
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mListener = new SensorEventListener(){
			public void onAccuracyChanged(Sensor sensor, int accuracy) {					
			}

			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				float x = event.values[SensorManager.DATA_X];
				float y = event.values[SensorManager.DATA_Y];
				float z = event.values[SensorManager.DATA_Z];
				mDataXText.setText(String.valueOf(x));
				mDataYText.setText(String.valueOf(y));
				mDataZText.setText(String.valueOf(z));
				Log.i("hcjSen", "x="+x);
				Log.i("hcjSen", "y="+y);
				Log.i("hcjSen", "z="+z);
			}
	    };
	    mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		mSensorManager.unregisterListener(mListener);
	}
}

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

import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * Detects steps and notifies all listeners (that implement StepListener).
 * @author Levente Bagi
 * @todo REFACTOR: SensorListener is deprecated
 */
public class StepDetector implements SensorEventListener
{
    private final static String TAG = "hcj.StepDetector";
    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;
    private long mLastStepTimeMillis;
	private long mLastDiffTimeMillis;
    
    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
    
    public StepDetector() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        mLastStepTimeMillis = SystemClock.uptimeMillis();
    }
    
    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    }
    
    public void addStepListener(StepListener sl) {
        mStepListeners.add(sl);
    }
    
    //public void onSensorChanged(int sensor, float[] values) {
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor; 
        synchronized (this) {
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                	detectStepByAccMethod1(event);
                }else if(sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                	int value = (int)event.values[0];
                	notifyStepValue(value);
                }
        }
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
    
    private void notifyStepValue(int value){
    	for (StepListener stepListener : mStepListeners) {
            stepListener.onStepValue(value);
        }
    }
    
    private void notifyStep(){
    	for (StepListener stepListener : mStepListeners) {
            stepListener.onStep();
        }
    }
    
    private void detectStepByAccMethod1(SensorEvent event){
        float vSum = 0;
        for (int i=0 ; i<3 ; i++) {
            final float v = mYOffset + event.values[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;
        
        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == - mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
            if (diff > 4.44/*mLimit*/) {
	     	  //Log.i("hcjStep","diff="+diff+",diff_gaptime="+(SystemClock.uptimeMillis()-mLastDiffTimeMillis));
            	//mLastDiffTimeMillis = SystemClock.uptimeMillis();
                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                boolean isNotContra = (mLastMatch != 1 - extType);
                
                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
	                long timeMillis = SystemClock.uptimeMillis();
	                if(timeMillis - mLastStepTimeMillis > 300){
	                    notifyStep();
	                    mLastMatch = extType;
	                    mLastStepTimeMillis = timeMillis;
	                }else{
	                	mLastMatch = -1;
	                }
                }
                else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;    
    }

}

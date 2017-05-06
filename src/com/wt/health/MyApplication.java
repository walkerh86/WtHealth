package com.wt.health;

import android.app.Application;
import android.os.Bundle;

public class MyApplication extends Application{
	@Override
	public void onCreate(){
		super.onCreate();
		
		PedometerSettings.getInstance(this);
	}
}

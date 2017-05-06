package com.wt.health;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
			PedometerSettings settings = PedometerSettings.getInstance(context);
			if(settings.isPedometerStart()){
				settings.startPedometerService(context);
			}
		}
	}

}

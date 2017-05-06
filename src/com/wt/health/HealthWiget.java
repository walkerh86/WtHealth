package com.wt.health;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class HealthWiget extends AppWidgetProvider{
	private static final String TAG = "hcj.AppWidgetProvider";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if("com.wt.health.APPWIDGET_UPDATE".equals(action)){
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            performUpdate(context, appWidgetManager,appWidgetManager.getAppWidgetIds(getComponentName(context)),null /* no eventIds */);
		}
	}
	
	static ComponentName getComponentName(Context context) {
        return new ComponentName(context, HealthWiget.class);
    }
	
	public static  void performUpdate(Context context,AppWidgetManager appWidgetManager, int[] appWidgetIds,long[] changedEventIds) {            
		//Log.i(TAG, "performUpdate appWidgetIds="+appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			//Log.i(TAG, "performUpdate appWidgetId="+appWidgetId);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.health_layout);
			
	        	PedometerSettings settings = PedometerSettings.getInstance(context);
			int steps = Settings.System.getInt(context.getContentResolver(),"today_steps",0);
			float distance = Settings.System.getFloat(context.getContentResolver(),"today_distance",0f);
			views.setTextViewText(R.id.steps_value, String.valueOf(steps));
			views.setTextViewText(R.id.distance_value, settings.getFormatDistance(distance));
			views.setViewVisibility(R.id.control_stop, View.GONE);
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	// onUpdate() 在更新 widget 时，被执行，
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		performUpdate(context, appWidgetManager, appWidgetIds, null /* no eventIds */);
	}
	
	// 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
	@Override  
    public void onAppWidgetOptionsChanged(Context context,AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {           
        Log.d(TAG, "onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);    
    }  
	
	// 第一个widget被创建时调用  
	@Override  
    public void onEnabled(Context context) {  
        Log.d(TAG, "onEnabled");
        super.onEnabled(context);  
    }  
	
	// 最后一个widget被删除时调用  
    @Override  
    public void onDisabled(Context context) {  
        Log.d(TAG, "onDisabled");
        // 在最后一个 widget 被删除时，终止服务
        super.onDisabled(context);  
    }
    
    // widget被删除时调用  
	@Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
        Log.d(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);
        super.onDeleted(context, appWidgetIds);  
    }
}

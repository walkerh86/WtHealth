package com.wt.health;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	private void performUpdate(Context context,AppWidgetManager appWidgetManager, int[] appWidgetIds,long[] changedEventIds) {            
		for (int appWidgetId : appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main);
			appWidgetManager.updateAppWidget(appWidgetId, views);
			views.setTextViewText(R.id.total_steps, "0");
			views.setTextViewText(R.id.distance_values, "0KM");
		}
	}

	// onUpdate() �ڸ��� widget ʱ����ִ�У�
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		performUpdate(context, appWidgetManager, appWidgetIds, null /* no eventIds */);
	}
	
	// �� widget ��������� ���� �� widget �Ĵ�С���ı�ʱ��������
	@Override  
    public void onAppWidgetOptionsChanged(Context context,AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {           
        Log.d(TAG, "onAppWidgetOptionsChanged");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);    
    }  
	
	// ��һ��widget������ʱ����  
	@Override  
    public void onEnabled(Context context) {  
        Log.d(TAG, "onEnabled");
        //context.startService(EXAMPLE_SERVICE_INTENT);        
        super.onEnabled(context);  
    }  
	
	// ���һ��widget��ɾ��ʱ����  
    @Override  
    public void onDisabled(Context context) {  
        Log.d(TAG, "onDisabled");
        // �����һ�� widget ��ɾ��ʱ����ֹ����
        //context.stopService(EXAMPLE_SERVICE_INTENT);
        super.onDisabled(context);  
    }
    
    // widget��ɾ��ʱ����  
	@Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
        Log.d(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);
        super.onDeleted(context, appWidgetIds);  
    }
}

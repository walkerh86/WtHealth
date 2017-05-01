package com.wt.health;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class ExtStepService extends Service {
	private static final String TAG = "hcj.ExtStepService";
	//private IExtStepServiceCB mIExtStepServiceCB;
	//private RemoteCallbackList<IExtStepServiceCB> mCallbacks = new RemoteCallbackList<IExtStepServiceCB>();
    //private Messenger mClientMessenger = null;
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		//mCallbacks.kill();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	private final IExtStepService.Stub mBinder = new IExtStepService.Stub(){
		@Override
		public void registerExtCB(IExtStepServiceCB cb) throws RemoteException {
			android.util.Log.i(TAG, "registerExtCB cb="+cb);
			if (cb != null){
				//mCallbacks.register(cb);
			}
			//notifyCB();
		}		
	};
	/*
	private void notifyCB(){	
		android.util.Log.i(TAG, "notifyCB mCallbacks="+mCallbacks);
		if(mCallbacks != null){
			try{	
				final int N = mCallbacks.beginBroadcast(); 
				for(int i=0;i<N;i++){
					mCallbacks.getBroadcastItem(i).stepsChanged(1);
				}
				mCallbacks.finishBroadcast();
			}catch(Exception e){
				android.util.Log.i(TAG, "notifyCB e="+e);
			}
		}
	}*/
}

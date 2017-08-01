package com.wt.health;

public class StepDevice {
	public static final int CMD_ENABLE_STEP = 0;
	public static final int CMD_DISABLE_STEP = 1;
	public static final int CMD_CLEAR_STEP = 2;
	
	private static StepDevice mInstance;
	
	static{
		 System.loadLibrary("step_dev_bm2260");
	}
	
	native protected int nativeCmd(int cmd, int param);
	
	public static StepDevice getInstance(){
		if(mInstance == null){
			mInstance = new StepDevice();
		}
		return mInstance;
	}
	
	public int sendCmd(int cmd, int param){
		return nativeCmd(cmd,param);
	}
}

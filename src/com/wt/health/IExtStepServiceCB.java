package com.wt.health;

import android.os.Parcel;
import android.os.Parcelable;

public class IExtStepServiceCB implements Parcelable{
	public void stepsChanged(int value){
		
	}
	
	public static final Parcelable.Creator<IExtStepServiceCB> CREATOR = new Parcelable.Creator<IExtStepServiceCB>() {  
		  
        @Override  
        public IExtStepServiceCB[] newArray(int size) {  
            return new IExtStepServiceCB[size];  
        }  
  
        @Override  
        public IExtStepServiceCB createFromParcel(Parcel source) {
            return new IExtStepServiceCB();  
        }  
    };

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}  
}
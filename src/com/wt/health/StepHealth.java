package com.wt.health;

import android.net.Uri;

public class StepHealth {
	public static final String TABLE_NAME = "health";
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.wt.health/"+TABLE_NAME);
	
	public static final String[] ALL_PROJECTION = new String[] {"_id","date", "steps", "diatance"};
}

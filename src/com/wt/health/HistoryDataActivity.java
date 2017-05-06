package com.wt.health;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryDataActivity extends Activity{
	private Cursor mCursor;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);

		setContentView(R.layout.data_layout);
		
		mCursor = this.getContentResolver().query(StepHealth.CONTENT_URI, StepHealth.ALL_PROJECTION, 
				null, null, null);
		ListView listView = (ListView)findViewById(R.id.data_list);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.item_layout,mCursor,
				new String[]{"date","steps","diatance"},
				new int[]{R.id.date,R.id.steps,R.id.distance});
		listView.setAdapter(adapter);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		if(mCursor != null){
			mCursor.close();
		}
	}
	
	class MyCursorAdapter extends CursorAdapter {
		private LayoutInflater mLayoutInflater;
		
		public MyCursorAdapter(Context context,Cursor cursor){
			super(context, cursor);
			mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) { 
			View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.mDateView = (TextView)view.findViewById(R.id.date);
			holder.mStepView = (TextView)view.findViewById(R.id.steps);
			holder.mDistanceView = (TextView)view.findViewById(R.id.distance);
			view.setTag(holder);
			return view;
		}
		
		@Override  
	    public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder)view.getTag();
		}
	}
	
	private class ViewHolder{
		public TextView mDateView;
		public TextView mStepView;
		public TextView mDistanceView;
	}
}
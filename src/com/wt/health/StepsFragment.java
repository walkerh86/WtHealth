package com.wt.health;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StepsFragment extends Fragment{
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.steps_layout, container, false);
		View todayWalksView = rootView.findViewById(R.id.current_walk);
		todayWalksView.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Context context = StepsFragment.this.getActivity();
				Intent intent = new Intent(context,CurrentWalkActivity.class);
				context.startActivity(intent);
			}
		});
		return rootView;
	}
}

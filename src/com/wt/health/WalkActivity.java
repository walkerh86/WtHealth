package com.wt.health;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class WalkActivity extends FragmentActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walk_layout);
		
		ViewPager viewPage = (ViewPager)findViewById(R.id.m_pager);
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new StepsFragment());
		fragmentList.add(new ColorisFragment());
		fragmentList.add(new DataFragment());
		viewPage.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
	}
	
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		ArrayList<Fragment> list;
		public MyFragmentPagerAdapter(android.support.v4.app.FragmentManager fm,ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}
		
		
		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			return list.get(arg0);
		}
	}
}

/**
 * 
 */
package com.yuzx.taskcoach;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

/**
 * @author yuzx
 *
 */
public class MainActivity extends FragmentActivity {
	
	private TabHost mTabHost;
	private TabManager mTabManager;
	private LinearLayout mSettingLinearLayout;
	private LinearLayout mMainLinearLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tabs);
		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		mTabManager = new TabManager(this, mTabHost, R.id.containertabcontent);
		RelativeLayout calendar = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.calendar_tab_layout, null);
		mTabManager.addTab(mTabHost.newTabSpec("Calendar").setIndicator(calendar),
				CalendarFragment.class, null);
		
		RelativeLayout schedule = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.schedule_tab_layout, null);
		mTabManager.addTab(mTabHost.newTabSpec("Schedule").setIndicator(schedule),
				ScheduleFragment.class, null);
		
		RelativeLayout note = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.note_tab_layout, null);
		mTabManager.addTab(mTabHost.newTabSpec("Note").setIndicator(note),
				NoteFragment.class, null);
		
		RelativeLayout taskboard = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.taskboard_tab_layout, null);
		mTabManager.addTab(mTabHost.newTabSpec("TaskBoard").setIndicator(taskboard),
				TaskBoardFragment.class, null);
		
	/*	RelativeLayout taskmap = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.taskmap_tab, null);
		mTabManager.addTab(mTabHost.newTabSpec("TaskMap").setIndicator(taskmap),
				TaskMapFragment.class, null);*/
		
		if (savedInstanceState != null) {
			
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tag"));
					
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tag", mTabHost.getCurrentTabTag());
	}
		
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final MainActivity mActivity;
		// save tab
		private final Map<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		private final TabHost mTabHost;
		private final int mContainerID;
		private TabInfo mLastTab;

		/**
		 * @param activity context
		 * @param tabHost tab
		 * @param containerID fragment's parent note
		 */
		public TabManager(MainActivity activity, TabHost tabHost,
				int containerID) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerID = containerID;
			mTabHost.setOnTabChangedListener(this);
		}

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _clss, Bundle _args) {
				tag = _tag;
				clss = _clss;
				args = _args;
			}
		}

		static class TabFactory implements TabHost.TabContentFactory {
			private Context mContext;
			TabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumHeight(0);
				v.setMinimumWidth(0);
				return v;
			}
		}

		// add tab
		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new TabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			final FragmentManager fm = mActivity.getSupportFragmentManager();
			info.fragment = fm.findFragmentByTag(tag);
			// isDetached 
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = fm.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}
			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentManager fragmentManager = mActivity
						.getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				// ����֮ǰ��tab
				if (mLastTab != null && mLastTab.fragment != null) {
					fragmentTransaction.detach(mLastTab.fragment);
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity,
								newTab.clss.getName(), newTab.args);
						fragmentTransaction.add(mContainerID, newTab.fragment,
								newTab.tag);
					} else {
						// ����
						fragmentTransaction.attach(newTab.fragment);
					}
				}
				mLastTab = newTab;
				fragmentTransaction.commit();
				 // ���ڽ��̵����߳��У����첽�ķ�ʽ��ִ��,�����Ҫ����ִ������ȴ��еĲ�������Ҫ�����������
				 // ���еĻص�����ص���Ϊ��������������б�ִ����ɣ����Ҫ��ϸȷ����������ĵ���λ�á�
				fragmentManager.executePendingTransactions();
			}
		}
	}

}
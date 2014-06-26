/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yuzx.taskcoach.db.DBOpenHelper;

/**
 * @author yuzx
 * 
 */
public class TaskBoardFragment extends Fragment {
	private Activity mActivity;
	private TabHost mTabHost;
	private ListView taskList;
	private ListView postponeTaskList;
	private ListView nextDaysTaskList;
	private ListView doneTaskList;

	private DBOpenHelper db;
	private Cursor cursor;

	ArrayList<HashMap<String, Object>> todayTaskItemList;
	ArrayList<HashMap<String, Object>> ppTaskItemList;
	ArrayList<HashMap<String, Object>> nextTaskItemList;
	ArrayList<HashMap<String, Object>> doneTaskItemList;

	private LinearLayout llpostpone;
	private LinearLayout lltasktoday;
	private LinearLayout llnextDays;
	private LinearLayout lldone;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.taskboard_layout, container,
				false);
		db = new DBOpenHelper(mActivity);
		mTabHost = (TabHost) view.findViewById(R.id.myTabHost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator("To Do")
				.setContent(R.id.lltab1));

		mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator("Done")
				.setContent(R.id.tbtv2));

		mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator("tab3")
				.setContent(R.id.tbtv3));

		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub

			}
		}); 

		ppTaskItemList = new ArrayList<HashMap<String, Object>>();
		todayTaskItemList = new ArrayList<HashMap<String, Object>>();
		nextTaskItemList = new ArrayList<HashMap<String, Object>>();
		doneTaskItemList = new ArrayList<HashMap<String, Object>>();
		

		taskList = (ListView) view.findViewById(R.id.taskList);
		/*
		 * SimpleAdapter mSimpleAdapter = new SimpleAdapter(mActivity,
		 * todayTaskItemList, R.layout.task_item, new String[] { "taskItemDate",
		 * "taskItemTime", "taskItemContent" }, new int[] { R.id.taskItemDate,
		 * R.id.taskItemTime, R.id.taskItemContent });
		 * taskList.setAdapter(mSimpleAdapter);
		 */
		postponeTaskList = (ListView) view.findViewById(R.id.taskPostponedList);
		nextDaysTaskList = (ListView) view.findViewById(R.id.taskNextDaysList);
		doneTaskList = (ListView) view.findViewById(R.id.taskDoneList);


		taskList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				view.setBackgroundColor(R.color.qing);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		taskList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				view.setBackgroundColor(R.color.qing);
				
				mActivity.findViewById(R.id.lltaskList).setVisibility(View.GONE);
				return false;
			}
		});


		llpostpone = (LinearLayout) view.findViewById(R.id.lltaskpostpone);
		lldone = (LinearLayout) view.findViewById(R.id.lltaskdone);
		llnextDays = (LinearLayout) view.findViewById(R.id.lltasknextdays);
		lltasktoday = (LinearLayout) view.findViewById(R.id.lltasktoday);
		
		refreshView();
		return view;
	}

	private void refreshView(){
		
		initTaskData();
		
		ListItemAdapter pptaskAdapter = new ListItemAdapter(mActivity,
				ppTaskItemList);
		postponeTaskList.setAdapter(pptaskAdapter);
		
		ListItemAdapter taskAdapter = new ListItemAdapter(mActivity,
				todayTaskItemList);
		taskList.setAdapter(taskAdapter);
		
		ListItemAdapter nextTaskAdapter = new ListItemAdapter(mActivity,
				nextTaskItemList);
		nextDaysTaskList.setAdapter(nextTaskAdapter);
		
		ListItemAdapter doneTaskAdapter = new ListItemAdapter(mActivity, 
				doneTaskItemList);
		doneTaskList.setAdapter(doneTaskAdapter);
		
		setListViewHeightBasedOnChildren(taskList);
		setListViewHeightBasedOnChildren(postponeTaskList);
		setListViewHeightBasedOnChildren(nextDaysTaskList);
		setListViewHeightBasedOnChildren(doneTaskList);
		
		if (postponeTaskList.getCount() == 0) {
			llpostpone.setVisibility(View.GONE);
		} else {
			llpostpone.setVisibility(View.VISIBLE);
		}
		
		if (taskList.getCount() == 0) {
			lltasktoday.setVisibility(View.GONE);
		} else {
			lltasktoday.setVisibility(View.VISIBLE);
		}

		
		if (nextDaysTaskList.getCount() == 0) {
			llnextDays.setVisibility(View.GONE);
		} else {
			llnextDays.setVisibility(View.VISIBLE);
		}

		
		if (doneTaskList.getCount() == 0) {
			lldone.setVisibility(View.GONE);
		} else {
			lldone.setVisibility(View.VISIBLE);
		}
	}
	
	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursor = db.selectSchedulesByType("0");
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("taskItemDate", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DATE)));
			map.put("taskItemTime", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME)));
			map.put("taskItemContent", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT)));
			listItem.add(map);
		}
		return listItem;
	}
	

	private void initTaskData() {
		doneTaskItemList.clear();
		ppTaskItemList.clear();
		todayTaskItemList.clear();
		nextTaskItemList.clear();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);

		cursor = db.selectSchedulesByType("0");
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String enddate = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDDATE));
			String endtime = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDTIME));
			String content = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT));
			String id = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ID));
			String done = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DONE));

			map.put("taskItemDate", enddate);
			map.put("taskItemTime", endtime);
			map.put("taskItemContent", content);
			map.put("taskHaveDone", done);
			map.put("taskId", id);

			int y = Integer.parseInt(enddate.split("\\/")[0]);
			int m = Integer.parseInt(enddate.split("\\/")[1]);
			int d = Integer.parseInt(enddate.split("\\/")[2]);
			
			if (done.equals("1")) {
				doneTaskItemList.add(map);
			} else {
				if ((y < year) || (y == year && m < month)
						|| (y == year && m == month && d < day)) {
					ppTaskItemList.add(map);
				}
				if (y == year && m == month && d == day) {
					todayTaskItemList.add(map);
				}
				if (y == year && m == month && d > day && d < day + 7) {
					nextTaskItemList.add(map);
				}
			}

		}
	}


	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);

			listItem.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, 68));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
			Log.d("height", String.valueOf(listItem.getMeasuredHeight()));
			// totalHeight += 68;
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() + 1));
		listView.setLayoutParams(params);
	}

	private class ListItemAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<HashMap<String, Object>> mItemList;

		public ListItemAdapter(Context context,
				ArrayList<HashMap<String, Object>> itemList) {
			this.mInflater = LayoutInflater.from(context);
			// this.mInflater = (LayoutInflater)
			// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mItemList = new ArrayList<HashMap<String, Object>>(itemList);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItemList != null ? mItemList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			final int pos = position;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.task_item, null);
				holder = new ViewHolder();
				holder.content = (TextView) convertView
						.findViewById(R.id.taskItemContent);
				holder.date = (TextView) convertView
						.findViewById(R.id.taskItemDate);
				holder.time = (TextView) convertView
						.findViewById(R.id.taskItemTime);
				holder.cb = (CheckBox) convertView.findViewById(R.id.taskcb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.content.setText(mItemList.get(position)
					.get("taskItemContent").toString());
			holder.date.setText(mItemList.get(position).get("taskItemDate")
					.toString());
			holder.time.setText(mItemList.get(position).get("taskItemTime")
					.toString());
			holder.cb.setChecked(mItemList.get(position).get("taskHaveDone")
					.toString().equals("1"));

			holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					if (mItemList.get(pos).get("taskHaveDone").toString()
							.equals("1")) {
						db.closeTask(mItemList.get(pos).get("taskId")
								.toString(), 0);
					} else {
						db.closeTask(mItemList.get(pos).get("taskId")
								.toString(), 1);
					}
					Log.d("task content",
							mItemList.get(pos).get("taskItemContent")
									.toString());
					refreshView();
				}
			});
			return convertView;
		}

		public final class ViewHolder {
			public TextView content;
			public TextView date;
			public TextView time;
			public CheckBox cb;
		}
	}
	

}

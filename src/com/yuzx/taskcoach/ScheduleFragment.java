/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.yuzx.taskcoach.db.DBOpenHelper;

/**
 * @author yuzx
 *
 */
public class ScheduleFragment extends Fragment {

	private Activity mActivity;
	private ImageButton btnNewTask;
	private ListView lvSchedule;
	private DBOpenHelper db;
	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.schedule_layout, container, false);
		db = new DBOpenHelper(mActivity);
		btnNewTask = (ImageButton)view.findViewById(R.id.btnNewTask);
		lvSchedule = (ListView)view.findViewById(R.id.scheduleList);
		
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mActivity, getData(),
				R.layout.schedule_item, new String[] { "schItemDate",
						"schItemTime", "schItemContent" }, new int[] {
						R.id.schItemDate, R.id.schItemTime, R.id.schItemContent });
		lvSchedule.setAdapter(mSimpleAdapter);
		
		btnNewTask.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,ScheduleActivity.class);
				startActivityForResult(intent, 1);
			}
			
		});

		lvSchedule.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity,ScheduleDetail.class);
				cursor.moveToPosition(position);
				
				intent.putExtra(DBOpenHelper.SCHEDULE_ID, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ID)));
				
/*				intent.putExtra(DBOpenHelper.SCHEDULE_CONTENT, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT)));
				intent.putExtra(DBOpenHelper.SCHEDULE_DATE, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DATE)));
				intent.putExtra(DBOpenHelper.SCHEDULE_TIME, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME)));
				intent.putExtra(DBOpenHelper.SCHEDULE_REMIND, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_REMIND)));
				intent.putExtra(DBOpenHelper.SCHEDULE_TYPE, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TYPE)));
				intent.putExtra(DBOpenHelper.SCHEDULE_LOCATION, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_LOCATION)));*/
				
				startActivityForResult(intent, 0);
			}
			
		});
		
		return view;
	}
	
	private ArrayList<HashMap<String, Object>> getData(){
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursor = db.selectSchedules();
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("schItemDate", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DATE)));
			map.put("schItemTime", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME)));
			map.put("schItemContent", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT)));
			listItem.add(map);
		}
		return listItem;
	}
	
	public void refreshListView() {
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mActivity, getData(),
				R.layout.schedule_item, new String[] { "schItemDate",
						"schItemTime", "schItemContent" }, new int[] {
						R.id.schItemDate, R.id.schItemTime, R.id.schItemContent });
		lvSchedule.setAdapter(mSimpleAdapter);
		lvSchedule.setVisibility(View.VISIBLE);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);	
		refreshListView();
	}
	
	@Override
	public void onDestroy(){
		db.close();
		super.onDestroy();
	}
	
}

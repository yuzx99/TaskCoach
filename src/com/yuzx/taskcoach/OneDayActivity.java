/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.yuzx.taskcoach.db.DBOpenHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author yuzx
 * 
 */
public class OneDayActivity extends Activity {

	private ImageButton btnBack = null;
	private ImageButton btnNew = null;
	private TextView tvTitle = null;
	private ListView lvSchedule = null;
	private ListView lvNote = null;
	private DBOpenHelper db;
	private String mDate;
	private Cursor cursorNote;
	private Cursor cursorSchedule;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.oneday);
		
		db = new DBOpenHelper(this);
		btnBack = (ImageButton) findViewById(R.id.btnOneDayBack);
		btnNew = (ImageButton) findViewById(R.id.btnOneDayNew);
		tvTitle = (TextView) findViewById(R.id.tvOneDayTitle);
		lvSchedule = (ListView) findViewById(R.id.lvOneDaySchedule);
		lvNote = (ListView) findViewById(R.id.lvOneDayNote);
		
		
		
		Intent intent = getIntent();
		if (intent.getStringExtra("DATE") != null) {
			mDate = intent.getStringExtra("DATE").split("\\.")[0];
			// tvTitle.setText(intent.getStringExtra("DATE").split("\\.")[0]);
		} else {
			final Calendar c = Calendar.getInstance();
			mDate = String.valueOf(c.get(Calendar.YEAR)) + "/"
					+ String.valueOf(c.get(Calendar.MONTH) + 1) + "/"
					+ String.valueOf(c.get(Calendar.DAY_OF_MONTH));

			// tvTitle.setText(new StringBuilder().append(c.get(Calendar.YEAR))
			// .append("/").append(c.get(Calendar.MONTH))
			// .append("/").append(c.get(Calendar.DAY_OF_MONTH)));
		}
		tvTitle.setText(mDate);

		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(OneDayActivity.this, v);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.popup, popup.getMenu());

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {
						case R.id.help:
							Toast.makeText(OneDayActivity.this, "help",
									Toast.LENGTH_LONG).show();
							return true;
						case R.id.newNotePopMenu:
							Intent iNote = new Intent();
							iNote.setClass(OneDayActivity.this,
									NoteEditActivity.class);
							startActivity(iNote);
							return true;
						case R.id.newTaskPopMenu:
							Intent iTask = new Intent();
							iTask.setClass(OneDayActivity.this,
									ScheduleActivity.class);
							startActivity(iTask);
							return true;
						default:
							return false;
						}
					}

				});
				popup.show();
			}

		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});

		SimpleAdapter noteAdapter = new SimpleAdapter(this, getNoteData(mDate),
				R.layout.oneday_note_item, new String[] { "noteItemTitle",
						"noteItemContent", "noteItemTime" }, new int[] {
						R.id.noteItemTitle, R.id.noteItemContent,
						R.id.noteItemTime });
		lvNote.setAdapter(noteAdapter);

		SimpleAdapter schAdapter = new SimpleAdapter(this,
				getScheduleData(mDate), R.layout.oneday_schedule_item,
				new String[] { "scheduleItemTime", "ScheduleItemContent" },
				new int[] { R.id.scheduleItemTime, R.id.ScheduleItemContent });
		lvSchedule.setAdapter(schAdapter);
		
		setListViewHeightBasedOnChildren(lvSchedule);
		setListViewHeightBasedOnChildren(lvNote);

		lvNote.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OneDayActivity.this,NoteDetail.class);
				//Cursor cursor = db.selectNotesByDate(mDate);
				cursorNote.moveToPosition(position);
				intent.putExtra(DBOpenHelper.NOTE_ID, cursorNote.getString(cursorNote
						.getColumnIndexOrThrow(DBOpenHelper.NOTE_ID)));
				
				startActivityForResult(intent, 0);
			}
			
		});
		
		lvSchedule.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OneDayActivity.this,ScheduleDetail.class);
				//Cursor cursor = db.selectNotesByDate(mDate);
				cursorSchedule.moveToPosition(position);
				intent.putExtra(DBOpenHelper.SCHEDULE_ID, cursorSchedule.getString(cursorSchedule
						.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ID)));				
				startActivityForResult(intent, 0);
			}
			
		});
	}

	private ArrayList<HashMap<String, Object>> getNoteData(String date) {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursorNote = db.selectNotesByDate(date);
		while (cursorNote.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("noteItemTitle", cursorNote.getString(cursorNote
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_TITLE)));
			map.put("noteItemContent", cursorNote.getString(cursorNote
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_CONTENT)));
			map.put("noteItemTime", cursorNote.getString(cursorNote
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_TIME)));
			listItem.add(map);
		}
		return listItem;
	}

	private ArrayList<HashMap<String, Object>> getScheduleData(String date) {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursorSchedule = db.selectSchedulesByDate(date);
		while (cursorSchedule.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("scheduleItemTime", cursorSchedule.getString(cursorSchedule
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME)));
			map.put("ScheduleItemContent", cursorSchedule.getString(cursorSchedule
					.getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT)));
			listItem.add(map);
		}
		return listItem;
	}

	public void refreshListView() {
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
				getNoteData(mDate), R.layout.oneday_note_item, new String[] {
						"noteItemTitle", "noteItemContent", "noteItemTime" },
				new int[] { R.id.noteItemTitle, R.id.noteItemContent,
						R.id.noteItemTime });
		lvNote.setAdapter(mSimpleAdapter);
		lvNote.setVisibility(View.VISIBLE);

		SimpleAdapter schAdapter = new SimpleAdapter(this,
				getScheduleData(mDate), R.layout.oneday_schedule_item,
				new String[] { "scheduleItemTime", "ScheduleItemContent" },
				new int[] { R.id.scheduleItemTime, R.id.ScheduleItemContent });
		lvSchedule.setAdapter(schAdapter);
		lvSchedule.setVisibility(View.VISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refreshListView();
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() + 1));
		listView.setLayoutParams(params);
	}

}

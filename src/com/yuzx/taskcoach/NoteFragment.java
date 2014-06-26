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
public class NoteFragment extends Fragment {
	private Activity mActivity;
	private ListView mNoteList;
	private DBOpenHelper db;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.note_layout, container, false);
		ImageButton btnBack = (ImageButton) view.findViewById(R.id.btnBackNoteFrag);
		ImageButton btnNewNote = (ImageButton) view.findViewById(R.id.btnNewNoteFrag);
		mNoteList = (ListView) view.findViewById(R.id.noteList);
		db = new DBOpenHelper(mActivity);
		// refreshListView();
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mActivity, getData(),
				R.layout.listview_item_layout, new String[] { "ItemDate",
						"ItemTitle", "ItemContent" }, new int[] {
						R.id.ItemDate, R.id.ItemTitle, R.id.ItemContent });
		mNoteList.setAdapter(mSimpleAdapter);
		btnBack.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, MainActivity.class);
				startActivity(intent);
				mActivity.finish();
			}

		});

		btnNewNote.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, NoteEditActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		mNoteList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, NoteDetail.class);
				cursor.moveToPosition(position);
				
				// intent.putExtra(DBOpenHelper.NOTE_ID, String.valueOf(id));
				intent.putExtra(DBOpenHelper.NOTE_ID, cursor.getString(cursor
						.getColumnIndexOrThrow(DBOpenHelper.NOTE_ID)));
//				intent.putExtra(
//						DBOpenHelper.NOTE_TITLE,
//						cursor.getString(cursor
//								.getColumnIndexOrThrow(DBOpenHelper.NOTE_TITLE)));
//				intent.putExtra(
//						DBOpenHelper.NOTE_CONTENT,
//						cursor.getString(cursor
//								.getColumnIndexOrThrow(DBOpenHelper.NOTE_CONTENT)));
				startActivityForResult(intent, 0);
			}

		});
		return view;
	}

	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursor = db.selectNotes();
		while (cursor.moveToNext()) {
			System.out.println("in cursor");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemDate", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_DATE)));
			map.put("ItemTitle", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_TITLE)));
			map.put("ItemContent", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.NOTE_CONTENT)));
			listItem.add(map);
		}
		return listItem;
	}

	public void refreshListView() {

		SimpleAdapter mSimpleAdapter = new SimpleAdapter(mActivity, getData(),
				R.layout.listview_item_layout, new String[] { "ItemDate",
						"ItemTitle", "ItemContent" }, new int[] {
						R.id.ItemDate, R.id.ItemTitle, R.id.ItemContent });
		mNoteList.setAdapter(mSimpleAdapter);
		mNoteList.setVisibility(View.VISIBLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refreshListView();
	}
	
	@Override
	public void onDestroy(){
		cursor.close();
		db.close();
		super.onDestroy();
	}
}

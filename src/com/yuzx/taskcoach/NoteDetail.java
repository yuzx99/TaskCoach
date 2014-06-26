/**
 * 
 */
package com.yuzx.taskcoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.view.BorderTextView;

/**
 * @author yuzx
 *
 */
public class NoteDetail extends Activity {

	private Intent intent;
	private DBOpenHelper db;
	private String id = null;
	private String title = null;
	private String content = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_detail);
		intent = getIntent();
		db = new DBOpenHelper(this);
		id = intent.getStringExtra(DBOpenHelper.NOTE_ID);
		setNote(id);
		
		ImageButton btnEdit = (ImageButton) findViewById(R.id.btnEditNoteDet);
		btnEdit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(NoteDetail.this, NoteEditActivity.class);
				i.putExtra(DBOpenHelper.NOTE_ID, id);
				i.putExtra(DBOpenHelper.NOTE_TITLE, title);
				i.putExtra(DBOpenHelper.NOTE_CONTENT, content);
				startActivityForResult(i, 0);		
			}
			
		});
		
		ImageButton btnBack = (ImageButton) findViewById(R.id.btnBackNoteDet);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentBack = new Intent();
				setResult(RESULT_OK,intentBack);
				finish();
			}
			
		});
		
		ImageButton btnMore = (ImageButton) findViewById(R.id.btnMoreOpsNoteDet);
		btnMore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(NoteDetail.this,v);
				
//				MenuInflater inflater = popup.getMenuInflater();
//				inflater.inflate(R.menu.popup, popup.getMenu());
				popup.getMenu().add(0, 1, 1, "Delete")
								.setIcon(R.drawable.share);
				popup.getMenu().add(0, 2, 2, "Share")
								.setIcon(R.drawable.share);
				
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener(){

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch(item.getItemId()){
						case 1: //delete note
							new AlertDialog.Builder(NoteDetail.this)
							.setTitle("提示")
							.setMessage("是否删除该条记事？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									try{
										db.deleteNote(id);
									}catch(Exception e){
										e.printStackTrace();
									}
									Intent i = new Intent();
									setResult(RESULT_OK,i);
									finish();
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							})
							.show();
							return true;
						case 2:
							Intent iShare = new Intent(Intent.ACTION_SEND);
							iShare.setType("text/plain");
							iShare.putExtra(Intent.EXTRA_SUBJECT, title);
							iShare.putExtra(Intent.EXTRA_TEXT,  content);
							startActivity(Intent.createChooser(iShare, getTitle()));
							return true;
						}
						return false;
					}
					
				});
				popup.show();
			}
			
		});
				
	}
	
	private void setNote(String sid){
		Cursor cursor =  db.selectNotes(sid);
		cursor.moveToFirst();
		title = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.NOTE_TITLE));
		content = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.NOTE_CONTENT));
		
		BorderTextView btvTitle = (BorderTextView) findViewById(R.id.btvNoteDetTitle);
		BorderTextView btvContent = (BorderTextView) findViewById(R.id.btvNoteDetContent);
		btvTitle.setText(title);
		btvContent.setText(content);
		cursor.close();
	}
	
	@Override
	public void onDestroy(){		
		super.onDestroy();
		db.close();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setNote(id);
	}
}

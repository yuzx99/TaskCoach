/**
 * 
 */
package com.yuzx.taskcoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.view.BorderEditText;

/**
 * @author yuzx
 *
 */
public class NoteEditActivity extends Activity {
	
	private BorderEditText etTitle, etContent;
	private ImageButton	btnSave;
	private ImageButton btnCancel;
	private DBOpenHelper db;
	private String listTitle, listContent; 
	private String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_edit_layout);
		
		etTitle = (BorderEditText)findViewById(R.id.et_title);
		etContent = (BorderEditText)findViewById(R.id.et_content);
		btnSave = (ImageButton)findViewById(R.id.btnSaveNote);
		btnCancel = (ImageButton)findViewById(R.id.btnCancelNote);
		db = new DBOpenHelper(this);
		
		Intent intent = getIntent();
		id = intent.getStringExtra(DBOpenHelper.NOTE_ID);
		listTitle = intent.getStringExtra(DBOpenHelper.NOTE_TITLE);
		listContent = intent.getStringExtra(DBOpenHelper.NOTE_CONTENT);
		
		if (listTitle!=null) {
			etTitle.setText(listTitle);
		}
		if (listContent!=null) {
			etContent.setText(listContent);
		}
		
		etContent.addTextChangedListener(new TextWatcher(){

			Boolean emptyTitle=false;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				if(etTitle.getText().toString().equals("")){
					emptyTitle=true;				
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if(emptyTitle){
					String content = etContent.getText().toString();
					if (content.length()<=5){
						etTitle.setText(content);
					}else{
						etTitle.setText(content.subSequence(0, 5));
					}
				}
					
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		btnSave.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String title = etTitle.getText().toString();
				String content = etContent.getText().toString();
				
				if (title.equals("")){
					Toast.makeText(getApplicationContext(), "标题不能为空！", Toast.LENGTH_LONG)
					.show();
				} else {
					if(id!=null){
						db.updateNote(id, title, content);	
					}
					else db.insertNote(title, content);
					
					Intent intent = new Intent();
					setResult(RESULT_OK,intent);
					finish();
				}
								
			}			
		});
		
		btnCancel.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String title = etTitle.getText().toString();
				String content = etContent.getText().toString();
				if(title.equals("") && content.equals("")){
					Intent intent = new Intent();
					setResult(RESULT_CANCELED,intent);
					finish();
				}else{
					new AlertDialog.Builder(NoteEditActivity.this)
					.setTitle("提示")
					.setMessage("不保存该条记事？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							Intent i = new Intent();
							setResult(RESULT_CANCELED,i);
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
				}
				
			}
			
		});
		
	}
	
	@Override
	public void onDestroy(){
		db.close();
		super.onDestroy();
	}
	
}

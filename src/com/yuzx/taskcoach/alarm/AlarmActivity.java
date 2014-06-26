/**
 * 
 */
package com.yuzx.taskcoach.alarm;


import com.yuzx.taskcoach.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * @author yuzx
 *
 */
public class AlarmActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//Notification noti = new Notification();
		Intent intent = getIntent();
		String content = "日程提醒";
		if(intent!=null){
			content=intent.getStringExtra("REMIND_CONTENT");
		}
		new AlertDialog.Builder(AlarmActivity.this,AlertDialog.THEME_HOLO_DARK)
		.setIcon(R.drawable.alarms)
		.setTitle("提醒").setMessage(content)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				AlarmActivity.this.finish();
			}
		}).show();
		
		 
	}
}

/**
 * 
 */
package com.yuzx.taskcoach.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.yuzx.taskcoach.R;

/**
 * @author yuzx
 *
 */
public class LocationAlert extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//Notification noti = new Notification();
		Intent intent = getIntent();
		String content = "λ������";
		if (intent!=null){
			content = intent.getStringExtra("REMIND_LOCATION");
		}
		new AlertDialog.Builder(LocationAlert.this,AlertDialog.THEME_HOLO_DARK)
		.setIcon(R.drawable.alarms)
		.setTitle("����").setMessage(content)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				LocationAlert.this.finish();
			}
		}).show();
		
		 
	}
}

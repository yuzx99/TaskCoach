/**
 * 
 */
package com.yuzx.taskcoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

/**
 * @author yuzx
 *
 */
public class SetTimeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_time);
		
		ImageButton btnSave = (ImageButton)findViewById(R.id.btnSaveTimeSetting);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				startActivity(intent);
			}
		});
	}
}

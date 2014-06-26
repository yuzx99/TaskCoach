/**
 * 
 */
package com.yuzx.taskcoach.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author yuzx
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		intent.setClass(context, AlarmActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}

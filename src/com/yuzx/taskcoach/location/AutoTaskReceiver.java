/**
 * 
 */
package com.yuzx.taskcoach.location;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;
import android.widget.Toast;

/**
 * @author yuzx
 *
 */
public class AutoTaskReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int autotype = intent.getIntExtra("AUTOTASK_TYPE", -1);
		if(autotype == 0){
			final AudioManager am = (AudioManager)
					context.getSystemService(Context.AUDIO_SERVICE);
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Toast.makeText(context, "…Ë÷√æ≤“Ù", Toast.LENGTH_LONG).show();
		}else if(autotype == 1){
			ContentResolver cr = context.getContentResolver();
			if(Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")){
				Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
				Intent iFly = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				context.sendBroadcast(iFly);
			}
		}else if(autotype == 2){
			ContentResolver cr = context.getContentResolver();
			if(Settings.System.getString(cr, Settings.System.WIFI_ON).equals("0")){
				Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
				Intent iFly = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
				context.sendBroadcast(iFly);
			}
		}
	}

}

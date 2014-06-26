/**
 * 
 */
package com.yuzx.taskcoach;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.AbstractWeibo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author yuzx
 * 
 */
public class FlashActivity extends Activity {
	int times = 0;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbstractWeibo.initSDK(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.flashlayout);
		
//		final Calendar c = Calendar.getInstance();
//		switch(c.get(Calendar.HOUR_OF_DAY)){
//		case 1:
//		case 2:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("1.jpg"));
//			break;
//		case 3:
//		case 4:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("3.jpg"));
//			break;
//		case 5:
//		case 6:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("5.jpg"));
//			break;
//		case 7:
//		case 8:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("7.jpg"));
//			break;
//		case 9:
//		case 10:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("9.jpg"));
//			break;
//		case 11:
//		case 12:
//			findViewById(R.id.flashLayout).setBackgroundDrawable(
//					getImageFromAssetsFile("11.jpg"));
//			break;
//		default:
//			break;
//		}
		
		
		startTimer();
	}

	private void openNextPage() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private class SpinnerTask extends TimerTask {
		public void run() {
			times++;
			if (times > 2) {
				timer.cancel();
				times = 0;
				openNextPage();
			}
		}
	}

	public void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new SpinnerTask(), 100, 1000);
		}
	}

	private BitmapDrawable getImageFromAssetsFile(String fileName) {
		Bitmap image = null;
		AssetManager am = getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BitmapDrawable bd=new BitmapDrawable(getResources(),image);
		return bd;

	}

}

package com.yuzx.taskcoach;

import java.util.ArrayList;
import java.util.HashMap;

import com.yuzx.taskcoach.share.AuthActivity;
import com.yuzx.taskcoach.weather.WeatherActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * @author yuzx
 * 
 */

public class MenuActivity extends Activity {

	private ImageView mCover;
	private ListView mList;
	private Animation mStartAnimation;
	private Animation mStopAnimation;
	private static final int DURATION_MS = 400;
	private static Bitmap sCoverBitmap = null;

	// 2个步骤
	// 1. activity-->other activity
	// 2. anim
	// 先切换到另一个activity
	// 再获得之前activity屏幕的快照将它作为一个cover覆盖在下一个屏幕的上面，然后通过动画移动这个cover，让人感觉好像是前一个屏幕的移动。

	public static void prepare(Activity activity, int id) {
		if (sCoverBitmap != null) {
			sCoverBitmap.recycle();
		}
		// 用指定大小生成一张透明的32位位图，并用它构建一张canvas画布
		sCoverBitmap = Bitmap.createBitmap(
				activity.findViewById(id).getWidth(), activity.findViewById(id)
						.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(sCoverBitmap);
		// 将指定的view包括其子view渲染到这种画布上，在这就是上一个activity布局的一个快照，现在这个bitmap上就是上一个activity的快照
		activity.findViewById(id).draw(canvas);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 绝对布局最上层覆盖了一个imageview
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);
		initAnim();
		mCover = (ImageView) findViewById(R.id.slidedout_cover);
		mCover.setImageBitmap(sCoverBitmap);
		mCover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
		mList = (ListView) findViewById(R.id.list);
		// ArrayList<HashMap<String, Object>> listItem = new
		// ArrayList<HashMap<String, Object>>();
		//
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("ivItemLabel", R.drawable.calendar);
		// map.put("tvItemTitle", " 活动");
		// listItem.add(map);
		//
		// HashMap<String, Object> map1 = new HashMap<String, Object>();
		// map1.put("ivItemLabel", R.drawable.note);
		// map1.put("tvItemTitle", " 记事");
		// listItem.add(map1);
		//
		// mList.setAdapter(new SimpleAdapter(this, listItem,
		// R.layout.menu_list_item,
		// new String[]{"ivItemLabel","tvItemTitle"},
		// new int[]{R.id.ivItemLabel,R.id.tvItemTitle}));
		mList.setAdapter(new ArrayAdapter<String>(MenuActivity.this,
				android.R.layout.simple_list_item_1, new String[] { " 我的一天",
						" 活动", " 记事", " 测试", " 天气", " Task Map", " 位置管理" }));
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0: {
					Intent iOneDay = new Intent();
					iOneDay.setClass(MenuActivity.this, OneDayActivity.class);
					startActivity(iOneDay);
				}
					break;
				case 1: {
					Intent iSchedule = new Intent();
					iSchedule.setClass(MenuActivity.this,
							ScheduleActivity.class);
					startActivity(iSchedule);
				}
					break;
				case 2: {
					Intent iNote = new Intent();
					iNote.setClass(MenuActivity.this, NoteEditActivity.class);
					startActivity(iNote);
				}
					break;
				case 3: {
//					final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//					am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

					ContentResolver cr = getContentResolver();
					if(Settings.System.getString(cr, Settings.System.AIRPLANE_MODE_ON).equals("0")){
						Settings.System.putString(cr, Settings.System.AIRPLANE_MODE_ON, "1");
						Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
						sendBroadcast(intent);
					}

					// final String LOC_BROADCAST =
					// "com.yuzx.taskcoach.action.LOC_BROADCAST";
					// final LocationManager lm = (LocationManager)
					// getSystemService(Context.LOCATION_SERVICE);
					// Intent intent = new Intent();
					// intent.setAction(LOC_BROADCAST);
					// intent.putExtra("REMIND_LOCATION", "location");
					// PendingIntent pIntent =
					// PendingIntent.getBroadcast(MenuActivity.this,
					// 0, intent, 0);
					// /*PendingIntent pIntent =
					// PendingIntent.getBroadcast(MenuActivity.this,
					// 0, new Intent(android.content.Intent.ACTION_VIEW,
					// Uri.parse("http://www.baidu.com")), 0);*/
					// lm.addProximityAlert(31.28502833, 121.5057, 50, -1,
					// pIntent);
					Toast.makeText(MenuActivity.this, " Location Alert",
							Toast.LENGTH_SHORT).show();
				}

					break;
				case 4: {
					Intent iWeather = new Intent();
					iWeather.setClass(MenuActivity.this, WeatherActivity.class);
					startActivity(iWeather);
				}
					break;
				case 5: {
					/*
					 * Intent iAuth = new Intent();
					 * iAuth.setClass(MenuActivity.this, AuthActivity.class);
					 * startActivity(iAuth);
					 */
					Intent iMap = new Intent();
					iMap.setClass(MenuActivity.this, TaskMapActivity.class);
					startActivity(iMap);
				}
					break;
				case 6: {
					Intent iLocation = new Intent();
					iLocation.setClass(MenuActivity.this,
							LocationActivity.class);
					startActivity(iLocation);
				}
					break;
				default:
					break;
				}

				close();
			}
		});
		open();
	}

	public void initAnim() {

		// 采用了绝对布局，绝对布局是view的左上角从(0,0)开始
		@SuppressWarnings("deprecation")
		final android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 0, 0);
		findViewById(R.id.slideout_placeholder).setLayoutParams(lp);

		// 屏幕的宽度
		int displayWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getWidth();
		// 右边的位移量，60dp转换成px
		int sWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
						.getDisplayMetrics());
		// 将快照向右移动的偏移量
		final int shift = displayWidth - sWidth;

		// 向右移动的位移动画向右移动shift距离，y方向不变
		mStartAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE,
				0, TranslateAnimation.ABSOLUTE, shift,
				TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0);

		// 回退时的位移动画
		mStopAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0,
				TranslateAnimation.ABSOLUTE, -shift,
				TranslateAnimation.ABSOLUTE, 0, TranslateAnimation.ABSOLUTE, 0);
		// 持续时间
		mStartAnimation.setDuration(DURATION_MS);
		// 动画完成时停留在结束位置
		mStartAnimation.setFillAfter(true);
		mStartAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束时回调
				// 将imageview固定在位移后的位置
				mCover.setAnimation(null);
				@SuppressWarnings("deprecation")
				final android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
						shift, 0);
				mCover.setLayoutParams(lp);
			}
		});

		mStopAnimation.setDuration(DURATION_MS);
		mStopAnimation.setFillAfter(true);
		mStopAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				finish();
				overridePendingTransition(0, 0);
			}
		});

	}

	public void open() {
		mCover.startAnimation(mStartAnimation);
	}

	public void close() {
		mCover.startAnimation(mStopAnimation);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 摁返回键时也要触发动画
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
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
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import cn.sharesdk.evernote.Evernote;
import cn.sharesdk.onekeyshare.SharePage;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.yuzx.taskcoach.db.ConstData;
import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.view.BorderTextView;

/**
 * @author yuzx
 *
 */
public class ScheduleDetail extends Activity {
	
	private ImageButton btnBack;
	private ImageButton btnShare;
	private BorderTextView btvContent;
	private BorderTextView btvDateTime;
	private BorderTextView btvRemind;
	private BorderTextView btvLocation;
	private BorderTextView btvEdit;
	private BorderTextView btvDelete;
	

	private String mID,mContent,mType,mDate,mTime,mRemind,mLocation,mEndDate,mEndTime;
	private Intent intent;
	private DBOpenHelper db;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.schedule_detail);
		intent = getIntent();
		db = new DBOpenHelper(this);
		mID = intent.getStringExtra(DBOpenHelper.SCHEDULE_ID);
		handler = new Handler();		
//		mContent = intent.getStringExtra(DBOpenHelper.SCHEDULE_CONTENT);
//		mDate = intent.getStringExtra(DBOpenHelper.SCHEDULE_DATE);
//		mTime = intent.getStringExtra(DBOpenHelper.SCHEDULE_TIME);
//		mRemind = intent.getStringExtra(DBOpenHelper.SCHEDULE_REMIND);
//		mLocation = intent.getStringExtra(DBOpenHelper.SCHEDULE_LOCATION);
//		mType = intent.getStringExtra(DBOpenHelper.SCHEDULE_TYPE);
		initializeViews();
		if (mID !=null){
			setSchedule(mID);
		}
		
	}
	
	private void setSchedule(String id){
		Cursor cursor = db.selectSchedules(id); 
		cursor.moveToFirst();
		mContent = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT));
		mDate = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DATE));
		mTime = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME));
		mEndDate = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDDATE));
		mEndTime = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDTIME));
		mRemind = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_REMIND));
		mLocation = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_LOCATION));
		mType = cursor.getString(cursor.
				getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TYPE));
		btvContent.setText(mContent);
		btvDateTime.setText(new StringBuilder().append(mDate).append("   ").append(mTime)
				.append(" -- ").append(mEndDate).append("   ").append(mEndTime));
		int i =0;
		try{
			i = Integer.parseInt(mRemind);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		btvRemind.setText(ConstData.reminds[i]);
		btvLocation.setText(mLocation);
		cursor.close();
	}
	
	private Thread newShareThread(final String platform) {
		return new Thread(){
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						Intent i = new Intent(ScheduleDetail.this, SharePage.class);
//						i.putExtra("notif_icon", R.drawable.ic_launcher);
//						i.putExtra("notif_title", menu.getContext().getString(R.string.app_name));
//						
//						i.putExtra("title", menu.getContext().getString(R.string.share));
//						i.putExtra("text", menu.getContext().getString(R.string.share_content));
//						i.putExtra("image", image);
//						i.putExtra("image_url", "http://sharesdk.cn/Public/Frontend/images/logo.png");
//						i.putExtra("site", menu.getContext().getString(R.string.app_name));
//						i.putExtra("siteUrl", "http://sharesdk.cn");
//						
//						i.putExtra("platform", platform);
//						menu.getContext().startActivity(i);
						String shareSubject = mContent;
						String shareText = mContent + "  "
								+ "时间：" + mDate + " "+ mTime +"--" + mEndDate + " "+mEndTime
								+ "地点：" + mLocation;
						
						i.putExtra("notif_icon", R.drawable.ic_launcher);
						i.putExtra("notif_title", ScheduleDetail.this.getString(R.string.app_name));
						
						i.putExtra("title", shareSubject);
						i.putExtra("text", shareText);
						i.putExtra("platform", platform);
						ScheduleDetail.this.startActivity(i);
					}
				});
			}
		};
	}
	
	private void initializeViews(){
		btnBack = (ImageButton) findViewById(R.id.btnBackSchDet);
		btnShare = (ImageButton) findViewById(R.id.btnShareSchDet);
		btvContent = (BorderTextView) findViewById(R.id.btvContentSchDet);
		btvDateTime = (BorderTextView) findViewById(R.id.btvDateTimeSchDet);
		btvRemind = (BorderTextView) findViewById(R.id.btvRemindSchDet);
		btvLocation = (BorderTextView) findViewById(R.id.btvLocSchDet);
		btvEdit = (BorderTextView) findViewById(R.id.btvEditSchDet);
		btvDelete = (BorderTextView) findViewById(R.id.btvDelSchDet);
		

		
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentBack = new Intent();
				setResult(RESULT_OK,intentBack);
				finish();
			}
			
		});
		
		btnShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(ScheduleDetail.this,v);
//				MenuInflater inflater = popup.getMenuInflater();
//				inflater.inflate(R.menu.popup, popup.getMenu());
				popup.getMenu().add(0, 1, 1, " 到Weibo");
				popup.getMenu().add(0, 2, 2, " 到Evernote");
				popup.getMenu().add(0, 3, 3, " 到短信/邮件");
				
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub						
						switch(item.getItemId()){
						case 1:
							newShareThread(SinaWeibo.NAME).start();
							return true;
						case 2:
							newShareThread(Evernote.NAME).start();
							return true;
						case 3:
							String shareSubject = mContent;
							String shareText = mContent + "  "
									+ "时间：" + mDate + " "+ mTime +"--" + mEndDate + " "+mEndTime
									+ "地点：" + mLocation;
							Intent email = new Intent(Intent.ACTION_SEND);
							email.setType("text/plain");
							email.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
							email.putExtra(Intent.EXTRA_TEXT,  shareText);
							startActivity(Intent.createChooser(email, getTitle()));
							return true;
						}
						return false;
					}
				});
				popup.show();
			}
			
		});
		
		btvEdit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentEdit = new Intent();
				intentEdit.setClass(ScheduleDetail.this, ScheduleActivity.class);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_ID, mID);
				/*
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_CONTENT, mContent);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_DATE, mDate);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_TIME, mTime);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_REMIND, mRemind);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_TYPE, mType);
				intentEdit.putExtra(DBOpenHelper.SCHEDULE_LOCATION, mLocation);
				*/
				startActivityForResult(intentEdit, 0);
			}
			
		});
		
		btvDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ScheduleDetail.this)
						.setTitle("提示")
						.setMessage("是否删除该条日程活动？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								try{
									db.deleteSchedule(mID);
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

																
			}
			
		});
				
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setSchedule(mID);
	}
	
	@Override
	public void onDestroy(){
		db.close();
		super.onDestroy();
	}
	
}

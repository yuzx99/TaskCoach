/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.view.BorderEditText;
import com.yuzx.taskcoach.view.BorderTextView;

/**
 * @author yuzx
 * 
 */
public class ScheduleActivity extends Activity {

	private ImageButton btnSave = null;
	private ImageButton btnCancel = null;
	// private Button btnSetDate = null;
	// private TextView tvSetDate = null;
	// private Button btnSetTime = null;
	// private TextView tvSetTime = null;
	private Spinner spSetType = null;
	private Spinner spSetAlarm = null;
	private BorderEditText betContent = null;
	private BorderTextView btvDate = null;
	private BorderTextView btvTime = null;
	private BorderEditText betLocation = null;
	private BorderTextView btvDateEnd = null;
	private BorderTextView btvTimeEnd = null;
	
	private static final int SHOW_DATEPICKER = 0;
	private static final int SET_DATE_DIALOG = 1;
	private static final int SHOW_TIMEPICKER = 2;
	private static final int SET_TIME_DIALOG = 3;
	private static final int SHOW_DATEPICKER2 = 4;
	private static final int SET_DATE_DIALOG2 = 5;
	private static final int SHOW_TIMEPICKER2 = 6;
	private static final int SET_TIME_DIALOG2 = 7;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private int mYearEnd;
	private int mMonthEnd;
	private int mDayEnd;
	private int mHourEnd;
	private int mMinuteEnd;
	
	private int mAlarm;
	private int mType;
	private String mContent;
	private String mLocation;

	private DBOpenHelper db;
	private Intent intent;
	private String id;
	
	public static final String NEW_BROADCAST = "com.yuzx.taskcoach.action.NEW_BROADCAST";
	//不提醒，正点，提前5min,10min,30min,1h,1d,3d in Mills
	public static final int[] remindTypes = {-1,0,300000,600000,1800000,
		3600000,86400000,259200000};
	private static int i = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.schedule_edit_layout);
		db = new DBOpenHelper(this);
		intent = getIntent();
		initializeViews();
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1 ;
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		mYearEnd = mYear;
		mMonthEnd = mMonth;
		mDayEnd = mDay;
		if(mHour < 23){
			mHourEnd = mHour + 1;
		}else{
			mHourEnd = mHour;
		}
		
		mMinuteEnd = mMinute;
		
		id = intent.getStringExtra(DBOpenHelper.SCHEDULE_ID);
	
		if (id != null){
			Cursor cursor = db.selectSchedules(id); 
			cursor.moveToFirst();
			String content = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_CONTENT));
			String date = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_DATE));
			String time = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TIME));
			String enddate = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDDATE));
			String endtime = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_ENDTIME));
			String alarm = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_REMIND));
			String location = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_LOCATION));
			String type = cursor.getString(cursor.
					getColumnIndexOrThrow(DBOpenHelper.SCHEDULE_TYPE));
			
//			String date = intent.getStringExtra(DBOpenHelper.SCHEDULE_DATE);
//			String time = intent.getStringExtra(DBOpenHelper.SCHEDULE_TIME);
//			String content = intent.getStringExtra(DBOpenHelper.SCHEDULE_CONTENT);
//			String location = intent.getStringExtra(DBOpenHelper.SCHEDULE_LOCATION);
//			String alarm = intent.getStringExtra(DBOpenHelper.SCHEDULE_REMIND);
//			String type = intent.getStringExtra(DBOpenHelper.SCHEDULE_TYPE);
//			
			mYear = Integer.parseInt(date.split("\\/")[0]);
			mMonth = Integer.parseInt(date.split("\\/")[1]);
			mDay = Integer.parseInt(date.split("\\/")[2]);
			mHour = Integer.parseInt(time.split("\\:")[0]);
			mMinute = Integer.parseInt(time.split("\\:")[1]);

			mYearEnd = Integer.parseInt(enddate.split("\\/")[0]);
			mMonthEnd = Integer.parseInt(enddate.split("\\/")[1]);
			mDayEnd = Integer.parseInt(enddate.split("\\/")[2]);
			mHourEnd = Integer.parseInt(endtime.split("\\:")[0]);
			mMinuteEnd = Integer.parseInt(endtime.split("\\:")[1]);
			
			mAlarm = Integer.parseInt(alarm);
			mType = Integer.parseInt(type);
			
			betContent.setText(content);
			betLocation.setText(location);
			spSetType.setSelection(mType);
			spSetAlarm.setSelection(mAlarm);
			
			cursor.close();
		}
		

		updateDateDisplay();
		updateTimeDisplay();
		updateEndDateDisplay();
		updateEndTimeDisplay();
//		setDate();
//		setTime();
	}

	private void initializeViews() {
		btnSave = (ImageButton) findViewById(R.id.btnSaveSchedule);
		btnCancel = (ImageButton) findViewById(R.id.btnCancelSchedule);

		betContent = (BorderEditText) findViewById(R.id.betScheduleContent);
		btvDate = (BorderTextView) findViewById(R.id.btvScheduleDate);
		btvTime = (BorderTextView) findViewById(R.id.btvScheduleTime);
		betLocation = (BorderEditText) findViewById(R.id.betScheduleLocation);
		btvDateEnd = (BorderTextView) findViewById(R.id.btvScheduleEndDate);
		btvTimeEnd = (BorderTextView) findViewById(R.id.btvScheduleEndTime);
		
		spSetAlarm = (Spinner) findViewById(R.id.spSetAlarm);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.setAlarm_labels,
				android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSetAlarm.setAdapter(adapter);
		spSetAlarm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mAlarm = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		spSetType = (Spinner) findViewById(R.id.spSetType);
		ArrayAdapter<CharSequence> adapterType = ArrayAdapter
				.createFromResource(this, R.array.setType_labels,
						android.R.layout.simple_spinner_item);
		adapterType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSetType.setAdapter(adapterType);
		spSetType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mType = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		btnSave.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContent = betContent.getText().toString();
				Log.d("Content",mContent);
				if (mContent.equals("")) {
					Toast.makeText(v.getContext(), "提醒内容不能为空", Toast.LENGTH_SHORT)
					.show();
				} else {
					String date = String.valueOf(mYear) + "/" +
								String.valueOf(mMonth) + "/" + String.valueOf(mDay);
					String time = mHour + ":" + mMinute;
					String enddate = String.valueOf(mYearEnd)+"/"+
								String.valueOf(mMonthEnd) +"/" + String.valueOf(mDayEnd);
					String endtime = mHourEnd + ":" + mMinuteEnd;
					mLocation = betLocation.getText().toString();
					if(id == null){						
						db.insertSchedule(mContent, mType, date, time, enddate,endtime,mAlarm,
								mLocation);		
						setAlarm(mAlarm, i, mContent);
					}else{
						db.updateSchedule(id,mContent, mType, date, time,enddate,endtime, mAlarm,
								mLocation);
						setAlarm(mAlarm, Integer.parseInt(id), mContent);
					}
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
					finish();
				}
			}

		});

		btnCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		btvDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if (btvDate.equals((BorderTextView) v)) {
					msg.what = ScheduleActivity.SHOW_DATEPICKER;
				}
				ScheduleActivity.this.hSetDateTime.sendMessage(msg);
			}
		});

		btvTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if (btvTime.equals((BorderTextView) v)) {
					msg.what = ScheduleActivity.SHOW_TIMEPICKER;
				}
				ScheduleActivity.this.hSetDateTime.sendMessage(msg);
			}
		});
		
		btvDateEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if (btvDateEnd.equals((BorderTextView) v)){
					msg.what = ScheduleActivity.SHOW_DATEPICKER2;
				}
				ScheduleActivity.this.hSetDateTime.sendMessage(msg);
			}
		});
		
		btvTimeEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if (btvTimeEnd.equals((BorderTextView) v)){
					msg.what = ScheduleActivity.SHOW_TIMEPICKER2;
				}
				ScheduleActivity.this.hSetDateTime.sendMessage(msg);
			}
		});
	}

	private void setAlarm(int remindType, int id, String content) {
		final AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent intent = new Intent();
		intent.putExtra("REMIND_CONTENT", content);
		intent.setAction(NEW_BROADCAST);
		long time1 = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.set(mYear, mMonth-1, mDay, mHour, mMinute, 0);
		Log.d("alarm",c.toString());
		Log.d("alarm",String.valueOf(remindType));
		long time2 = c.getTimeInMillis()-remindTypes[remindType];
		
		PendingIntent sender = PendingIntent.getBroadcast(
				ScheduleActivity.this, id, intent, 0);
		if(remindType!=0 && time2 - time1 > 0){
			
			am.set(AlarmManager.RTC_WAKEUP, time2, sender);
			Toast.makeText(ScheduleActivity.this, "SetAlarm", 
					Toast.LENGTH_SHORT).show();
//			//当备忘录建立成功是,弹出的toast通知
//			Toast.makeText(ScheduleActivity.this, R.string.build_memo,
//	                    Toast.LENGTH_SHORT).show();
		}else{
			am.cancel(sender);
		}
	
	}
	private void setDate() {
		final Calendar c = Calendar.getInstance();

		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		updateDateDisplay();
	}

	private void updateDateDisplay() {
		// tvSetDate.setText(new StringBuilder().append(mYear).append("-")
		// .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
		// .append("-").append((mDay < 10) ? "0" + mDay : mDay));
		btvDate.setText(new StringBuilder().append(mYear).append("/")
				.append(mMonth  < 10 ? "0" + mMonth : mMonth)
				.append("/").append((mDay < 10) ? "0" + mDay : mDay));
	}

	private void updateEndDateDisplay() {
		// tvSetDate.setText(new StringBuilder().append(mYear).append("-")
		// .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
		// .append("-").append((mDay < 10) ? "0" + mDay : mDay));
		btvDateEnd.setText(new StringBuilder().append(mYearEnd).append("/")
				.append(mMonthEnd  < 10 ? "0" + mMonthEnd : mMonthEnd)
				.append("/").append((mDayEnd < 10) ? "0" + mDayEnd : mDayEnd));
	}
	
	private void setTime() {
		final Calendar c = Calendar.getInstance();

		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		updateTimeDisplay();
	}

	private void updateTimeDisplay() {
		// tvSetTime.setText(new StringBuilder().append(mHour).append(":")
		// .append((mMinute < 10) ? "0" + mMinute : mMinute));
		btvTime.setText(new StringBuilder().append(mHour).append(":")
				.append((mMinute < 10) ? "0" + mMinute : mMinute));
		
	}
	
	private void updateEndTimeDisplay() {
		// tvSetTime.setText(new StringBuilder().append(mHour).append(":")
		// .append((mMinute < 10) ? "0" + mMinute : mMinute));
		btvTimeEnd.setText(new StringBuilder().append(mHourEnd).append(":")
				.append((mMinuteEnd < 10) ? "0" + mMinuteEnd : mMinuteEnd));
	
		
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = monthOfYear + 1;
			mDay = dayOfMonth;

			updateDateDisplay();
		}

	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mHour = hourOfDay;
			mMinute = minute;

			updateTimeDisplay();
		}

	};

	private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mYearEnd = year;
			mMonthEnd = monthOfYear + 1;
			mDayEnd = dayOfMonth;

			updateEndDateDisplay();
		}

	};

	private TimePickerDialog.OnTimeSetListener mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			mHourEnd = hourOfDay;
			mMinuteEnd = minute;

			updateEndTimeDisplay();
		}

	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SET_DATE_DIALOG:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth-1,
					mDay);
		case SET_TIME_DIALOG:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					true);
		case SET_DATE_DIALOG2:
			return new DatePickerDialog(this, mEndDateSetListener, mYearEnd, 
					mMonthEnd-1, mDayEnd);
		case SET_TIME_DIALOG2:
			return new TimePickerDialog(this, mEndTimeSetListener, mHourEnd, 
					mMinuteEnd, true);

		}
		return null;
	}

	@Override
	public void onDestroy(){
		db.close();
		super.onDestroy();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SET_DATE_DIALOG:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth-1, mDay);
			break;
		case SET_TIME_DIALOG:
			((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		case SET_DATE_DIALOG2:
			((DatePickerDialog) dialog).updateDate(mYearEnd, mMonthEnd-1, mDayEnd);
			break;
		case SET_TIME_DIALOG2:
			((TimePickerDialog) dialog).updateTime(mHourEnd, mMinuteEnd);
			break;
		}
	}

	Handler hSetDateTime = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ScheduleActivity.SHOW_DATEPICKER:
				showDialog(SET_DATE_DIALOG);
				break;
			case ScheduleActivity.SHOW_TIMEPICKER:
				showDialog(SET_TIME_DIALOG);
				break;
			case ScheduleActivity.SHOW_DATEPICKER2:
				showDialog(SET_DATE_DIALOG2);
				break;
			case ScheduleActivity.SHOW_TIMEPICKER2:
				showDialog(SET_TIME_DIALOG2);
				break;
			}
		}
	};
}

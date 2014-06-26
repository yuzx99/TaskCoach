package com.yuzx.taskcoach.db;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "yuzx_taskcoach_db";

	/*
	 * table schema
	 */
	private static final String NOTE_TABLE = "notepad";
	public static final String NOTE_ID = "note_id";
	public static final String NOTE_TITLE = "note_title";
	public static final String NOTE_CONTENT = "note_content";
	public static final String NOTE_DATE = "note_date";  // 2013/4/1
	public static final String NOTE_TIME = "note_time";  // 10:10 

	public static final String SCHEDULE_TABLE = "schedule";
	public static final String SCHEDULE_ID = "schedule_id";
	public static final String SCHEDULE_CONTENT = "schedule_content";
	public static final String SCHEDULE_TYPE = "schedule_type";
	public static final String SCHEDULE_DATE = "schedule_date";
	public static final String SCHEDULE_TIME = "schedule_time";
	public static final String SCHEDULE_ENDDATE = "schedule_end_date";
	public static final String SCHEDULE_ENDTIME = "schedule_end_time";
	public static final String SCHEDULE_REMIND = "schedule_remind";
	public static final String SCHEDULE_LOCATION = "schedule_location";
	public static final String SCHEDULE_DONE = "schedule_done";	//1 for done
	
	public static final String LOCATION_TABLE = "location";
	public static final String LOCATION_ID	= "loc_id";
	public static final String LOCATION_NAME = "loc_name";
	public static final String LOCATION_LONG = "loc_long";
	public static final String LOCATION_LATI = "loc_lati";
	
	public static final String AUTOTASK_TABLE = "autotask";
	public static final String AUTOTASK_ID = "auto_id";
	public static final String AUTOTASK_TYPE = "auto_type";
	public static final String AUTOTASK_LONG = "auto_long";
	public static final String AUTOTASK_LATI = "auto_lati";
	
	public DBOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBOpenHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public DBOpenHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DBOpenHelper(Context context) {
		this(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + NOTE_TABLE + " (" + NOTE_ID
				+ " integer primary key autoincrement, " + NOTE_TITLE
				+ " text, " + NOTE_CONTENT + " text, " + NOTE_DATE + " text, "
				+ NOTE_TIME + " text )";
		db.execSQL(sql);
		
		sql = "create table if not exists " + SCHEDULE_TABLE + " ("
			+ SCHEDULE_ID+ " integer primary key autoincrement, "
			+ SCHEDULE_CONTENT + " text, " + SCHEDULE_TYPE + " integer, "
			+ SCHEDULE_DATE + " text, " + SCHEDULE_TIME + " text, "
			+ SCHEDULE_ENDDATE + " text, " + SCHEDULE_ENDTIME + " text, "
			+ SCHEDULE_REMIND + " integer, " + SCHEDULE_LOCATION + " text, "
			+ SCHEDULE_DONE + " integer )";
		
		db.execSQL(sql);
		
		sql = "create table if not exists " + LOCATION_TABLE + " ("
			+ LOCATION_ID + " integer primary key autoincrement, "
			+ LOCATION_NAME + " text, " + LOCATION_LONG + " double, "
			+ LOCATION_LATI + " double )";
		
		db.execSQL(sql);
		
		sql = "create table if not exists " + AUTOTASK_TABLE + " ("
				+ AUTOTASK_ID + " integer primary key autoincrement, "
				+ AUTOTASK_TYPE + " integer, " + AUTOTASK_LONG + " double, "
				+ AUTOTASK_LATI + " double )";
		
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "drop table if exists " + NOTE_TABLE;
		db.execSQL(sql);
		sql = "drop table if exists " + SCHEDULE_TABLE;
		db.execSQL(sql);
		sql = "drop table if exists " + LOCATION_TABLE;
		db.execSQL(sql);
		sql = "drop table if exists " + AUTOTASK_TABLE;
		this.onCreate(db);
	}

	// search all notes
	public Cursor selectNotes() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(NOTE_TABLE, null, null, null, null, null,
				NOTE_DATE + " desc");
		return cursor;
	}

	public Cursor selectNotes(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValues = {id}; 
		Cursor cursor = db.query(NOTE_TABLE, null, where, whereValues, null, null,
				null);
		return cursor;
	}

	public Cursor selectNotesByDate(String date){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = NOTE_DATE + "=?";
		String[] whereValues = {date}; 
		Cursor cursor = db.query(NOTE_TABLE, null, where, whereValues, null, null,
				null);
		return cursor;
	}
	
	public long insertNote(String title, String content) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(NOTE_TITLE, title);
		cv.put(NOTE_CONTENT, content);
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String date = String.valueOf(year)+"/"+String.valueOf(month+1)
					+"/"+String.valueOf(day);
		String time = String.valueOf(hour)+":"+String.valueOf(minute);
		cv.put(NOTE_DATE, date);
		cv.put(NOTE_TIME, time);
		return db.insert(NOTE_TABLE, null, cv);
	}

	public void deleteNote(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValues = { id };
		db.delete(NOTE_TABLE, where, whereValues);
	}

	public int updateNote(String id, String title, String content) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = NOTE_ID + "=?";
		String[] whereValues = { id };
		ContentValues cv = new ContentValues();
		
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String date = String.valueOf(year)+"/"+String.valueOf(month+1)
					+"/"+String.valueOf(day);
		String time = String.valueOf(hour)+":"+String.valueOf(minute);
		cv.put(NOTE_TITLE, title);
		cv.put(NOTE_CONTENT, content);
		cv.put(NOTE_DATE, date);
		cv.put(NOTE_TIME, time);
		return db.update(NOTE_TABLE, cv, where, whereValues);
	}

	/*
	 * operations for schedule
	 */
	public Cursor selectSchedules(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(SCHEDULE_TABLE, null, null, null, null, null,
				null);
		return cursor;
	}
	
	public Cursor selectSchedules(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = SCHEDULE_ID + "=?";
		String[] whereValues = {id}; 
		Cursor cursor = db.query(SCHEDULE_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
	public Cursor selectSchedulesByDate(String date){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = SCHEDULE_DATE + "=?";
		String[] whereValues = {date}; 
		Cursor cursor = db.query(SCHEDULE_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
	public Cursor selectSchedulesByType(String type){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = SCHEDULE_TYPE + "=?";
		String[] whereValues = {type}; 
		Cursor cursor = db.query(SCHEDULE_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
	public long insertSchedule(String content, int type, String date, String time,
			String enddate, String endtime,int remind, String location){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(SCHEDULE_CONTENT, content);
		cv.put(SCHEDULE_TYPE, type);
		cv.put(SCHEDULE_DATE, date);
		cv.put(SCHEDULE_TIME, time);
		cv.put(SCHEDULE_ENDDATE, enddate);
		cv.put(SCHEDULE_ENDTIME, endtime);
		cv.put(SCHEDULE_REMIND, remind);
		cv.put(SCHEDULE_LOCATION, location);
		cv.put(SCHEDULE_DONE, 0);
		return db.insert(SCHEDULE_TABLE, null, cv);
	}
	
	public void deleteSchedule(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = SCHEDULE_ID + "=?";
		String[] whereValues = { id };
		db.delete(SCHEDULE_TABLE, where, whereValues);
	}
	
	public int updateSchedule(String id, String content, int type, String date, String time,
			String enddate, String endtime,int remind, String location){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = SCHEDULE_ID + "=?";
		String[] whereValues = { id };
		ContentValues cv = new ContentValues();
		cv.put(SCHEDULE_CONTENT, content);
		cv.put(SCHEDULE_TYPE, type);
		cv.put(SCHEDULE_DATE, date);
		cv.put(SCHEDULE_TIME, time);
		cv.put(SCHEDULE_ENDDATE, enddate);
		cv.put(SCHEDULE_ENDTIME, endtime);
		cv.put(SCHEDULE_REMIND, remind);
		cv.put(SCHEDULE_LOCATION, location);
		return db.update(SCHEDULE_TABLE, cv, where, whereValues);
	}
	
	public int closeTask(String id, int done){
		SQLiteDatabase db = this.getWritableDatabase();
		String where = SCHEDULE_ID + "=?";
		String[] whereValues = { id };
		ContentValues cv = new ContentValues();
		cv.put(SCHEDULE_DONE, done);
		return db.update(SCHEDULE_TABLE, cv, where, whereValues);
	}
	
	public Cursor selectLocations() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(LOCATION_TABLE, null, null, null, null, null,
				null);
		return cursor;
	}
	
	public Cursor selectLocations(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = LOCATION_ID + "=?";
		String[] whereValues = {id}; 
		Cursor cursor = db.query(LOCATION_TABLE, null, where, whereValues, null, null,
				null);
		return cursor;
	}

	public Cursor selectLocByName(String name){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = LOCATION_NAME + "=?";
		String[] whereValues = {name}; 
		Cursor cursor = db.query(LOCATION_TABLE, null, where, whereValues, null, null,
				null);
		return cursor;
	}
	
	public long insertLocation(String name, double longi, double lati) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(LOCATION_NAME, name);
		cv.put(LOCATION_LONG, longi);
		cv.put(LOCATION_LATI, lati);
		
		return db.insert(LOCATION_TABLE, null, cv);
	}

	public void deleteLocation(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = LOCATION_ID + "=?";
		String[] whereValues = { id };
		db.delete(LOCATION_TABLE, where, whereValues);
	}

	public int updateLocation(String id, String name,double longi, double lati) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = LOCATION_ID + "=?";
		String[] whereValues = { id };
		ContentValues cv = new ContentValues();

		cv.put(LOCATION_NAME, name);
		cv.put(LOCATION_LONG, longi);
		cv.put(LOCATION_LATI, lati);
		return db.update(LOCATION_TABLE, cv, where, whereValues);
	} 
	
	
	public Cursor selectAutoTask() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(AUTOTASK_TABLE, null, null, null, null, null,
				null);
		return cursor;
	}
	
	public long insertAutoTask(int type, double lng, double lat) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(AUTOTASK_TYPE, type);
		cv.put(AUTOTASK_LONG, lng);	
		cv.put(AUTOTASK_LATI, lat);
		long result = db.insert(AUTOTASK_TABLE, null, cv);
		db.close();
		return result;
	}

	public void deleteAutoTask(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = AUTOTASK_ID + "=?";
		String[] whereValues = { id };
		db.delete(AUTOTASK_TABLE, where, whereValues);
		db.close();
	}
	
	public void deleteAutoTask(double lng, double lat) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = AUTOTASK_LONG + "=?" + " and "
						+ AUTOTASK_LATI + "=?";
		Log.d("in delete", where);
		Log.d("in delete",String.valueOf(lng) );
		Log.d("in delete",String.valueOf(lat) );
		String[] whereValues = { String.valueOf(lng), String.valueOf(lat)};

		int i = db.delete(AUTOTASK_TABLE, where, whereValues);
		db.close();
		Log.d("in delete", String.valueOf(i));
	}
	
	public int getAutoTaskID(double lng, double lat) {
		int id = -1;
		SQLiteDatabase db = this.getReadableDatabase();
		String where = AUTOTASK_LONG + "=?" + " and "
				+ AUTOTASK_LATI + "=?";
		String[] whereValues = { String.valueOf(lng), String.valueOf(lat)};
		Cursor cursor = db.query(AUTOTASK_TABLE, null, where, whereValues, null, null, null);
		if(cursor!=null){
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_ID));
			cursor.close();
		}
		Log.d("in get id", String.valueOf(id));
		db.close();
		return id;
	}
	
	public Cursor getAutoTask(double lng,double lat){
		SQLiteDatabase db = this.getReadableDatabase();
		String where = AUTOTASK_LONG + "=?" + " and "
				+ AUTOTASK_LATI + "=?";
		String[] whereValues = { String.valueOf(lng), String.valueOf(lat)};
		Cursor cursor = db.query(AUTOTASK_TABLE, null, where, whereValues, null, null, null);
		db.close();
		return cursor;
	}
//	public String getCurDateTime() {
//		String date = null;
//		Calendar ca = Calendar.getInstance();
//		int year = ca.get(Calendar.YEAR);
//		int month = ca.get(Calendar.MONTH);
//		int day = ca.get(Calendar.DATE);
//		int minute = ca.get(Calendar.MINUTE);
//		int hour = ca.get(Calendar.HOUR);
////		int second = ca.get(Calendar.SECOND);
////		int WeekOfYear = ca.get(Calendar.DAY_OF_WEEK);
//		date = String.valueOf(year)+"/"
//			 + String.valueOf(month)+ "/"
//			 + String.valueOf(day)+"/  "
//			 + String.valueOf(hour)
//			 + ":"
//			 + String.valueOf(minute);
//		return date;
//	} 
}

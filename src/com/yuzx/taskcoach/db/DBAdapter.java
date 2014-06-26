/**
 * 
 */
package com.yuzx.taskcoach.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author yuzx
 *
 */
public class DBAdapter {
	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "yuzx_taskcoach_db2";
	
	private static final String AUTOTASK_TABLE = "autotask";
	private static final String AUTOTASK_ID = "auto_id";
	private static final String AUTOTASK_TYPE = "auto_type";
	private static final String AUTOTASK_LONG = "auto_long";
	private static final String AUTOTASK_LATI = "auto_lati";
	
	final Context context;
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	
	public DBAdapter(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String sql = "";
			sql = "create table if not exists " + AUTOTASK_TABLE + " ("
					+ AUTOTASK_ID + " integer primary key autoincrement, "
					+ AUTOTASK_TYPE + " integer, " + AUTOTASK_LONG + " double, "
					+ AUTOTASK_LATI + " double )";
			
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			String sql = "drop table if exists " + AUTOTASK_TABLE;
			db.execSQL(sql);
			this.onCreate(db);
		}
		
	}
	
	public DBAdapter open() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		DBHelper.close();
	}
	
	public Cursor selectAutoTask() {
		Cursor cursor = db.query(AUTOTASK_TABLE, null, null, null, null, null,
				null);
		return cursor;
	}
	
	public long insertAutoTask(int type, double lng, double lat) {
		ContentValues cv = new ContentValues();
		cv.put(AUTOTASK_TYPE, type);
		cv.put(AUTOTASK_LONG, lng);	
		cv.put(AUTOTASK_LATI, lat);
		long result = db.insert(AUTOTASK_TABLE, null, cv);
		return result;
	}

	public void deleteAutoTask(String id) {
		String where = AUTOTASK_ID + "=?";
		String[] whereValues = { id };
		db.delete(AUTOTASK_TABLE, where, whereValues);
	}
	
	public void deleteAutoTask(double lng, double lat) {
		String where = AUTOTASK_LONG + "=?" + " and "
						+ AUTOTASK_LATI + "=?";
		Log.d("in delete", where);
		Log.d("in delete",String.valueOf(lng) );
		Log.d("in delete",String.valueOf(lat) );
		String[] whereValues = { String.valueOf(lng), String.valueOf(lat)};

		int i = db.delete(AUTOTASK_TABLE, where, whereValues);
		Log.d("in delete", String.valueOf(i));
	}
	
	public int getAutoTaskID(double lng, double lat) {
		int id = -1;
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
		//db.close();
		return id;
	}
	
	public Cursor getAutoTask(double lng,double lat){
		String where = AUTOTASK_LONG + "=?" + " and "
				+ AUTOTASK_LATI + "=?";
		String[] whereValues = { String.valueOf(lng), String.valueOf(lat)};
		Cursor cursor = db.query(AUTOTASK_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
}

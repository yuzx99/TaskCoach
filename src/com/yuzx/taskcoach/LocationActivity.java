/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.location.LocationAddActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleAdapter;

/**
 * @author yuzx
 * 
 */
public class LocationActivity extends Activity {
	private ImageButton btnBack;
	private ImageButton btnNew;
	private ListView locationList;
	// private TextView tvAllLocation;
	// private TextView tvAddLocation;
	// private TextView tvDelLocation;
	// private TextView tvOpenGPS;

	private DBOpenHelper db;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location);

		db = new DBOpenHelper(this);
		btnBack = (ImageButton) findViewById(R.id.btnBackLocation);
		btnNew = (ImageButton) findViewById(R.id.btnNewLoc);
		locationList = (ListView) findViewById(R.id.locationList);

		
		refreshListView();
		// tvAllLocation = (TextView) findViewById(R.id.allLocation);
		// tvAddLocation = (TextView) findViewById(R.id.addLocation);
		// tvDelLocation = (TextView) findViewById(R.id.deleteLocation);
		// tvOpenGPS = (TextView) findViewById(R.id.openGPS);

		btnBack.setOnClickListener(new ClickListener());
		btnNew.setOnClickListener(new ClickListener());
		locationList.setOnItemClickListener(new LocItemClick());
		// tvAllLocation.setOnClickListener(new ClickListener());
		// tvAddLocation.setOnClickListener(new ClickListener());
		// tvDelLocation.setOnClickListener(new ClickListener());
		// tvOpenGPS.setOnClickListener(new ClickListener());
	}

	private class LocItemClick	implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			cursor.moveToPosition(position);
			final String locId = cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.LOCATION_ID));
			PopupMenu popup = new PopupMenu(LocationActivity.this,view);
//			MenuInflater inflater = popup.getMenuInflater();
//			inflater.inflate(R.menu.popup, popup.getMenu());
			popup.getMenu().add(0, 1, 1, "Delete");
			popup.getMenu().add(0, 2, 2, "Rename");
			
			popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					switch(item.getItemId()){
					case 1:
						new AlertDialog.Builder(LocationActivity.this)
						.setTitle("提示")
						.setMessage("是否删除该位置？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								try{
									db.deleteLocation(locId);
								}catch(Exception e){
									e.printStackTrace();
								}
								refreshListView();
//								Intent i = new Intent();
//								setResult(RESULT_OK,i);
//								finish();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						})
						.show();						
						return true;
					case 2:
						refreshListView();
						return true;
					}
					return false;
				}
			});
			popup.show();
		}
		
	}
	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnBackLocation:
				finish();
				break;
			case R.id.btnNewLoc:
				Intent iAddLoc = new Intent(LocationActivity.this,
						LocationAddActivity.class);
				startActivity(iAddLoc);
				break;
			// case R.id.addLocation:
			// Intent iAddLoc = new
			// Intent(LocationActivity.this,LocationAddActivity.class);
			// startActivity(iAddLoc);
			// break;
			// case R.id.openGPS:
			// Intent iOpenGPS = new
			// Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			// startActivity(iOpenGPS);
			// break;

			default:
				break;
			}
		}

	}

	public boolean hasGPSDevice(Context context) {
		final LocationManager mgr = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (mgr == null)
			return false;
		final List<String> providers = mgr.getAllProviders();
		if (providers == null)
			return false;
		return providers.contains(LocationManager.GPS_PROVIDER);
	}

	private ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		cursor = db.selectLocations();
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("locItemName", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.LOCATION_NAME)));
			map.put("locItemLong", String.valueOf(cursor.getDouble(cursor
					.getColumnIndexOrThrow(DBOpenHelper.LOCATION_LONG))));
			map.put("locItemLati", String.valueOf(cursor.getDouble(cursor
					.getColumnIndexOrThrow(DBOpenHelper.LOCATION_LATI))));
			listItem.add(map);
			
		}
		return listItem;
	}
	private void refreshListView(){
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, getData(),
				R.layout.loc_item, new String[] { "locItemName",
						"locItemLong", "locItemLati" }, new int[] {
						R.id.locItemName, R.id.locItemLong,
						R.id.locItemLati });
		locationList.setAdapter(mSimpleAdapter);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refreshListView();
	}
	
	@Override
	public void onDestroy(){
		cursor.close();
		db.close();
		super.onDestroy();
	}
}

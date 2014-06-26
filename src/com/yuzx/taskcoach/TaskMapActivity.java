/**
 * 
 */
package com.yuzx.taskcoach;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yuzx.taskcoach.db.ConstData;
import com.yuzx.taskcoach.db.DBOpenHelper;
import com.yuzx.taskcoach.location.LocationAddActivity;

/**
 * @author yuzx
 * 
 */
public class TaskMapActivity extends Activity {

	private GoogleMap mMap;
	/*
	 * private double mCurLat; private double mCurLng;
	 */

	private DBOpenHelper db;
	private ArrayList<Marker> markerList;
	final String ACTION_AUTOTASK = "com.yuzx.taskcoach.action.ACTION_AUTOTASK";
	final static int MAP_MENU_ITEM1 = 1001;
	final static int MAP_MENU_ITEM2 = 1002;
	final static int MAP_MENU_ITEM3 = 1003;

	private LocationManager lm;
	private LatLng curlatlng;
	private double mLat;
	private double mLng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_map);
		db = new DBOpenHelper(this);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLat = 31.285365;
		mLng = 121.500178;
		curlatlng = new LatLng(31.285365, 121.500178);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = lm.getBestProvider(criteria, true);
		Toast.makeText(this, provider, Toast.LENGTH_LONG).show();
		lm.requestLocationUpdates(provider, 60000, 0, locationListener);

		markerList = new ArrayList<Marker>();
		setUpMap();
	}

	private void setUpMap() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			if (mMap != null) {
				mMap.moveCamera(CameraUpdateFactory
						.newLatLngZoom(curlatlng, 17));
				//mMap.setMyLocationEnabled(true);
				//mMap.setIndoorEnabled(true);
				setUpMarkers();
				mMap.setOnMapLongClickListener(new MapLongClickListener());
				mMap.setOnMarkerClickListener(new MarkerClickListener());
				//mMap.setOnMyLocationChangeListener(new MyLocationChangeListener());

			}
		}
	}

	private class MapLongClickListener implements OnMapLongClickListener {

		@Override
		public void onMapLongClick(LatLng arg0) {
			// TODO Auto-generated method stub
			final double lat = arg0.latitude;
			final double lng = arg0.longitude;
			final LatLng latlng = arg0;

			AlertDialog.Builder builder = new AlertDialog.Builder(
					TaskMapActivity.this);
			builder.setItems(R.array.map_array,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case 0:
								Intent intent = new Intent(
										TaskMapActivity.this,
										LocationAddActivity.class);
								intent.putExtra("CALLED", "TaskMapActivity");
								intent.putExtra("LONG", lng);
								intent.putExtra("LATI", lat);
								startActivity(intent);
								break;
							case 1:

								// Toast.makeText(MenuActivity.this,
								// " Location Alert",
								// Toast.LENGTH_SHORT).show();

								mMap.addMarker(new MarkerOptions()
										.position(latlng).title("Auto Task")
										.snippet("自动静音").draggable(false));
								// .icon(BitmapDescriptorFactory.fromResource(R.drawable.volume_muted)));

								try {
									db.insertAutoTask(0, lng, lat);

								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}

								Intent iAutoTask = new Intent();
								iAutoTask.setAction(ACTION_AUTOTASK);
								iAutoTask.putExtra("AUTOTASK_TYPE", 0);
								int requestCode = 0;
								try {
									Cursor cursor = db.getAutoTask(lng, lat);
									if (cursor != null && cursor.moveToFirst()) {
										requestCode = cursor.getInt(cursor
												.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_ID));
										cursor.close();
									}
									requestCode = db.getAutoTaskID(lng, lat);
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
								Log.d("add marker", String.valueOf(requestCode));
								PendingIntent pIntent = PendingIntent
										.getBroadcast(TaskMapActivity.this,
												requestCode, iAutoTask, 0);
						
								lm.addProximityAlert(lat, lng, 200, 600000,
										pIntent);

								break;
							}
						}
					});
			builder.show();
		}

	}

	private class MarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			final Marker marker = arg0;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					TaskMapActivity.this);
			builder.setMessage("Choose Action")
					.setPositiveButton("Show Detail", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							marker.showInfoWindow();
						}
					}).setNegativeButton("Delete", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							LatLng latlng = marker.getPosition();
							int requestCode = 0;
							try {
								Cursor cursor = db.getAutoTask(
										latlng.longitude, latlng.latitude);
								if (cursor != null && cursor.moveToFirst()) {
									requestCode = cursor.getInt(cursor
											.getColumnIndex(DBOpenHelper.AUTOTASK_ID));
									cursor.close();
								}

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							try {
								// requestCode =
								// db.getAutoTaskID(latlng.longitude,
								// latlng.latitude);

								db.deleteAutoTask(latlng.longitude,
										latlng.latitude);

							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							Log.d("Long", String.valueOf(latlng.longitude));
							Log.d("LAT", String.valueOf(latlng.latitude));
							Log.d("requestcode", String.valueOf(requestCode));

							marker.remove();

							Intent iAutoTask = new Intent();
							iAutoTask.setAction(ACTION_AUTOTASK);
							iAutoTask.putExtra("AUTOTASK_TYPE", 0);
							PendingIntent pIntent = PendingIntent.getBroadcast(
									TaskMapActivity.this, requestCode,
									iAutoTask, 0);
							lm.removeProximityAlert(pIntent);
							// lm.addProximityAlert(latlng.latitude,
							// latlng.longitude, 50, 600000, pIntent);
						}
					}).show();

			return true;
		}

	}

	private void setUpMarkers() {

		Cursor cursor;
		cursor = db.selectAutoTask();
		while (cursor.moveToNext()) {
			double lng = cursor.getDouble(cursor
					.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_LONG));
			double lat = cursor.getDouble(cursor
					.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_LATI));
			int type = cursor.getInt(cursor
					.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_TYPE));
			mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
					.title("Auto Task").snippet(ConstData.autotasks[type])
					.draggable(false));
			Log.d("Long", String.valueOf(lng));
			Log.d("LAT", String.valueOf(lat));
			Log.d("ID", cursor.getString(cursor
					.getColumnIndexOrThrow(DBOpenHelper.AUTOTASK_ID)));
		}
		cursor.close();

	}

	private class MyLocationChangeListener implements OnMyLocationChangeListener{

		@Override
		public void onMyLocationChange(Location arg0) {
			// TODO Auto-generated method stub
			
			mLat = arg0.getLatitude();
			mLng = arg0.getLongitude();
			Log.d("mLat ..", String.valueOf(mLat));
			Log.d("mLng ..", String.valueOf(mLng));
			Toast.makeText(TaskMapActivity.this, mLat + "," + mLng,
					Toast.LENGTH_SHORT).show();
		}
		
	}
	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			mLat = location.getLatitude();
			mLng = location.getLongitude();
			Log.d("mLat", String.valueOf(mLat));
			Log.d("mLng", String.valueOf(mLng));
			Toast.makeText(TaskMapActivity.this, mLat + "," + mLng,
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		// db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MAP_MENU_ITEM1, 1, "定位到当前位置");
		menu.add(0, MAP_MENU_ITEM2, 2, "跳转到");
		menu.add(0, MAP_MENU_ITEM3, 3, "保留");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MAP_MENU_ITEM1:
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					mLat, mLng), 17));
			break;
		case MAP_MENU_ITEM2:
			break;
		case MAP_MENU_ITEM3:
			break;
		}
		return true; 
	}
}

/**
 * 
 */
package com.yuzx.taskcoach.location;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yuzx.taskcoach.R;
import com.yuzx.taskcoach.db.DBOpenHelper;

/**
 * @author yuzx
 *
 */
public class LocationAddActivity extends Activity {

	private ImageButton btnBack;
	private ImageButton btnLoc;
	private ImageButton btnAdd;
	
	private TextView tvLocName;
	private TextView tvLocLong;
	private TextView tvLocLati;
	
	private DBOpenHelper db;
	
	private double lng;
	private double lat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loc_add);
		
		db = new DBOpenHelper(this);
		
		Intent intent = getIntent();
		lng = 0.0;
		lat = 0.0;
		if(intent!=null){
			lng = intent.getDoubleExtra("LONG", 0.0);
			lat = intent.getDoubleExtra("LATI", 0.0);
		}
		btnBack = (ImageButton) findViewById(R.id.btnBackLocAdd);
		btnLoc = (ImageButton) findViewById(R.id.btnLocate);
		btnAdd = (ImageButton) findViewById(R.id.btnAddLoc);
		tvLocName = (TextView) findViewById(R.id.addLocName);
		tvLocLong = (TextView) findViewById(R.id.addLocLong);
		tvLocLati = (TextView) findViewById(R.id.addLocLati);
		
		btnBack.setOnClickListener(new ClickListener());
		btnLoc.setOnClickListener(new ClickListener());
		btnAdd.setOnClickListener(new ClickListener());
		
		tvLocLati.setText(String.valueOf(lat));
		tvLocLong.setText(String.valueOf(lng));
	}
	
	private class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btnLocate:
				LocationManager locationManager;
				String serviceName = Context.LOCATION_SERVICE;
				locationManager = (LocationManager) getSystemService(serviceName);
				String provider = LocationManager.GPS_PROVIDER;
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setCostAllowed(true);
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				provider = locationManager.getBestProvider(criteria, true);
				locationManager.requestLocationUpdates(provider, 60000, 10,locationListener);
			//	Location location = locationManager.getLastKnownLocation(provider);
			/*	if(location != null){
					tvLocLong.setText(String.valueOf(location.getLongitude()));
					tvLocLati.setText(String.valueOf(location.getLatitude()));
				}*/				
				break;
			case R.id.btnAddLoc:
				String name = tvLocName.getText().toString();
				if(name.equals("")){
					Toast.makeText(LocationAddActivity.this, "请输入名字", 
							Toast.LENGTH_SHORT).show();					
				}else{
					double lati = Double.parseDouble(tvLocLati.getText().toString());
					double longi = Double.parseDouble(tvLocLong.getText().toString());
					db.insertLocation(name, longi, lati);
					Toast.makeText(LocationAddActivity.this, "添加成功", 
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btnBackLocAdd:
				finish();
				break;
			default:
				break;
			}
		}
		
	}
	
	public void startLocation(){
		new Thread() {
			public void run(){
				try{
					LocationManager locationManager;
					String serviceName = Context.LOCATION_SERVICE;
					locationManager = (LocationManager) getSystemService(serviceName);
					String provider = LocationManager.GPS_PROVIDER;
					Criteria criteria = new Criteria();
					criteria.setAccuracy(Criteria.ACCURACY_FINE);
					criteria.setAltitudeRequired(false);
					criteria.setBearingRequired(false);
					criteria.setCostAllowed(true);
					criteria.setPowerRequirement(Criteria.POWER_LOW);
					provider = locationManager.getBestProvider(criteria, true);
					locationManager.requestLocationUpdates(provider, 2000, 10,locationListener);
					//Location location = locationManager.getLastKnownLocation(provider);
					
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			tvLocLong.setText(String.valueOf(location.getLongitude()));
			tvLocLati.setText(String.valueOf(location.getLatitude()));
			tvLocName.setText(getAddrByGeoPoint(location.getLongitude(),
					location.getLatitude()));
		}

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
		
	};
	
	public String getAddrByGeoPoint(double longitude, double latitude){
		String addrname = "";
		Geocoder gcoder = new Geocoder(this, Locale.getDefault());
		try{
			List<Address> list = gcoder.getFromLocation(latitude, longitude, 1);
			StringBuilder sb = new StringBuilder();
			if(list!=null && list.size()>0){
				Address address = list.get(0);
				for(int i = 0;i<address.getMaxAddressLineIndex();i++){
					sb.append(address.getAddressLine(i));
				}
				addrname = sb.toString();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return addrname;
	}
/*	private void updateWithNewLocation(Location location) {
	   String latLongString;
	     TextView myLocationText;
	      myLocationText = (TextView) findViewById(R.id.cityString);
	      if (location != null) {
	          double lat = location.getLatitude();
		            double lng = location.getLongitude();
	            latLongString = "纬度:" + lat + "\n经度:" + lng + "\n";
	           
	            Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
	            try { 
	                // 取得地址相关的一些信息\经度、纬度 
	                List<Address> addresses = gc.getFromLocation(lat, lng, 1); 
	                StringBuilder sb = new StringBuilder(); 
		                if (addresses.size() > 0) { 
		                    Address address = addresses.get(0); 
	                    sb.append(address.getLocality()).append("\n"); 
	                    latLongString = latLongString + sb.toString(); 
	                 } 
	            } catch (IOException e) { 
		            } 
		
	        } else {
		             latLongString = "无法获取地理信息";
		        }
		        myLocationText.setText("您当前的位置是:\n" + latLongString);
	     }  */  
}

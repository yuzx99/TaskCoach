/**
 * 
 */
package com.yuzx.taskcoach.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yuzx.taskcoach.R;

/**
 * @author yuzx
 * 
 */
public class WeatherActivity extends Activity {

	private String url;
	private TextView tvCity;
	private String strCity;
	private ImageButton btnRefresh;
	private ImageButton btnBack;
	private static final int CITY = 0x11;
	private Spinner spProvince;
	private Spinner spCity;
	private List<String> provincesList;
	private List<String> citysList;
	private SharedPreferences preference;
	private static String PREFS_NAME = "com.yuzx.taskcoach.weather";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather);
		
		preference = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		strCity = readSharpPreference();
		
		tvCity = (TextView) this.findViewById(R.id.city);
		tvCity.setText(strCity);
		btnRefresh = (ImageButton)this.findViewById(R.id.wea_refresh);
		btnRefresh.setOnClickListener(new ClickListener());
		tvCity.setOnClickListener(new ClickListener());
		btnBack = (ImageButton) this.findViewById(R.id.btnWeatherBack);
		btnBack.setOnClickListener(new ClickListener());
		
		
		url = "http://m.weather.com.cn/data/101010100.html";
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork() // 这里或者用
																		// .detectAll()
																		// 方法
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		
		//设置透明度
		findViewById(R.id.content_small_bg1).getBackground().setAlpha(100);
		findViewById(R.id.content_small_bg2).getBackground().setAlpha(100);
		findViewById(R.id.content_small_bg3).getBackground().setAlpha(100);
		refresh(strCity);
	}

	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.wea_refresh:
				/*try {
					String content = getContent(url);
					JSONObject jsonObj = new JSONObject(content);
					JSONObject weatherObj = jsonObj.getJSONObject("weatherinfo");
					tvCity.setText(weatherObj.getString("city"));			
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ToastText("Fail to get weather info");
				}*/
				refresh(strCity);
				break;
			case R.id.city:
				chooseCity(CITY);
				break;
			case R.id.btnWeatherBack:
				WeatherActivity.this.finish();
				break;
			default:
				break;
			}
		}
	
	}
	
	public void chooseCity(int id)
	{

		switch (id)
		{
		case CITY:

			// 取得city_layout.xml中的视图
			final View view = LayoutInflater.from(this).inflate(
					R.layout.city_layout, null);

			// 省份Spinner
			spProvince = (Spinner) view
					.findViewById(R.id.province_spinner);
			// 城市Spinner
			spCity = (Spinner) view.findViewById(R.id.city_spinner);

			// 省份列表
			provincesList = WeatherUtil.getProvinceList();

			ArrayAdapter adapter = new ArrayAdapter(this,
					android.R.layout.simple_spinner_item, provincesList);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spProvince.setAdapter(adapter);
			// 省份Spinner监听器
			spProvince
					.setOnItemSelectedListener(new OnItemSelectedListener()
					{

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long arg3)
						{

							citysList = WeatherUtil
									.getCityListByProvince(provincesList
											.get(position));
							ArrayAdapter adapter1 = new ArrayAdapter(
									WeatherActivity.this,
									android.R.layout.simple_spinner_item, citysList);
							adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spCity.setAdapter(adapter1);

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0)
						{

						}
					});

			// 城市Spinner监听器
			spCity.setOnItemSelectedListener(new OnItemSelectedListener()
			{

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3)
				{
					strCity = citysList.get(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{

				}
			});

			// 选择城市对话框
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("请选择所属城市");
			dialog.setView(view);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							tvCity.setText(strCity);
							writeSharpPreference(strCity);
							refresh(strCity);

						}
					});
			dialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();

						}
					});

			dialog.show();

			break;

		default:
			break;
		}

	}
	
	private void refresh(String city){

		SoapObject detail = WeatherUtil.getWeatherByCity(city);
		try{		
			// 当天天气
			// <string>4月29日 小雨</string>
			String weather = detail.getProperty(7).toString();
			TextView tvDate = (TextView) findViewById(R.id.weaDate);
			tvDate.setText(weather.split(" ")[0]);
			
			String tips = detail.getProperty(6).toString();
			TextView tvTips = (TextView) findViewById(R.id.weaTips);
			tvTips.setText(tips.split("\n")[0]);
						
			TextView cityText = (TextView) findViewById(R.id.cityText);
			cityText.setText(detail.getProperty(1).toString());
			
			// <string>7.gif</string>
			ImageView weaToday = (ImageView) findViewById(R.id.weaImageView);
			weaToday.setImageResource(parseIcon(detail.getProperty(10).toString()));
			
			TextView todayWeather = (TextView) findViewById(R.id.today_weather);
			todayWeather.setText(weather.split(" ")[1]);
			
			TextView temperature = (TextView) findViewById(R.id.temperature);
			temperature.setText(detail.getProperty(8).toString());
			
			//<string>今日天气实况：气温：21℃；风向/风力：东风 2级；湿度：69%</string>
			String weatherLive = detail.getProperty(4).toString();
			TextView humidity = (TextView) findViewById(R.id.humidity);
			humidity.setText(weatherLive.split("：")[4]);			
			
			//<string>东北风4-5级</string>
			TextView windForce = (TextView) findViewById(R.id.wind_force);
			windForce.setText(detail.getProperty(9).toString());
			
			//<string>空气质量：良；紫外线强度：弱</string>
			String air = detail.getProperty(5).toString();
			String airqty = air.split("；")[0];
			TextView airQuality = (TextView) findViewById(R.id.air_quality);
			airQuality.setText(airqty.split("：")[1]);
			
			String rays = air.split("；")[1];
			TextView ultRays = (TextView) findViewById(R.id.ult_rays);
			ultRays.setText(rays.split("：")[1]);
			
		   /* 明天天气
			 <string>4月30日 大雨转阴</string>
			 <string>11℃/17℃</string>
			 <string>东北风4-5级</string>
			 <string>9.gif</string>
			*/			
			String weather2 = detail.getProperty(12).toString();
			TextView data2 = (TextView) findViewById(R.id.wea_date2);
			data2.setText(weather2.split(" ")[0]);
			
			TextView tomorrowWeather = (TextView) findViewById(R.id.wea_weather2);
			tomorrowWeather.setText(weather2.split(" ")[1]);
			
			TextView temp2 = (TextView) findViewById(R.id.wea_temp2);
			temp2.setText(detail.getProperty(13).toString());
			
			Log.d("weather",detail.getProperty(13).toString());
			
			ImageView weaImage2 = (ImageView) findViewById(R.id.wea_image2);
			weaImage2.setImageResource(parseIcon(detail.getProperty(15).toString()));
			
		    // 后天天气
			String weather3 = detail.getProperty(17).toString();
			TextView data3 = (TextView) findViewById(R.id.wea_date3);
			data3.setText(weather3.split(" ")[0]);
			
			TextView weaWeather3 = (TextView) findViewById(R.id.wea_weather3);
			weaWeather3.setText(weather3.split(" ")[1]);
			
			TextView temp3 = (TextView) findViewById(R.id.wea_temp3);
			temp3.setText(detail.getProperty(18).toString());
			
			ImageView weaImage3 = (ImageView) findViewById(R.id.wea_image3);
			weaImage3.setImageResource(parseIcon(detail.getProperty(20).toString()));
			
			//大后天天气
			String weather4 = detail.getProperty(22).toString();
			TextView data4 = (TextView) findViewById(R.id.wea_date4);
			data4.setText(weather4.split(" ")[0]);
			
			TextView weaWeather4 = (TextView) findViewById(R.id.wea_weather4);
			weaWeather4.setText(weather4.split(" ")[1]);
			
			TextView temp4 = (TextView) findViewById(R.id.wea_temp4);
			temp4.setText(detail.getProperty(23).toString());
			
			ImageView weaImage4 = (ImageView) findViewById(R.id.wea_image4);
			weaImage4.setImageResource(parseIcon(detail.getProperty(25).toString()));
		}catch (Exception e) {
			// TODO: handle exception
		}
		ToastText("已更新到最新");
	}
	
	private int parseIcon(String strIcon)
	{
		if (strIcon == null)
			return -1;
		if ("0.gif".equals(strIcon))
			return R.drawable.a_0;
		if ("1.gif".equals(strIcon))
			return R.drawable.a_1;
		if ("2.gif".equals(strIcon))
			return R.drawable.a_2;
		if ("3.gif".equals(strIcon))
			return R.drawable.a_3;
		if ("4.gif".equals(strIcon))
			return R.drawable.a_4;
		if ("5.gif".equals(strIcon))
			return R.drawable.a_5;
		if ("6.gif".equals(strIcon))
			return R.drawable.a_6;
		if ("7.gif".equals(strIcon))
			return R.drawable.a_7;
		if ("8.gif".equals(strIcon))
			return R.drawable.a_8;
		if ("9.gif".equals(strIcon))
			return R.drawable.a_9;
		if ("10.gif".equals(strIcon))
			return R.drawable.a_10;
		if ("11.gif".equals(strIcon))
			return R.drawable.a_11;
		if ("12.gif".equals(strIcon))
			return R.drawable.a_12;
		if ("13.gif".equals(strIcon))
			return R.drawable.a_13;
		if ("14.gif".equals(strIcon))
			return R.drawable.a_14;
		if ("15.gif".equals(strIcon))
			return R.drawable.a_15;
		if ("16.gif".equals(strIcon))
			return R.drawable.a_16;
		if ("17.gif".equals(strIcon))
			return R.drawable.a_17;
		if ("18.gif".equals(strIcon))
			return R.drawable.a_18;
		if ("19.gif".equals(strIcon))
			return R.drawable.a_19;
		if ("20.gif".equals(strIcon))
			return R.drawable.a_20;
		if ("21.gif".equals(strIcon))
			return R.drawable.a_21;
		if ("22.gif".equals(strIcon))
			return R.drawable.a_22;
		if ("23.gif".equals(strIcon))
			return R.drawable.a_23;
		if ("24.gif".equals(strIcon))
			return R.drawable.a_24;
		if ("25.gif".equals(strIcon))
			return R.drawable.a_25;
		if ("26.gif".equals(strIcon))
			return R.drawable.a_26;
		if ("27.gif".equals(strIcon))
			return R.drawable.a_27;
		if ("28.gif".equals(strIcon))
			return R.drawable.a_28;
		if ("29.gif".equals(strIcon))
			return R.drawable.a_29;
		if ("30.gif".equals(strIcon))
			return R.drawable.a_30;
		if ("31.gif".equals(strIcon))
			return R.drawable.a_31;
		return 0;
	}
	/**
	 * 获取网址内容
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();

		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		// HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
		}
		return sb.toString();
	}
	
	public void ToastText(String toast){
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}
	
	public void writeSharpPreference(String string){
		
		SharedPreferences.Editor editor = preference.edit();
		editor.putString("city", string);
		editor.commit();
	
	}
	
	public String readSharpPreference(){
		
		String city = preference.getString("city", "上海");
		
		return city;
		
	}

}

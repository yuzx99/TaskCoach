/**
 * 
 */
package com.yuzx.taskcoach;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.yuzx.taskcoach.calendar.CalView;
import com.yuzx.taskcoach.view.BorderText;

/**
 * @author yuzx
 *
 */
public class CalendarFragment extends Fragment {
	private Activity mActivity;
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CalView calV = null;
	private GridView gridView = null;
	private BorderText topText = null;
	private Drawable draw = null;
	private static int jumpMonth = 0;      //ÿ�λ��������ӻ��ȥһ����,Ĭ��Ϊ0������ʾ��ǰ�£�
	private static int jumpYear = 0;       //������Խһ�꣬�����ӻ��߼�ȥһ��,Ĭ��Ϊ0(����ǰ��)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		
		setHasOptionsMenu(true);
		Date date = new Date();		
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    	currentDate = sdf.format(date);  //��������
    	year_c = Integer.parseInt(currentDate.split("-")[0]);
    	month_c = Integer.parseInt(currentDate.split("-")[1]);
    	day_c = Integer.parseInt(currentDate.split("-")[2]);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.calendar_layout, container, false);
		ImageButton btnMenu = (ImageButton)view.findViewById(R.id.btnMenu);
//		mCalView = (CalendarView)view.findViewById(R.id.calView);
		flipper = (ViewFlipper) view.findViewById(R.id.flipperCal);
		topText = (BorderText) view.findViewById(R.id.toptextCal);
        flipper.removeAllViews();
        calV = new CalView(mActivity, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        
        addGridView();
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        flipper.addView(gridView,0);
        
		addTextToTopTextView(topText);
		
		btnMenu.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MenuActivity.prepare(mActivity, R.id.calContent);
				Intent intent = new Intent(mActivity, MenuActivity.class);
				startActivity(intent);
				mActivity.overridePendingTransition(0,0);
			}
			
		});
		
		gestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener(){
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						int gvFlag = 0;         //ÿ�����gridview��viewflipper��ʱ���ı��
						if (e1.getX() - e2.getX() > 100) {
				            //���󻬶�
							addGridView();   //���һ��gridView
							jumpMonth++;     //��һ����
							
							calV = new CalView(mActivity, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
					        gridView.setAdapter(calV);
					        //flipper.addView(gridView);
					        addTextToTopTextView(topText);
					        gvFlag++;
					        flipper.addView(gridView, gvFlag);
							flipper.setInAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_left_in));
							flipper.setOutAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_left_out));
							flipper.showNext();
							flipper.removeViewAt(0);
							return true;
						} else if (e1.getX() - e2.getX() < -100) {
				            //���һ���
							addGridView();   //���һ��gridView
							jumpMonth--;     //��һ����
							
							calV = new CalView(mActivity,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
					        gridView.setAdapter(calV);
					        gvFlag++;
					        addTextToTopTextView(topText);
					        //flipper.addView(gridView);
					        flipper.addView(gridView,gvFlag);
					        
							flipper.setInAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_right_in));
							flipper.setOutAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_right_out));
							flipper.showPrevious();
							flipper.removeViewAt(0);
							return true;
						}
						return false;
					}
				});
		
/*		mCalView.setOnDateChangeListener(new OnDateChangeListener(){

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				// TODO Auto-generated method stub
				String date = year + "/" + month + "/" + dayOfMonth;
				//Toast.makeText(mActivity, date, 0).show();
			}
			
		});
		
		mCalView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mActivity, "test ...", 0).show();
			}
			
		});*/
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {

		menu.add(0, 1, 1, "����")
		.setIcon(android.R.drawable.ic_menu_day);
		menu.add(0, 2, 2, "��ת");
		menu.add(0, 3, 3, "�ճ�");
		menu.add(0, 4, 4, "����ת��");
		super.onCreateOptionsMenu(menu,inflater);
	}
	
	//���ͷ������� �����µ���Ϣ
	public void addTextToTopTextView(TextView view){
		StringBuffer textDate = new StringBuffer();
		draw = getResources().getDrawable(R.drawable.top_day);
		view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("��").append(
				calV.getShowMonth()).append("��").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("��").append(calV.getLeapMonth()).append("��")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("��").append("(").append(
				calV.getCyclical()).append("��)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	//���gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		//ȡ����Ļ�Ŀ�Ⱥ͸߶�
		WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth(); 
        int Height = display.getHeight();
        
		gridView = new GridView(mActivity);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(49);
		
		Log.d("Width", String.valueOf(Width));
		Log.d("Height",String.valueOf(Height));
		
		if(Width == 480 && Height == 800){
			gridView.setColumnWidth(49);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // ȥ��gridView�߿�
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
            //��gridview�еĴ����¼��ش���gestureDetector
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector
						.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position && position <= endPosition){
					String date = null;
					date = calV.getShowYear()+"/"
						+ calV.getShowMonth()+"/"
						+calV.getDateByClickItem(position);
						
					Intent intent = new Intent(mActivity,OneDayActivity.class);
					intent.putExtra("DATE", date);
					startActivity(intent);
				}
			}
			
		});
		gridView.setLayoutParams(params);
//		gridView.setOnItemClickListener(new OnItemClickListener() {
//            //gridView�е�ÿһ��item�ĵ���¼�
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				  //����κ�һ��item���õ����item������(�ų�����������յ�����(�������Ӧ))
//				  int startPosition = calV.getStartPositon();
//				  int endPosition = calV.getEndPosition();
//				  if(startPosition <= position  && position <= endPosition){
//					  String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //��һ�������
//					  //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //��һ�������
//	                  String scheduleYear = calV.getShowYear();
//	                  String scheduleMonth = calV.getShowMonth();
//	                  String week = "";
//	                  
	                  //ͨ�����ڲ�ѯ��һ���Ƿ񱻱�ǣ����������ճ̾Ͳ�ѯ������������ճ���Ϣ
//	                  String[] scheduleIDs = dao.getScheduleByTagDate(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
//	                  if(scheduleIDs != null && scheduleIDs.length > 0){
//	                	  //��ת����ʾ��һ��������ճ���Ϣ����
//		  				  Intent intent = new Intent();
//		  				  intent.setClass(CalendarActivity.this, ScheduleInfoView.class);
//		                  intent.putExtra("scheduleID", scheduleIDs);
//		  				  startActivity(intent);  
		  				  
//	                  }else{
//	                  //ֱ����ת����Ҫ����ճ̵Ľ���
//	                	  
//		                  //�õ���һ�������ڼ�
//		                  switch(position%7){
//		                  case 0:
//		                	  week = "������";
//		                	  break;
//		                  case 1:
//		                	  week = "����һ";
//		                	  break;
//		                  case 2:
//		                	  week = "���ڶ�";
//		                	  break;
//		                  case 3:
//		                	  week = "������";
//		                	  break;
//		                  case 4:
//		                	  week = "������";
//		                	  break;
//		                  case 5:
//		                	  week = "������";
//		                	  break;
//		                  case 6:
//		                	  week = "������";
//		                	  break;
//		                  }
//						 
//		                  ArrayList<String> scheduleDate = new ArrayList<String>();
//		                  scheduleDate.add(scheduleYear);
//		                  scheduleDate.add(scheduleMonth);
//		                  scheduleDate.add(scheduleDay);
//		                  scheduleDate.add(week);
//		                  //scheduleDate.add(scheduleLunarDay);
		                  
		                  
//		                  Intent intent = new Intent();
//		                  intent.putStringArrayListExtra("scheduleDate", scheduleDate);
//		                  intent.setClass(CalendarActivity.this, ScheduleView.class);
//		                  startActivity(intent);
//	                  }
//				  }
//			}
//		});
//		gridView.setLayoutParams(params);
//
	}
	

	
}

/**
 * 
 */
package com.yuzx.taskcoach;

/**
 * @author yuzx
 * 
 */
public class Schedule {
	private int scheduleID; // serial number : primary key
	private String content;// 日程备注
	private String startDate;// 日程日期
	private String startTime;// 日程时间
	private String endDate;// 闹钟日期
	private String endTime;// 闹钟时间
	private int typeID;    // 日程类型
	private int remindID;  // 提醒类型
	private String location;

	public Schedule() {

	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getTypeID() {
		return typeID;
	}

	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}

	public int getRemindID() {
		return remindID;
	}

	public void setRemindID(int remindID) {
		this.remindID = remindID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	// 此构造器为从数据库读取日程对象时用
	public Schedule(int sn, String date1, String time1, String date2,
			String time2, String title, String note, String type,
			String timeSet, String alarmSet) {
	}



	public static String toDateString(int y, int m, int d)// 静态方法，把int型的年月日转换成YYYY/MM/DD
	{
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append(m < 10 ? "0" + m : "" + m);
		sb.append("/");
		sb.append(d < 10 ? "0" + d : "" + d);
		return sb.toString();
	}

	public String toTimeString(int h, int m)// 把int型的时分转换成HH:MM
	{
		StringBuffer sb = new StringBuffer();
		sb.append(h < 10 ? "0" + h : "" + h);
		sb.append(":");
		sb.append(m < 10 ? "0" + m : "" + m);
		return sb.toString();
	}
}

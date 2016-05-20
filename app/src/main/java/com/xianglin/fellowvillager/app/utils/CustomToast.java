package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomToast {

	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	public static void showToast(Context mContext, String text, int duration) {

		mHandler.removeCallbacks(r);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		mHandler.postDelayed(r, duration);

		mToast.show();
	}

	public static void showToast(Context mContext, int resId, int duration) {
		String text = mContext.getResources().getString(resId);
		mHandler.removeCallbacks(r);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		mHandler.postDelayed(r, duration);

		mToast.show();
	}



	/**
	 * 格式化时间
	 * @param time
	 * @return
	 */
	public static String formatDateTime(String time) {
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(Long.parseLong(time));
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
//		format.format(cal.getTime());

		if(time==null ||"null".equals(time)){
			return "";
		}
		Date date = null;
		try {
			date = format.parse(time);
			Calendar current = Calendar.getInstance();
			Calendar today = Calendar.getInstance();	//今天
			today.set(Calendar.YEAR, current.get(Calendar.YEAR));
			today.set(Calendar.MONTH, current.get(Calendar.MONTH));
			today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
			//  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
			today.set( Calendar.HOUR_OF_DAY, 0);
			today.set( Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			Calendar yesterday = Calendar.getInstance();	//昨天
			yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
			yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
			yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
			yesterday.set( Calendar.HOUR_OF_DAY, 0);
			yesterday.set( Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			current.setTime(date);
			if(current.after(today)){
				return time.split(" ")[1];
			}else if(current.before(today) && current.after(yesterday)){
				return "昨天 ";
			}else{
				int index = time.indexOf("-")+1;
//			return time.substring(index, time.length());
				return time.substring(index, time.length()).split(" ")[0];
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "";

	}



	/**
	 * 格式化时间
	 * @param
	 * @return
	 */
	public static String CountTime(Long cTime) {
		if (null == cTime || "".equals(cTime))
			return "";
		Date currentTime = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			currentTime = cal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long between = (currentTime.getTime() - cTime) / 1000;// 除以1000是为了转换成秒

		long year = between / (24 * 3600 * 30 * 12);
		long month = between / (24 * 3600 * 30);
		long week = between / (24 * 3600 * 7);
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;

		StringBuffer sb = new StringBuffer();
		if (year != 0) {
			sb.append(year + "年");
			return sb.toString() + "前";
			// return cTime;
		}
		if (month != 0) {
			sb.append(month + "个月");
			return sb.toString() + "前";
			// return cTime;
		}
		if (week != 0) {
			sb.append(week + "周");
			return sb.toString() + "前";
			// return cTime;
		}
		if (day != 0) {
			sb.append(day + "天");
			return sb.toString() + "前";
		}
		if (hour != 0) {
			sb.append(hour + "小时");
			return sb.toString() + "前";
		}
		if (minute != 0) {
			sb.append(minute + "分钟");
			return sb.toString() + "前";
		}
		if (second != 0) {
			sb.append(second + "秒");
			return sb.toString() + "前";
		}

		return "1分钟内";
	}

	}

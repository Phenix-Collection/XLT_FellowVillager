package com.xianglin.fellowvillager.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 
 * @author andy
 * @version $Id: Formats.java, v 0.1 2012-12-21 ����11:16:51 andy Exp $
 */
@SuppressLint("SimpleDateFormat")
public class Formats {
	
	private static String PREFS_PRIVATE = "BluetoothInfo";

	private static DecimalFormat doubleFormat = new DecimalFormat("#0.##");
	private static DecimalFormat doubleFormat1 = new DecimalFormat("#0.00");
	private static DecimalFormat moneyFormat = new DecimalFormat("###,###,###,##0.##");

	/** format should not be changed if i start a export/import function **/
	private static DateFormat norDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private static DateFormat norDateFormatOld = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat norDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
		Locale.CHINA);
	private static DateFormat norDatetimeFormatOld = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat norDatetimeFormat2 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
	private static DateFormat norDatetimeFormatOld2 = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat norDatetimeFormat3 = new SimpleDateFormat("yyyyMMddHHmmss",
		Locale.CHINA);
	private static DateFormat norDatetimeFormatOld3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static DateFormat norDatetimeFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm",
		Locale.CHINA);
	private static DateFormat norDatetimeFormatOld4 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static DateFormat norDatetimeFormat5 = new SimpleDateFormat("HH:mm:ss");
	private static DecimalFormat norDoubleFormat = new DecimalFormat("#0.###");

	public static String double2String(Double d) {
		return double2String(d == null ? 0D : d);
	}

	public static String double2String(double d) {
		return doubleFormat.format(d);
	}
	public static String double2String8(double d) {
		return String.valueOf(d);
	}

	public static String double2String1(double d) {
		return doubleFormat1.format(d);
	}

	public static String money2String(double d) {
		return moneyFormat.format(d);
	}

	public static String num2String(long d) {
		return moneyFormat.format(d);
	}

	public static double string2Double(String d) {
		try {
			return doubleFormat.parse(d).doubleValue();
		} catch (ParseException e) {
			return 0D;
		}
	}
	
	public static double string2Double1(String d) {
		try {
			return doubleFormat1.parse(d).doubleValue();
		} catch (ParseException e) {
			return 0D;
		}
	}

	public static String normalizeDouble2String(Double d) {
		return normalizeDouble2String(d == null ? 0D : d);
	}

	public static String normalizeDouble2String(double d) {
		return norDoubleFormat.format(d);
	}

	public static double normalizeString2Double(String d) throws ParseException {
		return norDoubleFormat.parse(d).doubleValue();

	}

	public static String normalizeDate2String(Date date) {
		return norDateFormat.format(date);
	}

	public static Date normalizeString2Date(String date) throws ParseException {
		try {
			return norDateFormat.parse(date);
		} catch (ParseException x) {
			return norDateFormatOld.parse(date);
		}
	}

	public static String normalizeDatetime2String(Date date) {
		return norDatetimeFormat.format(date);
	}

	public static Date normalizeString2Datetime(String date) throws ParseException {
		try {
			return norDatetimeFormat.parse(date);
		} catch (ParseException x) {
			return norDatetimeFormatOld.parse(date);
		}
	}

	public static String normalizeDatetime2String2(Date date) {
		return norDatetimeFormat2.format(date);
	}

	public static Date normalizeString2Datetime2(String date) throws ParseException {
		try {
			return norDatetimeFormat2.parse(date);
		} catch (ParseException x) {
			return norDatetimeFormatOld2.parse(date);
		}
	}

	public static String normalizeDatetime2String3(Date date) {
		return norDatetimeFormat3.format(date);
	}

	public static Date normalizeString2Datetime3(String date) throws ParseException {
		try {
			return norDatetimeFormat3.parse(date);
		} catch (ParseException x) {
			return norDatetimeFormatOld3.parse(date);
		}
	}

	public static String normalizeDatetime2String4(Date date) {
		return norDatetimeFormat4.format(date);
	}
	
	public static Date normalizeString4Datetime2(String date) throws ParseException{
		try {
			return norDatetimeFormat4.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return norDatetimeFormatOld4.parse(date);
		}
	}

	public static String normalizeDatetime2String5(Date date) {
		return norDatetimeFormat5.format(date);
	}
	
	public static void main(String[] args){
		System.out.println(double2String(5.38239));
		System.out.println(double2String(5));
		System.out.println(double2String(1437));
	}
	
	public static int dip2px(Context context, float dipValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static String formatCashValue(String cashValue){
		String value = "";
		if(cashValue != null){
			if(cashValue.contains(".") && cashValue.split(".").length == 2){
				int afterDotLenth = cashValue.substring(cashValue.indexOf(".")).length();
				if(afterDotLenth == 1){
					value = cashValue+"0"; 
				}else if(afterDotLenth == 2){
					value = cashValue;
				}
			}else if(!cashValue.contains(".")){
				value = cashValue+".00";
			}
		}
		return value;
	}
	
    /**   
     * 获取两个日期之间的间隔分   
     * @return   
     */    
    public static long getGapCount(Date startDate, Date endDate) {    
       Calendar fromCalendar = Calendar.getInstance();      
       fromCalendar.setTime(startDate);      
     
       Calendar toCalendar = Calendar.getInstance();
       toCalendar.setTime(endDate); 
       
       long toMi = toCalendar.getTime().getTime();
       long fromMi = fromCalendar.getTime().getTime();
       long sub = toMi - fromMi;
       if(sub>=0){
    	   return (long) (sub / (1000 * 60));
       }else{
    	   return (long) ((-sub) / (1000 * 60));
       }
//       return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60));    
    }
    
    /**   
     * 获取两个日期之间的间隔分   
     * @return   
     */    
    public static long getDateSub(Date startDate, Date endDate) {  
       Calendar fromCalendar = Calendar.getInstance();      
       fromCalendar.setTime(startDate);      
     
       Calendar toCalendar = Calendar.getInstance();
       toCalendar.setTime(endDate); 
       
       long toMi = toCalendar.getTime().getTime();
       long fromMi = fromCalendar.getTime().getTime();
       long sub = toMi - fromMi;
       return sub;
    }
    
    public static Date setSystemDate(Date date, long index){
    	long minute = index*(1000 * 60);
    	Calendar fromCalendar = Calendar.getInstance();      
        fromCalendar.setTime(date);      
        
        long time = fromCalendar.getTime().getTime()+minute;
        
        fromCalendar.setTimeInMillis(time);
        return fromCalendar.getTime();
    }
}

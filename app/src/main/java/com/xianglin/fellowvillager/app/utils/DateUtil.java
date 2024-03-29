package com.xianglin.fellowvillager.app.utils;

import android.annotation.SuppressLint;

import com.xianglin.mobile.common.logging.LogCatLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @description 日期比较天 月 年 分钟
 * @author 
 */
public class DateUtil {

	private static final String TAG = DateUtil.class.getSimpleName();
	public DateUtil(){
		super();
	}
	
	/***
	 * 
	* @Title: getDateLong
	* @Description: TODO date 转long
	* @param @param date
	* @param @return    设定文件
	* @return long    返回类型
	* @throws
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getDateLong(String date){
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 long timeStart = 0;
		 try {
			timeStart=sdf.parse(date).getTime();
			return timeStart;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return timeStart;
	}
	
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String getDatePmorAm(String date){
		try{
			
			
			// 发布时间
			Date date1=Formats.normalizeString2Datetime(date);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd a");
			
			return dateFormat.format(date1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormatDate(String date){
		try{
			
			
			// 发布时间
			Date date1=Formats.normalizeString2Datetime(date);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月");
			
			return dateFormat.format(date1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取格式化日期
	 * @param date
	 * @return
	 */
	public static String  getDateFormat(String date){
		
		try{
			

			try{
				
				//本地时间
				Date nowTime=new Date();
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(nowTime);
				int nowYear=calendar.get(Calendar.YEAR);//年
				int nowMonth=calendar.get(Calendar.MONTH)+1;//月
				int nowDate = calendar.get(Calendar.DAY_OF_MONTH);//日
				int nowHour = calendar.get(Calendar.HOUR_OF_DAY);//小时
				int nowMinute = calendar.get(Calendar.MINUTE);// 分钟
				
				
				// 发布时间
				Date date1=Formats.normalizeString4Datetime2(date);
				Calendar calendar1 = GregorianCalendar.getInstance();
				calendar1.setTime(date1); 
				int publishYear=calendar1.get(Calendar.YEAR);// 年
				int publishMonth=calendar1.get(Calendar.MONTH) + 1;//月
				int publishDate=calendar1.get(Calendar.DAY_OF_MONTH);//日
				int publishHour = calendar1.get(Calendar.HOUR_OF_DAY);//小时
				int publishMinute =  calendar1.get(Calendar.MINUTE);//分钟
				
				if(publishYear<nowYear){//本地年 大于 发布年
					
					
					return publishYear+"年"+numberFormat(publishMonth)+"月"+numberFormat(publishDate)+"日";
					
				}else if(publishYear==nowYear){// 本地年 ＝＝ 发布年
					
					if(nowMonth>publishMonth){// 本地月大于发布月
						  return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
					}else if (nowMonth == publishMonth){// 本地月 大于 发布月
						
							if(nowDate > publishDate){// 本地日 大于 发布日
								if(nowDate-publishDate==1){
									return "昨天 "+numberFormat(publishHour)+":"+numberFormat(publishMinute);
								}else if(nowDate-publishDate==2){
									return "前天 "+numberFormat(publishHour)+":"+numberFormat(publishMinute);
								}else{
									return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
								}
							}else if(nowDate == publishDate){// 本地日 ＝＝ 发布日
								
								if(nowHour-publishHour == 1){// 本地小时 - 发布小时 =1
									return  "今天 "+numberFormat(publishHour)+":"+numberFormat(publishMinute);
//									return "1小时前";
								}else if(nowHour-publishHour ==0){// 本地小时－发布小时 ＝＝0
									if(nowMinute-publishMinute<=1){
										return "刚刚";
									}else{
										return  "今天 "+numberFormat(publishHour)+":"+numberFormat(publishMinute);
//										return nowMinute-publishMinute+"分钟前";
									}
									
								}else{
									return  "今天 "+numberFormat(publishHour)+":"+numberFormat(publishMinute);
								}
							}
						
					}else if(nowMonth< publishMonth){// 本地月 小于 发布月
						  return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
					}
					
				}else if(publishYear>nowYear){//本地年小于发布年
					return publishYear+"年"+numberFormat(publishMonth)+"月"+numberFormat(publishDate)+"日";
					
				}
				
					return date;
					
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return date;
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return date;
	}

	public static boolean isCloseEnough(String date, String prevDate) {
		long time = Utils.parseLong(date) - Utils.parseLong(prevDate);
		if(time < 0L) {
			time = -time;
		}

		return time < 30000L;
	}


	/**
	 * 获取格式化日期
	 * @param date
	 * @return
	 */
	public static String  getDateFormat1(String date){
		
		try{
			
			//本地时间
			Date nowTime=new Date();
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(nowTime);
			int nowYear=calendar.get(Calendar.YEAR);//年
			int nowMonth=calendar.get(Calendar.MONTH)+1;//月
			int nowDate = calendar.get(Calendar.DAY_OF_MONTH);//日
			int nowHour = calendar.get(Calendar.HOUR_OF_DAY);//小时
			int nowMinute = calendar.get(Calendar.MINUTE);// 分钟
			
			
			// 发布时间
			Date date1=Formats.normalizeString2Datetime(date);
			Calendar calendar1 = GregorianCalendar.getInstance();
			calendar1.setTime(date1); 
			int publishYear=calendar1.get(Calendar.YEAR);// 年
			int publishMonth=calendar1.get(Calendar.MONTH) + 1;//月
			int publishDate=calendar1.get(Calendar.DAY_OF_MONTH);//日
			int publishHour = calendar1.get(Calendar.HOUR_OF_DAY);//小时
			int publishMinute =  calendar1.get(Calendar.MINUTE);//分钟
			
			if(publishYear<nowYear){//本地年 大于 发布年
				
				
				return publishYear+"年"+numberFormat(publishMonth)+"月"+numberFormat(publishDate)+"日";
				
			}else if(publishYear==nowYear){// 本地年 ＝＝ 发布年
				
				if(nowMonth>publishMonth){// 本地月大于发布月
					  return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
				}else if (nowMonth == publishMonth){// 本地月 大于 发布月
					
						if(nowDate > publishDate){// 本地日 大于 发布日
							if(nowDate-publishDate==1){
								return "昨天";
							}else if(nowDate-publishDate==2){
								return "前天";
							//+numberFormat(publishHour)+":"+numberFormat(publishMinute);
							}else{
								return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
							}
						}else if(nowDate == publishDate){// 本地日 ＝＝ 发布日
							
//							if(nowHour-publishHour ==1){// 本地小时 - 发布小时 =1
//								return "1小时前";
//							}else if(nowHour-publishHour ==0){// 本地小时－发布小时 ＝＝0
//								
//								return nowMinute-publishMinute+"分钟前";
//								
//							}else{
								return  "今天 ";
								//+numberFormat(publishHour)+":"+numberFormat(publishMinute);
//							}
						}
					
				}else if(nowMonth< publishMonth){// 本地月 小于 发布月
					  return   numberFormat(publishMonth)+"月"+ numberFormat(publishDate)+"日";
				}
				
			}else if(publishYear>nowYear){//本地年小于发布年
				return publishYear+"年"+numberFormat(publishMonth)+"月"+numberFormat(publishDate)+"日";
				
			}
			
				return date;
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return date;
	}



    public static  String getCurrentTime(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return  sdf.format(date);
	}

	/**
	 * @return
	 */
	public static long dateDifference(String oldtime,String newtime){

		return fromDateStringToLong(newtime)-fromDateStringToLong(oldtime);

	}

	public static long fromDateStringToLong(String inVal) { //此方法计算时间毫秒
		Date date = null;   //定义时间类型
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			date = inputFormat.parse(inVal); //将字符型转换成日期型
			if(date!=null)
				return date.getTime();   //返回毫秒数
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}


	/**
	 * 数字格式化
	 * @return
	 */
	public static String numberFormat(long number){
		try{
			if(number<10){
				
				return "0"+number;
				
			}
			return number+"";
			
			
		}catch(Exception e){
			LogCatLog.e(TAG, "时间格式化错误");
		}
		return number+"";
	}


}

/**
 * 
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.exception;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.xianglin.fellowvillager.app.utils.ActivityManagerTool;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.mobile.common.info.AppInfo;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 未捕获异常处理类
 * 
 * @author songdiyuan
 * @version $Id: CustomCrashHandler.java, v 1.0.0 2015-8-4 下午5:36:04 xl Exp $
 */
public class CustomCrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CustomCrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CustomCrashHandler mInstance = new CustomCrashHandler();
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CustomCrashHandler() {
	}

	/**
	 * 单例模式，保证只有一个CustomCrashHandler实例存在
	 * 
	 * @return
	 */
	public static CustomCrashHandler getInstance() {
		return mInstance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 异常发生时，系统回调的函数，我们在这里处理一些操作
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		LogCatLog.e(TAG,"系统错误",ex);
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);

		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				LogCatLog.e(TAG, "error : ", e);
			}
			
//			 restartApp();

		}

	}

	/**
	 * 重启App
	 */
	private void restartApp() {
		ActivityManagerTool.getActivityManager().exit();

		Message msg = new Message();
		msg.what = 0;
		mHandler.sendMessageDelayed(msg, 2000);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		
		// 收集设备参数信息
		// collectDeviceInfo(mContext);
		// 保存日志文件
		// saveCrashInfo2File(ex);
		// 将一些信息保存到SDcard中
//		if (AppInfo.getInstance().isDebuggable()) {
			String filePath = savaInfoToSD(mContext, ex);
			
			// 使用Toast来显示异常信息 提示用户程序即将退出
			showToast("很抱歉，程序遭遇异常，即将退出！请查看错误日志。\n"+filePath);
//		}

		return true;
	}

	/**
	 * 显示提示信息，需要在线程中显示Toast
	 * 
	 * @param msg
	 */
	private void showToast(final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						ToastUtils.toastForLong(mContext, msg);
					}
				});
			}
		}).start();
	}

	/**
	 * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
	 * 
	 * @param context
	 * @return
	 */
	private HashMap<String, String> obtainSimpleInfo(Context context) {
		HashMap<String, String> map = new HashMap<String, String>();
		PackageManager mPackageManager = context.getPackageManager();
		PackageInfo mPackageInfo = null;
		try {
			mPackageInfo = mPackageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
			
			if(mPackageInfo != null){
				map.put("versionName", mPackageInfo.versionName);
				map.put("versionCode", "" + mPackageInfo.versionCode);
			}
			

			map.put("MODEL", "" + Build.MODEL);
			map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
			map.put("PRODUCT", "" + Build.PRODUCT);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 获取系统未捕捉的错误信息
	 * 
	 * @param throwable
	 * @return
	 */
	private String obtainExceptionInfo(Throwable throwable) {
		StringWriter mStringWriter = new StringWriter();
		PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
		throwable.printStackTrace(mPrintWriter);
		mPrintWriter.close();

		LogCatLog.e(TAG, mStringWriter.toString());
		return mStringWriter.toString();
	}

	/**
	 * 保存获取的 软件信息，设备信息和出错信息保存在SDcard中
	 * @param context
	 * @param ex
	 * @return
	 */
	private String savaInfoToSD(Context context, Throwable ex) {
		String fileName = null;
		StringBuffer sb = new StringBuffer();

		for (Map.Entry<String, String> entry : obtainSimpleInfo(context)
				.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append(" = ").append(value).append("\n");
		}

		sb.append(obtainExceptionInfo(ex));

		try {
			fileName = FileUtils.APK_LOG_PATH
					+ paserTime(System.currentTimeMillis()) + ".log";
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileName;

	}

	/**
	 * 
	 * @return
	 */
	private String getPath() {
		String path = DeviceInfo.getInstance().getExternalStoragePath("crash");
		if (path == null) {
			path = AppInfo.getInstance().getCacheDirPath();
		}
		return path;
	}

	/**
	 * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
	 * 
	 * @param milliseconds
	 * @return
	 */
	private String paserTime(long milliseconds) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		String times = formatter.format(new Date(milliseconds));

		return times;
	}

	/**
	 * 启动程序
	 */
	private void doStartApplicationWithPackageName() {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			mContext.startActivity(intent);
		}
	}

	private Handler mHandler = new Handler() {

		/**
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				doStartApplicationWithPackageName();
				break;
			default:
				break;

			}
		}

	};
}

package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;

/**
 * Created by ex-zhangxiang on 2015/11/28.
 */
public class JpushUtil {
    public static final String PREFS_NAME = "JPUSH_EXAMPLE";
    public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
    public static final String PREFS_START_TIME = "PREFS_START_TIME";
    public static final String PREFS_END_TIME = "PREFS_END_TIME";
    public static final String KEY_APP_KEY = "JPUSH_APPKEY";

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {

        }
        return appKey;
    }

    // 取得版本号
    public static String GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    public static void showToast(final String toast, final Context context)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String getImei(Context context, String imei) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Log.e(JpushUtil.class.getSimpleName(), e.getMessage());
        }
        return imei;
    }

    /**
     *
     * @param title
     * @param content
     */
    public static void addLocalNotification(String title, String content, String extras){
        JPushLocalNotification notification = new JPushLocalNotification();
        notification.setBuilderId(0);//设置本地通知样式
        notification.setContent(content);//设置本地通知的content
        notification.setTitle("乡邻");//设置本地通知的title
        notification.setExtras(extras);
//        notification.setNotificationId(notificationId) ;//设置本地通知的ID
//        notification.setBroadcastTime(System.currentTimeMillis() + 1000 * 60 * 10);

        JPushInterface.addLocalNotification(XLApplication.context, notification);
    }

    public static void addLocalNotification(String count, String extras){
        JPushLocalNotification notification = new JPushLocalNotification();
        notification.setBuilderId(0);//设置本地通知样式
        notification.setContent("您有" + count + "条未读消息");//设置本地通知的content
        notification.setTitle("乡邻");//设置本地通知的title
        notification.setExtras(extras);
//        notification.setNotificationId(notificationId) ;//设置本地通知的ID
//        notification.setBroadcastTime(System.currentTimeMillis() + 1000 * 60 * 10);

        JPushInterface.addLocalNotification(XLApplication.context, notification);
    }

    /**
     *  移除所有的本地通知
     */
    public static void clearLocalNotifications(){
        JPushInterface.clearLocalNotifications(XLApplication.context);
    }


    public static void setAlias(Context mContext,String chatId) { // 设置空的 alias   防止出现收到上个用户的通知
        JPushInterface.setAlias(mContext, chatId, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                LogCatLog.i("test", "int:" + i + ",String:" + s);
            }
        });
    }
}

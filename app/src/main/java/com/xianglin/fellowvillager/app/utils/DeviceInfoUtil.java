package com.xianglin.fellowvillager.app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.framework.XiangLinApplication;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

/**
 * @author chengshengli
 * @version v 1.0.0 2015/11/10 15:04 XLXZ Exp $
 */
public class DeviceInfoUtil {

    /**
     * 获取手机SDK版本号
     *
     * @return
     */
    public static int getSystemSDK() {
        int sys_version = -1;
        try {
            sys_version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sys_version;
    }

    public static int getVersionCode() {
        try {
            PackageManager pm = XiangLinApplication.getInstance().getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(XiangLinApplication.getInstance().getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            int versionCode = pinfo.versionCode;

            String[] pers = pm.getPackageInfo(XiangLinApplication.getInstance().getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
            for (int i = 0; pers != null && i < pers.length; i++) {
                System.out.println("permission=" + pers[i]);
            }
            return versionCode;
        } catch (NameNotFoundException e) {
        }
        return -1;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            // int versionCode = pinfo.versionCode;
            String versionName = pinfo.versionName;
            return versionName;
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    /**
     * 安装app
     *
     * @param context
     * @param path
     */

    public static void installApp(Context context, String path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static boolean isActive = false;

    /**
     * 程序是否在前台运行
     * 是否锁屏
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        /* add by zx */
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
        if (isScreenOn) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName.equals(packageName)
                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断程序的运行在前台还是后台
     *
     * @param context
     * @return 0在后台运行  大于0在前台运行  2表示当前主界面是MainFragmentActivity
     */
    public static int isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = "com.xianglin.fellowvillager.app";
        String bingMapMainActivityClassName = "com.xianglin.fellowvillager.app.activity.MainActivity";
        String bingMapMainActivityClassName2 = "com.xianglin.fellowvillager.app.chat.ChatMainActivity";
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            ComponentName topConponent = tasksInfo.get(0).topActivity;
            if (packageName.equals(topConponent.getPackageName())) {
                // 当前的APP在前台运行
                if (topConponent.getClassName().equals(
                        bingMapMainActivityClassName)
                        || topConponent.getClassName().equals(
                        bingMapMainActivityClassName2)) {
                    // 当前正在运行的是不是期望的Activity
                    return 2;
                }
                return 1;
            } else {
                // 当前的APP在后台运行
                return 0;
            }
        }
        return 0;
    }

    public static boolean isFromForetoBackground = false;
    public static boolean isFromBacktoForeground = false;

    public static int getWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }

    public static int getHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        return height;
    }

    public static int getDpi(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int Dpi = dm.densityDpi;
        return Dpi;
    }

    /**
     * 获取Application meta-data数据
     *
     * @param context
     * @return
     */
    public static String getAppMetaData(Context context, String node) {
        String from = "";
        try {
            if (context == null) {
                context = XLApplication.getInstance();
            }
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            from = appInfo.metaData.getString(node);// 001取不到要通过下面方式来取
//			if (from == null) {
//				int channelInt = appInfo.metaData.getInt("app_channel");
//				from = String.format("%03d", channelInt);
//			}

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return from;
    }

    /**
     * 获取Activity meta-data数据
     *
     * @param context
     * @return
     */
    public static String getAcivityMetaData(Activity context, String node) {
        String value = "";
        try {
            ActivityInfo appInfo = context.getPackageManager().getActivityInfo(
                    context.getComponentName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(node);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取屏幕密度比
     *
     * @return
     */
    public static float getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = XLApplication.getInstance().getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = XLApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = XLApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度dp值
     *
     * @param context
     * @return
     */
    public static int getWidthDip(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int Dpi = dm.densityDpi;
        return (160 * width) / Dpi;
    }

    /**
     * 获取屏幕高度dp值
     *
     * @param context
     * @return
     */
    public static int getHeightDip(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        int Dpi = dm.densityDpi;
        return 2 * (160 * height) / Dpi;
    }

    /**
     * 退出程序方法
     *
     * @param context
     */
    public static void exitApp(Context context) {
        // 清空所有活动Activity
        //XLApplication.getInstance().exit();
        // 跳转到桌面
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static boolean delFile(String path) {
        File file = new File(path);
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除dir目录和目录下的所有文件
     *
     * @param dir
     * @return
     */
    public static boolean delDir(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 删除dir目录下的所有文件
     *
     * @param dir
     * @return
     */
    public static boolean delAllFile(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        return true;
    }

    /**
     * @param dir
     * @return
     */
    public static boolean delDir(File dir, String beforeDate) {
        System.out.println("path=" + dir.getAbsolutePath());
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().compareTo(beforeDate) < 0) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        // dir.delete();
        return true;
    }

    /**
     * 返回某个目录文件下文件总大小 kb
     *
     * @param filepath
     * @return
     */
    public static double getFolderSize(String filepath) {
        File file = new File(filepath);
        double size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; fileList != null && i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i].getPath());
            } else {
                System.out.println("文件的名字是:" + fileList[i].getName());
                size = size + fileList[i].length();
            }
        }
        return size / 1024;
    }


    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailble(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            }
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()
                    || !networkinfo.isConnectedOrConnecting()) {
                return false;
            } else {// /如果有网络连接，再判断一下是否可以正常上网，
                return true;
//				if (openUrl()) {// //正常
//				} else {
//					Toast.makeText(context, "网络异常,请检查网络设置", Toast.LENGTH_LONG).show();
//					/*if(context instanceof LoadingActivity){
//						((LoadingActivity)context).finish();
//					}*/
//					return false;
//				}
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getWifiMacAddress(Context context) {
        try {




/*			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String m_szWLANMAC="";
			if(wm!=null){
				m_szWLANMAC= wm.getConnectionInfo().getMacAddress();
				PersonSharePreference.setAndroidMac(m_szWLANMAC);//保存wifi信息
			}*/

            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * 当android id 为空时 用来代替 它  同时保存cpu wifi bt 信息
     *
     * @param context
     * @return 设备id
     */
    public static String initXLAndroidID(Context context) {

        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String m_szImei = TelephonyMgr.getDeviceId();

            String m_szDevIDShort = "35"
                    +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                    + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                    + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                    + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                    + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                    + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                    + Build.USER.length() % 10;
            String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


            PersonSharePreference.setAndroidCpu(Build.CPU_ABI);//保存cpu信息

            String m_szWLANMAC = getWifiMacAddress(context);//wifi信息

            BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String m_szBTMAC = "";
            if (m_BluetoothAdapter != null) {
                m_szBTMAC = m_BluetoothAdapter.getAddress();
                PersonSharePreference.setAndroidBtMac(m_szBTMAC); //保存蓝牙信息
            }

            String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;

            MessageDigest m = null;

            m = MessageDigest.getInstance("MD5");

            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();

            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                if (b <= 0xF)
                    m_szUniqueID += "0";

                m_szUniqueID += Integer.toHexString(b);
            }
            m_szUniqueID = m_szUniqueID.toUpperCase();

            if (TextUtils.isEmpty(m_szAndroidID)) {

                return m_szUniqueID;

            } else {

                return m_szAndroidID; //当android id 为空时 用来代替 它
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return DeviceInfo.createInstance(context).getTimeStamp();
    }

/*	*/

    /**
     * 动态设置listview高度
     *
     * @param listView
     */

    public static void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 检测sd卡是否存在
     */
    public static boolean isHasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }

        return false;
    }

    /**
     * 获取listview的高度
     * @param pull
     * @return
     */
    public static int getListHeight(ListView pull){
        int totalHeight = 0;
        ListAdapter adapter= pull.getAdapter();
        if(adapter==null)
            return totalHeight;
        try{
            for (int i = 0;i < adapter.getCount(); i++) { //listAdapter.getCount()返回数据项的数目
                View listItem = adapter.getView(i, null, pull);
                listItem.measure(0, 0); //计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
            }

        } catch (Exception e){
            e.printStackTrace();
        }

//        ViewGroup.LayoutParams params = pull.getLayoutParams();
//        params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
//        pull.setLayoutParams(params);

        return totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
    }

    public static int checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        //AppOpsManager appOpsManager;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}

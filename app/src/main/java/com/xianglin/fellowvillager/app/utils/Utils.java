package com.xianglin.fellowvillager.app.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.utils.audio.AlipayVoiceRecorder;
import com.xianglin.mobile.framework.XiangLinApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version v 1.0.1 2015/11/11 15:02  XLXZ Exp $ 简化SharedPreferences
 */
public class Utils {

    private static final String CACHE_TYPE = "xlapp_base_cache"; //默认缓存文件名 by pengyang

    public static void showLongToast(Context context, String pMsg) {
        Toast.makeText(context, pMsg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String pMsg) {
        Toast.makeText(context, pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭 Activity
     *
     * @param activity
     */
    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }

    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     * @param name
     */
    //	public static void start_Activity(Activity activity, Class<?> cls,
    //			BasicNameValuePair... name) {
    //		Intent intent = new Intent();
    //		intent.setClass(activity, cls);
    //		if (name != null)
    //			for (int i = 0; i < name.length; i++) {
    //				intent.putExtra(name[i].getName(), name[i].getValue());
    //			}
    //		activity.startActivity(intent);
    //		activity.overridePendingTransition(R.anim.push_left_in,
    //				R.anim.push_left_out);
    //
    //	}

    /**
     * 发送文字通知
     *
     * @param context
     * @param Msg
     * @param Title
     * @param content
     * @param i
     */
    @SuppressWarnings("deprecation")
    public static void sendText(Context context, String Msg, String Title,
                                String content, Intent i) {
        NotificationManager mn = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher,
                Msg, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //notification.setLatestEventInfo(context, Title, content, contentIntent);
        mn.notify(0, notification);
    }

    /**
     * 移除SharedPreference
     *
     * @param context
     * @param key
     */
    public static final void RemoveValue(Context context, String key) {
        Editor editor = getSharedPreference().edit();
        editor.remove(key);
        boolean result = editor.commit();
        if (!result) {
            Log.e("移除Shared", "save " + key + " failed");
        }
    }

    private static final SharedPreferences getSharedPreference() {
        return XiangLinApplication.getInstance().getSharedPreferences(CACHE_TYPE, Context.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreference 值
     *
     * @param key
     * @return
     */
    public static final String getValue(String key) {
        return getSharedPreference().getString(key, "");
    }

    public static final Boolean getBooleanValue(String key) {
        return getSharedPreference().getBoolean(key, false);
    }

    public static final void putBooleanValue(String key,
                                             boolean bl) {
        Editor edit = getSharedPreference().edit();
        edit.putBoolean(key, bl);
        edit.commit();
    }

    public static final int getIntValue(String key) {
        return getSharedPreference().getInt(key, 0);
    }

    public static final int getIntValue(String key, int defaultVal) {
        return getSharedPreference().getInt(key, defaultVal);
    }

    public static final long getLongValue(String key,
                                          long default_data) {
        return getSharedPreference().getLong(key, default_data);
    }

    public static final boolean putLongValue(String key,
                                             Long value) {
        Editor editor = getSharedPreference().edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static final Boolean hasValue(String key) {
        return getSharedPreference().contains(key);
    }

    /**
     * 设置SharedPreference 值
     *
     * @param key
     * @param value
     */
    public static final boolean putValue(String key,
                                         String value) {
        value = value == null ? "" : value;
        Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        boolean result = editor.commit();
        if (!result) {
            return false;
        }
        return true;
    }

    /**
     * 设置SharedPreference 值
     *
     * @param key
     * @param value
     */
    public static final boolean putIntValue(String key,
                                            int value) {
        Editor editor = getSharedPreference().edit();
        editor.putInt(key, value);
        boolean result = editor.commit();
        if (!result) {
            return false;
        }
        return true;
    }

    public static Date stringToDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))" +
                "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 验证手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static float sDensity = 0;

    /**
     * DP转换为像素
     *
     * @param context
     * @param nDip
     * @return
     */
    public static int dipToPixel(Context context, int nDip) {
        if (sDensity == 0) {
            final WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            sDensity = dm.density;
        }
        return (int) (sDensity * nDip);
    }

    /**
     * 转换px为dip
     **/
    public static int convertPX2DIP(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 将时间戳转为日期字符串
     *
     * @param timeStamp
     * @param format
     * @return
     */
    public static String timeStamp2Date(long timeStamp, String format) {
        // String beginDate="1328007600000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (format != null) {
            sdf = new SimpleDateFormat(format);
        }
        String sd = sdf.format(new Date(timeStamp));

        return sd;
    }

    public static String timeStamp2Date(String timeStamp, String format) {
        // String beginDate="1328007600000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (format != null) {
            sdf = new SimpleDateFormat(format);
        }
        String sd = sdf.format(new Date(Utils.parseLong(timeStamp)));

        return sd;
    }

    /**
     * 将日期字符串转为时间戳
     *
     * @param str_date
     * @return
     */
    public static long DateStr2timeStamp(String str_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(str_date);
            long timeStamp = d.getTime();
            return timeStamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0L;
    }

    public static boolean getBoolDataByNode(String data, String node) {
        try {
            JSONObject obj = new JSONObject(data.toString());
            boolean code = obj.optBoolean(node);
            return code;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据json字符串的node节点获取对应字符串
     *
     * @param json
     * @param node
     * @return
     */
    public static String getStrDataByNode(String json, String node) {
        try {
            JSONObject obj = new JSONObject(json);
            String info = obj.optString(node, "");
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getIntDataNode(String json, String node) {
        try {
            JSONObject obj = new JSONObject(json);
            int info = obj.optInt(node, -1);
            return info;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static JSONObject getJsonObjDataNode(String json, String node) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.optJSONObject(node);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getJsonArryDataNode(String json, String node) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.optJSONArray(node);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) XLApplication.getInstance().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public static void showSoftKeyboard(View view) {
        view.requestFocus();
        ((InputMethodManager) XLApplication.getInstance().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.SHOW_FORCED);
    }

    public static boolean isValidLatAndLon(double lat, double lon) {

        if (lat <= 0 || lon <= 0) return false;

        return true;
    }

    public static long parseLong(String number) {

        try {
            return Long.parseLong(number);
        } catch (NumberFormatException n) {
            return 0L;
        }

    }

    public static int parseInt(String number) {

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException n) {
            return 0;
        }

    }

    public static boolean parseBoolean(String bool) {

        try {
            return Boolean.parseBoolean(bool);
        } catch (NumberFormatException n) {
            return false;
        }
    }

    public static double parseDouble(String number) {

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException n) {
            return 0;
        }

    }

    /**
     * 使用BigDecimal，保留货币小数点后位数
     * pointAfter  保留小数点后位数
     */
    public static String formatDecimal(double value, int pointAfter) {

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(pointAfter, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public static void initVoicePopMenu(View view, Context context, final AlipayVoiceRecorder mAlipayVoiceRecorder) {

        // 获取弹出菜单的布局
        View layout = LayoutInflater.from(context).inflate(R.layout.pop_voice_item_select,
                null);

        // 设置popupWindow的布局
        final PopupWindow popMenu = new PopupWindow(layout,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popMenu.setBackgroundDrawable(context.getResources().getDrawable(
                R.color.transparent));
        popMenu.setOutsideTouchable(true);
        popMenu.setFocusable(true);
        popMenu.setOutsideTouchable(true);
        popMenu.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popMenu.showAsDropDown(view,
                DeviceInfoUtil.dip2px(60),
                -DeviceInfoUtil.dip2px(80));

        layout.findViewById(R.id.earsBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popMenu.dismiss();
                        if (mAlipayVoiceRecorder != null) mAlipayVoiceRecorder.turnEarPhone();
                    }
                });
        layout.findViewById(R.id.speakerBtn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popMenu.dismiss();
                        if (mAlipayVoiceRecorder != null) mAlipayVoiceRecorder.turnSpeakerphoneOn();
                    }
                });

    }

    /**
     * 获取栈顶Activity的名字
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Activity context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return null;
    }

    /**
     * Map a value within a given range to another range.
     *
     * @param value    the value to map
     * @param fromLow  the low end of the range the value is within
     * @param fromHigh the high end of the range the value is within
     * @param toLow    the low end of the range to map to
     * @param toHigh   the high end of the range to map to
     * @return the mapped value
     */
    public static double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    /**
     * set margins of the specific view
     *
     * @param target
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargin(View target, int l, int t, int r, int b) {
        if (target.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) target.getLayoutParams();
            p.setMargins(l, t, r, b);
            target.requestLayout();
        }
    }

    /**
     * convert drawable to bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            Log.d("ANDROID_LAB", "context is null.");
            return false;
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            // 2.1以下的版本不支持下面的API：requestAudioFocus和abandonAudioFocus
            Log.d("ANDROID_LAB", "Android 2.1 and below can not stop music");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        Log.d("ANDROID_LAB", "pauseMusic bMute=" + bMute + " result=" + bool);
        return bool;
    }


    //---------------------生成消息key---------------------

    private static String prefix;
    private static char[] numbersAndLetters;
    private static Random randGen;
    private static long id;

    static {
        randGen = new Random();
        //numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        numbersAndLetters = "0123456789".toCharArray();
        prefix = randomString(5) + "-";
        id = 0L;
    }

    public static String getUniqueMessageId() {

     /*   String time = Long.toHexString(System.currentTimeMillis());
        time = time.substring(6);*/
        // return nextID() + "-" + time;
        //  return  System.currentTimeMillis()+(id++)+"";
        return System.currentTimeMillis() + "";
    }

    public static String getUniqueMessageForFigureidId() {

        //  String time =System.currentTimeMillis()+"";
        //    time = time.substring(10);
        //   return nextID() + "-" + time;
        //   return  System.currentTimeMillis()+(id++)+"";
        return randomString(2);

    }

    public static synchronized String nextID() {
        return prefix + Long.toString((long) (id++));
    }

    public static String randomString(int size) {
        if (size < 1) {
            return null;
        } else {
            char[] chars = new char[size];

            for (int i = 0; i < chars.length; ++i) {
                chars[i] = numbersAndLetters[randGen.nextInt(10)];
            }
            return new String(chars);
        }


    }
    //---------------------消息key---------------------

}

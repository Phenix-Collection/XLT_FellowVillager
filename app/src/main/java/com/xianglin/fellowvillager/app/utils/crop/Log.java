package com.xianglin.fellowvillager.app.utils.crop;

import com.xianglin.mobile.common.logging.LogCatLog;

class Log {

    private static final String TAG = "android-crop";

    public static void e(String msg) {
        LogCatLog.e(TAG, msg);
    }

    public static void e(String msg, Throwable e) {
        LogCatLog.e(TAG, msg, e);
    }

}

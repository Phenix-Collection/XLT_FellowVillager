package com.xianglin.fellowvillager.app.longlink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 监听屏幕关闭和开启＋ 锁屏
 *
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-19
 */
public class ScreenReceiver extends BroadcastReceiver{

    private static final String TAG = LongLinkConfig.TAG;
    public ScreenReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogCatLog.d(TAG,"检测到了 屏幕关闭＋开启＋ 锁屏"+intent.getAction());

    }
}

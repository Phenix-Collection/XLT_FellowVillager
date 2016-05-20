package com.xianglin.fellowvillager.app.longlink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.config.RepeatSendMessageTimeConfig;
import com.xianglin.fellowvillager.app.longlink.longlink.util.LogUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 当前手机网络检测
 * <p/>
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-01
 */
public class NetWorkReceiver extends BroadcastReceiver {
    private static final String LOGTAG = LongLinkConfig.TAG;

    public NetWorkReceiver() {
    }
    int lastType;
    public static final  String NET_AVAILABLE_ACTION="com.xianglin.networkavailable";
    public static final  String NET_NONE_ACTION="com.xianglin.networknone";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    LogCatLog.e(LOGTAG,"网络已连接");
                    Intent intent=new Intent(NET_AVAILABLE_ACTION);
                    XLApplication.getInstance().sendBroadcast(intent);
                    break;

                case 2:
                    LogCatLog.e(LOGTAG,"网络已断开");
                    intent=new Intent(NET_NONE_ACTION);
                    XLApplication.getInstance().sendBroadcast(intent);
                    break;
            }
        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        LogUtil.LogOut(3, LOGTAG, "onReceive() getAction=" + action);
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            RepeatSendMessageTimeConfig.settingConfig(context);

            // 获得网络连接服务
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo info = connManager.getActiveNetworkInfo();

            if (info == null || !connManager.getBackgroundDataSetting()) {
                lastType = -1;
                if(handler!=null)
                    handler.sendEmptyMessage(2);
            } else {//避免网络变化的多次广播  0 mobile 1 wifi
                int netType = info.getType();
                LogCatLog.e(LOGTAG, "netType=" + netType);
                if (netType != lastType) {
                    if (info.isConnected()) {
                        if(handler!=null)
                            handler.sendEmptyMessage(1);
                    } else {
                        if(handler!=null)
                            handler.sendEmptyMessage(2);
                    }
                    lastType = netType;
                }

            }


        }
    }

}

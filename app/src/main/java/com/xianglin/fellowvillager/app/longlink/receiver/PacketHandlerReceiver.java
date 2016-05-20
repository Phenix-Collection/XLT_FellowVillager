package com.xianglin.fellowvillager.app.longlink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xianglin.fellowvillager.app.longlink.MessageFilter;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.listener.RegisterListener;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.LongLinkMsgConstants;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 消息包 处理 监听
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-30
 */
public class PacketHandlerReceiver extends BroadcastReceiver {

    private static final String TAG = LongLinkConfig.TAG;

    public PacketHandlerReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String appData = intent.getStringExtra(LongLinkMsgConstants.LONGLINK_APPDATA);
        if (action.equals(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER+"DEFAULT")) {//默认
            /**
             * 默认 后面看需求
             */
            LogCatLog.d(TAG,"注册成功消息");
            Intent intent1 = new Intent();
            intent1.putExtra("ISSUCCESS",intent.getBooleanExtra("ISSUCCESS",false));// 注册成功
            intent1.setAction("android.intent.action.LONGLINKCONNECTHANDLER");
//            context.sendBroadcast(intent1);
            new RegisterListener().onRegister(context,intent1);

        } else if (action.equals(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER + "CHAT")) {// 聊天
            MessageFilter.getInstance().switchMessageHandlerListener(appData, context);// 消息过滤器
        }

    }
}

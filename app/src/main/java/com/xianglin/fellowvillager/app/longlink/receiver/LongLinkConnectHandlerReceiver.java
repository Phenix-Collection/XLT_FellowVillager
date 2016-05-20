package com.xianglin.fellowvillager.app.longlink.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.config.RepeatSendMessageTimeConfig;
import com.xianglin.fellowvillager.app.longlink.handler.RepeatSendMessageHandler;
import com.xianglin.fellowvillager.app.longlink.longlink.PacketHanlder;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.LongLinkMsgConstants;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.service.LongLinkPacketHandler;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 链接成功之后 对发送中的消息进行处理
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-21
 */
public class LongLinkConnectHandlerReceiver extends BroadcastReceiver{

    private static final String TAG = LongLinkConfig.TAG;
    private static  LocalBroadcastManager localBroadcastManager = null;
    private static  PacketHandlerReceiver packetHandlerReceiver = null;
    private static final String ACTION = "android.intent.action.LONGLINKCONNECTHANDLER";
    @Override
    public void onReceive(Context context, Intent intent) {
        RepeatSendMessageHandler.isOnResume = true;
        if (intent.getAction().equals(ACTION)){
            boolean  isSuccess = intent.getBooleanExtra("ISSUCCESS",true);
            if (isSuccess) {
                PacketHanlder packetHanlder = LongLinkPacketHandler.getInstance(context);
                XLApplication.longLinkServiceManager.registerCommonFunc(packetHanlder);//registerCommonFunc 处理消息回调
                RepeatSendMessageTimeConfig.settingConfig(context);//重新建立连接之后，设置重发的时间次数设置
                if (localBroadcastManager != null) {
                    unregisterMessageReceiver();
                }
                registerMessageReceiver(context);
                RepeatSendMessageHandler.isRegister = isSuccess;// 长连接注册成功
                sendingMessage(context);
                LogCatLog.d(TAG, "＝＝＝注册成功＝＝＝");
            }else{
                LogCatLog.e(TAG, "＝＝＝注册失败＝＝＝");
                RepeatSendMessageHandler.isRegister = isSuccess;// 长连接注册失败
                LogCatLog.e(TAG, "＝＝＝注册失败＝＝＝");
            }
        }

    }

    public void sendingMessage(Context mContext){
        // 链接成功之后，进行消息重发等问题...
       // MessageHandler.getInstance().queryLooperMessage(mContext);
    }

    //注册消息处理广播
    private void registerMessageReceiver(Context mContext) {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER);
        intentFilter.addAction(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER + "CHAT");
        intentFilter.addAction(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER + "NOTICE");
        packetHandlerReceiver = new PacketHandlerReceiver();
        localBroadcastManager.registerReceiver(packetHandlerReceiver, intentFilter);

    }
    /**
     * 注销 消息处理广播
     */
    private void unregisterMessageReceiver() {
        localBroadcastManager.unregisterReceiver(packetHandlerReceiver);
        localBroadcastManager = null;
    }
}

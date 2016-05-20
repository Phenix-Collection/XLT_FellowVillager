package com.xianglin.fellowvillager.app.longlink.listener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.longlink.receiver.PacketHandlerReceiver;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.config.RepeatSendMessageTimeConfig;
import com.xianglin.fellowvillager.app.longlink.handler.RepeatSendMessageHandler;
import com.xianglin.fellowvillager.app.longlink.longlink.PacketHanlder;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.LongLinkMsgConstants;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.service.LongLinkPacketHandler;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * new 注册成功 主要解决 小米类型的手机的广播接受不到
 *
 * Created by james on 16/3/1.
 */
public class RegisterListener {



    private static final String TAG = LongLinkConfig.TAG;
    private static LocalBroadcastManager localBroadcastManager = null;
    private static PacketHandlerReceiver packetHandlerReceiver = null;
    private static final String ACTION = "android.intent.action.LONGLINKCONNECTHANDLER";

    public void onRegister(Context context,Intent intent) {
        RepeatSendMessageHandler.isOnResume = true;
        LogCatLog.d(TAG,"长连接发过来的通知");
        if (intent.getAction().equals(ACTION)){
            boolean  isSuccess = intent.getBooleanExtra("ISSUCCESS",true);
            if (isSuccess) {
                LogCatLog.d(TAG, "＝＝＝注册成功＝＝＝");
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
                LogCatLog.d(TAG, "＝＝＝注册失败＝＝＝");
                RepeatSendMessageHandler.isRegister = isSuccess;// 长连接注册失败
                LogCatLog.d(TAG, "＝＝＝注册失败＝＝＝");
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

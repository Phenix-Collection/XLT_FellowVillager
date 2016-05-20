package com.xianglin.fellowvillager.app.longlink;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.constants.ENVController;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.receiver.PacketHandlerReceiver;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.listener.NetWorkListener;
import com.xianglin.fellowvillager.app.longlink.longlink.LongLinkServiceManager;
import com.xianglin.fellowvillager.app.longlink.longlink.PacketHanlder;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.LongLinkMsgConstants;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.service.LongLinkPacketHandler;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 初始化长连接
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-27
 */
public class LongLinkUtils {

    private static final String TAG = LongLinkConfig.TAG;
    private static final String LONGLINKPATH = "/sdcard/XiangLin/biz.txt";

    public LongLinkUtils() {

    }

    /**
     * 初始化长连接
     *
     * @param longLinkServiceManager
     */
    public boolean initLongLink(LongLinkServiceManager longLinkServiceManager, boolean mIsServiceBound, Context mContext) {
        try {
            if (longLinkServiceManager != null) {
                if (mIsServiceBound) {
                    String host = ENVController.LONGLINK_HOST;
                    int port = Integer.parseInt(ENVController.LONGLINK_PORT);
//                    String host = "172.16.12.153";//ENVController.LONGLINK_HOST;
//                    int port = 9999;//Integer.parseInt(ENVController.LONGLINK_PORT);
                    LogCatLog.d(TAG, "长链接地址" + host + ":" + port);
                    String sslFlag = "0";// ssl 标示
                    longLinkServiceManager.setLinkAddr(host, port, sslFlag);// 设置连接
                    longLinkServiceManager.startLink();// 启动连接
                    PacketHanlder packetHanlder = LongLinkPacketHandler.getInstance(mContext);
                    longLinkServiceManager.registerCommonFunc(packetHanlder);//registerCommonFunc 处理消息回调
                    //注册消息处理广播
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(LongLinkMsgConstants.LONGLINK_ACTION_CMD_TRANSFER + "DEFAULT");
                    PacketHandlerReceiver packetHandlerReceiver = new PacketHandlerReceiver();
                    localBroadcastManager.registerReceiver(packetHandlerReceiver, intentFilter);
                }
            }
            return true;
        } catch (Exception e) {
            LogCatLog.d(TAG, "初始化链接失败！！");

        }
        return false;

    }

    /**
     * 长连接启动
     */
    public void longLinkStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                } while (!XLApplication.isConnect);

                if (XLApplication.isConnect) {
                    String mKey = new MessageDBHandler(XLApplication.getInstance()).getRecentMsg();
                    long msgKey = TextUtils.isEmpty(mKey) ? 0 : Long.parseLong(mKey);
                    if (XLApplication.longLinkServiceManager != null) {
                        LogCatLog.i(TAG, "连接成功 －－》 注册用户信息");
                        XLApplication.longLinkServiceManager.setAppUserInfo(PersonSharePreference.getUserID() + "",
                                PersonSharePreference.getDeviceId(),
                                PersonSharePreference.getDeviceId(),
                                msgKey + "");// 进

                        LogCatLog.i(TAG, "＝＝＝＝启动消息发送＝＝＝＝");
                        if (XLApplication.repeatSendMessageHandler == null) {
                            XLApplication.initMessage();
                        }
                        new Thread(XLApplication.repeatSendMessageHandler).start();// 在注册之后，进行消息重发
                    }
                }

            }
        }).start();

    }

    public static void longLinkIsSuccess(Context context) {
        int stateNetWork = NetWorkListener.getInstance().getNetworkState(context);
        LogCatLog.d(TAG, "网络状态" + stateNetWork);
        switch (stateNetWork) {
            case NetWorkListener.NETWORK_CLASS_WIFI:
            case NetWorkListener.NETWORK_CLASS_2_G:
            case NetWorkListener.NETWORK_CLASS_3_G:
            case NetWorkListener.NETWORK_CLASS_4_G:
                if (XLApplication.isConnect) {
                    String mKey = new MessageDBHandler(XLApplication.getInstance()).getRecentMsg();
                    long msgKey = TextUtils.isEmpty(mKey) ? 0 : Long.parseLong(mKey);
                    if (XLApplication.longLinkServiceManager != null) {
                        LogCatLog.i(TAG, "连接成功 －－》 注册用户信息");
                        XLApplication.longLinkServiceManager.setAppUserInfo(
                                PersonSharePreference.getUserID() + "",
                                PersonSharePreference.getDeviceId(),
                                PersonSharePreference.getDeviceId(),
                                msgKey + "");// 进
                    }
                }
                break;
            case NetWorkListener.NETWORK_CLASS_UNKNOWN:
                LogCatLog.i(TAG, "===无连接===");
                break;
        }
    }


    /**
     * 长连接日志
     *
     * @param longLinkMessage
     */
    public static void longLink(String longLinkMessage) {
        RandomAccessFile randomFile = null;
        try {
            File file = new File(LONGLINKPATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\n======biz==========\n");
            stringBuffer.append("长连接数据:\n" + longLinkMessage + "\n");
            stringBuffer.append("======biz==========\n");

            randomFile = new RandomAccessFile(file.getAbsoluteFile(), "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(stringBuffer.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            LogCatLog.e(TAG, "写入日志失败", e);
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

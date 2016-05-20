package com.xianglin.fellowvillager.app.longlink.config;

import android.content.Context;

import com.xianglin.fellowvillager.app.longlink.listener.NetWorkListener;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 重发时间配置
 * 根据当前网络环境进行 时间分配
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-31
 */
public class RepeatSendMessageTimeConfig {

    private static final String TAG = LongLinkConfig.TAG;
    //======================================default=========================================//
    public static int LOOPTIME = 3000;// 默认轮询时间
    public static int NULLLOOPTIME = 6000;// 默认无消息轮询时间
    public static int ISMESSAGETIME1 = 1000*3;//默认之前发送的消息 与 这次loop 消息的 时间 间隔 普通文字消息
    public static int ISMESSAGETIME2 = -1000*3;
    public static int ISFILEMESSAGETIME1 = 1000*3;// 默认文件消息为3分钟
    public static int ISFILEMESSAGETIME2 = -1000*3; // 默认文件消息为3分钟
    public static int MESSAGEMACCOUNT = 2;// 默认消息最大数量
    public static int FILEMESSAGECOUNT = 2;// 文件发送消息最大数量
    public static int ISREGISTERTIME = 1000*15;// 是否注册 轮询
    //======================================default=========================================//


    //======================================WIFI============================================//
    public static int LOOPTIME_WIFI = 1000;// WI-FI轮询时间
    public static int NULLLOOPTIME_WIFI = 6000;// WI-FI无消息时间
    public static int ISMESSAGETIME1_WIFI = 1000*3;//WI-FI之前发送的消息 与 这次loop 消息的 时间 间隔 普通文字消息
    public static int ISMESSAGETIME2_WIFI = -1000*3;
    public static int ISFILEMESSAGETIME1_WIFI = 1000*3;// WI-FI文件消息为 2分钟
    public static int ISFILEMESSAGETIME2_WIFI = -1000*3; // WI-FI文件消息为2分钟
    public static int MESSAGEMACCOUNT_WIFI = 2;// WI-FI消息最大数量
    public static int FILEMESSAGECOUNT_WIFI = 2;// 文件发送消息
    //======================================WIFI============================================//


    //======================================4G==============================================//
    public static int LOOPTIME_4G = 1000;// 4G轮询时间
    public static int NULLLOOPTIME_4G = 6000;// 4G无消息时间
    public static int ISMESSAGETIME1_4G = 1000*3;//4G之前发送的消息 与 这次loop 消息的 时间 间隔 普通文字消息
    public static int ISMESSAGETIME2_4G = -1000*3;
    public static int ISFILEMESSAGETIME1_4G = 1000*3;// 4G文件消息为 2分钟
    public static int ISFILEMESSAGETIME2_4G = -1000*3; // 文件消息为2分钟
    public static int MESSAGEMACCOUNT_4G = 2;// 消息最大数量
    public static int FILEMESSAGECOUNT_4G = 2;// 文件发送消息
    //======================================4G==============================================//


    //======================================3G==============================================//
    public static int LOOPTIME_3G = 1000;// 3G轮询时间
    public static int NULLLOOPTIME_3G = 6000;// 3G无消息时间
    public static int ISMESSAGETIME1_3G = 1000*3;//3G之前发送的消息 与 这次loop 消息的 时间 间隔 普通文字消息
    public static int ISMESSAGETIME2_3G = -1000*3;
    public static int ISFILEMESSAGETIME1_3G = 1000*3;// 3G文件消息为 2分钟
    public static int ISFILEMESSAGETIME2_3G = -1000*3; // 3G文件消息为2分钟
    public static int MESSAGEMACCOUNT_3G = 2;// 3G消息最大数量
    public static int FILEMESSAGECOUNT_3G = 2;// 文件发送消息
    //======================================3G==============================================//


    //======================================2G==============================================//
    public static int LOOPTIME_2G = 3000;// 2G轮询时间
    public static int NULLLOOPTIME_2G = 6000;// 2G无消息时间
    public static int ISMESSAGETIME1_2G = 1000*3;//2G之前发送的消息 与 这次loop 消息的 时间 间隔 普通文字消息
    public static int ISMESSAGETIME2_2G = -1000*3;
    public static int ISFILEMESSAGETIME1_2G = 1000*3;// 2G文件消息为 3分钟
    public static int ISFILEMESSAGETIME2_2G = -1000*3; // 2G文件消息为3分钟
    public static int MESSAGEMACCOUNT_2G = 2;// 2G消息最大数量
    public static int FILEMESSAGECOUNT_2G = 2;// 文件发送消息
    //======================================2G==============================================//


    public RepeatSendMessageTimeConfig() {
    }

    public synchronized static void settingConfig(Context mContext) {
        int networkState = NetWorkListener.getInstance().getNetworkState(mContext);
        switch (networkState) {
            case NetWorkListener.NETWORK_CLASS_WIFI:
                LogCatLog.d(TAG,"WIFI环境 重发时间设置和次数设置");
                setValues(
                        LOOPTIME_WIFI,
                        NULLLOOPTIME_WIFI,
                        ISMESSAGETIME1_WIFI,
                        ISMESSAGETIME2_WIFI,
                        ISFILEMESSAGETIME1_WIFI,
                        ISFILEMESSAGETIME2_WIFI,
                        MESSAGEMACCOUNT_WIFI,
                        FILEMESSAGECOUNT_WIFI
                );
                break;
            case NetWorkListener.NETWORK_CLASS_4_G:
                LogCatLog.d(TAG,"4G环境 重发时间设置和次数设置");
                setValues(
                        LOOPTIME_4G,
                        NULLLOOPTIME_4G,
                        ISMESSAGETIME1_4G,
                        ISMESSAGETIME2_4G,
                        ISFILEMESSAGETIME1_4G,
                        ISFILEMESSAGETIME2_4G,
                        MESSAGEMACCOUNT_4G,
                        FILEMESSAGECOUNT_4G
                );
                break;
            case NetWorkListener.NETWORK_CLASS_3_G:
                LogCatLog.d(TAG,"3G环境 重发时间设置和次数设置");
                setValues(
                        LOOPTIME_3G,
                        NULLLOOPTIME_3G,
                        ISMESSAGETIME1_3G,
                        ISMESSAGETIME2_3G,
                        ISFILEMESSAGETIME1_3G,
                        ISFILEMESSAGETIME2_3G,
                        MESSAGEMACCOUNT_3G,
                        FILEMESSAGECOUNT_3G
                );
                break;
            case NetWorkListener.NETWORK_CLASS_2_G:
                LogCatLog.d(TAG,"2G环境 重发时间设置和次数设置");
                setValues(
                        LOOPTIME_2G,
                        NULLLOOPTIME_2G,
                        ISMESSAGETIME1_2G,
                        ISMESSAGETIME2_2G,
                        ISFILEMESSAGETIME1_2G,
                        ISFILEMESSAGETIME2_2G,
                        MESSAGEMACCOUNT_2G,
                        FILEMESSAGECOUNT_2G
                );
                break;
            case NetWorkListener.NETWORK_CLASS_UNKNOWN:
            default:
                LogCatLog.d(TAG,"其他（无）环境 重发时间设置和次数设置");
                setValues(
                        LOOPTIME,
                        NULLLOOPTIME,
                        ISMESSAGETIME1,
                        ISMESSAGETIME2,
                        ISFILEMESSAGETIME1,
                        ISFILEMESSAGETIME2,
                        MESSAGEMACCOUNT,
                        FILEMESSAGECOUNT

                );
                break;

        }
    }

    public static  void setValues(int loopTime,
                                  int nullloopTime ,
                                  int isMessageTime1,
                                  int isMessageTime2,
                                  int isFileMessageTime1,
                                  int isFileMessageTime2,
                                  int messageMacCount,
                                  int fileMessageCount
                                 ){
        LOOPTIME = loopTime;
        NULLLOOPTIME = nullloopTime;
        ISMESSAGETIME1 = isMessageTime1;
        ISMESSAGETIME2 = isMessageTime2;
        ISFILEMESSAGETIME1 = isFileMessageTime1;
        ISFILEMESSAGETIME2 = isFileMessageTime2;
        MESSAGEMACCOUNT = messageMacCount;
        FILEMESSAGECOUNT = fileMessageCount;
    }

}

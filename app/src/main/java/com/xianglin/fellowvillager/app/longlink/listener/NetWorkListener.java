package com.xianglin.fellowvillager.app.longlink.listener;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 网络判断 2G 3G 4G
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-31
 */
public class NetWorkListener {


    private volatile static NetWorkListener networkListener;
    private static final String LOGTAG = LongLinkConfig.TAG;

    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks. {@hide}
     */
    public static final int NETWORK_CLASS_4_G = 3;

    public static final int NETWORK_CLASS_WIFI= 4;

    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /**
     * Current network is GSM {@hide}
     */
    public static final int NETWORK_TYPE_GSM = 16;
    /**
     * Current network is TD_SCDMA {@hide}
     */
    public static final int NETWORK_TYPE_TD_SCDMA = 17;
    /**
     * Current network is IWLAN {@hide}
     */
    public static final int NETWORK_TYPE_IWLAN = 18;

    private NetWorkListener() {

    }


    public static NetWorkListener getInstance() {
        if (networkListener == null) {
            networkListener = new NetWorkListener();
        }
        return networkListener;
    }

    public int getNetworkState(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeInfo != null) {
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                /**
                 * WIFI 网络
                 */
                return NETWORK_CLASS_WIFI;
            } else {
                if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    /**
                     * MOBILE 网络
                     * 1:移动
                     *      1.1:2G
                     *      1.2:3G
                     *      1.3:4G
                     * 2:联通
                     *      2.1:2G
                     *      2.2:3G
                     *      2.3:4G
                     * 3:电信
                     *      3.1:2G
                     *      3.2:3G
                     *      3.3:4G
                     */
                    int networkType = activeInfo.getSubtype();
                    return getNetworkClass(networkType);
                } else {
                    LogCatLog.d(LOGTAG, "当前手机无移动网络连接");
                }
            }

        } else {
            LogCatLog.d(LOGTAG, "当前手机无网络连接");
        }
        return NETWORK_CLASS_UNKNOWN;
    }

    public int getNetworkClass(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_GSM:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:

                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
            case NETWORK_TYPE_TD_SCDMA:

                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
            case NETWORK_TYPE_IWLAN:

                return NETWORK_CLASS_4_G;
            default:

                return NETWORK_CLASS_UNKNOWN;
        }
    }

}

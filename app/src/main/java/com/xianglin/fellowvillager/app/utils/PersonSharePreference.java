package com.xianglin.fellowvillager.app.utils;

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */


import android.text.TextUtils;

/**
 * 应用数据缓存
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/11 14:49  XLXZ Exp $
 */
public class PersonSharePreference {

    private static final String CHAT_FID_MESSAGECOUNT = "chat_fid";

    /**
     * 记录之前的更新版本之前的versionCode
     * 解决新老版本数据不兼容
     */
    public static void setBeforeVersionCode(int versionCode) {
        if (getBeforeVersionCode() < versionCode) {
            cleanUserInfo();
        }
        Utils.putIntValue("versionCode", versionCode);
    }

    /**
     * 得到更新版本之前的versionCode
     */
    public static int getBeforeVersionCode() {
        try {

            return Utils.getIntValue("versionCode");
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 退出登录时清楚用户的登录信息
     */
    public static void cleanUserInfo() {

    }

    // TODO: 2015/11/16  需要确定 用户信息 是否保存在此处

    /**
     * 用户正常登录有id
     */

    public static void setUserID(String uid) {
        Long id = 0L;
        try {
            id = Long.parseLong(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserID(id);
    }

    public static void setUserID(long uid) {
        Utils.putLongValue("user_id", uid);
    }

    /**
     * 得到用户正常登录的id
     */
    public static long getUserID() {
        return Utils.getLongValue("user_id",0L);
    }

    public static void setFigureID(String uid) {
        Utils.putValue("figure_id", uid);
    }

    /**
     * 得到当前角色的id
     */
    public static String getFigureID() {
        return Utils.getValue("figure_id");
    }

    /**
     * 用户正常登录头像上传成功imgid
     */

    public static void setUserImgID(String uid) {
        Long id = 0L;
        try {
            id = Long.parseLong(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserImgID(id);
    }

    public static void setUserImgID(long uid) {
        Utils.putLongValue("user_imgid", uid);
    }
    /**
     * 得到用户头像的imgid
     */

    public static long getUserImgID() {
        return Utils.getLongValue("user_imgid",0L);
    }

    /**
     * 用户正常登录的user_nickname
     */

    public static void setUserNickName(String nickname) {
        Utils.putValue("user_nickname", nickname);

    }

    /**
     * 得到用户正常登录的user_nickname
     */


    public static String getUserNickName() {
        return Utils.getValue("user_nickname");
    }

    /**
     * 本地通知 判断是否在1分钟之内
     */

    public static void setIsInNoticeTime(String noticetime) {
        Utils.putValue("isin_noticetime", noticetime);

    }

    public static String getIsInNoticeTime() {
        return Utils.getValue("isin_noticetime");
    }

    /**
     * 判断是否点击了本地通知
     */

    public static void setIsClickNotify(String isClickNotify) {
        Utils.putValue("isclicknotify", isClickNotify);

    }

    public static String getIsClickNotify() {
        return Utils.getValue("isclicknotify");
    }

    /**
     * 储存当前环境
     */

    public static void setXlEnv(String xlEnv) {
        Utils.putValue("XlEnv", xlEnv);

    }

    public static String getXlEnv() {
        return Utils.getValue("XlEnv");
    }

    /**
     * 用于判断是否有新的msg
     */

    public static void setMsgKey(String uid) {
        Long id = 0L;
        try {
            id = Long.parseLong(uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUserImgID(id);
    }

    public static void setMsgKey(long uid) {
        Utils.putLongValue("msgKey", uid);
    }

    public static long getMsgKey() {
        return Utils.getLongValue("msgKey", 0L);
    }

/*
*
     * 用户是否登录了

*/

    public static void setLogin(boolean login) {
        Utils.putBooleanValue("user_login", login);
    }

    /*
         * 用户是否登录了
    */
    public static boolean isLogin() {
        return Utils.getBooleanValue("user_login");
    }


    /**
     *
     * @return  服务器返回的设备唯一id
     */
    public static String getDeviceId() {
        return Utils.getValue("DeviceId");
    }

    /** 服务器返回的设备唯一id
     * @param androidCpu
     */
    public static void setDeviceId(String androidCpu) {
        Utils.putValue("DeviceId", androidCpu);
    }




    public static String getAndroidCpu() {
        return Utils.getValue("androidCpu");
    }

    public static void setAndroidCpu(String androidCpu) {


        if(TextUtils.isEmpty(getAndroidCpu())) {
            Utils.putValue("androidCpu", androidCpu);
        }
    }


    public static String getAndroidSerialId() {
        return Utils.getValue("androidSerialId");
    }

    public static void setAndroidSerialId(String androidSerialId) {
        if(TextUtils.isEmpty(getAndroidSerialId())) {
            Utils.putValue("androidSerialId", androidSerialId);
        }
    }

    public static void setAndroidMac(String androidMac) {
        if(TextUtils.isEmpty(getAndroidMac())) {
            Utils.putValue("androidMac", androidMac);
        }
    }

    public static String getAndroidMac() {
        return Utils.getValue("androidMac");
    }

    public static void setAndroidBtMac(String androidMac) {
        if(TextUtils.isEmpty(getAndroidBtMac())) {
            Utils.putValue("androidBtMac", androidMac);
        }
    }
    public static String getAndroidBtMac() {
        return Utils.getValue("androidBtMac");
    }

    /** imei 和 imsi已经加了 判断不会第二次读取
     * @param androidImei
     */
    public static void setAndroidImei(String androidImei) {
        Utils.putValue("androidImei", androidImei);
    }

    public static String getAndroidImei() {
        return Utils.getValue("androidImei");
    }

    public static void setAndroidImsi(String androidImsi) {
        Utils.putValue("androidImsi", androidImsi);
    }

    public static String getAndroidImsi() {
        return Utils.getValue("androidImsi");
    }


    /**
     * 设置每个消息的角色消息
     * @param fid
     */
    public static void setChatFidCount(String fid){

        long message_count = getChatFidCount(fid);
        message_count++;
        Utils.putLongValue(CHAT_FID_MESSAGECOUNT+fid,message_count);


    }

    /**
     * 获取每个角色的消息数量
     * @param fid
     * @return
     */
    public static long  getChatFidCount(String fid){

       return  Utils.getLongValue(CHAT_FID_MESSAGECOUNT+fid,0);

    }

}

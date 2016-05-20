package com.xianglin.fellowvillager.app.longlink;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.rome.longlinkservice.LongLinkMsgConstants;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.List;

/**
 * 消息发送
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-27
 */
public class MessageSender {

    private static final String TAG = LongLinkConfig.TAG;
    public static volatile MessageSender messageSender;



    public MessageSender() {

    }

    /**
     * 获取消息发送实例
     *
     * @return
     */
    public synchronized static MessageSender getInstance() {
        if (messageSender == null) {
            messageSender = new MessageSender();
        }
        return messageSender;
    }


    /**
     * 长连接初始化
     *
     * @param mUserId
     * @param mDeviceId
     * @param mSky
     * @return
     */
    public void sendAppLongLinkInIt(String mUserId, String mDeviceId, long mSky) {
        Md md = new Md();
        md.setFromid(mUserId);
        md.setDeviceId(mDeviceId);
        md.setMsgKey(mSky);
        sendContentValues(JSON.toJSONString(md));

    }

    /**
     * 发送用户信息
     *
     * @param userId
     * @return
     */
    public boolean sendAppUserInfo(String userId) {
        XLApplication.getLongLinkService().setAppUserInfo(userId,"","se01", "100000000");//帐号注册
        LogCatLog.d(TAG, userId + "乡邻ID 注册成功");
        return true;
    }


    /**
     * 发送消息
     *
     * @param chatJson
     * @return
     */
    public boolean sendContentValues(String chatJson) {
        LogCatLog.d(TAG, "即将要发送的消息" + chatJson);
        XLApplication.getLongLinkService().sendPacketUplink(LongLinkMsgConstants.MSG_PACKET_CHANNEL_SYNC,
                LongLinkMsgConstants.MSG_PACKET_TYPE_CHAT, chatJson + "\r\n");//appData

        return true;
    }

    /**
     * 发送一组消息
     * @param list
     * @return
     */
    public boolean sendArrayContentValues(List<String> list){
        XLApplication.getLongLinkService().sendArrayPacketUplink(LongLinkMsgConstants.MSG_PACKET_CHANNEL_SYNC,
                LongLinkMsgConstants.MSG_PACKET_TYPE_CHAT, list);
        return true;
    }

    /**
     * 回复通知消息
     *
     * @param chatJson
     * @return
     */
    public boolean sendNoticeVlaues(String chatJson) {
        XLApplication.getLongLinkService().sendPacketUplink(LongLinkMsgConstants.MSG_PACKET_CHANNEL_SYNC,
                LongLinkMsgConstants.MSG_PACKET_TYPE_NOTICE, chatJson + "\r\n");//appData
        return true;
    }



}

package com.xianglin.fellowvillager.app.longlink;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 消息业务层过滤
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-07
 */
public class MessageFilter {

    private static final String TAG = LongLinkConfig.TAG;

    public static volatile MessageFilter messageFilter;

    private long XLID = PersonSharePreference.getUserID();

    public MessageFilter() {

    }

    /**
     * 获取消息过滤实例
     *
     * @return
     */
    public synchronized static MessageFilter getInstance() {

        if (messageFilter == null) {
            messageFilter = new MessageFilter();
        }
        return messageFilter;
    }


    /**
     * 消息选择回调监听
     *
     * @param appData
     * @param mContext
     */
    public void switchMessageHandlerListener(String appData, final Context mContext) {
        /**
         * 聊天
         */
        LongLinkUtils.longLink(appData);
        Md md = JSON.parseObject(appData, Md.class);
        LogCatLog.d(TAG,"消息回调成功，进行消息处理 本地key"+md.getSKey()+"消息内容"+md.getMessage());
        switch (md.getReplyType()) {
            case 0:// 消息
                notifyMessageListener(md, appData, mContext);
                //recvMessage(appData);// TODO: 16/1/18 回复消息统一到长连接里面进行处理 －－james
                break;
            case 1:
                MessageHandler.getInstance().handlerSendMsg(md);
                break;
        }
    }

    /**
     * 消息通知监听
     * @param md
     * @param appData
     * @param mContext
     */
    public void notifyMessageListener(final Md md, String appData, final Context mContext) {

        LogCatLog.d(TAG, "notifyMessageListener:md.getToid="+md.getToid()+"md.getFromid()"+md.getFromid());
        MessageHandler.getInstance().handlerMsg(md,appData);


/*        *//**
         *1:系统通知消息
         *2:个人［点对点］DB  联系人  消息回调
         *                  陌生人  请求接口 拿到你所要的陌生人的详细信息
         *
         *
         *3:群组      DB 存在的群组信息
         *           DB 未存在群组消息
         *
         *//*
        if (!DeviceInfoUtil.isAppOnForeground(mContext)) {//本地通知消息


            if (md.getSendType() == BorrowConstants.CHATTYPE_GROUP) {

                if (md.getFromid().equals(XLID + "")) {
                    return ; //过滤掉 发过来的群消息中单独发给自己的消息
                }
            }

            Intent intent1 = new Intent();
            intent1.setAction(NoticeReceiver.NOTIFYRECEIVER);
            intent1.putExtra("APPDATA", appData);
            mContext.sendBroadcast(intent1);
        } else {// 基本消息通知
            XLApplication.getMessageListenerManager().notifyListener(md);
        }*/
    }

    /**
     * 确认消息
     * @param appData
     */
    public void recvMessage(String appData){
        Md md1 = JSON.parseObject(appData, Md.class);

        md1.setReplyType(1);
        long userId =  PersonSharePreference.getUserID();// 确认出去的Toid 需改成当前用户的ID
        if (userId != 0){
            md1.setToid(userId+"");
        }
        if (md1.getNoticeType() > 0){
            new MessageSender().sendNoticeVlaues(JSON.toJSONString(md1));
        }else{
            new MessageSender().sendContentValues(JSON.toJSONString(md1));
        }
    }

}

package com.xianglin.fellowvillager.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.model.Extras;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.fellowvillager.app.utils.JpushUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.SingleThreadExecutor;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * Created by ex-zhangxiang on 2015/12/7.
 */
public class NoticeReceiver extends BroadcastReceiver {
    private String appData = null;
    private Context mContext;
    private String name = null;
    private int msgType;
    private int sendType;
    private String groupId;
    private String message = null;//消息
    private Long msgKey;

    public String TAG = "NoticeReceiver";
    public static final String NOTIFYRECEIVER = "com.xianglin.fellowvillager.app.Receiver.NoticeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogCatLog.i(TAG, "--------------onReceive--------------");
        mContext = context;
        if (intent.getAction().equals(NOTIFYRECEIVER)) {
            appData = intent.getStringExtra("APPDATA");
            addLocalNotification();
        }
    }

    /**
     * 添加本地通知
     */
    public void addLocalNotification() {
        LogCatLog.i(TAG, "--------------addLocalNotification--------------");
        Md md = JSON.parseObject(appData, Md.class);
        if (!TextUtils.isEmpty(md.getFromid()) && !TextUtils.isEmpty(md.getMessageType() + "")) {
            final String fromId = md.getFromid();
            msgType = md.getMessageType();//消息类型
            sendType = md.getSendType();
            groupId = md.getToid();
            msgKey = md.getMsgKey();
            if (!TextUtils.isEmpty(md.getMessage())) {
                message = md.getMessage();
            }
            Extras extras = new Extras();
            extras.setFfid(md.getFromFigure());
            extras.setFid(md.getFromid());
            extras.setGid(md.getGroupId());
            extras.setTfid(md.getToFigure());
            extras.setToid(md.getToid());
            extras.setSendType(sendType+"");
            extras.setType(sendType+"");
            extras.setLifeTime(md.getLifetime());
            appData = JSON.toJSONString(extras);
            showNotification(extras);
        }
    }




    /**
     * 判断类型显示本地通知
     */
    private void showNotification(Extras extras) {
        String title = name;

        if (sendType == BorrowConstants.CHATTYPE_GROUP) {
            GroupDBHandler groupDBHandler = new GroupDBHandler(mContext);
            if (null != groupDBHandler.query(groupId)) {
                title = groupDBHandler.query(groupId).xlGroupName;
            } else {
                title = "群消息";
            }
        }
        if (extras.isPrivate()){
            message = "[私密消息]";
        }else {
            switch (msgType) {
                case 0:     //不限
                    if (TextUtils.isEmpty(title)) {
                        title = "乡邻";
                    }
                    if (TextUtils.isEmpty(message)) {
                        message = "收到一条新消息";
                    }
                    break;
                case MessageChatAdapter.TEXT:     //文字消息

                    break;
                case MessageChatAdapter.IMAGE:     //图片
                    message = "[图片]";
                    break;
                case MessageChatAdapter.VOICE:     //音频
                    message = "[音频]";
                    break;
                case MessageChatAdapter.VIDEO:     //视频
                    message = "[视频]";
                    break;
                case MessageChatAdapter.IDCARD:
                    message = "[名片]";
                    break;
                case MessageChatAdapter.REDBUNDLE:
                    message = "[红包]";
                    break;
                case MessageChatAdapter.WEBSHOPPING:
                    message = "[网页]";
                    break;
                case MessageChatAdapter.NEWSCARD:
                    message = "[外部网页]";
                    break;
                case 11:    //系统消息
                    if (TextUtils.isEmpty(title)) {
                        title = "乡邻";
                    }
                    if (TextUtils.isEmpty(message)) {
                        message = "收到一条系统消息";
                    }
                    break;
                default:
                    break;
            }
        }
        JpushUtil.clearLocalNotifications();    //移除所有本地通知

        if (sendType == BorrowConstants.CHATTYPE_GROUP) {//群通知
            groupNotice(title);
        } else if (sendType == BorrowConstants.CHATTYPE_SINGLE) {
            JpushUtil.addLocalNotification(title, message, appData);
        }


    }

    /**
     * 群通知1分钟发一次
     *
     * @param title
     */
    private void groupNotice(String title) {
        LogCatLog.i(TAG, "--------------IsInNoticeTime--------------" + PersonSharePreference.getIsInNoticeTime());
        if (!PersonSharePreference.getIsInNoticeTime().equals("false")) {
            LogCatLog.i(TAG, "--------------发送通知--------------");
            if (PersonSharePreference.getIsClickNotify().equals("isclick")
                    || PersonSharePreference.getIsInNoticeTime().equals("true")) {//通知被点击了
                JpushUtil.addLocalNotification(title, message, appData);
                PersonSharePreference.setIsClickNotify("notclick");
            } else {
                MessageDBHandler messageDBHandler = new MessageDBHandler(mContext);
                int count = messageDBHandler.queryMsgWithState(3,99).size();
                if (count > 99) {
                    JpushUtil.addLocalNotification("99+", appData);
                } else if (count > 0) {
                    JpushUtil.addLocalNotification(count + "", appData);
                }
            }
            SingleThreadExecutor.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    PersonSharePreference.setIsInNoticeTime("false");
                    SystemClock.sleep(60 * 1000);
                    if (!PersonSharePreference.getIsClickNotify().equals("isclick")
                            || !PersonSharePreference.getIsInNoticeTime().equals("true")) {//通知没有点击了
                        while (msgKey != PersonSharePreference.getMsgKey()) {
                            MessageDBHandler messageDBHandler = new MessageDBHandler(mContext);
                            int count = messageDBHandler.queryMsgWithState(3,99).size();
                            if (count > 99) {
                                JpushUtil.addLocalNotification("99+", appData);
                            } else if (count > 0) {
                                JpushUtil.addLocalNotification(count + "", appData);
                            }
                            PersonSharePreference.setMsgKey(msgKey);
                            PersonSharePreference.setIsInNoticeTime("false");
                            SystemClock.sleep(60 * 1000);
                            if (PersonSharePreference.getIsClickNotify().equals("isclick")
                                    || PersonSharePreference.getIsInNoticeTime().equals("true")) {//通知被点击了
                                break;
                            }
                        }
                    }
                    PersonSharePreference.setIsInNoticeTime("true");
                }
            });
        }
    }
}
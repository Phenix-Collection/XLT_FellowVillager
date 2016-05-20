package com.xianglin.fellowvillager.app.longlink.handler;

import android.content.Context;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.config.RepeatSendMessageTimeConfig;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 重发消息管理
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-20
 */
public class RepeatSendMessageManager {

    private RepeatSendMessageHandler repeatSendMessageHandler;
    private Context mContext;
    private static final String TAG = LongLinkConfig.TAG;

    public RepeatSendMessageManager(RepeatSendMessageHandler repeatSendMessageHandler, Context mContext) {
        this.repeatSendMessageHandler = repeatSendMessageHandler;
        this.mContext = mContext;
    }


    /**
     * 是否发送消息
     * 1: 普通文字消息间隔和音频消息间隔
     *
     * @param message
     * @return
     */
    public boolean isSendMessage(RepeatMessage message) {
        boolean isSendMessage = false;
        long dateTime = 0;// 设置发送时间 如果当前message 时间不为空 取时间，否则 取本地时间
        long localDate = System.currentTimeMillis();
        try{
            dateTime = Long.parseLong(message.getDateTime());
        }catch(Exception e){
            dateTime = System.currentTimeMillis();
            LogCatLog.d(TAG,"时间格式化失败");
        }
        switch (message.getMessageBean().msgType){

            case SendingMessageHandler.IMAGE:
            case SendingMessageHandler.AUDIO:
            case SendingMessageHandler.VIDEO:
                if (localDate - dateTime >= RepeatSendMessageTimeConfig.ISFILEMESSAGETIME1 ||
                        localDate - dateTime <= RepeatSendMessageTimeConfig.ISFILEMESSAGETIME2) { // 本地时间－ 之前消息发送时间  >= ＋－3000  可发送
                    isSendMessage = true;
                }
                return isSendMessage;
            case SendingMessageHandler.TEXT:
            case SendingMessageHandler.BUCARD:
            case SendingMessageHandler.RED:
            case SendingMessageHandler.ITEM:
            case SendingMessageHandler.NEWS:
                if (localDate - dateTime >= RepeatSendMessageTimeConfig.ISMESSAGETIME1 ||
                        localDate - dateTime <= RepeatSendMessageTimeConfig.ISMESSAGETIME2) { // 本地时间－ 之前消息发送时间  >= ＋－3000  可发送
                    isSendMessage = true;
                }
                return isSendMessage;
            default:
                return isSendMessage;
        }

    }


    /**
     * 更新普通消息次数
     *
     * @param message
     * @return
     */
    public boolean updateMessage(Map<String,RepeatMessage > messages,RepeatMessage message,int count) {
        try{
            // 进行发送次数更新
            count++;
            message.setMessageCount(count);// 设置当前消息发送次数 每一条消息具有30次 超过30次的消息 这条消息制为发送失败，ui上显示小红点
            message.setDateTime(System.currentTimeMillis() + "");// 设置当前消息发送时间
            if (messages != null)
                messages.put(message.getMessageBean().msgKey, message);
            LogCatLog.d(TAG, "message size" + messages.size());
            return true;
        }catch(Exception e){
            LogCatLog.e(TAG, "更新消息次数失败", e);
            return false;
        }
    }


    /**
     * 发送消息
     * @param message
     */
    public void countSendMessage(Map<String,RepeatMessage> messages,RepeatMessage message) {
        int count = message.getMessageCount();//获取当前消息发送次数
        if (message.getMessageBean().msgType == SendingMessageHandler.AUDIO
                || message.getMessageBean().msgType == SendingMessageHandler.IMAGE){
            messageHandler(messages,count,RepeatSendMessageTimeConfig.FILEMESSAGECOUNT,message);
        }else{
            messageHandler(messages,count,RepeatSendMessageTimeConfig.MESSAGEMACCOUNT,message);
        }
    }







    /**
     * 消息处理
     * @param count 当前次数
     * @param countOut 最大次数
     * @param message 当前消息
     */
    public void messageHandler(Map<String,RepeatMessage> messages,int count,int countOut,RepeatMessage message){
        if (count > countOut) {
            LogCatLog.d(TAG, "此条" +
                    "文件消息发送次数已超过" + RepeatSendMessageTimeConfig.FILEMESSAGECOUNT + "次，" +
                    "不参与重发\n" +
                    "消息key = {" + message.getMessageBean().msgKey + "}\n" +
                    "消息内容 = {" + message.getMessageBean().msgContent + "}\n");
            // 删除集合数据
            this.repeatSendMessageHandler.removeMessage(message.getMessageBean().msgKey);
            // 改变数据库的状态
          //  new MessageDBHandler(mContext).sendFailMsg(message.getMessageBean().msgKey, );

            MessageBean messageBean=message.getMessageBean();
            messageBean.msgStatus= BorrowConstants.MSGSTATUS_FAIL;
            MessageHandler.notifySendMsgStatus(messageBean);


        } else {
            new SendingMessageHandler(this.repeatSendMessageHandler).sendMessage(mContext, message);//发送消息
            if (updateMessage(messages, message, count)) {// 更新消息
                LogCatLog.d(TAG, "更新当前发送次数成功");
            } else {
                LogCatLog.d(TAG, "更新当前发送次数失败");
            }
        }
    }
    /**
     * 组合数据
     *
     * @param messages
     * @return
     */
    public boolean objToArrayAndSendMessage(Map<String, RepeatMessage> messages) {
        objToArray(messages);
        return false;
    }


    /**
     * 对象转集合数组
     *
     * @param messages
     * @return
     */
    public List<String> objToArray(Map<String, RepeatMessage> messages) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, RepeatMessage> messageEntry : messages.entrySet()) {
            String key = messageEntry.getKey();
            RepeatMessage repeatMessage = messageEntry.getValue();
            String message = new SendingMessageHandler(this.repeatSendMessageHandler).getMessage(mContext, repeatMessage);
            if (message == null) {
                LogCatLog.e(TAG, "message 为null 情况如下：1.发送文件，而非文字；2.发送出现错误");
            }else {
                list.add(message);
            }
        }
        return list;
    }


    /**
     * 停止重发消息的线程
     */
    public static  void stopRepeatSendMessage(){
        if (RepeatSendMessageHandler.isOnResume) {// 是可以进行触发
            if (RepeatSendMessageHandler.isOpenRepeat) {//  已经打开重发
                if (!RepeatSendMessageHandler.isRegister) {// 为false 进行关闭
                    RepeatSendMessageHandler.isOpenRepeat = false;
                }
            }
        }
    }


    /**
     * 启动重发消息进程
     */
     public static  void startRepeatSendMessage(){
          if (RepeatSendMessageHandler.isOnResume){// 是可以进程触发
              if (!RepeatSendMessageHandler.isOpenRepeat){ //已经打开重发
                  if (XLApplication.repeatSendMessageHandler == null){
                      XLApplication.initMessage();
                  }
                  RepeatSendMessageHandler.isOpenRepeat  = true;
                  new Thread(XLApplication.repeatSendMessageHandler).start();
              }
          }

     }


}

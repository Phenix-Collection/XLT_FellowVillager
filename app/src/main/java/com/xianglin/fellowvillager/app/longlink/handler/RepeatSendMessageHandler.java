package com.xianglin.fellowvillager.app.longlink.handler;


import android.content.Context;

import com.xianglin.fellowvillager.app.longlink.LongLinkUtils;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.config.RepeatSendMessageTimeConfig;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *   消息发送
 * 1:消息集合
 * 2:添加消息
 * 3:删除消息
 * 4:消息发送
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-28
 */
public class RepeatSendMessageHandler implements Runnable {

    private static final String TAG = LongLinkConfig.TAG;

    public static volatile boolean isRegister = false;
    public static  boolean  isOnResume ;// 是否开始从生命周期进行判断
    public static  boolean isOpenRepeat = true;// 是否打开重发
    private static int loopcount = 0;

    /**
     * 消息集合对象
     * String: key 消息key
     * RepeatMessage: 重发消息对象
     *
     */
    private  static Map<String, RepeatMessage>   messages = new ConcurrentHashMap<String, RepeatMessage>();

    private Context mContext;

    private  RepeatSendMessageManager repeatSendMessageManager;

    public RepeatSendMessageHandler(Context mContext) {
        this.mContext = mContext;
        repeatSendMessageManager = new RepeatSendMessageManager(this,mContext);
    }

    @Override
    public void run() {
        int loopTime;
        try {
            do {
                if (isRegister) {// 是否注册，如没有注册不进行轮训消息发送 ，注册之后进行消息发送
                    if (messages.size() != 0) {
                        looper();//轮询
                        loopTime = RepeatSendMessageTimeConfig.LOOPTIME;
                    } else {
                        loopTime = RepeatSendMessageTimeConfig.NULLLOOPTIME;
                    }
                }else{
                    LogCatLog.i(TAG, "this connect no register");
                    loopTime = RepeatSendMessageTimeConfig.ISREGISTERTIME;
                    if (!isRegister) {
                        LongLinkUtils.longLinkIsSuccess(this.mContext);
                    }
                }
                Thread.sleep(loopTime);
            } while (isOpenRepeat);
            LogCatLog.i(TAG, "looper colse");
        } catch (Exception e) {
            LogCatLog.e(TAG, "send message fail", e);
        }
    }
    /**
     * 轮询
     * message.size > 0 开始轮询
     */
    public  void looper() {
        for (Map.Entry<String, RepeatMessage> entry : messages.entrySet()) {
            String key = entry.getKey();
            RepeatMessage message = entry.getValue();
            if (message != null && key != null) {
                if(repeatSendMessageManager.isSendMessage(message)) {
                    // 可将消息进行发送
                    this.repeatSendMessageManager.countSendMessage(messages,message);
                } else {
                    LogCatLog.d(TAG, "===message no timeout ，no repeat send===\n" +
                            "message key = {" + message.getMessageBean().msgKey + "}\n" +
                            "message content = {" + message.getMessageBean().msgContent + "}\n");
                }
            } else {
                LogCatLog.d(TAG, "repeat message get fail,remove this message");
            }
        }
    }

    /**
     * 添加消息
     * 每次添加消息 进行消息的一次发送
     *
     * @param message
     */
    public void addMessage(RepeatMessage message) {

        synchronized (this) {

            if (messages != null) {
//                sizeOf(); TODO 后面再说
                messages.put(message.getMessageBean().msgKey, message);
                LogCatLog.d(TAG, "message date" + message.getDateTime());
                if (isRegister) {// 没有注册 不进行消息发送
                    if (message.getMessageCount() == 0) {

                        this.repeatSendMessageManager.countSendMessage(messages,message);

                    }
                }

            }else{
                LogCatLog.d(TAG,"消息为null ");
            }


        }

    }


    /**
     * 更新消息
     * @param message
     */
    public void updateMessage(RepeatMessage message){
        try{
            synchronized (this){
                if (messages != null)
                    messages.put(message.getMessageBean().msgKey, message);
                LogCatLog.d(TAG,"{updateMessage}message data"+message.getDateTime());
            }
        }catch(Exception e){
            LogCatLog.e(TAG,"update message fail",e);
        }

    }

    /**
     * 获取消息
     * @param key
     * @return
     */
    public  RepeatMessage getMessage(String key){
        try{
            synchronized (this){
                if (key != null){
                    return messages.get(key);
                }
            }
        }catch(Exception e){
            LogCatLog.e(TAG, "get message fail", e);
        }
        return null;
    }

    /**
     * 删除发送消息
     *
     * @param
     */
    public void removeMessage(String key) {


        try{
            synchronized (this) {
                if (messages != null)
                    messages.remove(key);

            }

        }catch(Exception e){
            LogCatLog.e(TAG,"remove message fail",e);
        }
    }

    /**
     * 删除全部消息
     */
    public  void removeAllMessage(){

        try{
            synchronized (this) {
                if (messages != null)
                    messages.clear();

            }

        }catch(Exception e){
            LogCatLog.e(TAG,"remove all message fail",e);
        }

    }

    /**
     * 获取最早的key
     * @return
     */
    public String getFirstKey(){
        try{
            for (Map.Entry<String,RepeatMessage> entry : messages.entrySet()){
                return entry.getKey();
            }
        }catch(Exception e){
            LogCatLog.e(TAG,"get first key exception  ",e);
        }
        return null;
    }

    /**
     * 检查之前size
     */
    public void sizeOf(){
        if (messages != null) {
            int count = messages.size();// 当前size
            if (count >= LongLinkConfig.MAX_MESSAGE_COUNT) {
                String key = getFirstKey();
                removeMessage(key);
            } else {
                LogCatLog.d(TAG, "data add");
            }
        }else{
            LogCatLog.d(TAG, "message list null");
        }
    }
}

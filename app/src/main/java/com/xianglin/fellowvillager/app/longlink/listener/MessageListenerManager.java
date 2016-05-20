package com.xianglin.fellowvillager.app.longlink.listener;

import com.xianglin.fellowvillager.app.model.ListenerManagerModel;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.LinkedList;
import java.util.List;

/**
 * 消息监听管理
 * 1:注册
 * 2:删除
 * 3:查询
 * 4:通知
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-30
 */
public class MessageListenerManager {

    /**
     * 用来注册监听对象
     */
    private static List<ListenerManagerModel> listenerManagerModels = new LinkedList<ListenerManagerModel>();

    private static volatile MessageListenerManager messageListenerManager;

    private static final String TAG = MessageListenerManager.class.getSimpleName();

    private MessageListenerManager() {

    }


    /**
     * 获取监听管理
     *
     * @return
     */
    public synchronized static MessageListenerManager getInstance() {

        if (messageListenerManager == null) {
            messageListenerManager = new MessageListenerManager();
        }

        return messageListenerManager;
    }

    public int getListenerManagerSize(){
        if (listenerManagerModels != null){
            return listenerManagerModels.size();
        }else{
            return 0;
        }

    }

    /**
     * 添加监听对象
     *
     * @param listenerManagerModel
     */
    public void attach(ListenerManagerModel listenerManagerModel) {

        listenerManagerModels.add(listenerManagerModel);
        LogCatLog.d(TAG, "添加监听对象" + listenerManagerModels.size());
    }

    /***
     * 删除监听对象
     *
     * @param listenerManagerModel
     */
    public void detach(ListenerManagerModel listenerManagerModel) {

        listenerManagerModels.remove(listenerManagerModel);
        LogCatLog.d(TAG, "删除监听对象" + listenerManagerModels.size());
    }

    /**
     * 查询聊天监听对象
     */
    private ListenerManagerModel queryChatListener(int stateCode) {

        for (ListenerManagerModel listenerManagerModel : listenerManagerModels) {
            // 监听类型

        }

        return null;
    }


    /**
     * 通知监听对象
     * @param md
     * @return
     */
    public void notifyListener(Md md) {

        LogCatLog.d(TAG, "通知监听对象" + listenerManagerModels.size());
        for (ListenerManagerModel listenerManagerModel : listenerManagerModels) {
            LogCatLog.d(TAG, "回调类型" + listenerManagerModel.listenerType.getStateCode()
                    + "回调类型消息" + listenerManagerModel.listenerType.getMsg());
            // 监听类型
            listenerManagerModel.messageListener.processPacket(md);
        }
    }


}

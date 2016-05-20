/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat.controller;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.longlink.XLNotifierEvent;
import com.xianglin.fellowvillager.app.longlink.listener.XLEventListener;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.fellowvillager.app.utils.NetUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.XLError;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * 聊天管理
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/27 20:09  XLXZ Exp $
 */
public class ChatManager {

    private static final String TAG = ChatManager.class.getSimpleName();
    private static ChatManager instance = new ChatManager();
    private Context context = XLApplication.getInstance();
    private MessageDBHandler mMessageDBHandler = new MessageDBHandler(context);


    public static synchronized ChatManager getInstance() {
        return instance;
    }



    /**
     * 发送消息
     * @param message
     * @param callBack
     */
    public void sendMessage(MessageBean message, MessageCallBack callBack) {
                MessageCallBack mCallBack = this.getInnerCallBack(callBack, message);
        if (!NetUtil.checkNetWork(context)) {
            asyncCallback(mCallBack, XLError.NONETWORK_ERROR, context.getString(R.string.network_unavailable));
        } else {
            int error = checkMessageError(message);

            if (error != 0) {


                message.msgStatus = BorrowConstants.MSGSTATUS_FAIL;
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBSQLUtil.MSG_STATUS, String.valueOf(message.msgStatus));
                mMessageDBHandler.updateMsgState(message.msgKey, contentValues);

                if (mCallBack != null) {
                    asyncCallback(mCallBack, error, "消息发送错误");
                }
            } else if (message.getChatType() != MessageBean.ChatType.GroupChat) {
                sendNetMessage(message, mCallBack);
            } else {
                sendNetGroupMessage(message, mCallBack);
            }

        }
    }

    /**保存已读消息到缓存
     * @param messageBean   消息体
     */
    public void addMessage(MessageBean messageBean) {
        MessageHandler.getInstance().addMessage(messageBean);
    }

    /***保存消息到缓存
     * @param messageBean 消息体
     * @param isunread  是否已读
     */
    public void addMessage(MessageBean messageBean, boolean isunread) {
        MessageHandler.getInstance().addMessage(messageBean, isunread);
    }


    /** 发送单聊消息
     * @param message
     * @param callBack
     */
    private void sendNetMessage(MessageBean message, MessageCallBack callBack) {
        try {
            if (message.msgKey == null) {
                message.msgKey = Utils.getUniqueMessageId();
            }

            message.msgStatus = BorrowConstants.MSGSTATUS_SEND;
           // message.from = ContactManager.getInstance().getCurrentFigure();

            MessageHandler.getInstance().saveMessage(message);


            addToMessage(message);
        } catch (Exception e) {
            message.msgStatus = BorrowConstants.MSGSTATUS_FAIL;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBSQLUtil.MSG_STATUS, String.valueOf(message.msgStatus));
            mMessageDBHandler.updateMsgState(message.msgKey, contentValues);
            e.printStackTrace();
            asyncCallback(callBack, -2, e.getLocalizedMessage());
        }

    }

    /** 发送群消息
     * @param messageBean
     * @param callBack
     */
    public void sendNetGroupMessage(MessageBean messageBean, MessageCallBack callBack) {
        try {

            if (messageBean.msgKey == null) {
                messageBean.msgKey = Utils.getUniqueMessageId();
            }
            messageBean.msgStatus = BorrowConstants.MSGSTATUS_SEND;
          //  messageBean.from = ContactManager.getInstance().getCurrentFigure();

            MessageHandler.getInstance().saveMessage(messageBean);

            addToMessage(messageBean);
        } catch (Exception var5) {
            messageBean.msgStatus = BorrowConstants.MSGSTATUS_FAIL;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBSQLUtil.MSG_STATUS, String.valueOf(messageBean.msgStatus));
            mMessageDBHandler.updateMsgState(messageBean.msgKey, contentValues);

            var5.printStackTrace();
            if (callBack != null) {
                asyncCallback(callBack, -2, var5.getLocalizedMessage());
            }
        }

    }

    /**
     * 发送消息到服务器
     *
     * @param bean 消息bean
     */
    private void addToMessage(
            MessageBean bean) {

        if(bean.msgType==MessageChatAdapter.IMAGE)
            return;
        PersonSharePreference.setChatFidCount(bean.figureId);// 当前角色ID的消息次数
        RepeatMessage repeatMessage = new RepeatMessage();
        repeatMessage.setToChatId(bean.xlID);// 联系人ID
        repeatMessage.setChatType(bean.chatType.getChatType());// 聊天类型
        repeatMessage.setDateTime(bean.msgCreatedate);//设置当前发送时间
        repeatMessage.setMessageBean(bean);// 消息
        XLApplication.repeatSendMessageHandler.addMessage(repeatMessage);

    }

    /** 同步调用消息体的回调
     * @param callBack
     * @param message
     * @return
     */
    private MessageCallBack getInnerCallBack(final MessageCallBack callBack, final MessageBean message) {
        MessageCallBack mCallBack = new MessageCallBack() {
            public void onSuccess() {
                if (callBack != null) {
                    callBack.onSuccess();
                }

                if (message.messageStatusCallBack != null) {
                    message.messageStatusCallBack.onSuccess();
                }

            }

            public void onProgress(int var1x, String var2x) {
                if (callBack != null) {
                    callBack.onProgress(var1x, var2x);
                }

                if (message.messageStatusCallBack != null) {
                    message.messageStatusCallBack.onProgress(var1x, var2x);
                }

            }

            public void onError(int var1x, String var2x) {
                if (callBack != null) {
                    callBack.onError(var1x, var2x);
                }

                if (message.messageStatusCallBack != null) {
                    message.messageStatusCallBack.onError(var1x, var2x);
                }
            }
        };
        return mCallBack;
    }

    private static void asyncCallback(final MessageCallBack callBack, final int errorCode, final String errorInfo) {
        if (callBack != null) {
            (new Thread() {
                public void run() {
                    callBack.onError(errorCode, errorInfo);
                }
            }).start();
        }
    }

    /** 检查多媒体信息
     * @param messageBean
     * @return
     */
    private static int checkMessageError(MessageBean messageBean) {
        String content;
        File file;
        if (messageBean.msgType == MessageChatAdapter.FILE) {
            content = messageBean.msgContent;
            file = new File(content);
            if (!file.exists()) {
                LogCatLog.e(TAG, "文件不存在" + content);
                return XLError.FILE_NOT_FOUND;
            }
            if (file.length() == 0L) {
                LogCatLog.e(TAG, "文件大小为 0:" + content);
                return XLError.INVALID_FILE;
            }
        } else if (messageBean.msgType == MessageChatAdapter.IMAGE) {
            content = messageBean.msgContent;
            file = new File(content);
            if (!file.exists()) {
                LogCatLog.e(TAG, "图片不存在" + content);
                return XLError.FILE_NOT_FOUND;
            }

            if (file.length() == 0L) {
                LogCatLog.e(TAG, "图片大小为 0:" + content);
                return XLError.INVALID_FILE;
            }
        } else if (messageBean.msgType == MessageChatAdapter.VOICE) {
            content = messageBean.msgContent;
            file = new File(content);
            if (!file.exists()) {
                LogCatLog.e(TAG, "录音不存在" + content);
                return XLError.FILE_NOT_FOUND;
            }

            if (file.length() == 0L) {
                LogCatLog.e(TAG, "录音大小为 0" + content);
                return XLError.INVALID_FILE;
            }
        }
        return 0;
    }

    /** 获取会话
     * @param username 聊天对象id
     * @param isGroup  是否是群
     * @return
     */
    public XLConversation getConversation(String username, boolean isGroup) {
        return isGroup ? MessageHandler.getInstance().getConversation(username, isGroup, XLConversation.ConversationType
                .GroupChat) : MessageHandler.getInstance().getConversation(username, isGroup, XLConversation
                .ConversationType.Chat);
    }

    /**
     * 加载用户的全部会话
     * @param messageCallBack
     */
    public void loadConversation(final MessageCallBack messageCallBack ) {
       MessageHandler.getInstance(). asyncloadAllConversations(messageCallBack,1000);
    }

    /**
     * 监听依赖于聊天消息的事件
     * @param xlEventListener 监听器
     * @param events  事件类型
     */
    public void registerEventListener(XLEventListener xlEventListener, XLNotifierEvent.Event[] events) {
        MessageHandler.getInstance().registerEventListener(xlEventListener, events);
    }

    /** 注销监听器
     * @param xlEventListener
     */
    public void unregisterEventListener(XLEventListener xlEventListener) {
        MessageHandler.getInstance().removeEventListener(xlEventListener);
    }


    /**获取会话列表
     * @return
     */
    public Hashtable<String,XLConversation> getAllConversations() {
        return MessageHandler.getInstance().getConversations();
    }

    /**
     * 获取与某个联系人的所有会话,包括私密与普通
     * @param toChatId 联系人figureId
     * @param currentFigureId 当期角色figureId
     * @return
     */
    public List<MessageBean> getAllMessageAboutSomeOne(
            String toChatId,
            String currentFigureId
    ) {
        Hashtable<String, XLConversation> conversations = getAllConversations();
        if (conversations == null) {
            return null;
        }
        String contactId = ContactDBHandler.getContactId(toChatId, currentFigureId);
        if (TextUtils.isEmpty(contactId)) {
            return null;
        }
        XLConversation xlConversation = conversations.get(contactId);
        if (xlConversation == null) {
            return null;
        }
        List<MessageBean> allMessages = xlConversation.getAllMessages();
        return allMessages;
    }
}

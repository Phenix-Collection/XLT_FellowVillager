/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.longlink;

import android.text.TextUtils;

import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.MessageBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 抽象出每个聊天会话
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/21 17:43  XLXZ Exp $
 */
public class XLConversation {

    private static final String TAG = "conversation";
    List<MessageBean> messages;

    private int unreadMsgCount = 0;

    private String chatID;

    private long msgCount;

    private long msgSendCount;

    private long msgReceiveCount;

    private boolean isKeywordSearchEnabled;

    private boolean isGroup = false;

    private Contact opposite = null;

    private ConversationType type;

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public long getMsgSendCount() {
        return msgSendCount;
    }

    public long getMsgReceiveCount() {
        return msgReceiveCount;
    }

    public void setMsgReceiveCount(long msgReceiveCount) {
        this.msgReceiveCount = msgReceiveCount;
    }

    public void setMsgSendCount(long msgSendCount) {
        this.msgSendCount = msgSendCount;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public XLConversation(String chatID) {
        this.type = ConversationType.Chat;
        this.msgCount = 0L;
        this.isKeywordSearchEnabled = false;
        this.isGroup = false;  // XLConversation.getInstance().getGroup(var1) != null;
        this.chatID = chatID;
        if (this.messages == null) {
            this.messages = Collections.synchronizedList(new ArrayList());
        }
        if (this.unreadMsgCount <= 0) {
            this.unreadMsgCount = 0;
        }
    }

    public XLConversation(String chatID, List<MessageBean> messages, ConversationType conversationtype
            , Long msgCount, Long msgSendCount, Long msgReceiveCount, int unreadMsgCount) {
        this.type = ConversationType.Chat;
        this.msgCount = 0L;
        this.isKeywordSearchEnabled = false;
        this.chatID = chatID;
        this.type = conversationtype;
        this.isGroup = conversationtype != ConversationType.Chat;
        if (messages != null) {
            this.messages = Collections.synchronizedList(new ArrayList());
            this.messages.addAll(messages);
        }
        if (this.unreadMsgCount <= 0) {
            this.unreadMsgCount = 0;
        }

        this.msgCount = msgCount;
        this.msgSendCount = msgSendCount;
        this.msgReceiveCount = msgReceiveCount;
        this.unreadMsgCount = unreadMsgCount;
    }

    /**
     * 保存消息
     *
     * @param message
     * @param isunread 是否是已读消息
     */
    void addMessage(MessageBean message, boolean isunread) {
        if (message.getChatType() == MessageBean.ChatType.GroupChat) {
            this.isGroup = true;
        }

        if (this.messages.size() > 0) {
            MessageBean messageBean = (MessageBean) this.messages.get(this.messages.size() - 1);
            if (message.msgKey != null && messageBean.msgKey != null
                    && message.msgKey.equals(messageBean.msgKey)) {
                return;
            }
        }

        boolean isExist = false;
        Iterator messageBeanIterator = this.messages.iterator();

        while (messageBeanIterator.hasNext()) {
            MessageBean messageBean = (MessageBean) messageBeanIterator.next();
            if (messageBean.msgKey.equals(message.msgKey)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            this.messages.add(message);
            ++this.msgCount;
            if (message.direct == MessageBean.Direct.SEND) {
                ++msgSendCount;
            }
            if (message.direct == MessageBean.Direct.RECEIVE) {
                ++msgReceiveCount;
            }
            if (message.direct == MessageBean.Direct.RECEIVE && isunread) {
                ++this.unreadMsgCount;
                // this.saveUnreadMsgCount(this.unreadMsgCount);
            }
        }
    }

    /**
     * 这是id
     *
     * @return
     */
    public String getChatID() {
        return this.chatID;
    }

    public List<MessageBean> getAllMessages() {
        return messages;
    }

    public List<MessageBean> getAllMessages(String firstKey) {
        if(TextUtils.isEmpty(firstKey)){
            return messages;
        }
        int start=0;
        for(int i=0;i<messages.size();i++){
            if(messages.get(i).msgKey.equals(firstKey)){
                MessageBean bean=messages.get(i);
                start=i;
                break;
            }
        }
        List<MessageBean> list= messages.subList(start,messages.size());
        return list;
    }

//    List<MessageBean> msgList = Collections.synchronizedList(new ArrayList());
//    public List<MessageBean> getAllValidMessages() {
//        MessageBean messageBean;
//        msgList = messages;
//        for (int i = 0; i < messages.size(); i++) {
//            messageBean = messages.get(i);
//            if (messageBean.isPrivate() && messageBean.isExpired) {
//                msgList.remove(messageBean);
//            }
//        }
//        return msgList;
//    }

/*    public static int CHATTYPE_SINGLE = 0;//点对点
    public static int CHATTYPE_GROUP =1;//群聊
    public static int CHATTYPE_SYS =0;//系统消息 暂时改成0*/

    public static enum ConversationType {
        Chat,
        GroupChat,
        SYS;

        private ConversationType() {
        }
    }

    /**
     * 根据消息类型获取聊天类型
     *
     * @param chatType 消息类型
     * @return
     */
    public static ConversationType msgType2ConversationType(MessageBean.ChatType chatType) {
        return chatType == MessageBean.ChatType.Chat ?
                ConversationType.Chat : (chatType == MessageBean.ChatType.GroupChat
                ? ConversationType.GroupChat : ConversationType.Chat);
    }

    public MessageBean getLastMessage() {
        return this.messages.size() == 0 ? null : (MessageBean) this.messages.get(this.messages.size() - 1);
    }
}

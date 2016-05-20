package com.xianglin.fellowvillager.app.model;

/**
 * 重发消息对象
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-28
 */
public class RepeatMessage {

    /**
     * 具体消息对象
     */
    private MessageBean messageBean;

    /**
     * 接收人的ID
     */
    private String toChatId;

    /**
     * 聊天类型
     */
    private int chatType;

    /**
     * 消息计数
     * 满30 这条消息在集合删掉
     */
    private int messageCount = 0;

    /**
     * 发送时间
     */
    private String dateTime;


    /**
     * 发送状态
     */
    private int fileSendState = DEF;

    public static final int DEF = 0x0000;
    /**
     * 文件发送成功，删除上传文件指令
     */
    public static final int  SUCCESS  = 0x0001;
    /**
     * 文件发送失败，进行重发
     */
    public static final int FAILURE = 0x0002;
    /**
     * 处理中
     */
    public static final int HANDLEING = 0x0003;

    public RepeatMessage (){

    }

    public MessageBean getMessageBean() {
        return messageBean;
    }

    public RepeatMessage setMessageBean(MessageBean messageBean) {
        this.messageBean = messageBean;
        return this;
    }

    public String getToChatId() {
        return toChatId;
    }

    public RepeatMessage setToChatId(String toChatId) {
        this.toChatId = toChatId;
        return this;
    }

    public int getChatType() {
        return chatType;
    }

    public RepeatMessage setChatType(int chatType) {
        this.chatType = chatType;
        return this;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public RepeatMessage setMessageCount(int messageCount) {
        this.messageCount = messageCount;
        return this;
    }

    public String getDateTime() {
        return dateTime;
    }

    public RepeatMessage setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public int getFileSendState() {
        return fileSendState;
    }

    public RepeatMessage setFileSendState(int fileSendState) {
        this.fileSendState = fileSendState;
        return this;
    }

    @Override
    public String toString() {
        return "RepeatMessage{" +
                "messageBean=" + messageBean +
                ", toChatId='" + toChatId + '\'' +
                ", chatType=" + chatType +
                ", messageCount=" + messageCount +
                '}';
    }
}

package com.xianglin.fellowvillager.app.chat.model;

/**
 *
 * 消息类型
 * Created by zhanglisan on 16/3/31.
 */
public enum MessageType {
    /**接收普通文本*/
    MESSAGE_TYPE_RECV_TXT(0),
    /**发送普通文本*/
    MESSAGE_TYPE_SENT_TXT(1),
    /**发送图片*/
    MESSAGE_TYPE_SENT_IMAGE(2),
    /**接收图片*/
    MESSAGE_TYPE_RECV_IMAGE(3),
    /**发送语音*/
    MESSAGE_TYPE_SENT_VOICE(4),
    /**接收语音*/
    MESSAGE_TYPE_RECV_VOICE(5),
    /**发送名片*/
    MESSAGE_TYPE_SENT_IDCARD(6),
    /**接收名片*/
    MESSAGE_TYPE_RECV_IDCARD(7),
    /**发送商品链接卡片*/
    MESSAGE_TYPE_SENT_WEBSHOPPING(8),
    /**接收商品链接卡片*/
    MESSAGE_TYPE_RECV_WEBSHOPPING(9),
    /**发送系统消息*/
    MESSAGE_TYPE_SENT_SYS(10),
    /**接收系统消息*/
    MESSAGE_TYPE_RECV_SYS(11);

    private int value = -1;

    MessageType(int value) {
        this.value = value;
    }

    public static MessageType valueOf(int value) {
        switch (value) {
            case 0:
                return MESSAGE_TYPE_RECV_TXT;
            case 1:
                return MESSAGE_TYPE_SENT_TXT;
            case 2:
                return MESSAGE_TYPE_SENT_IMAGE;
            case 3:
                return MESSAGE_TYPE_RECV_IMAGE;
            case 4:
                return MESSAGE_TYPE_SENT_VOICE;
            case 5:
                return MESSAGE_TYPE_RECV_VOICE;
            case 6:
                return MESSAGE_TYPE_SENT_IDCARD;
            case 7:
                return MESSAGE_TYPE_RECV_IDCARD;
            case 8:
                return MESSAGE_TYPE_SENT_WEBSHOPPING;
            case 9:
                return MESSAGE_TYPE_RECV_WEBSHOPPING;
            case 10:
                return MESSAGE_TYPE_SENT_SYS;
            case 11:
                return MESSAGE_TYPE_RECV_SYS;
            default:
                return null;

        }
    }

    public int value() {
        return this.value;
    }
}

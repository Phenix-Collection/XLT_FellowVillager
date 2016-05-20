package com.xianglin.fellowvillager.app.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.MessageCallBack;
import com.xianglin.fellowvillager.app.chat.controller.PrivateMessageCallBack;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 消息体
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class MessageBean implements Serializable ,Cloneable {




    /**
     * 乡邻联系人ID
     */
    public String xlID;

    public String msgLocalKey;
    /**
     * 乡邻联系人昵称
     */
    public String xlName;

    /**
     * 乡邻联系人图像
     */
    public String xlImagePath;

    /**
     * 乡邻联系人备注
     */
    public String xlReMarks;

    public String file_id;//本地头像的文件id

    public long fileLength;// 文件长度
    /**
     * 群成员id
     */
    public String xlgroupmemberid;

    /**
     * 消息Key
     */

    public String msgKey;

    /**
     * 消息Key
     */
    public String imageSize;

    /**
     * 消息类型
     * 0x11 文字
     * 0x22 语音
     * 0x33 图片
     * 0x44 文件
     */
    @Deprecated
    public int msgType;

    /*    点对点
         d)	消息被拒绝通知<黑名单> sendType=0 noticeType=1
         e)	用户信息更新通知        sendType=0 noticeType=2
         f)	消息被拒绝通知<未初始化>   sendType=0 noticeType=3
         g)	消息被拒绝通知<未登陆>    sendType=0 noticeType=4
         h)	Device信息检查不通过通知  sendType=0 noticeType=5

        点对群
         a)	用户群信息更新通知  sendType=1 noticeType=1
         b)	新用户主动加入群通知    sendType=1 noticeType=2
         c)	群信息更新通知         sendType=1 noticeType=3
         d)	群解散通知             sendType=1 noticeType=4
         e)	用户被动退出群通知     sendType=1 noticeType=5
         f)	用户主动退出群通知     sendType=1 noticeType=6
         g)	发送群消息被拒绝通知    sendType=1 noticeType=7
         */
    public int noticeType; //通知类型


    /**
     * 消息内容
     */
    public String msgContent;

    /**
     * 消息状态
     * 0x1105 发送成功
     * 0x2205 发送失败
     * 0x3305 已读
     * 0x4405 未读
     */
    @Deprecated
    public int msgStatus;

    public int isplayed;//是否播放 1 播发过,0未播放, 2播放完毕

    /**
     * 消息时间 服务器时间 发送成功后的 yyyy-dd....
     */
    public String msgDate;
    /*
    * 消息时间 插入数据库的本地时间戳
    * */
    public String msgCreatedate;

    public String thumbnail;// 缩略图

    public String recordlength;// 录音长度

    public int msgcurrent;// 一条消息在listview中显示的位置

    /**
     * 消息Key
     */
    public String msg;

    /**
     * 最后一次点击的时间
     */
    public String lasttime;

    public NameCardBean idCard; //名片

    public GoodsDetailBean goodsCard;//商品详情

    public NewsCard newsCard;//新闻

    /**
     * 联系人角色id
     */
    public String figureUsersId;//

    /**
     * 当前角色id
     */
    public String figureId;//
    /**
     * 私密消息时间
     */
    public Integer lifetime = -1;//

    /**
     * 私密消息接时间
     */
    public String privateDate ;//

    /**
     * 返回卡片,商品,红包类型
     *
     * @return T
     */
    public Object getMsgTypeBean() {
        Object t = null;
        switch (msgType) {
            case MessageChatAdapter.IDCARD://名片
                t = idCard;
                break;
            case MessageChatAdapter.WEBSHOPPING://卡片
                t = goodsCard;
                break;
            case MessageChatAdapter.NEWSCARD://卡片
                t = newsCard;
                break;
        }
        return t;
    }

    public MessageBean(Builder builder) {
        this.msgKey = builder.msgKey;
        this.msgType = builder.msgType;
        this.msgContent = builder.msgContent;
        this.msgStatus = builder.msgStatus;
        this.msgDate = builder.msgDate;
        this.thumbnail = builder.thumbnail;
        this.recordlength = builder.recordlength;
        this.xlgroupmemberid = builder.xlgroupmemberid;
        this.xlID = builder.xlID;
        this.xlName = builder.xlName;
        this.xlImagePath = builder.xlImagePath;
        this.xlReMarks = builder.xlReMarks;
        this.file_id = builder.file_id;
        this.imageSize = builder.imageSize;
        this.isplayed = builder.isplayed;
        this.msgcurrent = builder.msgcurrent;
        this.noticeType = builder.noticeType;
        this.fileLength = builder.fileLength;
        this.msgLocalKey = builder.msgLocalKey;
        this.lasttime = builder.lasttime;
        this.msgCreatedate = builder.msgCreatedate;
        this.figureId = builder.figureId;
        this.figureUsersId = builder.figureUsersId;
        this.chatType = builder.chatType;
        this.lifetime = builder.lifetime;
        if (builder.direct == null) {
            this.direct = Direct.SEND;
        } else {
            this.direct = builder.direct;
        }

    }

    public static class Builder {

        private String thumbnail;// 缩略图

        private String recordlength;// 录音长度
        private String msgLocalKey;//

        private int isplayed;//是否播放 1 播发过,0未播放
        private long fileLength;// 文件长度
        private String lasttime;//
        private String figureId;//
        /**
         * 消息Key
         */
        private String msgKey;
        private String xlgroupmemberid;
        private int msgcurrent;
        private int noticeType;
        private MessageBean.Direct direct;
        private MessageBean.ChatType chatType;

        /**
         * 消息类型
         * 0x11 文字
         * 0x22 语音
         * 0x33 图片
         * 0x44 文件
         */
        private int msgType;

        /**
         * 消息内容
         */
        private String msgContent;

        /**
         * 消息状态
         * 0x110 发送成功
         * 0x220 发送失败
         * 0x330 已读
         * 0x440 未读
         */
        private int msgStatus;

        /**
         * 消息时间 服务器时间 发送成功后的 yyyy-dd....
         */
        private String msgDate;
        /*
        * 消息时间 插入数据库的本地时间戳
        * */
        private String msgCreatedate;

        private String imageSize;

        /**
         * 乡邻联系人ID
         */
        private String xlID;

        /**
         * 乡邻联系人昵称
         */
        private String xlName;

        /**
         * 乡邻联系人图像
         */
        private String xlImagePath;

        /**
         * 乡邻联系人备注
         */
        private String xlReMarks;

        private String file_id;//本地头像的文件id
        private String figureUsersId;

        private Integer lifetime;

        public Builder lifeTime(Integer lifetime){
            this.lifetime = lifetime;
            return this;
        }
        public Builder xlID(String xlID) {
            this.xlID = xlID;
            return this;
        }
        public Builder chatType(MessageBean.ChatType chatType) {
            this.chatType = chatType;
            return this;
        }
        public Builder figureUsersId(String figureUsersId) {
            this.figureUsersId = figureUsersId;
            return this;
        }

        public Builder xlName(String xlName) {
            this.xlName = xlName;
            return this;
        }

        public Builder figureId(String figureId) {
            this.figureId = figureId;
            return this;
        }

        public Builder direct(MessageBean.Direct direct) {
            this.direct = direct;
            return this;
        }

        public Builder xlImagePath(String xlImagePath) {
            this.xlImagePath = xlImagePath;
            return this;
        }

        public Builder msgLocalKey(String msgLocalKey) {
            this.msgLocalKey = msgLocalKey;
            return this;
        }

        public Builder lasttime(String lasttime) {
            this.lasttime = lasttime;
            return this;
        }

        public Builder xlReMarks(String xlReMarks) {
            this.xlReMarks = xlReMarks;
            return this;
        }

        public Builder file_id(String file_id) {
            this.file_id = file_id;
            return this;
        }

        public Builder fileLength(long fileLength) {
            this.fileLength = fileLength;
            return this;
        }

        public Builder msgKey(String msgKey) {
            this.msgKey = msgKey;
            return this;
        }

        public Builder msgCreatedate(String msgCreatedate) {
            this.msgCreatedate = msgCreatedate;
            return this;
        }

        public Builder msgType(int msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder msgContent(String msgContent) {
            this.msgContent = msgContent;
            return this;
        }

        public Builder msgStatus(int msgStatus) {
            this.msgStatus = msgStatus;
            return this;
        }

        public Builder msgDate(String msgDate) {
            this.msgDate = msgDate;
            return this;
        }

        public Builder thumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder recordlength(String recordlength) {
            this.recordlength = recordlength;
            return this;
        }

        public Builder xlgroupmemberid(String xlgroupmemberid) {
            this.xlgroupmemberid = xlgroupmemberid;
            return this;
        }

        public Builder imageSize(String imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        public Builder isplayed(int isplayed) {
            this.isplayed = isplayed;
            return this;
        }

        public Builder msgcurrent(int msgcurrent) {
            this.msgcurrent = msgcurrent;
            return this;
        }

        public Builder noticeType(int noticeType) {
            this.noticeType = noticeType;
            return this;
        }

        public MessageBean build() {
            return new MessageBean(this);
        }

    }

    //-----------------------------------

    public MessageBean(int type) {


        this.isAcked = false;
        this.isDelivered = false;
        this.chatType = MessageBean.ChatType.Chat;
        this.progress = 0;
        this.unread = true;
        this.offline = false;
        this.error = 0;

        this.msgType=type;
        this.msgStatus= BorrowConstants.MSGSTATUS_SEND;

        this.msgCreatedate = System.currentTimeMillis() + "";
    }

    public String from;
    public String to;

    public transient boolean unread;
    public transient boolean offline;

    /**
     * 下载进度,不用缓存
     */
    public transient int progress;
    public boolean isAcked;
    public boolean isDelivered;



    public MessageBean.Direct direct;
   // public MessageBean.Status status;
    public MessageBean.ChatType chatType;



    private int error;

    /**
     * 消息状态改变回调
     */
    public MessageCallBack messageStatusCallBack;

    public PrivateMessageCallBack getBigImageMessageStatusCallBack() {
        return bigImageMessageStatusCallBack;
    }

    public void setBigImageMessageStatusCallBack(PrivateMessageCallBack bigImageMessageStatusCallBack) {
        this.bigImageMessageStatusCallBack = bigImageMessageStatusCallBack;
    }

    /**
     * 大图回调
     */
    public PrivateMessageCallBack bigImageMessageStatusCallBack;

    /**
     * 私密消息计时回调
     */
    public PrivateMessageCallBack privateMessageCallBack;

    /**
     * 私密消息暂停计时
     */
    public boolean isPrivatePause;

    /**
     * 当前私密消息的生存剩余时间
     */
    public int currentlifetime;

    public void setMessageStatusCallback(MessageCallBack mCallBack) {
        this.messageStatusCallBack = mCallBack;
    }
    public void setPrivateMessageCallBack(PrivateMessageCallBack privateMessageCallBack) {
        this.privateMessageCallBack = privateMessageCallBack;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }


    public String getFrom() {
        return this.from == null ? null : this.from;
    }

    public String getTo() {
        return this.to == null ? null : this.to;
    }

    public MessageBean.ChatType getChatType() {
        return this.chatType;
    }

    public void setChatType(MessageBean.ChatType chatType) {
        this.chatType = chatType;
    }


    public static MessageBean createTxtSendMessage(String msgContent) {
        if (msgContent.length() > 0) {
            MessageBean messageBean = createSendMessage(MessageChatAdapter.TEXT);
            messageBean.msgContent = msgContent;
            return messageBean;
        } else {
            LogCatLog.e("msg", "消息内容长度大于0");
            
            return null;
        }
    }

    public static MessageBean createVoiceSendMessage(String voicePath,String voiceName,int voiceLength) {
            MessageBean messageBean = createSendMessage(MessageChatAdapter.VOICE);
            messageBean.msgContent = voicePath+voiceName;
            messageBean.recordlength=String.valueOf(voiceLength);
            return messageBean;
    }

    public static MessageBean createPictrueSendMessage(String bigImagePath,String smallImagePath,String imageSize) {
        MessageBean messageBean = createSendMessage(MessageChatAdapter.IMAGE);
        messageBean.msgContent = bigImagePath;
        messageBean.thumbnail=smallImagePath;
        messageBean.imageSize=imageSize;
        return messageBean;
    }

    public static MessageBean createIDCardSendMessage(final Contact contact) {
        MessageBean messageBean = createSendMessage(MessageChatAdapter.IDCARD);
        JSONObject contOjb = new JSONObject() {
            {
                try {
                    put("type", 0);
                    put("name", contact.getXlUserName());
                    put("figureId", contact.figureUsersId);
                    put("imgId", contact.file_id);
                    put("userId", contact.xlUserID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        messageBean.msgContent = contOjb.toString();
        NameCardBean nameCardBean = JSON.parseObject(messageBean.msgContent, NameCardBean
                .class);
        nameCardBean.setMsg_key(messageBean.msgKey);
        messageBean.idCard=nameCardBean;

        return messageBean;
    }

    public static MessageBean createGoodsSendMessage(final String goodsId, final String title,
                                                     final String imgURL, final String price, final String abstraction,
                                                     final String url) {
        MessageBean messageBean = createSendMessage(MessageChatAdapter.WEBSHOPPING);
        JSONObject contOjb = new JSONObject() {
            {
                try {
                    put("goodsId", goodsId);
                    put("name", title);
                    put("imgURL", imgURL);
                    put("price", price);
                    put("abstraction", abstraction);
                    put("url", url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        messageBean.msgContent = contOjb.toString();
        GoodsDetailBean goodsDetailBean = JSON.parseObject(messageBean.msgContent, GoodsDetailBean
                .class);
        goodsDetailBean.setMsg_key(messageBean.msgKey);
        messageBean.goodsCard=goodsDetailBean;

        return messageBean;
    }

    public static MessageBean createNewsSendMessage(final String newsId, final String title,
                                                     final String imgURL, final String abstraction,
                                                     final String url) {
        MessageBean messageBean = createSendMessage(MessageChatAdapter.NEWSCARD);
        JSONObject contOjb = new JSONObject() {
            {
                try {
                    put("newsId", newsId);
                    put("title", title);
                    put("imgURL", imgURL);
                    put("summary", abstraction);
                    put("url", url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
        messageBean.msgContent = contOjb.toString();
        NewsCard newsCard = JSON.parseObject(messageBean.msgContent, NewsCard
                .class);
        newsCard.setMsg_key(messageBean.msgKey);
        messageBean.newsCard=newsCard;

        return messageBean;
    }



    /**
     * 创建一个发送消息模版
     * @param type
     * @return
     */
    private static MessageBean createSendMessage(int type) {

        MessageBean messageBean = new MessageBean(type);

        messageBean.direct = MessageBean.Direct.SEND;
        messageBean.from = null;
        messageBean.msgKey = Utils.getUniqueMessageId();
        messageBean.msgLocalKey =messageBean.msgKey;
        messageBean.lifetime=-1;
        messageBean.currentlifetime=messageBean.lifetime*2;
        messageBean.msgDate= Utils.timeStamp2Date(
                System.currentTimeMillis(),
                "yyyy-MM-dd HH:mm"
        );
        return messageBean;
    }

    /**
     * 设置聊天对象to
     * @param figureId
     */
    public void setTo(String figureId) {

        this.to = figureId;
    }
    /**
     * 设置聊天对象to
     * @param figureId
     */
    public void setFrom(String figureId) {
        this.from = figureId;
    }


    public enum ChatType {
        /*
                 public static int CHATTYPE_SINGLE = 0;//点对点
                 public static int CHATTYPE_GROUP =1;//群聊
                 public static int CHATTYPE_SYS =0;//系统消息 暂时改成2*/
        Chat(0),
        GroupChat(1),
        SYS(2);

        public int getChatType() {
            return chatType;
        }

        private int chatType;

        private ChatType(int chatType) {
            this.chatType=chatType;
        }
    }

    public static enum Direct {
        SEND,
        RECEIVE;
        private Direct() {
        }
    }

/*
    public static enum Status {

        */
/*        public static int MSGSTATUS_SEND =-1; //发送中
                public static int MSGSTATUS_OK =0; //发送成功
                public static int MSGSTATUS_FAIL =1; //发送失败
                public static int MSGSTATUS_READ = 2; //已读
                public static int MSGSTATUS_UNREAD =3; //未读
                public static int MSGSTATUS_RECEIVE =4; //接收中*//*

        CREATE(-1),
        SUCCESS(0),
        FAIL(1),
        READ(2),
        UNREAD(3),
        INPROGRESS(4);
        private int statusCode;

        public int getStatusCode() {
            return statusCode;
        }

        private Status(int i) {
            statusCode=i;
        }
    }
*/



    @Override
    public String toString() {
        return "MessageBean{" +
                "xlID='" + xlID + '\'' +
                ", msgLocalKey='" + msgLocalKey + '\'' +
                ", xlName='" + xlName + '\'' +
                ", xlImagePath='" + xlImagePath + '\'' +
                ", xlReMarks='" + xlReMarks + '\'' +
                ", file_id='" + file_id + '\'' +
                ", fileLength=" + fileLength +
                ", xlgroupmemberid='" + xlgroupmemberid + '\'' +
                ", msgKey='" + msgKey + '\'' +
                ", imageSize='" + imageSize + '\'' +
                ", msgType=" + msgType +
                ", noticeType=" + noticeType +
                ", msgContent='" + msgContent + '\'' +
                ", msgStatus=" + msgStatus +
                ", isplayed=" + isplayed +
                ", msgDate='" + msgDate + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", recordlength='" + recordlength + '\'' +
                ", msgcurrent=" + msgcurrent +
                ", msg='" + msg + '\'' +
                ", idCard=" + idCard +
                ", goodsCard=" + goodsCard +
                ", newsCard=" + newsCard +
                ", lifetime=" + lifetime +
                '}';
    }


    /**
     * 判断消息是已读还是未读
     *
     * @return MSGSTATUS_REA or MSGSTATUS_UNREAD
     */
    public static int getMsgState(int msgtype ,String toChatID) {
        //当前和用户xxx 正在聊天界面的消息  标记为已读
        int msg_state;

        if (!TextUtils.isEmpty(XLApplication.toChatId) && XLApplication.toChatId.equals(toChatID)) {


           // if(msgtype == MessageChatAdapter.IMAGE || msgtype == MessageChatAdapter.VOICE){
            if(msgtype == MessageChatAdapter.IMAGE ){

                msg_state = BorrowConstants.MSGSTATUS_INPROGRESS;

            }else{

                msg_state = BorrowConstants.MSGSTATUS_READ;

            }

        } else {

            msg_state = BorrowConstants.MSGSTATUS_UNREAD;
        }

        return msg_state;
    }

    public Object clone() {
        MessageBean o = null;
        try {
            o = (MessageBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }


    /**
     * 私密消息是否过期,已经过期的消息不在显示
     */
    public  boolean isExpired;

    /**
     * 是否为私密消息
     */
    public boolean isPrivate(){

        if (lifetime > 0)
            return true;

        return false;
    }
}

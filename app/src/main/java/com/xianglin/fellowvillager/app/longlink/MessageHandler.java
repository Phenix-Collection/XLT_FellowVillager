/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.longlink;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.chat.controller.MessageCallBack;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.handler.RepeatSendMessageHandler;
import com.xianglin.fellowvillager.app.longlink.listener.XLEventListener;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.fellowvillager.app.receiver.NoticeReceiver;
import com.xianglin.fellowvillager.app.rpc.remote.GroupMemberSync;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.SingleThreadExecutor;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.messagetask.ContactDetailsTask;
import com.xianglin.fellowvillager.app.utils.messagetask.GroupDetailsTask;
import com.xianglin.fellowvillager.app.utils.messagetask.MessageEventManagerThread;
import com.xianglin.fellowvillager.app.utils.messagetask.MessageEventTaskManager;
import com.xianglin.fellowvillager.app.utils.messagetask.PrivateLongTimeMessageTask;
import com.xianglin.fellowvillager.app.utils.messagetask.PrivateMessageTask;
import com.xianglin.fellowvillager.app.utils.messagetask.XLAssistantDetailsTask;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.mobile.common.utils.TimeTag;
import com.xianglin.mobile.framework.XiangLinApplication;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接受到消息后插入数据库
 *
 * @author pengyang
 * @version v 1.0.0 2015/12/11 16:57  XLXZ Exp $
 */
public class MessageHandler {

    private static final String TAG = LongLinkConfig.TAG;

    public Hashtable<String, MessageBean> getAllMessages() {
        return allMessages;
    }

    /**
     * 只初始化一次messagedb
     */
    private boolean isIntied = false;

    /**
     * 所有消息表
     */
    private Hashtable<String, MessageBean> allMessages = new Hashtable();

    public Hashtable<String, XLConversation> getConversations() {
        return conversations;
    }

    /**
     * 常用会话集合,默认添加到conversations
     */
    private Hashtable<String, XLConversation> conversations = new Hashtable();
/*
    *//**
     * 临时会话,当conversations查不到时,启用tempConversations进行管理
     *//*
    private Hashtable<String, XLConversation> tempConversations = new Hashtable();*/

    /**
     * 接受消息事件监听
     */
    private Hashtable<XLNotifierEvent.Event, List<XLEventListener>> filteredEventListeners = new Hashtable();

    /**
     * 会话是否加载完成
     */
    private boolean allConversationsLoaded = false;

    private MessageDBHandler messageDBHandler;
    private ContactDBHandler contactDBHandler;
    private GroupDBHandler mGroupDBHandler;
    private GroupMemberDBHandler mGroupMemberDBHandler;
    private MessageEventTaskManager eventTaskManager;
    private long XLID = PersonSharePreference.getUserID();

    private MessageHandler() {
        messageDBHandler = new MessageDBHandler(XLApplication.getInstance());
        contactDBHandler = new ContactDBHandler(XLApplication.getInstance());
        mGroupDBHandler = new GroupDBHandler(XLApplication.getInstance());
        mGroupMemberDBHandler = new GroupMemberDBHandler(XLApplication.getInstance());

        eventTaskManager = MessageEventTaskManager.getInstance();
        MessageEventManagerThread eventManagerThread = new MessageEventManagerThread();
        new Thread(eventManagerThread).start();
    }




    /**
     * 初始化会话信息
     *
     * @param messageCallBack 成功回调
     * @param msgCount        是否需要统计各个会话中的消息条数
     */
    public void asyncloadAllConversations(final MessageCallBack messageCallBack, final long msgCount) {

        if (!this.isIntied) {
            BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                @Override
                public void execute() {
                    try {
                        loadAllConversations(msgCount);
                        if (messageCallBack != null) {
                            messageCallBack.onSuccess();
                        }
                    } catch (Throwable e) {
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
                                .currentThread(), e);
                    }
                }
            });
            isIntied=true;
        }

    }

    /**
     * 初始化程序时 载入会话20条
     *
     * @param msgCount 统计消息条数
     */
    synchronized void loadAllConversations(long msgCount) {
        this.allConversationsLoaded = true;
        if (this.allConversationsLoaded) {
            this.conversations.clear();
            // this.tempConversations.clear();
            TimeTag timeTag = new TimeTag();
            timeTag.start();
            LogCatLog.d(TAG, "开始载入会话:");

            this.conversations = messageDBHandler.queryConversation(msgCount);

            Hashtable conversations = this.conversations;
            XLConversation xlConversation;
            Iterator iterator;

            synchronized (this.conversations) {
                iterator = this.conversations.values().iterator();

                while (true) {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    xlConversation = (XLConversation) iterator.next();
                    LogCatLog.d(TAG, "loaded user " + xlConversation.getChatID() + "msgCount:" + xlConversation
                            .getMsgCount());
                    Iterator messageBeanIterator = xlConversation.messages.iterator();
                    while (messageBeanIterator.hasNext()) {
                        MessageBean messageBean = (MessageBean) messageBeanIterator.next();
                        Hashtable hashtable = this.allMessages;
                        synchronized (this.allMessages) {
                            this.allMessages.put(messageBean.msgKey, messageBean);
                        }
                    }
                }
            }

            if (this.conversations != null && this.allMessages != null && this.conversations.size() > 0) {

                LogCatLog.d(TAG, "初始化消息条数 conversations.size()=" + this.conversations.size() + " allMessages()= " + this
                        .allMessages.size() + " 耗时timeTag=" + timeTag.stop());
            }

            this.allConversationsLoaded = true;
        }
    }

    /**
     * 插入信息
     */
    private void insertMsg(Md md, final String appData) {

        final MessageBean mb = createMsgBean(md);

        if (mb.msgType == MessageChatAdapter.SYS) {// 存入系统消息

            addMessage(mb);
            notifyChatMsg(mb);
            messageDBHandler.addReceivedMsg(mb, true);
            //处理系统通知
            mb.noticeType = md.getNoticeType();

            if (mb.noticeType != 0) {
                insertSysMsg(mb);
            }

        } else {
            //插入普通聊天数据
            if (mb.getChatType().getChatType() == MessageBean.ChatType.GroupChat.getChatType()) {// 群消息

                if(addMessage(mb)){
                    //回调监听
                    notifyChatMsg(mb);
                    messageDBHandler.addReceivedMsg(mb, true);
                    eventTaskManager.addMessageEventTaskTask(new GroupDetailsTask(mb));
                };


            } else if (mb.getChatType().getChatType() == MessageBean.ChatType.Chat.getChatType()) {// 点对点消息

                mb.currentlifetime =mb.lifetime*PrivateMessageTask.UI_RATE;

                if(addMessage(mb)) {

                    notifyChatMsg(mb);

                    messageDBHandler.addReceivedMsg(mb, true);
                    //请求联系人详情,排序
                    eventTaskManager.addMessageEventTaskTask(new ContactDetailsTask(mb));

                    if(mb.isPrivate()&&ContactManager.getInstance().getContact(ContactDBHandler.getContactId(mb))!=null){
                        //添加私密消息处理,不处理陌生人
                        eventTaskManager.addMessageEventTaskTask(new PrivateMessageTask(mb));

                        //启动30分钟计时器
                        if(mb.msgStatus==BorrowConstants.MSGSTATUS_UNREAD){
                            eventTaskManager.addMessageEventTaskTask(new PrivateLongTimeMessageTask(mb));
                        }

                    }else{
                        LogCatLog.d(TAG,"不处理陌生人添加私密消息处理");
                    }
                }

            } else if (mb.getChatType().getChatType() == MessageBean.ChatType.SYS.getChatType()) {// 系统消息

                notifyChatMsg(mb);
                eventTaskManager.addMessageEventTaskTask(new XLAssistantDetailsTask(mb));
            }


        }
        //发送通知
        sendNotice(true, md, appData);

        if (mb.msgStatus != BorrowConstants.MSGSTATUS_UNREAD) {
            //下载图片和语音
            if (mb.msgType == MessageChatAdapter.IMAGE || mb.msgType == MessageChatAdapter.VOICE) {
                ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread(mb);
                fileMsgQueue.submit(receiveMessageThread);
            }
        }

    }


    /**
     * 发送标题栏通知
     *
     * @param isAddMessage 消息入库数量
     * @param md
     * @param appData
     */
    private void sendNotice(boolean isAddMessage, Md md, String appData) {
        /**
         *1:系统通知消息
         *2:个人［点对点］DB  联系人  消息回调
         *                  陌生人  请求接口 拿到你所要的陌生人的详细信息
         *
         *
         *3:群组      DB 存在的群组信息
         *           DB 未存在群组消息
         *
         */
        if (isAddMessage) { //插入成功后才通知数据库

            if (md.getNoticeType() != 0) return; //不通知通知类型

            if (!DeviceInfoUtil.isAppOnForeground(XLApplication.getInstance())) {//本地通知消息

                Intent intent1 = new Intent();
                intent1.setAction(NoticeReceiver.NOTIFYRECEIVER);
                intent1.putExtra("APPDATA", appData);
                XLApplication.getInstance().sendBroadcast(intent1);
            } else {// 基本消息通知
                XLApplication.getMessageListenerManager().notifyListener(md);
            }
        }
    }


    /**
     * 插入系统消息
     */
    private void insertSysMsg(final MessageBean mb) {

        String xluserid = PersonSharePreference.getUserID() + "";


        if (mb.getChatType().getChatType() == MessageBean.ChatType.Chat.getChatType()) {
            //点对点
/*          d)	消息被拒绝通知<黑名单> sendType=0 noticeType=1
            e)	用户信息更新通知        sendType=0 noticeType=2
            f)	消息被拒绝通知<未初始化>   sendType=0 noticeType=3
            g)	消息被拒绝通知<未登陆>    sendType=0 noticeType=4
            h)	Device信息检查不通过通知  sendType=0 noticeType=5*/
            switch (mb.noticeType) {
                case 1: {
                    //"appType":"Android","fileLength":0,"fromid":"11149","mct":"1453466878695","message":"结婚",
                    // "messageType":1,"msgDate":1453466878627,"msgKey":1453466878605414,"noticeType":1,
                    // "replyType":1,"sKey":"1453466878618","sendType":1,"toid":"990"
                    // mb.msgContent = "消息被拒绝通知<黑名单>";

                    if (!TextUtils.isEmpty(mb.msgContent)) {
                        mb.chatType = MessageBean.ChatType.GroupChat;
                        messageDBHandler.addReceivedMsg(mb, true);
                    }
                }
                break;
                case 2: {
                    // mb.msgContent = "用户信息更新通知";
                    //insertSysMsg=false;
                }
                break;
                case 3: {
                    // mb.msgContent = "消息被拒绝通知<未初始化>";
                }
                break;
                case 4: {
                    // mb.msgContent = "消息被拒绝通知<未登陆> ";
                }
                break;
                case 5: {
                    //mb.msgContent = "Device信息检查不通过通知";
                }
                break;
                case 6: {
                    //mb.msgContent = "Device信息检查不通过通知";
                }
                break;
                case 7: {
                    //mb.msgContent = "Device信息检查不通过通知";
                    //  用户被群主踢出群后，给被踢用户发送单独的退出群通知
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            GroupManager.getInstance().exitContactInternal(GroupDBHandler.getGroupId(mb.xlID, mb.figureId));

                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                mb.chatType = MessageBean.ChatType.GroupChat;
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });

                }
                break;

            }
        } else if (mb.getChatType().getChatType() == MessageBean.ChatType.GroupChat.getChatType()) {
            //点对群
/*           a)	发送群消息被拒绝通知 sendType=1 noticeType=1
            b)	用户主动退出群通知    sendType=1 noticeType=2
            c)	用户被动退出群通知     sendType=1 noticeType=3
            d)	群解散通知             sendType=1 noticeType=4
           e)	群信息更新通知  sendType=1 noticeType=5
           f)	新用户主动加入群通知     sendType=1 noticeType=6
           g)	新用户被动加入群通知    sendType=1 noticeType=7
            h)	用户群信息更新通知          sendType=1 noticeType=8             */
            switch (mb.noticeType) {
                case 1: {
                    // mb.msgContent = "发送群消息被拒绝通知";
                    //1,设置群不可再次输入
                    //2,显示系统提示

                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            // mGroupDBHandler.delWithChatRecord(mb.xlID);
                            GroupManager.getInstance().exitContactInternal(GroupDBHandler.getGroupId(mb.xlID, mb.figureId));
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });

                }
                break;
                case 2: {

        /*            if(!xluserid.equals(mb.xlgroupmemberid)){
                        insertSysMsg=false;// 不显示通知
                    }*/

                    //  mb.msgContent = "用户主动退出群通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            // mGroupDBHandler.delWithChatRecord(mb.xlID);

                            GroupManager.getInstance().deleteMember(mb.xlgroupmemberid);

                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });


                }
                break;
                case 3: {

/*                    if(!xluserid.equals(mb.xlgroupmemberid)){
                        insertSysMsg=false;// 不显示通知
                    }*/
                    //    mb.msgContent = "用户被动退出群通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            GroupManager.getInstance().deleteMember(mb.xlgroupmemberid);
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });
                }
                break;
                case 4: {
                    //   mb.msgContent = "群解散通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            GroupManager.getInstance().exitContactInternal(GroupDBHandler.getGroupId(mb.xlID, mb.figureId));
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });
                }
                break;
                case 5: {
                    //  mb.msgContent = "群信息更新通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            getDetailGroup(mb);
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });
                }
                break;
                case 6: {
                    //  mb.msgContent = "新用户主动加入群通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            getDetailGroup(mb);

                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }

                        }
                    });
                }
                break;
                case 7: {

                    /*                    if(!xluserid.equals(mb.xlgroupmemberid)){
                        insertSysMsg=false;// 不显示通知
                    }*/

                    // insertSysMsg=false;// 不显示通知
                    //   mb.msgContent = "新用户被动加入群通知";
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {

                            Group group = mGroupDBHandler.query(GroupDBHandler.getGroupId(mb.xlID, mb.figureId));
                            LogCatLog.e(TAG, "group=" + group + ",toChatID=" + mb.xlID);
                            if (group != null && group.isJoin.equals("1")) {
                                //db有参与的群
                                new GroupMemberSync(mb.xlID).autoRequestGroupMember();
                            } else {
                                getDetailGroup(mb);
                            }
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }
                        }
                    });
                }
                break;
                case 8: {
                    // 群用户更新个人群信息（如群备注），向群成员发送通知
                    SingleThreadExecutor.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            getDetailGroup(mb);
                            if (!TextUtils.isEmpty(mb.msgContent)) {
                                messageDBHandler.addReceivedMsg(mb, true);
                            }

                        }
                    });
                }
                break;
                case 9: {

                    if (!TextUtils.isEmpty(mb.msgContent)) {
                        messageDBHandler.addReceivedMsg(mb, true);
                    }
                    break;
                }
            }
        }

    }

    /**
     * 适配消息id
     *
     * @return [0] 当前聊天主体的id(群or用户id)  [1] 具体聊天对象(群成员) 单聊为""
     */
    private String[] createAddMsgID(Md md) {

        if (md.getNoticeType() == 0) {
            //非通知类型消息
            String toChatID = "";

            if (md.getSendType() == BorrowConstants.CHATTYPE_GROUP) {
                toChatID = md.getGroupId(); //群组id 和 群成员 id
            /*    if (md.getFromid().equals(XLID + "")) {
                    return null; //过滤掉 发过来的群消息中单独发给自己的消息
                } else {*/
                return new String[]{toChatID, GroupMemberDBHandler.getMemberId(toChatID, md.getFromFigure(), md
                        .getToFigure())};
                // }
            } else if (md.getSendType() == BorrowConstants.CHATTYPE_SINGLE) {
                toChatID = md.getFromid();
            } else if (md.getSendType() == BorrowConstants.CHATTYPE_SYS) {
                toChatID = md.getFromid();
            }
            return new String[]{toChatID, ""};//单聊只需要知晓对方id即可
        } else {
            //通知类型消息
   /*         fromid
              toid*/

            String toChatID = "";

            if (md.getSendType() == BorrowConstants.CHATTYPE_GROUP) {

                return new String[]{md.getGroupId(), ""};

            } else {
                toChatID = md.getToid();
                return new String[]{toChatID, ""};//单聊只需要知晓对方id即可
            }


        }
    }

    /**
     * md转为MessageBean 接受到的消息
     *
     * @return
     */
    private MessageBean createMsgBean(Md md) {

        String[] toChatIDArg = createAddMsgID(md);

        if (toChatIDArg == null) return null;

        String toChatID = toChatIDArg[0];
        String memberID = toChatIDArg[1];

        int msgType = md.getMessageType() == null ? 0 : md.getMessageType();

        MessageBean bean = new MessageBean.Builder().
                msgType(msgType)
                .xlID(toChatID)
                .msgKey(md.getMsgKey() + "")
                .xlgroupmemberid(memberID)
                .msgLocalKey(md.getSKey())
                .direct(MessageBean.Direct.RECEIVE)
                .lifeTime(md.getLifetime())// TODO: 16/3/29  增加消息销毁时间
                .msgCreatedate(System.currentTimeMillis()+"")
                .msgDate(Utils.timeStamp2Date(md.getMsgDate(), "yyyy-MM-dd HH:mm")
                ).build();

        if(bean.isPrivate()){
            bean.privateDate=bean.msgDate;
        }

        if (md.getSendType() == MessageBean.ChatType.GroupChat.getChatType()) {// 群
            bean.setChatType(MessageBean.ChatType.GroupChat);
            bean.setFrom(toChatID);
            bean.xlgroupmemberid = memberID;
            bean.figureId = md.getToFigure();
            bean.msgStatus = MessageBean.getMsgState(msgType, toChatID);
        } else if (md.getSendType() == MessageBean.ChatType.Chat.getChatType()) {//点对点
            bean.setChatType(MessageBean.ChatType.Chat);
            bean.setFrom(md.getFromFigure());
            bean.figureUsersId = md.getFromFigure();
            bean.figureId = md.getToFigure();
            bean.msgStatus = MessageBean.getMsgState(msgType, md.getFromFigure());
        } else if (md.getSendType() == MessageBean.ChatType.SYS.getChatType()) { // 系统消息
            //需要兼容发送给单一角色或者全部角色的情况setFrom,figureUsersId
            bean.setChatType(MessageBean.ChatType.SYS);
            bean.setFrom(TextUtils.isEmpty(md.getFromFigure()) ? bean.xlID : md.getFromFigure());
            bean.figureUsersId = TextUtils.isEmpty(md.getFromFigure()) ? bean.xlID : md.getFromFigure();
            bean.figureId = md.getToFigure();
            bean.msgStatus = MessageBean.getMsgState(msgType, md.getFromFigure());

        }

        // bean.status=getMsgState(toChatID)==MessageBean.Status.READ.getStatusCode()?MessageBean.Status
        // .READ:MessageBean.Status.UNREAD;

        if (md.getMessageType() == null) {
            //// TODO: 2016/3/21 针对群通知特殊处理
            bean.msgType = MessageChatAdapter.SYS;
            bean.noticeType = md.getNoticeType();
            bean.msgContent = md.getMessage();
            return bean;
        }

        if (msgType == MessageChatAdapter.TEXT) {
            bean.msgContent = md.getMessage();
        } else if (msgType == MessageChatAdapter.IDCARD) {
            bean.msgContent = md.getMessage();
            NameCardBean nameCardBean = JSON.parseObject(bean.msgContent, NameCardBean
                    .class);
            nameCardBean.setMsg_key(bean.msgKey);
            bean.idCard = nameCardBean;

        } else if (msgType == MessageChatAdapter.WEBSHOPPING) {
            bean.msgContent = md.getMessage();
            GoodsDetailBean goodsDetailBean = JSON.parseObject(bean.msgContent, GoodsDetailBean
                    .class);
            goodsDetailBean.setMsg_key(bean.msgKey);
            bean.goodsCard = goodsDetailBean;
        } else if (msgType == MessageChatAdapter.NEWSCARD) {
            bean.msgContent = md.getMessage();
            NewsCard newsCard = JSON.parseObject(bean.msgContent, NewsCard
                    .class);
            newsCard.setMsg_key(bean.msgKey);
            bean.newsCard = newsCard;
        } else if (msgType == MessageChatAdapter.REDBUNDLE) {
            bean.msgContent = md.getMessage();
        } else if (msgType == MessageChatAdapter.VOICE) {
            bean.recordlength = md.getFileTime() + "";
            bean.msgContent = md.getFileId();
            // bean.msgStatus = BorrowConstants.MSGSTATUS_INPROGRESS;
        } else if (msgType == MessageChatAdapter.IMAGE) {
            bean.imageSize = md.getImgSize();
            bean.msgContent = md.getFileId();
            //bean.msgStatus = BorrowConstants.MSGSTATUS_INPROGRESS
            ;
        }
        return bean;
    }

    /**
     * 添加已读消息到缓存
     *
     * @param message
     */
    public boolean addMessage(MessageBean message) {
        return this.addMessage(message, true);
    }

    /**
     * 添加消息到缓存
     *
     * @param message  消息
     * @param isUnRead 是否读过
     */
    public boolean addMessage(MessageBean message, boolean isUnRead) {


        if (!TextUtils.isEmpty(message.figureId)) {
            //针对单一角色的消息才需要修改msgkey
            String key;
            FigureMode figureMode = ContactManager.getInstance().getAllFigureTable().get(message.figureId);
            if (figureMode == null) {
                key = message.msgKey + Utils.getUniqueMessageId();
            } else {
                key = message.msgKey + figureMode.getFigure_usersid_shortid();
            }

            if (key.length() > Long.MAX_VALUE) {
                key = Utils.getUniqueMessageId();
                //ios的localkey是long类型,不能超过19位
                LogCatLog.e(TAG, "msgkey长度大于19位出现异常,请检查外部传入的msgkey长度");
            }
            message.msgKey = key;
            message.msgLocalKey = key;
        }

        String msgid = message.msgKey;

        if (!this.allMessages.containsKey(msgid)) {
            this.allMessages.put(msgid, message);
            boolean isGroup = false;
            String id = "";
            if (message.getChatType() == MessageBean.ChatType.Chat) {

                if (message.direct == MessageBean.Direct.RECEIVE) {
                    id = ContactDBHandler.getContactId(message.getFrom(), message.figureId);
                } else {
                    id = ContactDBHandler.getContactId(message.getTo(), message.figureId);
                }
            } else if (message.getChatType() == MessageBean.ChatType.GroupChat) {
                if (message.direct == MessageBean.Direct.RECEIVE) {
                    id = GroupDBHandler.getGroupId(message.getFrom(), message.figureId);
                } else {
                    id = GroupDBHandler.getGroupId(message.getTo(), message.figureId);
                }
                isGroup = true;
            } else if (message.getChatType() == MessageBean.ChatType.SYS) {
                if (message.direct == MessageBean.Direct.RECEIVE) {
                    id = ContactDBHandler.getContactId(message.getFrom(), message.figureId);
                }
            }
            XLConversation xlConversation = this.getConversation(id, isGroup, XLConversation.msgType2ConversationType
                    (message.getChatType()));
            xlConversation.addMessage(message, isUnRead);
            if (!this.conversations.containsKey(id)) {
                this.conversations.put(id, xlConversation);
            }

            return true;
        }

        return false;
    }

    /**
     * 保存发送的消息到内存和数据库
     * 保存会话列表
     *
     * @param messageBean
     */
    public void saveMessage(MessageBean messageBean) {
        // LogCatLog.d(TAG, "save message:" + messageBean.msgKey);
        try {
            //   if (!this.allMessages.containsKey(messageBean.msgKey)) {
            this.addMessage(messageBean);
            //  db
            //插入数据到数据库
            messageDBHandler.addSendMsg(
                    messageBean,
                    true);
            // }
            //  this.addConversationToDB(userid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理发送消息后的回调
     */
    public void handlerSendMsg(final Md md) {

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
            @Override
            public void execute() {
                try {

                    if ((md.getMsgKey() + "").equals("null")) {

                        return; //这条消息没有msgkey 不用插入.
                    }

                    if (XLApplication.repeatSendMessageHandler != null) {
                        XLApplication.repeatSendMessageHandler.removeMessage(md.getSKey());//删除消息数据 勿动
                    }
                    MessageBean bean = new MessageBean
                            .Builder()
                            .msgLocalKey(md.getSKey())
                            .msgKey(md.getMsgKey() + "")
                            .msgDate(md.getMsgDate())
                            .xlID(md.getToid())
                            .figureUsersId(md.getToFigure())
                            .figureId(md.getFromFigure())
                            .msgStatus(BorrowConstants.MSGSTATUS_OK)
                            .direct(MessageBean.Direct.SEND)
                            .chatType(md.getSendType() == BorrowConstants.CHATTYPE_GROUP ?
                                    MessageBean.ChatType.GroupChat : MessageBean.ChatType.Chat)
                            .build();
                    //通知界面刷新
                    notifySendMsgStatus(bean);

                    // bean.file_id=md.getFileId();
                    //标记发送的消息状态


                    if (md.getSendType() == BorrowConstants.CHATTYPE_SINGLE) {
                        //单聊
                        try {

                            new ContactDetailsTask(bean).isAutoAddContacts();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MessageEventTaskManager.getInstance().addMessageEventTaskTask(new PrivateMessageTask(bean));
                    }

                    if (md.getSendType() == BorrowConstants.CHATTYPE_SYS || md.getToid().equals("1111")) {

                        new XLAssistantDetailsTask(bean).isAutoAddContacts();
                    }




                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
                            .currentThread(), e);
                }
            }
        });

    }


    /**
     * 处理接受到消息
     */
    public void handlerMsg(final Md md, final String appData) {

        //老
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
            @Override
            public void execute() {
                try {

                    insertMsg(md, appData);

                } catch (Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
                            .currentThread(), e);
                }
            }
        });
    }

    public Group getDetailGroup(MessageBean mb) {

        GroupDTO groupDto = SyncApi.getInstance().detail(mb.xlID, mb.figureId, XLApplication.context,
                new SyncApi.CallBack<GroupDTO>() {
                    @Override
                    public void success(GroupDTO mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                    }
                });

        Group group = GroupManager.getInstance().swapGroupDTOtoGroup(groupDto, true);

        GroupManager.getInstance().addGroup(group);
        notifyNewGroup(GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(group)));
        return group;
    }


    /**
     * 查询本地消息 进行消息发送
     *
     * @param mContext
     */
    public void queryLooperMessage(final Context mContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                RepeatSendMessageHandler repeatSendMessageHandler = XLApplication.repeatSendMessageHandler;
                if (repeatSendMessageHandler != null) {
                    repeatSendMessageHandler.removeAllMessage();
                    List<MessageBean> messageBeans = new MessageDBHandler(mContext).querySendingMsg();
                    for (MessageBean messageBean : messageBeans) {
                        addMessage(repeatSendMessageHandler, messageBean);//重发 添加消息
                    }
                }
            }
        }).start();
    }

    /**
     * 获取聊天会话
     *
     * @param username 用户id
     * @param isGroup  是否群聊
     * @param type
     * @return
     */
    public XLConversation getConversation(String username, boolean isGroup, XLConversation.ConversationType type) {
        LogCatLog.d(TAG, "get conversation for user:" + username);
        XLConversation xlConversation = (XLConversation) this.conversations.get(username);
        if (xlConversation != null) {
            return xlConversation;
        } else {

            List list = null;
            // 查询一个消息列表 群 or 个人
            int chatType = isGroup ? BorrowConstants.CHATTYPE_GROUP : BorrowConstants.CHATTYPE_SINGLE;

            MessageDBHandler.MessageCursor messagecursor = new MessageDBHandler.MessageCursor(new MessageDBHandler
                    (XiangLinApplication.getInstance()).queryChatHistory(username, chatType, 20000));

            list = messagecursor.getMessageBeanList(chatType);

            xlConversation = new XLConversation(username, list, type, 0L, 0L, 0L, 0);
            this.conversations.put(username, xlConversation);
            return xlConversation;

        }
    }


    private static class MessageHandlerHelper {
        private static MessageHandler instance = new MessageHandler();
    }

    public static MessageHandler getInstance() {
        return MessageHandlerHelper.instance;
    }

    /**
     * 发送消息
     *
     * @param repeatSendMessageHandler 消息发送实现
     * @param messageBean              消息体
     */
    public void addMessage(RepeatSendMessageHandler repeatSendMessageHandler, MessageBean messageBean) {
        if (messageBean != null) {
            String toChatId = messageBean.xlID;
/*            int toChatType = 0;
            if (messageBean.xlgroupmemberid != null
                    && !messageBean.xlgroupmemberid.equals("null")
                    && !messageBean.equals("")) {
                toChatType = BorrowConstants.CHATTYPE_GROUP;
            } else {
                toChatType = BorrowConstants.CHATTYPE_SINGLE;
            }*/
            RepeatMessage repeatMessage = new RepeatMessage();
            repeatMessage.setToChatId(toChatId);// 联系人ID
            repeatMessage.setChatType(messageBean.getChatType().getChatType());// 聊天类型
            repeatMessage.setMessageBean(messageBean);// 消息
            repeatMessage.setDateTime(System.currentTimeMillis() + "");// 发送时间
            repeatSendMessageHandler.addMessage(repeatMessage);
        }
    }

    private ExecutorService notifierThread = Executors.newSingleThreadExecutor();
    private ExecutorService newMsgQueue = Executors.newSingleThreadExecutor();
    private ExecutorService fileMsgQueue = Executors.newSingleThreadExecutor();

    /**
     * 通知聊天界面刷新消息发送后状态
     *
     * @param bean
     */
    public static void notifySendMsgStatus(MessageBean bean) {
        //群聊groupid,单聊Contactid
        String msgkey = bean.msgLocalKey;
        String toChatId;
        boolean isGroup = false;


        if (bean.getChatType().getChatType() == MessageBean.ChatType.GroupChat.getChatType()) {
            toChatId = GroupDBHandler.getGroupId(bean.xlID, bean.figureId);
            isGroup = true;
        } else {
            toChatId = ContactDBHandler.getContactId(bean);
            isGroup = false;
        }

        XLConversation conversation = ChatManager.getInstance().getConversation(toChatId,
                isGroup);
        List<MessageBean> list = conversation.getAllMessages();
        for (int i = list.size() - 1; i >= 0; i--) {
            MessageBean messageBean = list.get(i);

            if (messageBean.msgKey.equals(msgkey)) {
                messageBean.msgKey = bean.msgKey;

                synchronized (MessageHandler.getInstance().allMessages) {
                    if (MessageHandler.getInstance().allMessages.containsKey(msgkey)) {
                        MessageHandler.getInstance().allMessages.put(messageBean.msgKey, messageBean);
                        MessageHandler.getInstance().allMessages.remove(msgkey);
                    }
                }
//                messageBean.msgStatus = BorrowConstants.MSGSTATUS_OK;// TODO: 16/3/31
                  messageBean.msgStatus=bean.msgStatus;
                if (messageBean.messageStatusCallBack != null) {
                    //todo 名片通知待优化
                    if (messageBean.msgType == MessageChatAdapter.IDCARD) {
                        XLApplication.getInstance().getContentResolver().notifyChange(
                                CardDBHandler.SYNC_SIGNAL_URI,
                                null);
                    }
                    messageBean.messageStatusCallBack.onSuccess();
                }
                break;
            }

        }

        Contact contact = ContactManager.getInstance().getContact(ContactDBHandler.getContactId(bean));
        if (contact != null) {
            contact.updatedate = System.currentTimeMillis() + "";
            new ContactDBHandler(XLApplication.getInstance()).updateContactUpdateTime(contact);
        }

        if(!isGroup&&bean.msgStatus==BorrowConstants.MSGSTATUS_FAIL){
            MessageEventTaskManager.getInstance().addMessageEventTaskTask(new PrivateLongTimeMessageTask(bean));
        }



        MessageDBHandler.sendMessageResult(bean);
    }


    /**
     * 通知新消息
     *
     * @param messageBean 消息体
     */
    public void notifyChatMsg(final MessageBean messageBean) {
        this.newMsgQueue.submit(new Runnable() {
            public void run() {
                publishEvent(XLNotifierEvent.Event.EventNewMessage, messageBean);
            }
        });
    }

    /**
     * 通知新加的联系人给订阅者
     *
     * @param contact
     */
    public void notifyNewContact(final Contact contact) {
        this.newMsgQueue.submit(new Runnable() {
            public void run() {
                publishEvent(XLNotifierEvent.Event.EventNewContact, contact);
            }
        });
    }

    /**
     * 通知有新的群给订阅者
     *
     * @param group
     */
    public void notifyNewGroup(final Group group) {
        this.newMsgQueue.submit(new Runnable() {
            public void run() {
                publishEvent(XLNotifierEvent.Event.EventNewGroup, group);
            }
        });
    }

    /**
     * 推送是事件
     *
     * @param event  事件类型
     * @param object 业务对象
     * @return
     */
    boolean publishEvent(final XLNotifierEvent.Event event, final Object object) {

        if (this.containsType(event)) {
            this.notifierThread.submit(new Runnable() {
                public void run() {
                    synchronized (filteredEventListeners) {
                        if (containsType(event)) {
                            List list = (List) filteredEventListeners.get(event);
                            if (list != null) {
                                Iterator iterator = list.iterator();
                                publishEvent(iterator, event, object);
                            }
                        }

                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private void publishEvent(Iterator<XLEventListener> listenerIterator, XLNotifierEvent.Event event, Object object) {
        while (listenerIterator.hasNext()) {
            XLNotifierEvent xlNotifierEvent = new XLNotifierEvent();
            xlNotifierEvent.setEventData(object);
            xlNotifierEvent.setEvent(event);
            XLEventListener xlEventListener = (XLEventListener) listenerIterator.next();
            if (xlEventListener != null) {
                xlEventListener.onEvent(xlNotifierEvent);
            }
        }

    }

    private boolean containsType(XLNotifierEvent.Event event) {
        return this.filteredEventListeners.containsKey(event);
    }

    /**
     * 注销监听
     *
     * @param xlEventListener 监听器
     */
    public void removeEventListener(XLEventListener xlEventListener) {
        if (xlEventListener != null) {
            Hashtable hashtable = this.filteredEventListeners;
            synchronized (this.filteredEventListeners) {
                Collection collection = this.filteredEventListeners.values();
                if (collection != null) {
                    Iterator iterator = collection.iterator();

                    while (iterator.hasNext()) {
                        this.remove((List) iterator.next(), xlEventListener);
                    }
                }

            }
        }
    }

    /**
     * 事件注册
     *
     * @param xlEventListener 监听器
     * @param events          事件类型
     */
    public void registerEventListener(XLEventListener xlEventListener, XLNotifierEvent.Event[] events) {
        Hashtable hashtable = this.filteredEventListeners;
        synchronized (this.filteredEventListeners) {
            int var6 = events.length;
            for (int i = 0; i < var6; ++i) {
                XLNotifierEvent.Event event = events[i];
                this.registerEventListener(xlEventListener, event);
            }

        }
    }

    private void registerEventListener(XLEventListener xlEventListener, XLNotifierEvent.Event event) {
        if (!this.filteredEventListeners.containsKey(event)) {
            ArrayList list = new ArrayList();
            list.add(xlEventListener);
            this.filteredEventListeners.put(event, list);
        } else {
            List list = (List) this.filteredEventListeners.get(event);
            if (!list.contains(xlEventListener)) {
                list.add(0, xlEventListener);
            }
        }

    }

    private void remove(List<XLEventListener> listeners, XLEventListener xlEventListener) {
        Iterator iterator = listeners.iterator();
        while (iterator.hasNext()) {
            XLEventListener var4 = (XLEventListener) iterator.next();
            if (var4 == xlEventListener) {
                iterator.remove();
            }
        }

    }

    /**
     * 清理监听
     */
    public void clearEventListener() {
        this.filteredEventListeners.clear();
    }
}

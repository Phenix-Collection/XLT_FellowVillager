/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import android.net.Uri;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 私密消息计时器 短时时间计时器
 *
 * @author pengyang
 * @version v 1.0.0 2016/3/31 16:50  XLXZ Exp $
 */
public class PrivateMessageTask extends MessageEventTask {

    /**
     * 更新速度
     */
    public static int UI_RATE=2;

    private MessageBean mMessageBean;

    private MessageDBHandler mMessageDBHandler;

    public PrivateMessageTask(MessageBean mb) {
        super(ContactDBHandler.getContactId(mb)); //没有考虑群
        mMessageBean = mb;
        mMessageDBHandler = new MessageDBHandler(XLApplication.getInstance());
    }

    /**
     *
     */
    @Override
    public void run() {

        XLConversation conversation = ChatManager.getInstance()
                .getConversation(ContactDBHandler.getContactId(mMessageBean), false);

        boolean isExist = true;

        while (isExist) {

            synchronized (conversation) {
                long s = System.currentTimeMillis();
                List<MessageBean> messages = conversation.getAllMessages();
                List<MessageBean> list = Collections.synchronizedList(new ArrayList());
                list.addAll(messages);

                LogCatLog.d(TAG, "开始计时,用户:" + conversation.getChatID() + "messageSize:" + list.size());

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isPrivate()
                            &&!list.get(i).isExpired
                            &&((list.get(i).msgStatus==BorrowConstants.MSGSTATUS_READ)
                            ||(list.get(i).msgStatus==BorrowConstants.MSGSTATUS_OK))
                            ||(list.get(i).msgStatus==BorrowConstants.MSGSTATUS_INPROGRESS)){
                        isExist = true;
                        break;
                    }
                    isExist = false;
                }
                if (!isExist) {
                    onEndTask();
                    break;
                }

                Iterator<MessageBean> iterator = list.iterator();

                while (iterator.hasNext()) {

                    MessageBean mb = iterator.next();

                    boolean isReceive =
                            mb.direct == MessageBean.Direct.RECEIVE &&mb.msgStatus==BorrowConstants.MSGSTATUS_READ
                                    &&mb.currentlifetime >0;


                    boolean isSend = mb.direct == MessageBean.Direct.SEND && mb.msgStatus == BorrowConstants
                            .MSGSTATUS_OK && mb.currentlifetime > 0;

                    if (isReceive || isSend) {

                        //下载中语音 直接播放
                        if(mb.msgStatus==BorrowConstants.MSGSTATUS_INPROGRESS&&mb.msgType== MessageChatAdapter.IMAGE){
                            continue;
                        }

                        if (mb.isPrivatePause) {
                           //计时暂停
                            LogCatLog.d(TAG, "暂停计时,MessageBean=" + mb.msgContent + " currentlifetime=" + mb
                                    .currentlifetime
                                    + "s");


                            if (mb.privateMessageCallBack != null) {
                                mb.privateMessageCallBack.onPause(mb);
                            }
                            if (mb.privateMessageCallBack != null) {
                                mb.privateMessageCallBack.onPause(mb);
                            }


                        } else {
                              mb.currentlifetime--;
                                if(mb.currentlifetime >= 0) {
                                    //计时中
                                    if (mb.privateMessageCallBack != null) {
                                        mb.privateMessageCallBack.onProgress(mb.lifetime, 100 - (100 * mb
                                                .currentlifetime) / (mb.lifetime * UI_RATE), mb.currentlifetime / UI_RATE,mb );
                                    }
                                    if (mb.bigImageMessageStatusCallBack != null) {
                                        mb.bigImageMessageStatusCallBack.onProgress(mb.lifetime, 100 - (100 * mb
                                                .currentlifetime) / (mb.lifetime * UI_RATE), mb.currentlifetime / UI_RATE,mb );
                                    }
                                    LogCatLog.d(TAG, "开始计时,MessageBean=" + mb.msgContent + " currentlifetime=" + mb
                                            .currentlifetime
                                            + " pictime=" + mb.currentlifetime / UI_RATE);

                                    if (mb.currentlifetime ==0) {
                                        //30秒~60秒计时完成
                                        if (mb.privateMessageCallBack != null) {
                                            mb.privateMessageCallBack.onEnd(mb);
                                        }
                                        if (mb.bigImageMessageStatusCallBack != null) {
                                            mb.bigImageMessageStatusCallBack.onEnd(mb);
                                        }
                                        mb.isExpired = true;
                                        mMessageDBHandler.cleanMessageContent(mb);
                                        LogCatLog.d(TAG, "结束计时,移除MessageBean=" + mb.msgContent + " currentlifetime=" + mb
                                                .currentlifetime +
                                                "s");
                                        if (mb.equals(list.get(list.size() - 1))) {
                                            //发送通知刷新消息列表
                                            notifyChange(mb);
                                        }
                                    }

                                    mMessageDBHandler.updatePrivateMsgTime(mb);
                                }

                        }
                    }
                }

                long x = System.currentTimeMillis() - s;
                LogCatLog.d(TAG, "结束计时,xxxx:" + x);
                try {
                    if (x <= 1000/UI_RATE) {
                        Thread.sleep((1000/UI_RATE) - x);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            LogCatLog.d(TAG, "结束计时,用户:" + conversation.getChatID());
        }

    }

    /** 刷新消息列表
     * @param mb
     */
    private void notifyChange(MessageBean mb) {
        if (TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())) {
            XLApplication.getInstance().getContentResolver().notifyChange(MessageDBHandler.SYNC_SIGNAL_URI, null);
        } else {
            XLApplication.getInstance().getContentResolver().notifyChange(
                    Uri.withAppendedPath(
                            MessageDBHandler.SYNC_SIGNAL_URI,
                            mb.figureUsersId
                    ),
                    null
            );

        }
    }


}

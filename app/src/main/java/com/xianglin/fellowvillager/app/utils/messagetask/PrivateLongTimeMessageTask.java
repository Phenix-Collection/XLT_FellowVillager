/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.utils.messagetask;

import android.net.Uri;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * 私密消息计时器 30分钟计时器
 * @author pengyang
 * @version v 1.0.0 2016/3/31 16:50  XLXZ Exp $
 */
public class PrivateLongTimeMessageTask extends MessageEventTask {

    /**
     * 更新速度
     */
    public static int UI_RATE=2;

    private MessageBean mMessageBean;



    private MessageDBHandler mMessageDBHandler;

    public PrivateLongTimeMessageTask(MessageBean mb) {
        super("10001"); //没有考虑群
        mMessageBean = mb;
        mMessageDBHandler = new MessageDBHandler(XLApplication.getInstance());
    }

    /**
     *
     */
    @Override
    public void run() {


        boolean isExist = true;

        while (isExist) {

            synchronized ( Collections.synchronizedList(new ArrayList())) {
                long s = System.currentTimeMillis();
                List<MessageBean> list = Collections.synchronizedList(new ArrayList());
                Hashtable<String, MessageBean> map= MessageHandler.getInstance().getAllMessages();
                list.addAll(map.values());

             //   LogCatLog.d(TAG, "开始计时,用户:" + conversation.getChatID() + "messageSize:" + list.size());

                for (int i = 0; i < list.size(); i++) {
                    if (((list.get(i).msgStatus==BorrowConstants.MSGSTATUS_UNREAD)
                            ||(list.get(i).msgStatus==BorrowConstants.MSGSTATUS_FAIL))
                            &&list.get(i).isPrivate()
                            &&!list.get(i).isExpired) {
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

                        if(mb.isExpired){
                            continue;
                        }

                        mb.isExpired=   MessageDBHandler.getMessageIsExpired(mb.lifetime,mb.msgDate);

                        if(mb.isExpired) {
                            mb.isExpired = true;
                            mb.currentlifetime=0;
                            mMessageDBHandler.cleanMessageContent(mb);

                            LogCatLog.d(TAG, "30分钟结束计时,移除MessageBean=" + mb.msgContent + " currentlifetime=" + mb
                                    .currentlifetime +
                                    "s");

                            mMessageDBHandler.updatePrivateMsgTime(mb);

                            XLConversation xlConversation= MessageHandler.getInstance().getConversations().get(ContactDBHandler.getContactId(mb));
                           if(xlConversation!=null) {
                               boolean isSuccess=xlConversation.getAllMessages().remove(mb);
                               if(isSuccess){
                                   LogCatLog.e(TAG, "30分钟结束计时,内存移除MessageBean失败=" + mb.msgContent + " currentlifetime=" + mb
                                           .currentlifetime +
                                           "s");
                               }

                               xlConversation.setMsgSendCount(xlConversation.getUnreadMsgCount() - 1);

                           }
                            notifyChange(mb);
                        }


                }

                long x = System.currentTimeMillis() - s;
                LogCatLog.d(TAG, "30分钟结束计时,计算耗时:" + x);
                try {
                    if (x <= 1000/UI_RATE) {
                        Thread.sleep((1000/UI_RATE) - x);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
          //  LogCatLog.d(TAG, "结束计时,用户:" + conversation.getChatID());
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

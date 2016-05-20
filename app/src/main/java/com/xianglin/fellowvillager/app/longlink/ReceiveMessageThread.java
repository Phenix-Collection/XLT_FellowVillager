/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.longlink;

import android.content.ContentValues;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBSQLUtil;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 执行消息接收后的内容下载任务 图片,录音
 *
 * @author pengyang
 * @version v 1.0.0 2016/2/23 13:49  XLXZ Exp $
 */
public class ReceiveMessageThread implements Runnable {
    private static final String TAG = "ReceiveMessageThread";
    private MessageBean msg;
    private MessageDBHandler mMessageDBHandler;
    public ReceiveMessageThread(MessageBean msg) {
        this.msg = msg;
        mMessageDBHandler=   new MessageDBHandler(XLApplication.getInstance());
    }

    @Override
    public void run() {



        SystemClock.sleep(100);

        if (this.msg.msgType == MessageChatAdapter.IMAGE) {
              receiveImage();
            //标记为下载状态
            msg.msgStatus = BorrowConstants.MSGSTATUS_INPROGRESS;

        } else if (this.msg.msgStatus == MessageChatAdapter.VOICE) {

        }

    }

    /**
     * 下载图片
     */
    private void receiveImage() {

        //把数据库默认标记为失败
        updateMsgState(BorrowConstants.MSGSTATUS_RECEIVE_FAIL);

        FileUtils.downloadFile(XLApplication.getInstance(), PersonSharePreference.getUserID(), msg.msgContent,
                FileUtils.IMG_SAVE_PATH, new FileMessageListener<FileTask>() {
                    @Override
                    public void success(int statusCode, final FileTask fileTask) {


                        synchronized (this) {

                            msg.msgStatus = getMsgState(msg.getFrom());
                            msg.progress = 100;
                            updateMsgState(msg.msgStatus);
                            if (msg.messageStatusCallBack != null) {

                                //  SystemClock.sleep(2000);

                                LogCatLog.d(TAG, "success" + "下载图片MessageBean:" + msg.toString());
                                msg.messageStatusCallBack.onProgress(100, (String) null);

                                msg.messageStatusCallBack.onSuccess();

                            }
                        }

                    }

                    @Override
                    public void handleing(int statusCode, FileTask fileTask) {
                        msg.progress = statusCode;
                        if( msg.messageStatusCallBack != null) {
                            msg.messageStatusCallBack.onProgress(statusCode, (String)null);
                        }
                    }
                    @Override
                    public void failure(int statusCode, FileTask fileTask) {

                        msg.msgStatus = BorrowConstants.MSGSTATUS_RECEIVE_FAIL;
                        LogCatLog.e(TAG, "下载失败" + msg.getFrom() + ",imageid or fileid : " + msg.msgContent);
                       // SystemClock.sleep(2000);
                         updateMsgState( msg.msgStatus);

                        if( msg.messageStatusCallBack != null) {
                            msg.messageStatusCallBack.onError(statusCode, "");
                        }
                    }
                });
    }

    protected void updateMsgState(int msgStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSQLUtil.MSG_STATUS, String.valueOf(msgStatus));
        mMessageDBHandler.updateMsgState(msg.msgKey, contentValues);
}

    public static int getMsgState(String toChatID) {
        //当前和用户xxx 正在聊天界面的消息  标记为已读
        int msg_state;

        if (!TextUtils.isEmpty(XLApplication.toChatId) && XLApplication.toChatId.equals(toChatID)) {



                msg_state = BorrowConstants.MSGSTATUS_READ;



        } else {

            msg_state = BorrowConstants.MSGSTATUS_UNREAD;
        }

        return msg_state;
    }


}

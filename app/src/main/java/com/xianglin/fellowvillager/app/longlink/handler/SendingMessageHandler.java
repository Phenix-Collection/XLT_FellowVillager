package com.xianglin.fellowvillager.app.longlink.handler;

import android.content.Context;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.MessageSender;
import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.longlink.message.MessageAudioBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageBusinessCardBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageImageBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageItemBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageNewsBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageRedBundle;
import com.xianglin.fellowvillager.app.longlink.message.MessageTextBundle;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.File;

/**
 * 发送消息处理
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-21
 */
public class SendingMessageHandler {

    private static final String TAG = LongLinkConfig.TAG;
    private RepeatSendMessageHandler repeatSendMessageHandler;
    private long XLID = PersonSharePreference.getUserID();
    /**
     * 1 普通文字
     * 2 图片
     * 3 音频
     * 4 视频
     * 5 名片
     * 6 红包
     * 7 商品
     */
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int AUDIO = 3;
    public static final int VIDEO = 4;
    public static final int BUCARD = 5;
    public static final int RED = 6;
    public static final int ITEM = 7;
    public static final int NEWS = 8;

    public SendingMessageHandler(RepeatSendMessageHandler repeatSendMessageHandler) {
        this.repeatSendMessageHandler = repeatSendMessageHandler;
    }


    /**
     * 发送消息
     *
     * @param mContext
     * @param repeatMessage
     */
    public void sendMessage(Context mContext, RepeatMessage repeatMessage) {
        String message = getMessage(mContext, repeatMessage);
        if (message == null) {
            LogCatLog.e(TAG, "message 为null 情况如下：1.发送文件，而非文字；2.发送出现错误");
            return;// message 为null 情况如下：1.发送文件，而非文字；2.发送出现错误
        }
        sendMessage(message);
    }

    public void sendMessage(String syncStr) {
        LogCatLog.d(TAG, "发送内容" + syncStr);
        if (syncStr != null)
            MessageSender.getInstance().sendContentValues(syncStr);
    }

    public String getMessage(Context mContext, RepeatMessage repeatMessage) {
        MessageBean messageBean = repeatMessage.getMessageBean();
        messageBean.xlID = repeatMessage.getToChatId();// tochatID// 对方ID
        int toChatType = repeatMessage.getChatType();
        String message = null;
        if (messageBean != null) {
            switch (messageBean.msgType) {
                case TEXT:// 1普通文字
                    message = sendTextMessage(XLID, toChatType, messageBean);
                    break;
                case IMAGE:// 2图片
                    message = sendImage(mContext, repeatMessage);
                    break;
                case AUDIO:// 3音频
                    message = sendAudio(mContext, repeatMessage);
                    break;
                case VIDEO:// 4 视频
                    break;
                case BUCARD://5 名片
                    message = sendBusinessCardMessage(XLID, toChatType, messageBean);
                    break;
                case RED:// 6 红包
                    message = sendRedMessage(XLID, toChatType, messageBean);
                    break;
                case ITEM:// 7 商品
                    message = sendItemMessage(XLID, toChatType, messageBean);
                    break;
                case NEWS:// 8 新闻
                    message = sendNewsMessage(XLID, toChatType, messageBean);
                    break;
                default:
                    break;
            }
        } else {
            LogCatLog.d(TAG, "需要发送的消息为null");
        }

        return message;
    }


    /**
     * 发送商品消息
     *
     * @param xlId
     * @param chatType
     * @param messageBean
     */
    public String sendItemMessage(long xlId, int chatType, MessageBean messageBean) {
        return itemMessage(chatType, xlId, messageBean);
    }

    /**
     * 发送红包消息
     *
     * @param xlId
     * @param chatType
     * @param messageBean
     */
    public String sendRedMessage(long xlId, int chatType, MessageBean messageBean) {
        return redMessage(chatType, xlId, messageBean);
    }

    /**
     * 发送名片消息
     *
     * @param xlId
     * @param chatType
     * @param messageBean
     */
    public String sendBusinessCardMessage(long xlId, int chatType, MessageBean messageBean) {
        return businessCardMessage(chatType, xlId, messageBean);
    }

    /**
     * 发送名片消息
     *
     * @param xlId
     * @param chatType
     * @param messageBean
     */
    public String sendNewsMessage(long xlId, int chatType, MessageBean messageBean) {
        return newsMessage(chatType, xlId, messageBean);
    }

    /**
     * 发送普通文字消息
     *
     * @param xlId        当前用户ID
     * @param messageBean
     * @return
     */
    public String sendTextMessage(long xlId, int chatType, MessageBean messageBean) {
        //TODO 2016-2-29 修改 james
//        String syncStr = new MessageTextBundle(xlId + "",// 乡邻ID
//                messageBean.xlID,//乡邻联系人ID
//                chatType,//聊天类型，个人＋ 群
//                messageBean.msgKey) // 消息msgkey
//                .bundleTextValues(messageBean.msgContent);// 消息内容

        String syncStr = new MessageTextBundle(xlId+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                chatType,// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime// 消息生存时间
        ).bundleTextValues(messageBean.msgContent);// 消息内容
        return syncStr;
    }

    /**
     * 图片
     *
     * @param repeatMessage
     */
    public String imageMessage(RepeatMessage repeatMessage) {
        /**
         * FileTask fileTask,
         int toChatType,
         long xlId,
         MessageBean messageBean,
         File file
         */
        MessageBean messageBean = repeatMessage.getMessageBean();
        // TODO 2016-2-29 修改 james
//        String syncStr = new MessageImageBundle(
//                XLID + "",// 乡邻ID
//                messageBean.xlID,// 联系人ID
//                repeatMessage.getChatType(),// 聊天类型
//                messageBean.msgKey)
//                .setFileId(messageBean.file_id)
//                .setFileLength(messageBean.fileLength)
//                .setImgSize(messageBean.imageSize)
//                .bundleTextValues("");
        String syncStr = new MessageImageBundle(
                XLID+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                repeatMessage.getChatType(),// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime )// 消息生存时间

                .setFileId(messageBean.file_id)// 文件ID
                .setFileLength(messageBean.fileLength)// 文件长度
                .setImgSize(messageBean.imageSize)// 图片像素
                .bundleTextValues("");
        return syncStr;
    }



    /**
     * 音频
     *
     * @param repeatMessage
     */
    public String audioMessage(RepeatMessage repeatMessage) {
        MessageBean messageBean = repeatMessage.getMessageBean();
        // TODO 2016-2-29 修改 james
//        String syncStr = new MessageAudioBundle(
//                XLID + "",// 乡邻ID
//                messageBean.xlID + "",// 联系人ID
//                repeatMessage.getChatType(),// 聊天类型
//                messageBean.msgKey)
//                .setFileId(messageBean.file_id)
//                .setFileLength(messageBean.fileLength)
//                .setFileTime(Integer.parseInt(messageBean.recordlength))
//                .bundleTextValues("");


        String syncStr = new MessageAudioBundle(
                XLID+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                repeatMessage.getChatType(),// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime )// 消息生存时间

                .setFileId(messageBean.file_id)//文件ID
                .setFileLength(messageBean.fileLength)// 文件长度
                .setFileTime(Integer.parseInt(messageBean.recordlength))// 音频时长
                .bundleTextValues("");
        return syncStr;
    }

    /**
     * 商品
     *
     * @param toChatType
     * @param xlId
     * @param messageBean
     */
    public String itemMessage(int toChatType,
                              long xlId,
                              MessageBean messageBean) {
        //TODO  2016 - 2 - 29 修改 james
//        String syncStr = new MessageItemBundle(
//                xlId + "",// 乡邻ID
//                messageBean.xlID + "",// 联系人ID
//                toChatType,// 聊天类型
//                messageBean.msgKey)
//                .bundleTextValues(messageBean.msgContent);

        String syncStr = new MessageItemBundle(
                xlId+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                toChatType,// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime
        ).bundleTextValues(messageBean.msgContent);// 消息内容
        return syncStr;
    }

    /**
     * 名片
     *
     * @param toChatType
     * @param xlId
     * @param messageBean
     */
    public String businessCardMessage(int toChatType,
                                      long xlId,
                                      MessageBean messageBean) {
        // TODO 2016-2-29 修改 james
//        String syncStr = new MessageBusinessCardBundle(
//                xlId + "",// 乡邻ID
//                messageBean.xlID + "",// 联系人ID
//                toChatType,// 聊天类型
//                messageBean.msgKey)
//                .bundleTextValues(messageBean.msgContent);
        String syncStr = new MessageBusinessCardBundle(
                xlId+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                toChatType,// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime
        ).bundleTextValues(messageBean.msgContent);// 消息内容
        return syncStr;
    }

    /**
     * 红包
     *
     * @param toChatType
     * @param xlId
     * @param messageBean
     */
    public String redMessage(int toChatType,
                             long xlId,
                             MessageBean messageBean) {
        //TODO 2016-2-29 修改 james
//        String syncStr = new MessageRedBundle(
//                xlId + "",// 乡邻ID
//                messageBean.xlID + "",// 联系人ID
//                toChatType,// 聊天类型
//                messageBean.msgKey)
//                .bundleTextValues(messageBean.msgContent);

        String syncStr = new MessageRedBundle(
                xlId+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                toChatType,// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime
        ).bundleTextValues(messageBean.msgContent);// 消息内容
        return syncStr;
    }

    /**
     * 新闻类消息
     *
     * @param toChatType
     * @param xlId
     * @param messageBean
     */
    public String newsMessage(int toChatType, long xlId, MessageBean messageBean) {
        // TODO 2016-2-29 修改 jams
//        String syncStr = new MessageNewsBundle(
//                xlId + "",// 乡邻ID
//                messageBean.xlID + "",// 联系人ID
//                toChatType,// 聊天类型
//                messageBean.msgKey)
//                .bundleTextValues(messageBean.msgContent);
        String syncStr = new MessageNewsBundle(
                xlId+"",//用户ID
                messageBean.figureId,// 用户角色ID
                messageBean.xlID,// 联系人ID
                messageBean.figureUsersId,// 联系人角色ID
                toChatType,// 聊天类型 个人＋群
                messageBean.msgKey,// 消息KEY
                messageBean.lifetime
        ).bundleTextValues(messageBean.msgContent);// 消息内容
        return syncStr;
    }


    /**
     * 发送图片
     *
     * @param mContext
     * @param repeatMessage
     * @return
     */
    public String sendImage(Context mContext, RepeatMessage repeatMessage) {
        MessageBean messageBean = repeatMessage.getMessageBean();
        messageBean.xlID = repeatMessage.getToChatId();// tochatID// 对方ID
        int toChatType = repeatMessage.getChatType();
        if (repeatMessage.getFileSendState() == RepeatMessage.DEF) {
            repeatMessage.setFileSendState(RepeatMessage.HANDLEING);
            sendImageMessage(mContext, toChatType, XLID, messageBean);
        } else if (repeatMessage.getFileSendState() == RepeatMessage.HANDLEING) {
            LogCatLog.d(TAG, "图片发送中");
        } else if (repeatMessage.getFileSendState() == RepeatMessage.SUCCESS) {
            LogCatLog.d(TAG, "图片发送成功， 开始发送消息");
            return imageMessage(repeatMessage);// TODO 修改
        } else if (repeatMessage.getFileSendState() == RepeatMessage.FAILURE) {
            LogCatLog.d(TAG, "图片发送失败，进行图片发送");
            sendImageMessage(mContext, toChatType, XLID, messageBean);
        }
        return null;
    }

    /**
     * 发送音频
     *
     * @param mContext
     * @param repeatMessage
     */
    public String sendAudio(Context mContext, RepeatMessage repeatMessage) {
        MessageBean messageBean = repeatMessage.getMessageBean();
        messageBean.xlID = repeatMessage.getToChatId();// tochatID// 对方ID
        int toChatType = repeatMessage.getChatType();
        if (repeatMessage.getFileSendState() == RepeatMessage.DEF) {
            repeatMessage.setFileSendState(RepeatMessage.HANDLEING);
            sendAudioMessage(mContext, toChatType, XLID, messageBean);
        } else if (repeatMessage.getFileSendState() == RepeatMessage.HANDLEING) {
            LogCatLog.d(TAG, "音频发送中");
        } else if (repeatMessage.getFileSendState() == RepeatMessage.SUCCESS) {
            LogCatLog.d(TAG, "音频发送成功， 开始发送消息");
            return audioMessage(repeatMessage); // TODO 修改
        } else if (repeatMessage.getFileSendState() == RepeatMessage.FAILURE) {
            LogCatLog.d(TAG, "音频发送失败，进行图片发送");
            sendAudioMessage(mContext, toChatType, XLID, messageBean);
        }
        return null;
    }




    /**
     * 发送语音信息
     *
     * @param mContext
     * @param xlId        当前用户ID
     * @param messageBean
     */
    public void sendAudioMessage(Context mContext, final int toChatType, final long xlId, final MessageBean messageBean) {

        final File file = new File(messageBean.msgContent);
        if (file.exists()) {
            final RepeatMessage repeatMessage = repeatSendMessageHandler.getMessage(messageBean.msgKey);
            FileUtils.uploadFile(mContext, xlId, messageBean.msgContent, new FileMessageListener<FileTask>() {
                @Override
                public void success(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "语音文件传输成功");
//                    audioMessage(fileTask, toChatType, xlId, messageBean, file);
                    /**
                     * 文件发送成功
                     * 1:进行数据库更新－－ 暂放
                     * 2:数据更新
                     * 4:进行消息发送
                     *
                     */
                    if (fileTask.fileID != null) {

                        if (repeatMessage != null) {
                            repeatMessage.setFileSendState(RepeatMessage.SUCCESS);
                            repeatMessage.getMessageBean().file_id = fileTask.fileID;
                            repeatMessage.getMessageBean().fileLength = file.length();
//                          repeatSendMessageHandler.updateMessage(repeatMessage);
                            repeatMessage.setMessageCount(0);
                            repeatSendMessageHandler.addMessage(repeatMessage);// 消息添加 发送
                        }else{
                            LogCatLog.d(TAG,"消息为null ");
                        }
                    }
                }

                @Override
                public void handleing(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "语音文件传输中");
                    /**
                     *
                     */
                    if (repeatMessage != null) {
                        repeatMessage.setFileSendState(RepeatMessage.HANDLEING);
                        repeatSendMessageHandler.updateMessage(repeatMessage);
                    }
                }

                @Override
                public void failure(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "语音文件传输失败");
                    /**
                     * 传输失败进行重传
                     *
                     */
                    if (repeatMessage != null) {
                        repeatMessage.setFileSendState(RepeatMessage.FAILURE);
                        repeatSendMessageHandler.updateMessage(repeatMessage);
                    }
                }
            });
        } else {
            LogCatLog.e(TAG, "语音文件传输失败,语音文件不存在！");
            // 这条音频消息制为发送失败
            XLApplication.repeatSendMessageHandler.removeMessage(messageBean.msgKey);// 发送失败 删除文件消息，不进行重发
          //  new MessageDBHandler(mContext).sendFailMsg(messageBean.msgKey, messageBean);

            messageBean.msgStatus= BorrowConstants.MSGSTATUS_FAIL;
            MessageHandler.notifySendMsgStatus(messageBean);



        }

    }

    /**
     * 发送图片消息
     *
     * @param mContext
     * @param xlId        // 当前用户ID
     * @param messageBean
     */
    public void sendImageMessage(Context mContext, final int toChatType, final long xlId, final MessageBean messageBean) {

        final File file = new File(messageBean.msgContent);
        if (file.exists()) {
            final RepeatMessage repeatMessage = repeatSendMessageHandler.getMessage(messageBean.msgKey);
            FileUtils.uploadFile(mContext, xlId, messageBean.msgContent, new FileMessageListener<FileTask>() {
                @Override
                public void success(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "图片文件传输成功");
                    /**
                     * 文件发送成功
                     * 1:进行数据库更新-- 暂放
                     * 2:删除文件消息
                     * 3:进行消息添加
                     * 4:进行消息发送
                     */
                    //imageMessage(fileTask, toChatType, xlId, messageBean, file);
                    if (fileTask.fileID != null) {

                        if(repeatMessage != null) {
                            repeatMessage.setFileSendState(RepeatMessage.SUCCESS);
                            repeatMessage.getMessageBean().file_id = fileTask.fileID;
                            repeatMessage.getMessageBean().fileLength = file.length();
                            repeatMessage.setMessageCount(0);
//                          repeatSendMessageHandler.updateMessage(repeatMessage);
                            repeatSendMessageHandler.addMessage(repeatMessage);
                        }else{
                            LogCatLog.d(TAG,"消息为null ");
                        }
                    }
                }

                @Override
                public void handleing(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "图片文件传输中");
                    if(repeatMessage != null) {
                        repeatMessage.setFileSendState(RepeatMessage.HANDLEING);
                        repeatSendMessageHandler.updateMessage(repeatMessage);
                    }
                }

                @Override
                public void failure(int statusCode, FileTask fileTask) {
                    LogCatLog.d(TAG, "图片文件传输失败");
                    /**
                     * 传输失败进行重传3次
                     *
                     */
                    if(repeatMessage != null) {
                        repeatMessage.setFileSendState(RepeatMessage.FAILURE);
                        repeatSendMessageHandler.updateMessage(repeatMessage);
                    }
                }
            });
        } else {
            LogCatLog.e(TAG, "图片文件传输失败,图片文件不存在！");
            XLApplication.repeatSendMessageHandler.removeMessage(messageBean.msgKey);// 发送失败 删除文件消息，不进行重发
            //new MessageDBHandler(mContext).sendFailMsg(messageBean.msgKey, messageBean);
            messageBean.msgStatus= BorrowConstants.MSGSTATUS_FAIL;
            MessageHandler.notifySendMsgStatus(messageBean);
        }
    }

}

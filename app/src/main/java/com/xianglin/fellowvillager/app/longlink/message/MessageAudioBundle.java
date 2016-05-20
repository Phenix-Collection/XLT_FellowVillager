package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 音频消息绑定
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-07
 */
public class MessageAudioBundle extends MessageBundle {

    private int messageType = 3;// 图片消息类型
    /**
     * 文件长度
     */
    private long fileLength;

    /**
     * 文件时间
     */
    private int fileTime;
    /**
     * 文件ID
     */
    private String fileId;


    public MessageAudioBundle(String userId, String toId,int sendType,String msgkey) {
        super(userId, toId,sendType,msgkey);
    }

    public MessageAudioBundle(String userId,
                              String fromFigure,
                              String toId,
                              String toFigure,
                              int sendType,
                              String msgKey,
                              Integer lifetime) {
        super(userId, fromFigure,toId,toFigure,sendType,msgKey,lifetime);

    }
    @Override
    public String bundleTextValues(String contentValues) {
        return toString(contentValues);
    }

    @Override
    public String toString(String contentValues) {

        Md md = getDefMd();
        md.setMessageType(messageType);
        md.setFileLength(fileLength);
        md.setFileTime(fileTime);
        md.setFileId(fileId);
        LogCatLog.d(TAG, "要发送音频信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }

    public MessageAudioBundle setFileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public MessageAudioBundle setFileTime(int fileTime) {
        this.fileTime = fileTime;
        return this;
    }

    public MessageAudioBundle setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }
}

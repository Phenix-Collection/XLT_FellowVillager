package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 图片消息绑定
 *
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-07
 */
public class MessageImageBundle extends MessageBundle {




    private int messageType = 2;// 图片消息类型

    /**
     * 图片大小
     */
    private String imgSize;

    /**
     * 文件长度
     */
    private long fileLength;

    /**
     * 文件Id
     */
    private String fileId;

    public MessageImageBundle(String userId,String toId,int sendType,String msgkey){
        super(userId,toId,sendType,msgkey);
    }

    public MessageImageBundle(String userId,
                              String fromFigure,
                              String toId,
                              String toFigure,
                              int sendType,
                              String msgKey,Integer lifetime) {
        super(userId, fromFigure,toId,toFigure,sendType,msgKey,lifetime);

    }

    @Override
    public String bundleTextValues(String contentValues) {
        return toString(contentValues);
    }


    @Override
    public String toString(String contentValues) {

        Md md  = getDefMd();
        md.setMessageType(messageType);
        md.setImgSize(imgSize);
        md.setFileLength(fileLength);
        md.setFileId(fileId);
        LogCatLog.d(TAG, "要发送图片信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);

    }

    public MessageImageBundle setMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

    public MessageImageBundle setImgSize(String imgSize) {
        this.imgSize = imgSize;
        return this;
    }

    public MessageImageBundle setFileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public MessageImageBundle setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }
}

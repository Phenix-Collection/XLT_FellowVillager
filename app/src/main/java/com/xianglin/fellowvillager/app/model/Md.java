package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;

/**
 * 消息主体类
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-03
 */
public class Md implements Serializable {


    /**
     * 当前乡邻ID
     */
    private String fromid;

    /**
     * 发送身份ID
     */
    private String fromFigure;

    /**
     * 要发送的对方的乡邻ID
     */
    private String toid;
    /**
     * 接受身份ID
     */
    private String toFigure;

    /**
     * 群id
     */
    private String groupId;

    /**
     * 发送方设备类型
     */
    private String appType;

    /**
     * 发送类型
     * 0：点对点消息
     * 1：群消息
     * 2：系统消息
     */
    private Integer sendType;
    /**
     * 消息类容类型
     * 0：默认值，不限
     * 1：文字信息
     * 2：图片
     * 3：音频
     * 4：视频
     * 5：名片
     * 6：红包
     * 7：商品
     * 8：新闻
     * 11：系统消息
     */
    private Integer messageType;

    /**
     * 发送方设备号
     */
    private String deviceId;

    /**
     * Session信息
     */
    private String sessionId;

    /**
     * 消息key
     */
    private Long msgKey;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 日期
     */
    private String msgDate;

    /**
     * 文件大小
     */
    private long fileLength;

    /**
     * 图片像素
     */
    private String imgSize;

    /**
     * 音频(视频)时长
     */
    private Integer fileTime;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 应答类型
     */
    private int replyType;

    /**
     * 客户端消息sKey
     */
    private String sKey = "";

    private int noticeType;


    private String appid;

    private String mct;

    /** 私密消息的生存期，单位：秒，负值和0表示不是私密消息 */
    private Integer lifetime = -1;

    public boolean isPrivate() {
        return lifetime > 0;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMct() {
        return mct;
    }

    public void setMct(String mct) {
        this.mct = mct;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(Long msgKey) {
        this.msgKey = msgKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public long getFileLength() {
        return fileLength;
    }

    public Md setFileLength(long fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public String getImgSize() {
        return imgSize;
    }

    public void setImgSize(String imgSize) {
        this.imgSize = imgSize;
    }

    public Integer getFileTime() {
        return fileTime;
    }

    public void setFileTime(Integer fileTime) {
        this.fileTime = fileTime;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public String getSKey() {
        return sKey;
    }

    public Md setSKey(String sKey) {
        this.sKey = sKey;
        return this;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public Md setNoticeType(int noticeType) {
        this.noticeType = noticeType;
        return this;
    }


    public String getFromFigure() {
        return fromFigure;
    }

    public void setFromFigure(String fromFigure) {
        this.fromFigure = fromFigure;
    }

    public String getToFigure() {
        return toFigure;
    }

    public void setToFigure(String toFigure) {
        this.toFigure = toFigure;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public Integer getLifetime() {
        return lifetime;
    }

    public void setLifetime(Integer lifetime) {
        this.lifetime = lifetime;
    }
}


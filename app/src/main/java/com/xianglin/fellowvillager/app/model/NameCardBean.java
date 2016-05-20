/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;

/**
 * 名片
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/6 17:02  XLXZ Exp $
 */
public class NameCardBean implements Serializable {

    //ui字段
    private String figureId;//需要显示的figure id
    private String name; //名字
    private String imgId;//userId
    private String head_image_path;//头像
    private String Remarks; //备注
    public String msgDate; //创建时间

    //逻辑字段
    private String msg_key;// 消息KEY

    private int type;//0：个人名片，1：群名片
    private int isopen;// 是否查看过详情
    private String userId;//当前用户id
    private String toChatId;//接受人的id toChatId

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getHead_image_path() {
        return head_image_path;
    }

    public void setHead_image_path(String head_image_path) {
        this.head_image_path = head_image_path;
    }


    public String getFigureId() {
        return figureId;
    }

    public void setFigureId(String figureId) {
        this.figureId = figureId;
    }

    public String getToChatId() {
        return toChatId;
    }

    public void setToChatId(String toChatId) {
        this.toChatId = toChatId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsopen() {
        return isopen;
    }

    public void setIsopen(int isopen) {
        this.isopen = isopen;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    @Override
    public String toString () {
        return "NameCardBean{" +
                "figureId='" + figureId + '\'' +
                ", name='" + name + '\'' +
                ", imgId='" + imgId + '\'' +
                ", head_image_path='" + head_image_path + '\'' +
                ", Remarks='" + Remarks + '\'' +
                ", msgDate='" + msgDate + '\'' +
                ", msg_key='" + msg_key + '\'' +
                ", type=" + type +
                ", isopen=" + isopen +
                ", userId='" + userId + '\'' +
                ", toChatId='" + toChatId + '\'' +
                '}';
    }
}

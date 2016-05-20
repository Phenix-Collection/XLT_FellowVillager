package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;

/**
 * @author  james
 * 用于通知 扩展参数
 *
 */
public class Extras  implements Serializable{


    /**
     * {"fromId":"1000000000001317","msgKey":1458613899611320,
     * "replyType":0,"toId":"1000000000001320","msgDate":1458613899659,
     * "sendType":0,"toFigure":"89055908","messageType":1,"message":"[害羞]",
     * "fileLength":0,"mct":"1458614503845",
     * "fromFigure":"88966746","appType":"Android","sKey":"145861450379105175","noticeType":0}
     */
    /**
     *  发送人ID
     */
    private String  fromid;

    //TODO james 通知新加的
    /**
     * 接受人角色ID
     */
    private String  tfid;

    /**
     * 发送人的角色ID
     */
    private String  ffid;

    /**
     * 发送类型
     */
    private String sendType;
    /**
     * 接收人ID
     */
    private String toid;


    private String id;

    private String type;

    /**
     * 群ID
     */
    private String gid;

    /**
     *
     */
    private String fid;
    /**
     * 数量
     */
    private String  count;

    /**
     * 私密销毁时间
     */
    private int lifeTime;


    /**
     * 是否从聊天界面点入
     * 如果是聊天界面 需设置为true
     * 1012 成功
     * 1011 报错
     */
    private boolean isChatMain;

    public Extras(){

    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getTfid() {
        return tfid;
    }

    public void setTfid(String tfid) {
        this.tfid = tfid;
    }

    public String getFfid() {
        return ffid;
    }

    public void setFfid(String ffid) {
        this.ffid = ffid;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public boolean isChatMain() {
        return isChatMain;
    }

    public void setChatMain(boolean chatMain) {
        isChatMain = chatMain;
    }

    @Override
    public String toString() {
        return "Extras{" +
                "fromid='" + fromid + '\'' +
                ", tfid='" + tfid + '\'' +
                ", ffid='" + ffid + '\'' +
                ", sendType='" + sendType + '\'' +
                ", toid='" + toid + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", gid='" + gid + '\'' +
                ", fid='" + fid + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
    /**
     * 是否为私密消息
     */
    public boolean isPrivate(){

        if (lifeTime > 0)
            return true;

            return false;
    }

}

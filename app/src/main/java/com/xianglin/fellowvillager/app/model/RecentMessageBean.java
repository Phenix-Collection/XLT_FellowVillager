package com.xianglin.fellowvillager.app.model;

/**
 *
 */
public class RecentMessageBean     {
    private String xlId;

    private String xlListId; //群or单聊  id
    private int xlListType; //群or单聊  public static int CHATTYPE_SINGLE = 0;//发送给用户  	public static int CHATTYPE_GROUP
    // =1;//发送到群主
    private String xlImagePath;

    private String file_id;


    /**
     * 是否是私密消息
     */
    private boolean isPrivate;

    /**
     * 消息状态
     */
    private int msgStatus;

    /**
     * 当前角色id
     */
    private String figureId;//

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    private String createdate;//

    /**消息是否过期
     * @return
     */
    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    private boolean isExpired;//

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    private String contactId;//

    private String issend;//是否是发送 0 false 1 true

    private String xlListTitle;//备注or昵称
    private String xlLastMsg;
    private String xlMsgNum;
    private String xlLastTime;
    private String msg_type;//引用MessageChatAdapter中的常量

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    /**
     * true是陌生人发来的消息  false群或者联系人发来的消息
     */
    private boolean isstranger;

    public String getXlListId() {
        return xlListId;
    }

    public void setXlListId(String xlListId) {
        this.xlListId = xlListId;
    }

    public RecentMessageBean() {

    }

    public boolean isstranger() {
        return isstranger;
    }

    public void setIsstranger(boolean isstranger) {
        this.isstranger = isstranger;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getXlMsgNum() {
        return xlMsgNum;
    }

    public void setXlMsgNum(String xlMsgNum) {
        this.xlMsgNum = xlMsgNum;
    }

    public String getXlId() {
        return xlId;
    }

    public void setXlId(String xlId) {
        this.xlId = xlId;
    }

    public String getIssend() {
        return issend;
    }

    public void setIssend(String issend) {
        this.issend = issend;
    }

    public int getXlListType() {
        return xlListType;
    }

    public void setXlListType(int xlListType) {
        this.xlListType = xlListType;
    }

    public String getXlImagePath() {
        return xlImagePath;
    }

    public void setXlImagePath(String xlImagePath) {
        this.xlImagePath = xlImagePath;
    }

    public String getXlListTitle() {
        return xlListTitle;
    }

    public void setXlListTitle(String xlListTitle) {
        this.xlListTitle = xlListTitle;
    }

    public String getXlLastMsg() {
        return xlLastMsg;
    }

    public void setXlLastMsg(String xlLastMsg) {
        this.xlLastMsg = xlLastMsg;
    }

    public String getXlLastTime() {
        return xlLastTime;
    }

    public void setXlLastTime(String xlLastTime) {
        this.xlLastTime = xlLastTime;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }


    public String getFigureId() {
        return figureId;
    }

    public void setFigureId(String figureId) {
        this.figureId = figureId;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
    }


    /**
     * 私密消息时间
     */
    public Integer lifetime = -1;//

    @Override
    public String toString() {
        return "RecentMessageBean{" +
                "xlId='" + xlId + '\'' +
                ", xlListId='" + xlListId + '\'' +
                ", xlListType=" + xlListType +
                ", xlImagePath='" + xlImagePath + '\'' +
                ", file_id='" + file_id + '\'' +
                ", msgStatus=" + msgStatus +
                ", figureId='" + figureId + '\'' +
                ", issend='" + issend + '\'' +
                ", xlListTitle='" + xlListTitle + '\'' +
                ", xlLastMsg='" + xlLastMsg + '\'' +
                ", xlMsgNum='" + xlMsgNum + '\'' +
                ", xlLastTime='" + xlLastTime + '\'' +
                ", msg_type='" + msg_type + '\'' +
                ", isstranger=" + isstranger +
                '}';
    }
}

package com.xianglin.fellowvillager.app.model;

import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 联系人
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class Contact extends BaseBean implements Serializable {

    /**
     * 乡邻ID
     */
    public String xlID;


    /**
     * A-xx B-xx
     */

    /**
     * 内容类型
     */
    public static final int ITEM = 0;
    /**
     * 标题类型
     */
    public static final int SECTION = 1;

    public String section;

    public int type;//是否内容or标题

    /**
     * 乡邻联系人ID
     */
    public String xlUserID;


    /**
     * 乡邻联系人昵称
     */
    public String xlUserName;

    /**
     * 乡邻联系人图像
     */
    public String xlImagePath;

    /**
     * 乡邻联系人备注
     */
    public String xlReMarks;
    /**
     * 乡邻联系人是否联系人
     */
    public String isContact;//0 ,1

    public String pinying;//0 ,1

    /**
     * true就在某个群中
     */
    public boolean isgroupmember;
    public boolean isSelected;

    public int groupnumber;//临时变量 用户的群数量

    public String file_id;//本地头像的文件id

    /**
     * 当前角色id
     */
    public String info;//


    /**
     * 性别
     */
    public String gender;//
    /**
     * 好关系来源
     */

    public RelationEstablishType relationshipInfo;//
    /**
     * 用户小图图片
     */

    public String imagePathThumbnail;//


    /**
     * 创建联系的时间
     */
    public String relationshipTime;//

    /**
     * 当前角色id
     */
    public String figureId;//

    public String contactId;//

    /**
     * 联系人角色id
     */
    public String figureUsersId;//
    /**
     * 联系人层级
     */
    public ContactLevel contactLevel;//

    public String updatedate;//最后聊天时间
    public String createdate;//最后聊天时间
    /**
     * 联系人排序分值
     */
    public String score;
    /**
     * 性取向
     */
    public String sexualorientation;
    /**
     * 当前角色的所属身份
     */
    public ArrayList<FigureMode> figureGroup;

    /**
     * 是否是私密聊天
     */
    public boolean isPrivateSession;

    /**
     * 消息销毁时间设置
     */
    public String privateSessionDate;

    public Contact(Builder builder, int type) {

        this.type = type;
        this.xlID = builder.xlID;
        this.xlUserID = builder.xlUserID;
        this.xlUserName = builder.xlUserName;
        this.xlImagePath = builder.xlImagePath;
        this.xlReMarks = builder.xlReMarks;
        this.section = builder.section;
        this.isgroupmember = builder.isgroupmember;
        this.file_id = builder.file_id;
        this.groupnumber = builder.groupnumber;
        this.isContact = builder.isContact;
        this.pinying = builder.pinying;
        this.figureId = builder.figureId;
        this.figureUsersId = builder.figureUsersId;
        this.info = builder.info;
        this.contactLevel = builder.contactLevel;
        this.gender = builder.gender;
        this.relationshipInfo = builder.relationshipInfo;
        this.imagePathThumbnail = builder.imagePathThumbnail;
        this.relationshipTime = builder.relationshipTime;
        this.score = builder.score;
        this.sexualorientation = builder.sexualorientation;
        this.contactId = builder.contactId;
        this.figureGroup = builder.figureGroup;
        this.updatedate = builder.updatedate;
        this.createdate = builder.createdate;
        this.isPrivateSession = builder.isPrivateSession;
        this.privateSessionDate = builder.privateSessionDate;
    }


    public static class Builder {

        /**
         * 当前角色id
         */
        public String info;//
        public String contactId;//
        public String createdate;//

        /**
         * 当前角色id
         */
        public ContactLevel contactLevel;//
        public ArrayList<FigureMode> figureGroup;//

        /**
         * 性别
         */
        public String gender;//
        /**
         * 好关系来源
         */

        public RelationEstablishType relationshipInfo;//
        /**
         * 用户小图图片
         */

        public String imagePathThumbnail;//

        /**
         * 创建联系的时间
         */
        public String relationshipTime;//

        public final int type;//是否内容or标题

        public int groupnumber;//临时变量 用户的群数量
        /**
         * 乡邻ID
         */
        private String xlID;

        private String section;
        private String pinying;

        /**
         * 乡邻联系人ID
         */
        private String xlUserID;
        private String isContact;

        /**
         * 乡邻联系人昵称
         */
        private String xlUserName;

        /**
         * 乡邻联系人图像
         */
        private String xlImagePath;

        /**
         * 乡邻联系人备注
         */
        private String xlReMarks;
        private String figureId;
        private String figureUsersId;

        private boolean isgroupmember;
        private String file_id;

        private String updatedate;//最后聊天时间

        /**
         * 联系人排序分值
         */
        private String score;
        /**
         * 性取向
         */
        private String sexualorientation;

        /**
         * 如果type是标题 只需要传section
         *
         * @param type
         */
        public Builder(int type) {
            this.type = type;
        }

        /**
         * 是否是私密聊天
         */
        private boolean isPrivateSession;

        /**
         * 消息销毁时间设置
         */
        private  String privateSessionDate;

        /**
         * 设置私密聊天开关
         * @param isPrivateSession
         * @return
         */
        public Builder isPrivateSession(boolean isPrivateSession){
            this.isPrivateSession = isPrivateSession;
            return this;
        }

        /**
         * 私密聊天时间
         * @param privateSessionDate
         * @return
         */
        public Builder privateSessionDate(String privateSessionDate){
            this.privateSessionDate = privateSessionDate;
            return this;
        }


        public Builder xlID(String xlID) {
            this.xlID = xlID;
            return this;

        }

        public Builder createdate(String createdate) {
            this.createdate = createdate;
            return this;

        }

        public Builder score(String score) {
            this.score = score;
            return this;

        }

        public Builder contactId(String contactId) {
            this.contactId = contactId;
            return this;

        }

        public Builder updatedate(String updatedate) {
            this.updatedate = updatedate;
            return this;

        }

        public Builder figureGroup(ArrayList<FigureMode> figureGroup) {
            this.figureGroup = figureGroup;
            return this;

        }

        public Builder sexualorientation(String sexualorientation) {
            this.sexualorientation = sexualorientation;
            return this;
        }

        public Builder groupnumber(int groupnumber) {
            this.groupnumber = groupnumber;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }


        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder relationshipInfo(RelationEstablishType relationshipInfo) {
            this.relationshipInfo = relationshipInfo;
            return this;
        }

        public Builder imagePathThumbnail(String imagePathThumbnail) {
            this.imagePathThumbnail = imagePathThumbnail;
            return this;
        }

        public Builder relationshipTime(String relationshipTime) {
            this.relationshipTime = relationshipTime;
            return this;
        }

        public Builder contactLevel(ContactLevel contactLevel) {
            this.contactLevel = contactLevel;
            return this;
        }

        public Builder xlUserId(String xlUserID) {
            this.xlUserID = xlUserID;
            return this;
        }

        public Builder xlUserName(String xlUserName) {
            this.xlUserName = xlUserName;
            return this;
        }

        public Builder figureUsersId(String figureUsersId) {
            this.figureUsersId = figureUsersId;
            return this;
        }

        public Builder xlImagePath(String xlImagePath) {
            this.xlImagePath = xlImagePath;
            return this;
        }

        public Builder xlReMarks(String xlReMarks) {
            this.xlReMarks = xlReMarks;
            return this;
        }

        public Builder section(String section) {
            this.section = section;
            return this;
        }

        public Builder isgroupmember(boolean isgroupmember) {
            this.isgroupmember = isgroupmember;
            return this;
        }

        public Builder file_id(String file_id) {
            this.file_id = file_id;
            return this;
        }

        public Builder isContact(String isContact) {
            this.isContact = isContact;
            return this;
        }

        public Builder figureId(String figureId) {
            this.figureId = figureId;
            return this;
        }

        public Builder pinying(String pinying) {
            this.pinying = pinying;
            return this;
        }

        public Contact build() {
            return new Contact(this, type);
        }
    }


    public String getXlUserName() {
        return xlUserName;
    }

    public void setXlUserName(String xlUserName) {
        this.xlUserName = xlUserName;
    }

    public String getXlReMarks() {
        return xlReMarks;
    }

    public void setXlReMarks(String xlReMarks) {
        this.xlReMarks = xlReMarks;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Contact)) {
            return false;
        }
        return getUIName().equals(((Contact) o).getUIName());
    }

    @Override
    public String toString() {
        return "Contact{" +
                "xlID='" + xlID + '\'' +
                ", section='" + section + '\'' +
                ", type=" + type +
                ", xlUserID='" + xlUserID + '\'' +
                ", xlUserName='" + xlUserName + '\'' +
                ", xlImagePath='" + xlImagePath + '\'' +
                ", xlReMarks='" + xlReMarks + '\'' +
                ", isContact='" + isContact + '\'' +
                ", pinying='" + pinying + '\'' +
                ", isgroupmember=" + isgroupmember +
                ", groupnumber=" + groupnumber +
                ", file_id='" + file_id + '\'' +
                ", info='" + info + '\'' +
                ", gender='" + gender + '\'' +
                ", relationshipInfo=" + relationshipInfo +
                ", imagePathThumbnail='" + imagePathThumbnail + '\'' +
                ", relationshipTime='" + relationshipTime + '\'' +
                ", figureId='" + figureId + '\'' +
                ", figureUsersId='" + figureUsersId + '\'' +
                ", contactLevel=" + contactLevel +
                ", score='" + score + '\'' +
                ", sexualorientation='" + sexualorientation + '\'' +
                ", figureGroup=" + figureGroup +
                ", contactId=" + contactId +
                '}';
    }

    public static enum ContactLevel {
        HIGH,//--高频联系人;
        NORMAL,//--普通联系人;
        LOW,//--低频联系人;
        UMKNOWN,//--陌生人;
        BLACK;//--黑名单.

        private ContactLevel() {
        }

        public static ContactLevel valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException(" enum ContactLevel Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static enum RelationEstablishType {
        DEFAULT, //--默认方式;
        NAME_CARD,//--名片;
        QRCODE,//--二维码;
        PHONE_CONCACTS;//-,//-手机通讯录.

        private RelationEstablishType() {
        }

        public static RelationEstablishType valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException(" enum RelationEstablishType Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    /**
     * 获取在ui上显示的 昵称
     *
     * @return
     */
    public String getUIName() {

        if (TextUtils.isEmpty(xlReMarks)) {
            return xlUserName;
        } else {
            return xlReMarks;
        }

    }


}

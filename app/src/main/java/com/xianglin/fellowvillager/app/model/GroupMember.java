package com.xianglin.fellowvillager.app.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 项目名称：乡邻小站
 * 类描述：r
 * 创建人：何正纬
 * 创建时间：2015/11/27 11:27
 * 修改人：hezhengwei
 * 修改时间：2015/11/27 11:27
 * 修改备注：
 */
public class GroupMember implements Serializable {

    public String xluserid;  //
    @Deprecated
    public String xlContactId;
    public String xlGroupId;
    public String xlRemarkName;
    public String xlUserName;
    public String xlImgPath;
    public String groupmemberid;

    public String isContact;//0false 1true
    public String isOwner;  //
    public String sortLetters;
    public String file_id;
    public String localgroupId;

    public String gender;
    public String sexualOrientation;
    public String individualitySignature;
    public long joinTime;


    /**
     * 获取在ui上显示的 昵称
     *
     * @return
     */
    public String getUIName() {

        if (TextUtils.isEmpty(xlRemarkName)) {
            return xlUserName;
        } else {
            return xlRemarkName;
        }

    }

    /**
     * 联系人角色id
     */
    public String figureUsersId;//

    /**
     * 当前角色id
     */
    public String figureId;//

    private GroupMember(Builder builder) {
        xluserid = builder.xluserid;
        xlContactId = builder.xlContactId;
        xlGroupId = builder.xlGroupId;
        xlRemarkName = builder.xlRemarkName;
        xlImgPath = builder.xlImgPath;
        isContact = builder.isContact;
        isOwner = builder.isOwner;
        sortLetters = builder.sortLetters;
        file_id = builder.file_id;
        groupmemberid = builder.groupmemberid;
        figureUsersId = builder.figureUsersId;
        figureId = builder.figureId;
        xlUserName = builder.xlUserName;

        gender = builder.gender;
        sexualOrientation = builder.sexualOrientation;
        individualitySignature = builder.individualitySignature;
        joinTime = builder.joinTime;
        localgroupId = builder.localgroupId;
    }

    public static final class Builder {
        private String xluserid;
        private String xlContactId;
        private String xlGroupId;
        private String xlRemarkName;
        private String xlImgPath;
        private String isContact;
        private String isOwner;
        private String sortLetters;
        private String file_id;
        private String groupmemberid;
        private String figureUsersId;
        private String figureId;
        private String xlUserName;
        private String gender;
        private String sexualOrientation;
        private String individualitySignature;
        private long joinTime;
        private String localgroupId;



        public Builder() {
        }

        public Builder gender(String val) {
            gender = val;
            return this;
        }
        public Builder localgroupId(String val) {
            localgroupId = val;
            return this;
        }

        public Builder joinTime(long val) {
            joinTime = val;
            return this;
        }

        public Builder sexualOrientation(String val) {
            sexualOrientation = val;
            return this;
        }

        public Builder individualitySignature(String val) {
            individualitySignature = val;
            return this;
        }

        public Builder xluserid(String val) {
            xluserid = val;
            return this;
        }

        public Builder xlUserName(String val) {
            xlUserName = val;
            return this;
        }

        public Builder figureUsersId(String val) {
            figureUsersId = val;
            return this;
        }

        public Builder figureId(String val) {
            figureId = val;
            return this;
        }

        public Builder file_id(String val) {
            file_id = val;
            return this;
        }

        @Deprecated
        public Builder xlContactId(String val) {
            xlContactId = val;
            return this;
        }

        public Builder xlGroupId(String val) {
            xlGroupId = val;
            return this;
        }

        public Builder xlRemarkName(String val) {
            xlRemarkName = val;
            return this;
        }

        public Builder xlImgPath(String val) {
            xlImgPath = val;
            return this;
        }

        public Builder isContact(String val) {
            isContact = val;
            return this;
        }

        public Builder isOwner(String val) {
            isOwner = val;
            return this;
        }

        public Builder sortLetters(String val) {
            sortLetters = val;
            return this;
        }

        public Builder groupmemberid(String val) {
            groupmemberid = val;
            return this;
        }

        public GroupMember build() {
            return new GroupMember(this);
        }
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "xluserid='" + xluserid + '\'' +
                ", xlContactId='" + xlContactId + '\'' +
                ", xlGroupId='" + xlGroupId + '\'' +
                ", xlRemarkName='" + xlRemarkName + '\'' +
                ", xlImgPath='" + xlImgPath + '\'' +
                ", groupmemberid='" + groupmemberid + '\'' +
                ", isContact='" + isContact + '\'' +
                ", isOwner='" + isOwner + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                ", file_id='" + file_id + '\'' +
                '}';
    }
}

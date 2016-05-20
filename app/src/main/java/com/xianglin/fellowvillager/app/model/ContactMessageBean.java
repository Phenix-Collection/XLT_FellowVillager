/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

/**
 * 联系人消息体
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/23 15:16  XLXZ Exp $
 */
public class ContactMessageBean {

    public ContactMessageBean(Builder builder) {
        this.xlID = builder.xlID;
        this.xlUserID = builder.xlUserID;
        this.msgKey = builder.msgKey;
        this.createDate = builder.createDate;
        this.xlgroupID = builder.xlgroupID;
        this.xlgroupmemberid = builder.xlgroupmemberid;
        this.msgLocalKey = builder.msgLocalKey;
        this.figureId = builder.figureId;
        this.figureUsersId = builder.figureUsersId;
        this.contactId = builder.contactId;
        this.localgroupId = builder.localgroupId;
    }
    public String localgroupId;
    /**
     * 乡邻ID
     */
    public String xlID;
    /**
     * 乡邻联系人ID
     */
    public String xlUserID;
    /**
     * 乡邻群ID
     */
    public String xlgroupID;
    /**
     * 消息Key
     */
    public String msgKey;
    public String msgLocalKey;
    /**
     * 创建时间
     */
    public String xlgroupmemberid;

    /**
     * 创建时间
     */
    public String createDate;

    /**
     * 联系人角色id
     */
    public String figureUsersId;//

    /**
     * 当前角色id
     */
    public String figureId;//

    public String contactId;//

    public static class Builder {

        /**
         * 乡邻群ID
         */
        public String xlgroupID;
        public String figureUsersId;
        public String figureId;
        public String contactId;//
        public String localgroupId;
        private String xlID;
        private String msgLocalKey;
        /**
         * 乡邻联系人ID
         */
        private String xlUserID;
        /**
         * 消息Key
         */
        private String msgKey;

        /**
         * 群成员id
         */
        private String xlgroupmemberid;
        /**
         * 创建时间
         */
        private String createDate;

        public Builder xlID(String xlID) {
            this.xlID = xlID;
            return this;
        }
        public Builder localgroupId(String localgroupId) {
            this.localgroupId = localgroupId;
            return this;
        }

        public Builder xlUserID(String xlUserID) {
            this.xlUserID = xlUserID;
            return this;
        }

        public Builder msgKey(String msgKey) {
            this.msgKey = msgKey;
            return this;
        }

        public Builder contactId(String contactId) {
            this.contactId = contactId;
            return this;
        }

        public Builder figureUsersId(String figureUsersId) {
            this.figureUsersId = figureUsersId;
            return this;
        }

        public Builder xlgroupID(String xlgroupID) {
            this.xlgroupID = xlgroupID;
            return this;
        }

        public Builder figureId(String figureId) {
            this.figureId = figureId;
            return this;
        }
        public Builder msgLocalKey(String msgLocalKey) {
            this.msgLocalKey = msgLocalKey;
            return this;
        }

        public Builder createDate(String createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder xlgroupmemberid(String xlgroupmemberid) {
            this.xlgroupmemberid = xlgroupmemberid;
            return this;
        }

        public ContactMessageBean build() {
            return new ContactMessageBean(this);
        }


    }

    @Override
    public String toString() {
        return "ContactMessageBean{" +
                "xlID='" + xlID + '\'' +
                ", xlUserID='" + xlUserID + '\'' +
                ", msgKey='" + msgKey + '\'' +
                '}';
    }
}



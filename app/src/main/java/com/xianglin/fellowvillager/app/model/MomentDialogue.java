package com.xianglin.fellowvillager.app.model;

import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;

/**
 * 临时对话列表
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-17
 */
public class MomentDialogue {
    /**
     * 乡邻ID
     */
    public String xlID; //乡邻ID
    /**
     * 联系人ID
     */
    public String xlUserID;//XLUSERID 联系人ID
    /**
     * 乡邻群组ID
     */
    public String xlGroupID;//XLGROUPID 乡邻群组ID
    /**
     * 最后聊天时间
     */
    public String xlLastMsgDate;//LAST_MSG_DATE 最后聊天时间

    /**
     * 联系人角色id
     */
    public String figureUsersId;//


    public String contactId;//

    /**
     * 当前角色id
     */
    public String figureId;//
    public String localGroupId;//


    public MomentDialogue(Builder builder) {
        this.xlID = builder.xlID;
        this.xlUserID = builder.xlUserID;
        this.xlGroupID = builder.xlGroupID;
        this.xlLastMsgDate = builder.xlLastMsgData;
        this.figureUsersId = builder.figureUsersId;
        this.figureId = builder.figureId;
        this.contactId = builder.contactId;
        this.localGroupId = builder.localGroupId;
    }


    public static class Builder {

        /**
         * 乡邻ID
         */
        private String localGroupId;//

        private String xlID; //乡邻ID
        private String figureUsersId; //乡邻ID
        private String figureId; //乡邻ID
        private String contactId; //乡邻ID
        /**
         * 联系人ID
         */
        private String xlUserID;//XLUSERID 联系人ID
        /**
         * 乡邻群组ID
         */
        private String xlGroupID;//XLGROUPID 乡邻群组ID

        private String xlLastMsgData;//LAST_MSG_DATE 最后聊天时间


        public Builder() {

        }

        public Builder xlID(String xlID) {
            this.xlID = xlID;

            return this;
        }

        public Builder localGroupId(String localGroupId) {
            this.localGroupId = localGroupId;

            return this;
        }

        public Builder figureUsersId(String figureUsersId) {
            this.figureUsersId = figureUsersId;

            return this;
        }

        public Builder contactId(String contactId) {
            this.contactId = contactId;

            return this;
        }

        public Builder figureId(String figureId) {
            this.figureId = figureId;

            return this;
        }

        public Builder xlUserID(String xlUserID) {
            this.xlUserID = xlUserID;

            return this;
        }

        public Builder xlGroupID(String xlGroupID) {
            this.xlGroupID = xlGroupID;

            return this;
        }

        /**
         * 需要在toChat设置之后
         * 设置聊天类型
         *
         * @param type @link BorrowConstants.CHATTYPE_GROUP =1;//发送到群主  CHATTYPE_SINGLE = 0;//发送给用户
         * @return
         */
        public Builder setToChatType(int type, MessageBean messageBean) {

            if (type == BorrowConstants.CHATTYPE_GROUP) {
                this.xlGroupID = messageBean.xlID;
                this.localGroupId = GroupDBHandler.getGroupId(messageBean.xlID,messageBean.figureId);
                this.xlUserID = null;
                this.figureUsersId = null;
                this.figureId = messageBean.figureId;
                this.contactId =null;

            } else if (type == BorrowConstants.CHATTYPE_SINGLE) {
                this.xlGroupID = null;
                this.localGroupId=null;
                this.xlUserID = messageBean.xlID;
                this.figureUsersId = messageBean.figureUsersId;
                this.figureId = messageBean.figureId;
                this.contactId(ContactDBHandler.getContactId(messageBean.figureUsersId,messageBean.figureId));
            } else if (type == BorrowConstants.CHATTYPE_SYS) {
                this.xlGroupID = null;
                this.localGroupId=null;
                this.xlUserID = messageBean.xlID;
                this.figureUsersId = messageBean.figureUsersId;
                this.figureId = messageBean.figureId;
                this.contactId(ContactDBHandler.getContactId(messageBean.figureUsersId,messageBean.figureId));
            }
            return this;
        }

        public Builder xlLastMsgData(String xlLastMsgData) {
            this.xlLastMsgData = xlLastMsgData;
            return this;
        }


        public MomentDialogue build() {

            return new MomentDialogue(this);
        }
    }


    @Override
    public String toString() {
        return "MomentDialogue{" +
                "xlID='" + xlID + '\'' +
                ", xlUserID='" + xlUserID + '\'' +
                ", xlGroupID='" + xlGroupID + '\'' +
                ", xlLastMsgDate='" + xlLastMsgDate + '\'' +
                '}';
    }
}

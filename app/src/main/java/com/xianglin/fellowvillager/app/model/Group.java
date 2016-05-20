package com.xianglin.fellowvillager.app.model;

import java.util.ArrayList;

/**
 * 群组
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class Group extends BaseBean {


    /**
     * 乡邻ID
     */
    public String xlID;

    /**
     *
     * 乡邻群组ID
     * 本地不是唯一的
     */
    public String xlGroupID;

    /**
     * 本地唯一id
     */
    public String localGroupId;

    /**
     * 乡邻群组服务端分类名称
     */
    public String groupType;

    /**
     * 乡邻群组本地分类名称
     * A 管理的群
     * B 参与的群
     * C 解散的群
     */
    public String groupCustomType;

    /**
     * 乡邻群组昵称
     */
    public String xlGroupName;

    /**
     * 乡邻群组图像
     */
    public String xlGroupImagePath;

    /**
     * 乡邻群组当前数量
     */

    public String xlGroupCurrentNum;
    /**
     * 乡邻群组最大数量
     */

    public String xlGroupNumMax;

    public String isJoin; //是否加入了此群

    public String file_id;//本地头像的文件id


    public String xlimagePath;
    public String description;
    public String status;
    public String updateGroupTime;
    public String createGroupTime;

    /**
     * 群主唯一标识
     */
    public String  ownerUserId;


    /**
     * 群主身份角色ID
     */
    public String ownerFigureId;
    /**
     * 当前角色的所属身份
     */
    public ArrayList<FigureMode> figureGroup;
    /**
     * 当前角色id
     */
    public String figureId;//

    /**
     * 显示数据拼音的首字母
     */
    public String sortLetters;

    private Group(Builder builder) {
        xlID = builder.xlID;
        xlGroupID = builder.xlGroupID;
        groupType = builder.groupType;
        xlGroupName = builder.xlGroupName;
        xlGroupImagePath = builder.xlGroupImagePath;
        xlGroupCurrentNum = builder.xlGroupCurrentNum;
        xlGroupNumMax = builder.xlGroupNumMax;
        sortLetters = builder.sortLetters;
        file_id = builder.file_id;
        isJoin = builder.isJoin;
        figureId = builder.figureId;
        xlimagePath = builder.xlimagePath;
        description = builder.description;
        status = builder.status;
        updateGroupTime = builder.updateGroupTime;
        createGroupTime = builder.createGroupTime;
        ownerUserId = builder.ownerUserId;
        ownerFigureId = builder.ownerFigureId;
        localGroupId = builder.localGroupId;

    }


    public static final class Builder {
        private String xlID;
        private String xlGroupID;
        private String groupType;
        private String xlGroupName;
        private String xlGroupImagePath;
        private String xlGroupCurrentNum;
        private String xlGroupNumMax;
        private String sortLetters;
        private String file_id;
        private String isJoin;
        private String figureId;
        private String xlimagePath;
        private String description;
        private String status;
        private String updateGroupTime;
        private String createGroupTime;
        private String ownerUserId;
        private String ownerFigureId;
        private String localGroupId;


        public Builder() {
        }

        public Builder ownerUserId(String val) {
            ownerUserId = val;
            return this;
        }
        public Builder localGroupId(String val) {
            localGroupId = val;
            return this;
        }

        public Builder ownerFigureId(String val) {
            ownerFigureId = val;
            return this;
        }
        public Builder xlID(String val) {
            xlID = val;
            return this;
        }
        public Builder xlimagePath(String val) {
            xlimagePath = val;
            return this;
        }
        public Builder updateGroupTime(String val) {
            updateGroupTime = val;
            return this;
        }
        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder createGroupTime(String val) {
            createGroupTime = val;
            return this;
        }
        public Builder status(String val) {
            status = val;
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

        public Builder xlGroupID(String val) {
            xlGroupID = val;
            return this;
        }

        public Builder groupType(String val) {
            groupType = val;
            return this;
        }

        public Builder xlGroupName(String val) {
            xlGroupName = val;
            return this;
        }

        public Builder xlGroupImagePath(String val) {
            xlGroupImagePath = val;
            return this;
        }

        public Builder xlGroupCurrentNum(String val) {
            xlGroupCurrentNum = val;
            return this;
        }

        public Builder xlGroupNumMax(String val) {
            xlGroupNumMax = val;
            return this;
        }

        public Builder isJoin(String val) {
            isJoin = val;
            return this;
        }

        public Builder sortLetters(String val) {
            sortLetters = val;
            return this;
        }

        public Group build() {
            return new Group(this);
        }
    }

    @Override
    public String toString() {
        return "Group{" +
                "xlID='" + xlID + '\'' +
                ", xlGroupID='" + xlGroupID + '\'' +
                ", groupType='" + groupType + '\'' +
                ", xlGroupName='" + xlGroupName + '\'' +
                ", xlGroupImagePath='" + xlGroupImagePath + '\'' +
                ", xlGroupCurrentNum='" + xlGroupCurrentNum + '\'' +
                ", xlGroupNumMax='" + xlGroupNumMax + '\'' +
                ", isJoin='" + isJoin + '\'' +
                ", file_id='" + file_id + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        if (xlGroupID == null) {
            if (other.xlGroupID != null) {
                return false;
            }
        } else if (!xlGroupID.equals(other.xlGroupID)) {
            return false;
        }
        return true;
    }
}

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

import android.text.TextUtils;

import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色实体
 */
public class FigureMode implements Serializable {

    /**
     * 乡邻id
     */
    private String xlId;

    /**
     * 角色id
     */
    private String figureUsersid;

    /**
     * 角色昵称
     */
    private String figureName;
    /**
     * 角色备注
     */
    private String figureXlremarks;
    /**
     * 角色简介
     */
    private String figureInfo;
    /**
     * 角色分组
     */
    private String figureGroup;
    /**
     * 创建时间
     */
    private long createDate;
    /**
     * 更新时间
     */
    private long updateDate;

    /**
     * 头像id
     */
    private String figureImageid;

    /**
     * 关系来源
     */
    private String figureRelationship;
    /**
     * 头像小图
     */
    private String imagePathThumbnail;

    /**
     * 头像大图
     */
    private String imagePpath;


    /**用来拼接msgkey
     * @return
     */
    public String getFigure_usersid_shortid() {
        return figure_usersid_shortid;
    }

    public void setFigure_usersid_shortid(String figure_usersid_shortid) {

        Map<String, FigureMode> modeMap= ContactManager.getInstance().getAllFigureTable();

        List<Map.Entry<String,FigureMode>> list = new ArrayList<Map.Entry<String,FigureMode>>(modeMap.entrySet());

        for (int i=0;i<list.size();i++){
            if(figure_usersid_shortid.equals(list.get(i).getValue().figure_usersid_shortid)){
                //5位随机数重复的可能性比较高
                setFigure_usersid_shortid(Utils.getUniqueMessageForFigureidId());
                return ;
            }
        }
        this.figure_usersid_shortid = figure_usersid_shortid;
        return ;
    }

    private String figure_usersid_shortid;

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public FigureGender getFigureGender() {
        return figureGender;
    }

    public void setFigureGender(FigureGender figureGender) {
        this.figureGender = figureGender;
    }

    public SexualOrientation getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(SexualOrientation sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public Status getFigureStatus() {
        return figureStatus;
    }

    public void setFigureStatus(Status figureStatus) {
        this.figureStatus = figureStatus;
    }
    /**
     * 是否公开
     */
    private String  isOpen;

    /**
     * 性别
     */
    private FigureGender figureGender;

    /**
     * 性取向
     */
    private SexualOrientation sexualOrientation;
    /**
     * 角色状态
     */
    private Status figureStatus;

    public static enum SexualOrientation {
        UNKNOWN	;//未知	可能是用户未设置，或者用户选择保密
        private SexualOrientation( ) {
        }
        public static SexualOrientation valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException(" enum ContactLevel Invalid ordinal");
            }
            return values()[ordinal];
        }
    }
    public static enum Status {
        ACTIVE	,//活动状态	新创建的身份角色状态默认为活动状态
        FREEZE;//	冻结状态
        private Status( ) {
        }
        public static Status valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException(" enum ContactLevel Invalid ordinal");
            }
            return values()[ordinal];
        }
    }
    public static enum FigureGender {
        UNKNOWN	,//未知	可能是用户未设置，或者用户选择保密
        MALE,//	男
        FEMALE	,//女
        PRIVATE;
        private FigureGender( ) {
        }
        public static FigureGender valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException(" enum ContactLevel Invalid ordinal");
            }
            return values()[ordinal];
        }
    }


    public String getXlId() {
        return xlId;
    }

    public void setXlId(String xlId) {
        this.xlId = xlId;
    }

    public String getFigureUsersid() {
        return figureUsersid;
    }

    public void setFigureUsersid(String figureUsersid) {
        this.figureUsersid = figureUsersid;
    }


    public String getFigureName() {
        return figureName;
    }

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }

    public String getFigureXlremarks() {
        return figureXlremarks;
    }

    public void setFigureXlremarks(String figureXlremarks) {
        this.figureXlremarks = figureXlremarks;
    }

    public String getFigureInfo() {
        return figureInfo;
    }

    public void setFigureInfo(String figureInfo) {
        this.figureInfo = figureInfo;
    }

    public String getFigureGroup() {
        return figureGroup;
    }

    public void setFigureGroup(String figureGroup) {
        this.figureGroup = figureGroup;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getFigureImageid() {
        return figureImageid;
    }

    public void setFigureImageid(String figureImageid) {
        this.figureImageid = figureImageid;
    }

    public String getImagePpath() {
        return imagePpath;
    }

    public void setImagePpath(String imagePpath) {
        this.imagePpath = imagePpath;
    }


    public String getFigureRelationship() {
        return figureRelationship;
    }

    public void setFigureRelationship(String figureRelationship) {
        this.figureRelationship = figureRelationship;
    }

    public String getImagePathThumbnail() {
        return imagePathThumbnail;
    }

    public void setImagePathThumbnail(String imagePathThumbnail) {
        this.imagePathThumbnail = imagePathThumbnail;
    }

    /**
     * 有备注则显示备注
     * @return
     */
    public String getUIname() {

        if (!TextUtils.isEmpty(this.figureXlremarks)) {
            return figureXlremarks;
        } else {
            return figureName;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof FigureMode)) {
            return false;
        }
        return getUpdateDate()==(((FigureMode) o).getUpdateDate());
    }

    public static FigureDTO FigureModeToFigureDTO(FigureMode figureMode){
        if (figureMode == null) {
            return null;
        }
        FigureDTO figureDTO=new FigureDTO();
        figureDTO.setFigureId(figureMode.getFigureUsersid());
        figureDTO.setStatus(figureMode.getFigureStatus().name());
        figureDTO.setAvatarUrl(figureMode.getFigureImageid());
        figureDTO.setGender(figureMode.getFigureGender().name());
        figureDTO.setNickName(figureMode.getFigureName());
        figureDTO.setIndividualitySignature(figureMode.getFigureInfo());
        figureDTO.setOpen(figureMode.isOpen.equals("1"));
        return figureDTO;
    }

/*  仅供参考
   public static FigureMode FigureDTOToFigureMode(FigureDTO figureDTO){
        FigureMode figureMode=new FigureMode();
        figureMode.setXlId(PersonSharePreference.getUserID()+"");
        figureMode.setCreateDate();
        figureMode.setFigureGroup("");
        figureMode.setFigureInfo(figureDTO.getIndividualitySignature());
        figureMode.setFigureName(figureDTO.getNickName());
        figureMode.setFigureStatus(Status.valueOf(figureDTO.getStatus()));
        figureMode.setFigureUsersid(figureDTO.getFigureId());
        figureMode.setFigureImageid(figureDTO.getAvatarUrl());
        figureMode.setFigureGender(FigureMode.FigureGender.valueOf(figureDTO.getGender()));
        figureMode.setSexualOrientation(FigureMode.SexualOrientation.valueOf(figureDTO.getSexualOrientation()));
        figureMode.setFigureRelationship(System.currentTimeMillis() + "");
        figureMode.setImagePathThumbnail(System.currentTimeMillis() + "");
        figureMode.setImagePpath("");
        figureMode.setFigureXlremarks("");
        figureMode.setUpdateDate(System.currentTimeMillis());
        figureMode.setIsOpen(figureDTO.isOpen()?"1":"0");//1公开0私有
        return figureMode;
    }*/

    @Override
    public String toString () {
        return "FigureMode{" +
                "xlId='" + xlId + '\'' +
                ", figureUsersid='" + figureUsersid + '\'' +
                ", figureName='" + figureName + '\'' +
                ", figureXlremarks='" + figureXlremarks + '\'' +
                ", figureInfo='" + figureInfo + '\'' +
                ", figureGroup='" + figureGroup + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate=" + updateDate +
                ", figureImageid='" + figureImageid + '\'' +
                ", figureRelationship='" + figureRelationship + '\'' +
                ", imagePathThumbnail='" + imagePathThumbnail + '\'' +
                ", imagePpath='" + imagePpath + '\'' +
                ", isOpen='" + isOpen + '\'' +
                ", figureGender=" + figureGender +
                ", sexualOrientation=" + sexualOrientation +
                ", figureStatus=" + figureStatus +
                '}';
    }
}

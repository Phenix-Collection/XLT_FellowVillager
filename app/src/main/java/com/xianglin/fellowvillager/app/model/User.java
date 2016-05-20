package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;

/**
 * 用户
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-12
 */
public class User implements Serializable {

    public User() {

        super();

    }
    /**
     * 乡邻ID
     */
    public  String xlID;

    /**
     * 设备ID
     */
    public String deviceID;

    /**
     * 乡邻昵称
     */
    public String xlUserName;

    /**
     * 当前角色id
     */
    public String figureId;//
    /**
     * 用户图片路径
     */
    public  String imagePath;

    public User(String xlID, String deviceID, String xlUserName, String imagePath) {
        this.xlID = xlID;
        this.deviceID = deviceID;
        this.xlUserName = xlUserName;
        this.imagePath = imagePath;
    }

    public User(Builder builder){

        xlID = builder.xlID;
        deviceID = builder.deviceID;
        xlUserName = builder.xlUserName;
        imagePath = builder.imagePath;
        figureId = builder.figureId;

    }



    public static class Builder{

        /**
         * 乡邻ID
         */
        private String xlID;
        private String figureId;

        /**
         * 设备ID
         */
        private String deviceID;

        /**
         * 乡邻昵称
         */
        private String xlUserName;

        /**
         * 用户图片路径
         */
        private String imagePath;

        public Builder xlID(String xlID) {
            this.xlID = xlID;
            return this;
        }

        public Builder deviceID(String deviceID) {
            this.deviceID = deviceID;
            return this;
        }

        public Builder xlUserName(String xlUserName) {
            this.xlUserName = xlUserName;
            return this;
        }

        public Builder figureId(String figureId) {
            this.figureId = figureId;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public User build(){

           return  new  User(this);

        }
    }

    @Override
    public String toString() {
        return "User{" +
                "xlID='" + xlID + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", xlUserName='" + xlUserName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }


}

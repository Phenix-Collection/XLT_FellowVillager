package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 名片发送
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-07
 */
public class MessageBusinessCardBundle extends MessageBundle{



    private int messageType = 5;// 名片类型

    public MessageBusinessCardBundle(String userId, String toId, int sendType, String msgkey){
        super(userId,toId,sendType,msgkey);
    }

    public MessageBusinessCardBundle(String userId,
                                     String fromFigure,
                                     String toId,
                                     String toFigure,
                                     int sendType,
                                     String msgKey,
                                     Integer lifetime) {
        super(userId, fromFigure,toId,toFigure,sendType,msgKey,lifetime);

    }
    @Override
    public String bundleTextValues(String contentValues) {

        return toString(contentValues);
    }

    @Override
    public String toString(String contentValues) {
        Md md  = getDefMd();
        md.setMessageType(messageType);
        md.setMessage(contentValues);
        LogCatLog.d(TAG, "要发送名片信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }


    /**
     * 名片
     */
    public class BusinessCard{

        /**
         * 0 个人名片
         * 1 群名片
         */
        private int type ;

        /**
         * 如果 type =0 则代表个人的xlId
         * 如果 type =1 则代表个群Id
         */
        private int id;

        /**
         * 如果 type=0 则代表个人昵称
         * 如果 type=1 则代表群昵称
         */
        private String name;

        /**
         *如果 type=0 则代表个人图像ID
         *如果 type=1 则代表群图片ID
         */
        private String imageId;


        public BusinessCard(int type, int id, String name, String imageId) {
            this.type = type;
            this.id = id;
            this.name = name;
            this.imageId = imageId;
        }

        public int getType() {
            return type;
        }

        public BusinessCard setType(int type) {
            this.type = type;
            return this;
        }

        public int getId() {
            return id;
        }

        public BusinessCard setId(int id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public BusinessCard setName(String name) {
            this.name = name;
            return this;
        }

        public String getImageId() {
            return imageId;
        }

        public BusinessCard setImageId(String imageId) {
            this.imageId = imageId;
            return this;
        }
    }


}

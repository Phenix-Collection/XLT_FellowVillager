package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 商品消息
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-07
 */
public class MessageItemBundle extends MessageBundle{



    private int messageType = 7;// 卡片消息类型

    public MessageItemBundle(String userId, String toId, int sendType, String msgkey){
        super(userId,toId,sendType,msgkey);
    }

    public MessageItemBundle(String userId,
                             String fromFigure,
                             String toId,
                             String toFigure,
                             int sendType,
                             String msgKey,Integer lifetime) {
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
        LogCatLog.d(TAG, "要发送商品信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }

    /**
     * 商品信息
     */
    public  class Goods {
        /**
         * 商品ID
         */
        private String goodsId;
        /**
         * 商品名称
         */
        private String name;
        /**
         * 商品图片ID
         */
        private String imageId;
        /**
         * 商品价格
         */
        private double price;
        /**
         * 商品摘要描述
         */
        private String abstraction;
        /**
         * 商品链接url
         */
        private String url;


        public Goods(String goodsId, String name, String imageId, double price, String abstraction, String url) {
            this.goodsId = goodsId;
            this.name = name;
            this.imageId = imageId;
            this.price = price;
            this.abstraction = abstraction;
            this.url = url;
        }


        public String getGoodsId() {
            return goodsId;
        }

        public Goods setGoodsId(String goodsId) {
            this.goodsId = goodsId;
            return this;
        }

        public String getName() {
            return name;
        }

        public Goods setName(String name) {
            this.name = name;
            return this;
        }

        public String getImageId() {
            return imageId;
        }

        public Goods setImageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public double getPrice() {
            return price;
        }

        public Goods setPrice(double price) {
            this.price = price;
            return this;
        }

        public String getAbstraction() {
            return abstraction;
        }

        public Goods setAbstraction(String abstraction) {
            this.abstraction = abstraction;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Goods setUrl(String url) {
            this.url = url;
            return this;
        }
    }

}

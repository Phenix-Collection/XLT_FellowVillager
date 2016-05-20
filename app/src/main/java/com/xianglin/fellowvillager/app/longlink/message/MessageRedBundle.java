package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 红包
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-07
 */
public class MessageRedBundle extends MessageBundle {

    private int messageType = 6;// 名片类型

    public MessageRedBundle(String userId, String toId, int sendType, String msgkey){
        super(userId,toId,sendType,msgkey);
    }

    public MessageRedBundle(String userId,
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
        LogCatLog.d(TAG, "要发送红包信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }

    /**
     * 红包
     */
    public class Red{

        /**
         * 金额
         */
        private double amount;

        /**
         * 红包总个数
         */
        private int count;

        /**
         * 红包信息
         */
        private String text;

        /**
         * 预留字段，表示红包类型，
         */
        private int catalog;

        public Red() {
        }

        public Red(double amount, int count, String text, int catalog) {
            this.amount = amount;
            this.count = count;
            this.text = text;
            this.catalog = catalog;
        }

        public double getAmount() {
            return amount;
        }

        public Red setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public int getCount() {
            return count;
        }

        public Red setCount(int count) {
            this.count = count;
            return this;
        }

        public String getText() {
            return text;
        }

        public Red setText(String text) {
            this.text = text;
            return this;
        }

        public int getCatalog() {
            return catalog;
        }

        public Red setCatalog(int catalog) {
            this.catalog = catalog;
            return this;
        }
    }


}

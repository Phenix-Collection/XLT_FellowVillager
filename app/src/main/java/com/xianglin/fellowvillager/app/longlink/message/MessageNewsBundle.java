package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 新闻类消息
 * Javadoc
 *
 * @author james
 * @version 0.1, 2016-01-18
 */
public class MessageNewsBundle extends MessageBundle {


    private int messageType = 8;// 新闻类型

    public MessageNewsBundle(String userId, String toId, int sendType, String msgkey){
        super(userId,toId,sendType,msgkey);
    }

    public MessageNewsBundle(String userId,
                             String fromFigure,
                             String toId,
                             String toFigure,
                             int sendType,
                             String msgKey,
                             Integer lifetime) {
        super(userId, fromFigure,toId,toFigure,sendType,msgKey, lifetime);

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
        LogCatLog.d(TAG, "要发送新闻类信息JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }

    /**
     * 新闻对象
     */
    public  class News{


        /**
         * news ID
         */
        private long newsId;//:"123456",                      //新闻id
        /**
         * 新闻标题
         */
        private String title;//:"某人发表重要讲话",                //新闻标题
        /**
         * 新闻图片地址
         */
        private String imgURL;//:”http://www.xxx.com/xx.jpg”,  //新闻图片地址
        /***
         * 新闻概述
         */
        private String summary;//:”新闻概述，新闻简讯”,         //新闻概述
        /**
         * 新闻连接
         */
        private String url;//:"http://wwww.taobao.com/……"    //新闻链接

        public long getNewsId() {
            return newsId;
        }

        public News setNewsId(long newsId) {
            this.newsId = newsId;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public News setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getImgURL() {
            return imgURL;
        }

        public News setImgURL(String imgURL) {
            this.imgURL = imgURL;
            return this;
        }

        public String getSummary() {
            return summary;
        }

        public News setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public News setUrl(String url) {
            this.url = url;
            return this;
        }

        @Override
        public String toString() {
            return "News{" +
                    "newsId=" + newsId +
                    ", title='" + title + '\'' +
                    ", imgURL='" + imgURL + '\'' +
                    ", summary='" + summary + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}

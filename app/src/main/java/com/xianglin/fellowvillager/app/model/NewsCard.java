/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;

/**
 * 新闻卡片
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/18 11:05  XLXZ Exp $
 */
public class NewsCard implements Serializable {

    private String xlid;//;//  not null  //乡邻id
    private String newsid;//  not null  //新闻id
    private String title;//    //新闻标题
    private String imgurl;//    //新闻图片地址
    private String summary;//    //新闻概述
    private String url;//    //新闻链接

    private int  isopen;//  not null default 0 // 是否查看过详情
    private String  msg_key;//  not null unique // 消息key

    public String getXlid() {
        return xlid;
    }

    public void setXlid(String xlid) {
        this.xlid = xlid;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsopen() {
        return isopen;
    }

    public void setIsopen(int isopen) {
        this.isopen = isopen;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    @Override
    public String toString () {
        return "NewsCard{" +
                "xlid='" + xlid + '\'' +
                ", newsid='" + newsid + '\'' +
                ", title='" + title + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", isopen=" + isopen +
                ", msg_key='" + msg_key + '\'' +
                '}';
    }
}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * 商品详情
 *
 * @author pengyang
 * @version v 1.0.0 2016/1/6 15:46  XLXZ Exp $
 */
public class GoodsDetailBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String goodsId;
    private String xlid;  //当前用户id
    private String name;  //标题
    private String name_2;  //子标题
    private String price;  //价格
    private String price_unit; //价格单位
    private String abstraction; //详情
    private String originPrice;//过期价格
    private String title_image_path;//商品标题
    private String category_id;//商品分类
    private String sys_tags;   //促销活动
    private String sales_vol;//销售量
    private String stock_status; //库存状态
    private String url;//促销活动url
    private String msg_key;//消息key
    private String imgURL;//图片id
    private String goodsCount ;//商品数量


    private int isOpen;//是否打开过

    private boolean is_shopping_car = true;//购物车中是否选中

    private List<GoodsImageBean> images; //

    //扩展字段
    private String seo_title;
    private String title_image_id;
    private String keyword;
    private String column_id;
    private String create_time;
    private String sort;
    private String is_open;
    private String is_top;
    private String pv;
    private String uv;
    private String item_number;
    private String price_group;
    private String brand_id;
    private String dr_id;
    private String place_origin;
    private String place_origin_id;
    private String place_ship;
    private String date_ship;
    private String stock_num;
    private String goods_status;
    private String goods_status_id;
    private String content;
    private String format;



    public String getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(String originPrice) {
        this.originPrice = originPrice;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_2() {
        return name_2;
    }

    public void setName_2(String name_2) {
        this.name_2 = name_2;
    }

    public String getAbstraction() {
        return abstraction;
    }

    public void setAbstraction(String abstraction) {
        this.abstraction = abstraction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getXlid() {
        return xlid;
    }

    public void setXlid(String xlid) {
        this.xlid = xlid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }





    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(String price_unit) {
        this.price_unit = price_unit;
    }




    public String getTitle_image_path() {
        return title_image_path;
    }

    public void setTitle_image_path(String title_image_path) {
        this.title_image_path = title_image_path;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSys_tags() {
        return sys_tags;
    }

    public void setSys_tags(String sys_tags) {
        this.sys_tags = sys_tags;
    }

    public String getSales_vol() {
        return sales_vol;
    }

    public void setSales_vol(String sales_vol) {
        this.sales_vol = sales_vol;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }



    public String getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(String goodsCount) {
        this.goodsCount = goodsCount;
    }

    public boolean is_shopping_car() {
        return is_shopping_car;
    }

    public void setIs_shopping_car(boolean is_shopping_car) {
        this.is_shopping_car = is_shopping_car;
    }

    /**
     * 店铺图片实体
     */
    public static class GoodsImageBean implements Serializable {

        private static final long serialVersionUID = 1L;
        private String file_id;
        private String mediumpath;
        private String largepath;

        public String getFile_id() {
            return file_id;
        }

        public void setFile_id(String file_id) {
            this.file_id = file_id;
        }

        public String getMediumpath() {
            return mediumpath;
        }

        public void setMediumpath(String mediumpath) {
            this.mediumpath = mediumpath;
        }

        public String getLargepath() {
            return largepath;
        }

        public void setLargepath(String largepath) {
            this.largepath = largepath;
        }

    }

    @Override
    public String toString () {
        return "GoodsDetailBean{" +
                "goodsId='" + goodsId + '\'' +
                ", xlid='" + xlid + '\'' +
                ", name='" + name + '\'' +
                ", name_2='" + name_2 + '\'' +
                ", price='" + price + '\'' +
                ", price_unit='" + price_unit + '\'' +
                ", abstraction='" + abstraction + '\'' +
                ", title_image_path='" + title_image_path + '\'' +
                ", category_id='" + category_id + '\'' +
                ", sys_tags='" + sys_tags + '\'' +
                ", sales_vol='" + sales_vol + '\'' +
                ", stock_status='" + stock_status + '\'' +
                ", url='" + url + '\'' +
                ", msg_key='" + msg_key + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", goodsCount='" + goodsCount + '\'' +
                ", isOpen=" + isOpen +
                ", is_shopping_car=" + is_shopping_car +
                ", images=" + images +
                ", seo_title='" + seo_title + '\'' +
                ", title_image_id='" + title_image_id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", column_id='" + column_id + '\'' +
                ", create_time='" + create_time + '\'' +
                ", sort='" + sort + '\'' +
                ", is_open='" + is_open + '\'' +
                ", is_top='" + is_top + '\'' +
                ", pv='" + pv + '\'' +
                ", uv='" + uv + '\'' +
                ", item_number='" + item_number + '\'' +
                ", price_group='" + price_group + '\'' +
                ", brand_id='" + brand_id + '\'' +
                ", dr_id='" + dr_id + '\'' +
                ", place_origin='" + place_origin + '\'' +
                ", place_origin_id='" + place_origin_id + '\'' +
                ", place_ship='" + place_ship + '\'' +
                ", date_ship='" + date_ship + '\'' +
                ", stock_num='" + stock_num + '\'' +
                ", goods_status='" + goods_status + '\'' +
                ", goods_status_id='" + goods_status_id + '\'' +
                ", content='" + content + '\'' +
                ", format='" + format + '\'' +
                '}';
    }

}

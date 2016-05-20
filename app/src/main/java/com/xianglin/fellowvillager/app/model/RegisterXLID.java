package com.xianglin.fellowvillager.app.model;
/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */

import java.io.Serializable;
import java.util.List;

/**
 * 选择乡邻id
 * @author pengyang
 * @version v 1.0.0 2015/11/24 16:14  XLXZ Exp $
 */
public class RegisterXLID implements Serializable {

/*    "xlidList": [
    {
        "XLID": 10098
    },*/

    public RegisterXLID( ) {
      super();
    }

    private String tempXlid;
    private List<IDListEntity> xlidList;

    public  static class IDListEntity implements Serializable {
        private  String XLID;

        public void setXLID(String XLID) {
            this.XLID = XLID;
        }

        public String getXLID() {
            return XLID;
        }
    }

    @Override
    public String toString() {
        return "RegisterXLID{" +
                "tempXlid='" + tempXlid + '\'' +
                ", xlidList=" + xlidList +
                '}';
    }

    public String getTempXlid() {
        return tempXlid;
    }


    public List<IDListEntity> getXlidList() {
        return xlidList;
    }

    public void setXlidList(List<IDListEntity> xlidList) {
        this.xlidList = xlidList;
    }

    public void setTempXlid(String tempXlid) {
        this.tempXlid = tempXlid;
    }
}

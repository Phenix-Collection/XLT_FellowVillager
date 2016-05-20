/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.model;

import java.io.Serializable;
import java.util.List;

/**
 *  附近的人
 * @author pengyang
 * @version v 1.0.0 2015/11/20 12:49  XLXZ Exp $
 */
public class NearbysBean implements Serializable{

    public NearbysBean( ) {
        super();
    }

    private List<NearbysEntity> nearbys;

    public  static class NearbysEntity implements Serializable {

        private  String remarkName;
        private  String meters;
        private  String xlid;

        public String getXlid() {
            return xlid;
        }

        public void setXlid(String xlid) {
            this.xlid = xlid;
        }

        public String getMeters() {
            return meters;
        }

        public void setMeters(String meters) {
            this.meters = meters;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }
    }

    public List<NearbysEntity> getNearbys() {
        return nearbys;
    }

    public void setNearbys(List<NearbysEntity> nearbys) {
        this.nearbys = nearbys;
    }
}

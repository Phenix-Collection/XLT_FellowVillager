package com.xianglin.fellowvillager.app.model;

/**
 * 类描述：
 * 创建人：chengshengli
 * 创建时间：2016/3/29 17:54  17 54
 * 修改人：chengshengli
 * 修改时间：2016/3/29 17:54  17 54
 * 修改备注：
 */
public class SecretBean {

    private String time;
    private boolean isSelected;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public SecretBean(String time,boolean isSelected){
        this.time=time;
        this.isSelected=isSelected;
    }
}

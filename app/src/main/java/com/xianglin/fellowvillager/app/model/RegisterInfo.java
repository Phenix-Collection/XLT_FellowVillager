
package com.xianglin.fellowvillager.app.model;

/**
 * 注册信息
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/16 19:59  XLXZ Exp $
 */
public class RegisterInfo {

 
    public String xlid;// XXXXXX,
    public String deviceId;// public String XXXXXXXXXXXXpublic String

    //一下 
    public String tempXlid;// XXXXXXXXXXXXXXXX,
    public String password;//123456public String ,
    public String trueName;// public String 张三public String ,
    public String imgName;// public String IMG_20151105.jpgpublic String ,
    public String deviceType;// public String 0public String ,
    public String androidInfo;// public String XXXXXXXXpublic String ,
    public String androidMac;// public String XXXXXXpublic String ,
    public String iosInfo;// public String XXXXXXXXXXXXpublic String
 
    

/*    {
        "id": 0,
            "memo": "操作成功",
            "result": {
        "body": {
            "deviceId": "5f1962e5-758c-4c61-ae2b-75c05ca80c45",
                    "xlid": 12088
        },
        "tips": "返回正常",
                "code": "000000",
                "msg": "正常",
                "header": {
            "deviceId": "5f1962e5-758c-4c61-ae2b-75c05ca80c45",
                    "xlid": 12088
        }
    },
        "resultStatus": 1000
    }*/


    @Override
    public String toString() {
        return "RegisterInfo{" +
                "tempXlid='" + tempXlid + '\'' +
                ", xlid='" + xlid + '\'' +
                ", password='" + password + '\'' +
                ", trueName='" + trueName + '\'' +
                ", imgName='" + imgName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", androidInfo='" + androidInfo + '\'' +
                ", androidMac='" + androidMac + '\'' +
                ", iosInfo='" + iosInfo + '\'' +
                '}';
    }
}

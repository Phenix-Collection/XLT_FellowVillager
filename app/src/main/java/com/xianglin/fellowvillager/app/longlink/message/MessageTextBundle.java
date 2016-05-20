package com.xianglin.fellowvillager.app.longlink.message;

import com.alibaba.fastjson.JSON;
import com.xianglin.fellowvillager.app.model.Md;
import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * 文字普通消息绑定
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-07
 */
public class MessageTextBundle extends MessageBundle{


    private int messageType = 1;// 消息文字类型

    public MessageTextBundle(String userId,String toId,int sendType,String msgKey){
        super(userId,toId,sendType,msgKey);
    }


    public MessageTextBundle(String userId,
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

    public String toString(String contentValues){
        /**
         * {"sKey":0,"sData":[{"md":
         * {"fromid":"10488",
         * "message":"好吧那你",
         * "replyType":0,
         * "toid":"12300"}
         *
         * ,"biz":"chat"}
         * ],"sOpCode":2001}
         */
        Md md = getDefMd();
        md.setMessageType(messageType);
        md.setMessage(contentValues);
        LogCatLog.e(TAG, "要发送文字信息 JSON" + JSON.toJSONString(md));
        return JSON.toJSONString(md);
    }

}

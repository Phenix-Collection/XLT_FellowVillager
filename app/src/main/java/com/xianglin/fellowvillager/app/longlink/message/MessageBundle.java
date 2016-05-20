package com.xianglin.fellowvillager.app.longlink.message;

import com.xianglin.fellowvillager.app.longlink.config.LongLinkConfig;
import com.xianglin.fellowvillager.app.model.Md;

import java.util.Date;

/**
 * 消息绑定
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-27
 */
public abstract class MessageBundle {


    protected static final String TAG = LongLinkConfig.TAG;
    /**
     * UserID 用户ID
     */
    public String userId;

    /**
     * 要发送的ID
     */
    public String toId;


    /**
     * 当前时间
     */
    public Date date = new Date();

    /**
     * app 系统类型
     */
    public String appType = "Android";

    /**
     * 消息Key
     */
    public String msgKey = "";
    /**
     * 发送身份ID
     */
    public String fromFigure;

    /**
     * 接受身份ID
     */
    public String toFigure;

    /**
     * 群id  群ID 发送群消息时需要
     */
    public String groupId;

    /**
     * 消息发送类型
     * 0 : 点对点 默认
     * sendType	Int	发送类型	"0:点对点消息 1:群消息 2:系统消息"
     */
    public int sendType = 0;

    /**
     * 私密消息的生存期，单位：秒，负值和0表示不是私密消息
     */
    private Integer lifetime = -1;


    public MessageBundle(String userId, String toId, int sendType, String msgKey) {
        this.userId = userId;// 当前用户ID
        this.toId = toId;// 需要发送的用户ID
        this.sendType = sendType;// 发送类型
        this.msgKey = msgKey;// 消息KEY
    }

    public MessageBundle(String userId,
                         String fromFigure,
                         String toId,
                         String toFigure,
                         int sendType,
                         String msgKey,
                         Integer lifetime) {
        this.userId = userId;// 当前用户ID
        this.fromFigure = fromFigure;// 当前用户角色ID
        this.toId = toId;// 需要发送的用户ID
        this.toFigure = toFigure;// 需要发送的用户角色ID
        this.sendType = sendType;// 发送类型
        this.msgKey = msgKey;// 消息KEY
        this.lifetime =  lifetime;// 私密消息的生存期

    }


    /**
     * 用户对用户[角色对角色]
     *
     * @return
     */
    public Md getDefMd() {

        Md md = new Md();// 内容数据
        md.setFromid(this.userId);// 用户ID
        md.setFromFigure(this.fromFigure);//发送身份id
        md.setToid(this.toId);// 要发送用户ID
        md.setToFigure(this.toFigure);//接收身份id
        md.setAppType(this.appType); // App 类型 android \ios
        md.setMct(this.date.getTime() + ""); // 基本时间
        if (this.sendType == 1) {// 如果发送群消息 得把群组ID 传送过来
            md.setGroupId(this.toId);// TOID ＝＝群ID
        }
        md.setSendType(this.sendType);// 发送类型
        md.setSKey(this.msgKey);//本地KEY
        md.setLifetime(this.lifetime);//私密消息的生存期
        return md;
    }

    /**
     * 绑定基本文字消息
     *
     * @param contentValues
     * @return
     */
    public abstract String bundleTextValues(String contentValues);


    /**
     * 消息转化
     *
     * @param contentValues
     * @return
     */
    public abstract String toString(String contentValues);

}

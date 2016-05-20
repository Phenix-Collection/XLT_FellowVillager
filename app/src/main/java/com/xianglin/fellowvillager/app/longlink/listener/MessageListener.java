package com.xianglin.fellowvillager.app.longlink.listener;

import com.xianglin.fellowvillager.app.model.Md;

/**
 * 消息监听
 * 注册 消息监听
 * 拿到 消息回调
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-11-30
 */
public   interface MessageListener  {


    /**
     * 消息回调
     */
    public  void  processPacket(Md md);


}

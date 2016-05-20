/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat.controller;

import com.xianglin.fellowvillager.app.model.MessageBean;

/**
 * 私密回调
 * @author pengyang
 * @version v 1.0.0 2016/3/30 14:31  XLXZ Exp $
 */
public interface PrivateMessageCallBack {

    Object data = null;

    public void onStart();
    public void onPause(MessageBean messageBean);
    public  void onEnd(MessageBean messageBean);

    /**
     * @param lefttime 总生存时间
     * @param progress     进度条百分比
     * @param picTime      图片倒计时
     * @param messageBean
     */
    public void onProgress(int lefttime, int progress, int picTime, MessageBean messageBean);


}

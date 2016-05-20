package com.xianglin.fellowvillager.app.chat.widget.chatrow;

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
public interface EMCallBack {

    Object data = null;

    void onSuccess();

    void onError(int errCode, String err);

    void onProgress(int errCode, String err);

}

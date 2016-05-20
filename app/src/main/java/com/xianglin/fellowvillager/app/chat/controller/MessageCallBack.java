package com.xianglin.fellowvillager.app.chat.controller;

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
public interface MessageCallBack {

    Object data = null;

    void onSuccess();

    void onError(int errCode, String err);

    void onProgress(int progress , String err);

}

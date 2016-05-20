/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xianglin.fellowvillager.app.db.MessageDBHandler;

/**
 *  加载当前聊天记录
 * @author pengyang
 * @version v 1.0.0 2015/11/24 11:30  XLXZ Exp $
 */
public class MessageDialogueLoader extends  SQLiteCursorLoader{
    /**
     * 聊天对象的id
     */
    private String toCharId;
    private int chatType;

    private MessageDBHandler mMessageDBHandler;


    private  boolean isLoadMore=false;



    private long pageIndex;

    public MessageDialogueLoader(Context context, String toCharID,int chatType,long pageIndex) {
        super(context);
        this.toCharId=toCharID;
        this.chatType=chatType;
        this.pageIndex=pageIndex;
        mMessageDBHandler=new MessageDBHandler(context);
        mNotificationUri= Uri.withAppendedPath(MessageDBHandler.SYNC_SIGNAL_URI,toCharID);//需要附加
    }
    @Override
    protected Cursor loadCursor() {
            return mMessageDBHandler.queryChatHistory(toCharId,chatType,pageIndex);
    }

    public void getNextPage(){
        pageIndex= pageIndex+1;
        onContentChanged();
    }

    @Override
    public void onContentChanged() {

        super.onContentChanged();

    }




}







package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.db.CardDBHandler;

/**
 * 乡邻小站
 * Copyright (c) 2011-2016 Xianglin,Inc.All Rights Reserved.
 */
public class CardLoader extends SQLiteCursorLoader {


    /**
     * 聊天对象的id
     */
    private String toCharId;
    private int chatType;

    private long pageIndex;
    private boolean isdesc;
    private long pageSize;
    private boolean isShowAllType;

    private CardDBHandler mDBHandler;


    /**
     *
     * 查询全部
     *
     * 取最后5条卡片  pageIndex=0,pageSize=5
     * @param context
     * @param toCharID 聊天对象id(群id或者单聊用户id)   为""或者null 时查询全部消息中的卡片,
     * @param chatType 聊天类型群聊 BorrowConstants.CHATTYPE_GROUP BorrowConstants.CHATTYPE_SINGLE
     * @param pageIndex  分页页数 0是第一页
     * @param pageSize   一页有多少条,  0时返回全部
     * @param isdesc  否是降序
     * @param isShowAllType  是否显示 接受且点开过的卡片  true 显示 false 不显示
     */
    public CardLoader(Context context, String toCharID, int chatType, long pageIndex, long pageSize, boolean isdesc, boolean isShowAllType) {
        super(context);
        this.toCharId = toCharID;
        this.chatType = chatType;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.isdesc = isdesc;
        mNotificationUri= CardDBHandler.SYNC_SIGNAL_URI;//需要附加
        mDBHandler = new CardDBHandler();
        this.isShowAllType=isShowAllType;
    }

    @Override
    protected Cursor loadCursor() {
        if(!isShowAllType) {
            return mDBHandler.queryChatCardHistory(toCharId, chatType, pageIndex, pageSize, isdesc);

        }else{
            return mDBHandler.queryChatCardHistoryALL(toCharId, chatType, pageIndex, pageSize, isdesc);

        }
    }

}

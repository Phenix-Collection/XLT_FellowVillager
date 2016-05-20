/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.db.MomentDialogueDBHandler;

/**
 *  最近联系人消息Loader
 * @author pengyang
 * @version v 1.0.0 2015/11/25 16:40  XLXZ Exp $
 */
public class MomentMessageLoader extends  SQLiteCursorLoader{

    private MomentDialogueDBHandler mMomentDialogueDBHandler;
    public MomentMessageLoader(Context context) {
        super(context);
        mMomentDialogueDBHandler=new MomentDialogueDBHandler(context);
        mNotificationUri=MessageDBHandler.SYNC_SIGNAL_URI;
    }

    @Override
    protected Cursor loadCursor() {

    return mMomentDialogueDBHandler.queryChatRecentMessage(ContactManager.getInstance().getCurrentFigureID());

    }
}

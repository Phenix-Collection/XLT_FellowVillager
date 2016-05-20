/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.db.GroupDBHandler;

/**
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/24 17:03  XLXZ Exp $
 */
public class GroupListLoader extends SQLiteCursorLoader {

    GroupDBHandler mGroupDBHandler ;

    public GroupListLoader(Context context) {
        super(context);
        mGroupDBHandler=new GroupDBHandler(context);
        mNotificationUri=GroupDBHandler.SYNC_SIGNAL_URI;
    }
    @Override
    protected Cursor loadCursor() {
        return mGroupDBHandler.queryGrList();
    }

}

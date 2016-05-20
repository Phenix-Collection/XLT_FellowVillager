package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
public class GroupMemberLoader extends SQLiteCursorLoader {
    private String groupid;

    private GroupMemberDBHandler mGroupMemberDBHandler;


    public GroupMemberLoader(Context context, String groupid    ) {
        super(context);
        this.groupid = groupid;
        mGroupMemberDBHandler = new GroupMemberDBHandler(context);
        mNotificationUri=GroupMemberDBHandler.SYNC_SIGNAL_URI;
    }

    @Override
    protected Cursor loadCursor() {
        return mGroupMemberDBHandler.queryGroupMemberList(groupid);
    }
}

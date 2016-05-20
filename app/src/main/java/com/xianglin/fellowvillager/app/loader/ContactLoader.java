/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;

import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.Group;

/**
 * @author pengyang
 * @version v 1.0.0 2015/11/24 17:03  XLXZ Exp $
 */
public class ContactLoader extends SQLiteCursorLoader {

    ContactDBHandler mContactDBHandler;
    Group group;

    public void setContactLevel(Contact.ContactLevel contactLevel) {
        mContactLevel = contactLevel;
    }

    private  Contact.ContactLevel mContactLevel;

    /**
     * 判断联系人是否在群中
     *
     * @param context
     * @param group 群
     */
    public ContactLoader(Context context, Group group) {
        this(context);
        this.group = group;
        mNotificationUri = null;
    }
    public ContactLoader(Context context) {
        super(context);
        mContactDBHandler = new ContactDBHandler(context);
        mNotificationUri = ContactDBHandler.SYNC_SIGNAL_URI;
    }

    @Override
    protected Cursor loadCursor() {
         if(mContactLevel==null){
             mContactLevel= Contact.ContactLevel.HIGH;
         }
        //判断联系人是否在群中
        return mContactDBHandler.queryContactForGroup(ContactManager.getInstance().getCurrentFigureID(), group,mContactLevel);
    }

    public void  onContentChanged(Contact.ContactLevel contactLevel) {
        mContactLevel=contactLevel;
        onContentChanged();
      }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }
}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

public abstract  class BaseCursorAdapter extends CursorAdapter  {

    public BaseCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }


}

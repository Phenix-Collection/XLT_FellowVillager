/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 简化Adapter
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/12 11:54  XLXZ Exp $
 */
public abstract class XLBaseAdapter<T> extends BaseAdapter {

    protected List<T> mlist;

    protected Context mContext;

    protected LayoutInflater mInflater;

    public XLBaseAdapter(List<T> list, Context context) {
        super();
        this.mlist = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public T getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

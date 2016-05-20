package com.xianglin.fellowvillager.app.db;

import android.content.Context;
import android.net.Uri;

import com.xianglin.fellowvillager.app.BuildConfig;
import com.xianglin.mobile.common.db.DBUtil;

/**
 * 增删改查 类
 * Javadoc
 * @author james
 * @version 0.1, 2015-11-12
 */
public class BaseBDHandler {

    protected static final String  TAG = BaseBDHandler.class.getSimpleName();
    protected Context mContext;
    protected  DBUtil dbUtil;

    protected   static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    protected   static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public BaseBDHandler (Context mContext){
       this. mContext=mContext;
        dbUtil = new DBUtil ();

    }

}

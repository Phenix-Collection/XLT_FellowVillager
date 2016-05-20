/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.xianglin.mobile.common.logging.LogCatLog;

/**
 *  自定一个抽象的CursorLoader
 * @author pengyang
 * @version v 1.0.0 2015/11/18 12:58  XLXZ Exp $
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

	private Cursor mCursor;

	public ForceLoadContentObserver getObserver () {
		return mObserver;
	}

	private final Loader.ForceLoadContentObserver mObserver;

	Uri mNotificationUri;//设置对应数据源改变的信号

	public SQLiteCursorLoader(Context context) {
		super(context);
		this.mObserver = new Loader.ForceLoadContentObserver();
	}
	protected abstract Cursor loadCursor();

	/**在loadInBackground()方法中执行数据库查询，
	 * @return 返回Cursor；
	 */
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = loadCursor();
		if(cursor != null){
			cursor.getCount();
			cursor.registerContentObserver(mObserver);
			if(mNotificationUri!=null)
			cursor.setNotificationUri(getContext().getContentResolver(), mNotificationUri);

			LogCatLog.d("SQLiteCursorLoader","loadInBackground:异步读取数据库信息"+"\n"+"mNotificationUri:"+mNotificationUri);
		}
		return cursor;
	}
	/**
	 * loadInBackground完成后会回调此方法关闭老的cursor
	 * 在deliverResult()方法中执行跟适配器交换数据的操作。 adapter.swapCursor(data)。
	 * @param data Cursor
	 */
	@Override
	public void deliverResult(Cursor data){
		Cursor oldCursor = mCursor;
		mCursor = data;
		
		if(isStarted()){
			super.deliverResult(data);
		}
		
		if(oldCursor != null && oldCursor != data && !oldCursor.isClosed()){
			oldCursor.close();
		}
	}

	/**
	 * onStartLoading中调用forceLoad()才能依次调用下一个即将执行的方法。
	 */
	@Override
	protected void onStartLoading(){
		if(mCursor != null) {
			deliverResult(mCursor);
		}
		if(takeContentChanged() || mCursor == null){
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading(){

		cancelLoad();
	}

	@Override
	public void onCanceled(Cursor cursor){
		if(cursor != null && !cursor.isClosed()){
			cursor.close();
		}
	}
	
	@Override
	protected void onReset(){
		super.onReset();

		onStopLoading();
		
		if(mCursor != null && !mCursor.isClosed()){
			mCursor.close();
		}
		mCursor = null;
	}
}

package com.xianglin.fellowvillager.app.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
/**
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/20 13:21  XLXZ Exp $
 */
public class DataLoader<D> extends AsyncTaskLoader<D> {

	private D mData;

	protected  Context mContext;
	
	public DataLoader(Context context) {
		super(context);
		mContext=context;
	}

	@Override
	protected void onStartLoading(){
		if(mData != null){
			deliverResult(mData);
		} else {
			forceLoad();
		}
	}

	/** 回调LoaderManager.OnLoadCompleteListener.onLoadFinished()
	 * @param data
	 */
	@Override
	public void deliverResult(D data){
		mData = data;
		if(isStarted()){
			super.deliverResult(data);
		}
	}
	
	@Override
	public D loadInBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

package com.xianglin.fellowvillager.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 抽取自定义Adapter继承制 BaseAdapter，实现getCount，getItem，getItemId
 * 
 * @author huangyang
 * @version $Id: DefaultAdapter.java, v 1.0.0 2015年8月26日 下午3:42:04 huangyang Exp
 *          $
 */
public abstract class DefaultAdapter<T> extends BaseAdapter {

	protected List<T> listDatas = new ArrayList<T>();
	protected Context mContext;

	public DefaultAdapter(Context aContext,List<T> listData) {
		this.mContext = aContext;
		if (listData != null) {
			this.listDatas.clear();
			this.listDatas.addAll(listData);
		}
	}

	public void setData(List<T> listData) {
		if (listData != null) {
			this.listDatas.clear();
			this.listDatas.addAll(listData);
		}
	}

	@Override
	public int getCount() {
		if (listDatas == null || listDatas.size() == 0)
			return 0;
		return listDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}

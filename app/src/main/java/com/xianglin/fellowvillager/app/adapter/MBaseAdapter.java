package com.xianglin.fellowvillager.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Aizaz
 *
 */


public class MBaseAdapter<T> extends BaseAdapter {

	protected Context context;
	protected ArrayList<T> models;

	public MBaseAdapter(Context context, ArrayList<T> models) {
		this.context = context;
		if (models == null)
			this.models = new ArrayList<T>();
		else
			this.models = models;
	}

	@Override
	public int getCount() {
		if (models != null) {
			return models.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

	/** ������� */
	public void update(List<T> models) {
		if (models == null)
			return;
		this.models.clear();
		for (T t : models) {
			this.models.add(t);
		}
		notifyDataSetChanged();
	}
	public void add(T models) {
		if (models == null)
			return;
		this.models.add(models);
		notifyDataSetChanged();
	}
	public void addAll(List<T> models) {
		if (models == null)
			return;
		this.models.addAll(models);
		notifyDataSetChanged();
	}
	public void add(int index,T models) {
		if (models == null)
			return;
		this.models.add(index,models);
		notifyDataSetChanged();
	}

	public ArrayList<T> getItems() {
		return models;
	}

}

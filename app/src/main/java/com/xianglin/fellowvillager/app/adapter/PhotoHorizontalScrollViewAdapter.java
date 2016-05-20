package com.xianglin.fellowvillager.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.PhotoItem;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;

public class PhotoHorizontalScrollViewAdapter extends MBaseAdapter<PhotoModel>{

	private int itemWidth;
	private int horizentalNum = 3;
	private PhotoItem.onPhotoItemCheckedListener listener;
	private AbsListView.LayoutParams itemLayoutParams;
	private PhotoItem.onItemClickListener mCallback;
	private View.OnClickListener cameraListener;

	private PhotoHorizontalScrollViewAdapter(Context context, ArrayList<PhotoModel> models) {
		super(context, models);
	}
	public PhotoHorizontalScrollViewAdapter(Context context, ArrayList<PhotoModel> models,
									   int screenWidth, PhotoItem.onPhotoItemCheckedListener listener,
									   PhotoItem.onItemClickListener mCallback, View.OnClickListener cameraListener)
	{
		this(context, models);
		setItemWidth(screenWidth);
		this.listener = listener;
		this.mCallback = mCallback;
		this.cameraListener = cameraListener;
	}

	public void setItemWidth(int screenWidth) {
		int horizentalSpace = context.getResources().getDimensionPixelSize(
				R.dimen.sticky_item_horizontalSpacing);
		this.itemWidth = (screenWidth - (horizentalSpace * (horizentalNum - 1)))
				/ horizentalNum;
		this.itemLayoutParams = new AbsListView.LayoutParams(itemWidth, itemWidth);
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		PhotoItem item = null;
		if (convertView == null || !(convertView instanceof PhotoItem)) {
			item = new PhotoItem(context, listener);
			item.setLayoutParams(itemLayoutParams);
			convertView = item;
		} else {
			item = (PhotoItem) convertView;
		}
		PhotoModel info = models.get(position);

		item.setImageDrawable(models.get(position));
		item.setSelected(models.get(position).isChecked());
		item.setOnClickListener(mCallback, position);
		return convertView;
	}

}

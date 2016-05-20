package com.xianglin.fellowvillager.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.xianglin.fellowvillager.app.chat.GalleryPhotoItem;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;

import java.util.ArrayList;

/**
 * 
 * @author Aizaz AZ
 * 
 */
public class PhotoSelectorAdapter extends MBaseAdapter<PhotoModel> {
	private GalleryPhotoItem.onPhotoItemCheckedListener listener;
	private GalleryPhotoItem.onItemClickListener mCallback;

	private PhotoSelectorAdapter(
			Context context,
			ArrayList<PhotoModel> models
	) {
		super(context, models);
	}

	public PhotoSelectorAdapter(
			Context context,
			ArrayList<PhotoModel> models,
			GalleryPhotoItem.onPhotoItemCheckedListener listener,
			GalleryPhotoItem.onItemClickListener mCallback
	) {
		this(context, models);
		this.listener = listener;
		this.mCallback = mCallback;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GalleryPhotoItem item;
		if (convertView == null || !(convertView instanceof GalleryPhotoItem)) {
			item = new GalleryPhotoItem(context, listener);
			convertView = item;
		} else {
			item = (GalleryPhotoItem) convertView;
		}
		PhotoModel info = models.get(position);
		if (info == null) {
			return convertView;
		}
		item.setImageDrawable(info);
		item.setSelected(info.isChecked());
		item.setOnClickListener(mCallback, position);
		return convertView;
	}
}

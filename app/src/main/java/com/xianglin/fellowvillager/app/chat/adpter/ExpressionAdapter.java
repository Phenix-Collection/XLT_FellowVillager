package com.xianglin.fellowvillager.app.chat.adpter;
/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.xianglin.fellowvillager.app.R;

import java.util.List;
/**
 *
 * 聊天表情adapter
 * @author chengshengli
 * @version v 1.0.0 2015/11/30 17:49 XLXZ Exp $
 */
public class ExpressionAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private static final String DRAWABLE_DIR = "drawable";

	public ExpressionAdapter(
			Context context,
			int textViewResourceId,
			List<String> objects
	) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.row_expression,
					parent,
					false
			);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String filename = getItem(position);
		if (TextUtils.isEmpty(filename)) {
			return convertView;
		}
		int resId = getContext().getResources().getIdentifier(
				filename,
				DRAWABLE_DIR,
				mContext.getPackageName()
		);
		holder.imageView.setImageResource(resId);
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
	}
}

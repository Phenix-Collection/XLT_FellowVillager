package com.xianglin.fellowvillager.app.chat.adpter;
/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
/**
 *
 * 聊天表情adapter
 * @author chengshengli
 * @version v 1.0.0 2015/11/30 17:50 XLXZ Exp $
 */
public class ExpressionPagerAdapter extends PagerAdapter {

	private List<View> views;

	public ExpressionPagerAdapter(List<View> views) {
		this.views = views;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));

	}
}

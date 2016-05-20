/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.widget.tab;

import android.content.Context;
import android.util.AttributeSet;
/**
 *  自定义一个tab
 * @author pengyang
 * @version v 1.0.0 2015/12/18 15:54  XLXZ Exp $
 */
public class MyFragmentTabHost extends FragmentTabHost {

	private String mCurrentTag;

	private String mNoTabChangedTag;

	public MyFragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onTabChanged(String tag) {

		if (tag.equals(mNoTabChangedTag)) {
			setCurrentTabByTag(mCurrentTag);
		} else {
			super.onTabChanged(tag);
			mCurrentTag = tag;
		}
	}




	public void setNoTabChangedTag(String tag) {
		this.mNoTabChangedTag = tag;
	}
}
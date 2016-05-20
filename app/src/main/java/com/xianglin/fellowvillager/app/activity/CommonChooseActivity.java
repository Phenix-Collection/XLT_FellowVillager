/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * 公共的由下往上的选择弹框
 * @author pengyang
 * @version v 1.0.0 2015/11/13 17:35  XLXZ Exp $
 */


@EActivity(R.layout.common_choose_layout)
public class CommonChooseActivity extends BaseActivity {

	public static final String BUNDLEKEY = "mList";
	@ViewById(R.id.cancel_text)
    public TextView mCancelText;
	@ViewById(R.id.rl_choose_top)
    public RelativeLayout mLlChooseTop;

	 @ViewById(R.id.lv_menu)
	public ListView mListView;

	private static OnChooseListener mOnChooseListener;
	private ArrayList<String> mList = new ArrayList<String>();


	/** list最好别过多,最多5条,不然屏幕会被占去很多  */
	public static void show(Activity activity, ArrayList<String> list, OnChooseListener listener){
		CommonChooseActivity.mOnChooseListener = listener;
		Intent intent = new Intent(activity, CommonChooseActivity_.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(BUNDLEKEY, list);
		intent.putExtras(bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
	}
	/** list最好别过多,最多5条,不然屏幕会被占去很多  */
	public static void show(Activity activity, String[] list, OnChooseListener listener){
		ArrayList<String> items = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			items.add(list[i]);
		}
		show(activity, items, listener);
	}
	/*//需要使用特殊的Activity动画*/
	public void finishPage(){

		CommonChooseActivity.this.finish();
		 overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
		//overridePendingTransition(R.anim.push_bottom_out, R.anim.push_bottom_in);
	}

	//注解完成执行
	@AfterViews
	protected void initView() {
		//需要在setContentView之后调用设置全屏代码 才能消除dalog的外边据
		this.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		this.mList = getIntent().getExtras().getStringArrayList(BUNDLEKEY);

		mCancelText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishPage();
			}
		});
         mLlChooseTop.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
                 finishPage();
             }
         });
		mListView.setAdapter(new CommonChooseAdapter());

	}

	class CommonChooseAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		public CommonChooseAdapter(){
			inflater = LayoutInflater.from(CommonChooseActivity.this);
		}
		@Override
		public int getCount() {
			return mList.size();
		}
		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(final int arg0, View view, ViewGroup arg2) {
			view = inflater.inflate(R.layout.common_choose_item, null);
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.item_layout);
			View line = view.findViewById(R.id.line);
			TextView text = (TextView) view.findViewById(R.id.text);
			text.setText(mList.get(arg0));
			if(mList.size() == 1){
				line.setVisibility(View.GONE);

			}else{
				if(arg0 == 0){
					line.setVisibility(View.VISIBLE);

				}else if(arg0 == mList.size()-1){
					line.setVisibility(View.GONE);

				}else{
					line.setVisibility(View.VISIBLE);

				}
			}

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finishPage();
					if (null != mOnChooseListener) {
						mOnChooseListener.onChoose(arg0);
					}
				}
			});
			return view;
		}
	}

	public interface OnChooseListener{
		void onChoose(int position);
	}

	@Override
	public void onBackPressed() {
		finishPage();
	}

}

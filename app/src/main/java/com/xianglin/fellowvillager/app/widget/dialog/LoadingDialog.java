package com.xianglin.fellowvillager.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;


/**
 * @Description 加载中等待框（纯动画，show即可）
 */
public class LoadingDialog extends Dialog {
	private TextView tv;

	public LoadingDialog(Context context) {
		super(context,R.style.loadingDialogStyle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_layout);
		tv = (TextView)findViewById(R.id.tv_loading);
		LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.ll_loading_layout);
		linearLayout.getBackground().setAlpha(210);
	}

	@Override
	public void dismiss() {
		try {
			if (isShowing()) {
				super.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}

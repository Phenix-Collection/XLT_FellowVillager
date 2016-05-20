package com.xianglin.fellowvillager.app.activity;

import android.content.Intent;
import android.view.View;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 添加朋友（都是先进行相关操作，然后如果是“全部”，那么再进行角色的选择）
 */
@EActivity(R.layout.activity_add_friends)
public class AddFriendsActivity extends BaseActivity {

	@ViewById(R.id.top_bar)
	TopView topView;

	//注解完成执行
	@AfterViews
	public void initView () {
		topView.setAppTitle("添加朋友");
		topView.setLeftImageResource(R.drawable.icon_back);
		topView.setLeftImgOnClickListener();
	}

	@Click({R.id.ll_addfriends_scan, R.id.ll_addfriends_near})
	public void onClick (View view) {
		switch (view.getId()) {
			// 扫一扫
			case R.id.ll_addfriends_scan:

				startActivity(new Intent(this, CaptureActivity.class));
				animLeftToRight();

				break;
			// 附近的人
			case R.id.ll_addfriends_near:

				NearbyBaseActivity_.intent(this).start();
				animLeftToRight();

				break;
			default:

				break;
		}

	}

}



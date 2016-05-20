package com.xianglin.fellowvillager.app.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.NewContactAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 新联系人:频繁联系人、普通联系人
 */
@EActivity(R.layout.activity_new_contact)
public class NewContactActivity extends BaseActivity {

	@ViewById(R.id.top_bar)
	TopView topView;

	// 没有数据时显示一张图片
	@ViewById(R.id.iv_no_data_tip)
	ImageView mNoDataTip;

	@ViewById(R.id.rv_choose_role)
	RecyclerView rv_choose_role;

	private List<Contact> mList_Contact;


	/**
	 	当前角色没有新联系人或者群聊时，这两项隐藏；否则展示。
	 */
	//注解完成执行
	@AfterViews
	public void initView(){
		topView.setAppTitle("新联系人");
		topView.setLeftImageResource(R.drawable.icon_back);
		topView.setLeftImgOnClickListener();

		getDataFromDB();
	}

	@Background
	void getDataFromDB() {

		mList_Contact = new ContactDBHandler(this).queryNewContact(ContactManager.getInstance().getCurrentFigureID());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mList_Contact == null || mList_Contact.isEmpty()) {
					mNoDataTip.setVisibility(View.VISIBLE);
					mNoDataTip.setImageResource(R.drawable.new_contact_no_people);
					return;
				}
				mNoDataTip.setVisibility(View.GONE);
				rv_choose_role.setLayoutManager(new LinearLayoutManager(NewContactActivity.this));
				rv_choose_role.setAdapter(new NewContactAdapter(NewContactActivity.this, mList_Contact));
			}
		});
	}


}

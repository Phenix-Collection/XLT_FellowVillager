package com.xianglin.fellowvillager.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.ContactAdapter;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 查看名录中的个人主页

 @author chengshengli
 @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $ */
@EActivity(R.layout.activity_user_detail)
public class UserDetailBeforeChatActivity extends BaseActivity {

	@ViewById(R.id.topview)
	TopView mTopView;// 标题栏
	@ViewById(R.id.iv_person_header)
	CircleImage person_header_iv;// 头像
	@ViewById(R.id.tv_person_name)
	TextView person_name_tv;// 用户名
	@ViewById(R.id.tv_person_number)
	TextView person_number_tv;// 用户角色号
	@ViewById(R.id.tv_person_nickname)
	TextView tv_person_nickname;//用户昵称
	@ViewById(R.id.tv_user_decribe)
	TextView tv_user_decribe;//个人描述
	@ViewById(R.id.ll_show)
	LinearLayout ll_show; // 共同联系人、共同联系群、关系时间、建立关系方式

	@ViewById(R.id.tv_user_create)
	TextView tv_user_create;//关系时间
	@ViewById(R.id.tv_add_type)
	TextView tv_add_type;//添加方式
	/**相同联系人视图*/
	@ViewById(R.id.same_contact_layout)
	LinearLayout mSameContactLayout;
	/**相同群视图*/
	@ViewById(R.id.same_group_layout)
	LinearLayout mSameGroupLayout;
	/**相同联系人的icon视图*/
	@ViewById(R.id.same_contact_icon_layout)
	LinearLayout same_contact_icon_layout;
	/**相同群的icon视图*/
	@ViewById(R.id.same_group_icon_layout)
	LinearLayout same_group_icon_layout;
	@ViewById(R.id.same_contact_arrow)
	ImageView same_contact_arrow;
	@ViewById(R.id.same_group_arrow)
	ImageView same_group_arrow;

	@ViewById(R.id.ll_user_create)
	LinearLayout ll_user_create;
	@ViewById(R.id.ll_add_type)
	LinearLayout ll_add_type;

	// 选择角色后去哪里
	private String goWhere;
	// “附近的人”的对象
	private Serializable mSerializable;
	@Extra
	int chatType;
	@Extra
	String titleName;
	@Extra
	String headerImgId;//发消息人的头像ID
	@Extra
	String toChatName;//发消息的人名称

	@Extra
	String toChatId;//聊天对方的figureID或群ID
	@Extra
	String toChatXlId;//聊天人的userId
	@Extra
	String contactId;//联系人id
	@Extra
	String gender; // 性别

	@Extra
	String description; // 个人描述

	@Extra
	String currentFigureId;//当前选择的角色id

	Contact contact;

	private CircleImage sameContactView1, sameContactView2, sameContactView3;
	private CircleImage sameGroupView1, sameGroupView2, sameGroupView3;

	@AfterViews
	void initView() {
		initSameContactView();
		initSameGroupView();
		mTopView.setAppTitle("详细资料");
		mTopView.setLeftImageResource(R.drawable.icon_back);
		mTopView.setLeftImgOnClickListener();

		if(TextUtils.isEmpty(contactId) ||  TextUtils.isEmpty(currentFigureId)){
			mTopView.setRightImageVisibility(View.GONE);
//			ll_show.setVisibility(View.GONE);
			ll_user_create.setVisibility(View.GONE);
			ll_add_type.setVisibility(View.GONE);
		}else{
			ll_show.setVisibility(View.VISIBLE);
			mTopView.setRightImageDrawable(R.drawable.more_info_icon);
			mTopView.getRightLayout().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(UserDetailBeforeChatActivity.this,
							UserInfoEditActivity_.class).putExtra("toChatId", toChatId)
							.putExtra("contactId", contactId));

				}
			});
			contact = ContactManager.getInstance().getContact(contactId);
			if (contact != null && contact.contactLevel == Contact.ContactLevel.UMKNOWN) { // 该联系人为陌生人
				mTopView.getRightLayout().setVisibility(View.GONE);
			}
			if (contact != null&&contact.xlUserID.equals("1111")) { // 该联系人为乡邻助手
				mTopView.getRightLayout().setVisibility(View.GONE);
			}
		}

		LogCatLog.e(TAG, "contactId=" + contactId);


		if(getIntent() != null){
			goWhere = getIntent().getStringExtra("gowhere");
			mSerializable = getIntent().getSerializableExtra("serializable");
		}
		getSameContactData();
		getSameGroupData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(chatType==BorrowConstants.CHATTYPE_SINGLE){
			if(TextUtils.isEmpty(contactId)){
				FigureMode figureMode = new FigureMode();
				figureMode.setFigureName(titleName);
				if (gender == null) {
					figureMode.setFigureGender(FigureMode.FigureGender.UNKNOWN);
				} else {
					figureMode.setFigureGender(FigureMode.FigureGender.valueOf(gender));
				}
				tv_user_decribe.setText(description);
				DataDealUtil.showGenderImg(context, person_name_tv, figureMode);
				tv_person_nickname.setText("昵称: " + titleName);
			}else{
				contact = ContactManager.getInstance().getContact(contactId);
				if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
					ll_user_create.setVisibility(View.GONE);
					ll_add_type.setVisibility(View.GONE);
				}else{
					ll_user_create.setVisibility(View.VISIBLE);
					ll_add_type.setVisibility(View.VISIBLE);
				}
				tv_user_decribe.setText(contact.info);
				//取不到更新时间和创建时间
				//tv_user_create.setText(contact.relationshipTime+"-"+ Utils.stringToDate(contact.updatedate));
				String startTime=!TextUtils.isEmpty(contact.relationshipTime)?contact.relationshipTime:contact.createdate;

				if(!TextUtils.isEmpty(startTime)&&!TextUtils.isEmpty(contact.updatedate)){

					tv_user_create.setText(Utils.timeStamp2Date(startTime, "yyyy.MM.dd") + "-" + Utils.timeStamp2Date(contact.updatedate, "yyyy.MM.dd"));
				}else if(!TextUtils.isEmpty(startTime)){
					tv_user_create.setText(Utils.timeStamp2Date(startTime, "yyyy.MM.dd") + "-" +Utils.timeStamp2Date(startTime, "yyyy.MM.dd"));
				}


				tv_add_type.setText(contact.relationshipInfo.name());

                DataDealUtil.setEstablishType(tv_add_type, contact.relationshipInfo);

				DataDealUtil.showGenderImg(context, person_name_tv, contact);
				tv_person_nickname.setText("昵称: " + contact.getXlUserName());
			}
			ImageUtils.showCommonImage(this, person_header_iv,
					FileUtils.IMG_CACHE_HEADIMAGE_PATH, headerImgId, R.drawable.head);
			person_number_tv.setText("figure id: " + toChatId);


		}
	}

	/**
	 * 初始化相同联系人视图
	 */
	private void initSameContactView() {
		sameContactView1 = (CircleImage) same_contact_icon_layout.findViewById(R.id.common_image1);
		sameContactView2 = (CircleImage) same_contact_icon_layout.findViewById(R.id.common_image2);
		sameContactView3 = (CircleImage) same_contact_icon_layout.findViewById(R.id.common_image3);
	}

	/**
	 * 初始化相同联系群视图
	 */
	private void initSameGroupView() {
		sameGroupView1 = (CircleImage) same_group_icon_layout.findViewById(R.id.common_image1);
		sameGroupView2 = (CircleImage) same_group_icon_layout.findViewById(R.id.common_image2);
		sameGroupView3 = (CircleImage) same_group_icon_layout.findViewById(R.id.common_image3);
	}


	@Background
	void getSameContactData() {
		SyncApi.getInstance().sameContacts(
				toChatId,
				this,
				sameContactCallBack
		);

	}

	/**
	 * 相同联系人回调
	 */
	private SyncApi.CallBack sameContactCallBack = new SyncApi.CallBack<List<String>>() {
		@Override
		public void success(List<String> mode) {
			setSameContactView(getSameContactForDb(mode));
		}

		@Override
		public void failed(String errTip, int errCode) {
			tip(errTip);
			setSameContactView(null);
		}
	};

	/**
	 * 设置相同联系人数据
	 */
	@UiThread
	void setSameContactView(List<Contact> contactList) {
		if (contactList == null || contactList.size() == 0) {
			sameContactView1.setVisibility(View.GONE);
			sameContactView2.setVisibility(View.GONE);
			sameContactView3.setVisibility(View.GONE);
			same_contact_arrow.setVisibility(View.GONE);
			return;
		}
		same_contact_arrow.setVisibility(View.VISIBLE);
		if (contactList.size() == 1) {
			sameContactView1.setVisibility(View.VISIBLE);
			sameContactView2.setVisibility(View.GONE);
			sameContactView3.setVisibility(View.GONE);
		} else if (contactList.size() == 2) {
			sameContactView1.setVisibility(View.VISIBLE);
			sameContactView2.setVisibility(View.VISIBLE);
			sameContactView3.setVisibility(View.GONE);
		} else if (contactList.size() >= 3) {
			sameContactView1.setVisibility(View.VISIBLE);
			sameContactView2.setVisibility(View.VISIBLE);
			sameContactView3.setVisibility(View.VISIBLE);
			contactList = contactList.subList(0, 3);
		}
		for (int i = 0; i < contactList.size(); i++) {
			String contactImgPath = contactList.get(i).file_id;
			switch (i) {
				case 0: {
					ImageUtils.showCommonImage(
							this,
							sameContactView1,
							FileUtils.IMG_CACHE_HEADIMAGE_PATH,
							contactImgPath,
							R.drawable.head
					);
				}
				break;
				case 1: {
					ImageUtils.showCommonImage(
							this,
							sameContactView2,
							FileUtils.IMG_CACHE_HEADIMAGE_PATH,
							contactImgPath,
							R.drawable.head
					);
				}
				break;
				case 2: {
					ImageUtils.showCommonImage(
							this,
							sameContactView3,
							FileUtils.IMG_CACHE_HEADIMAGE_PATH,
							contactImgPath,
							R.drawable.head
					);
				}
				break;
			}
		}
	}


	/**
	 * 从内存和数据库中读取共同联系人
	 * @param contactFigureIdList 共同联系人figureid列表
	 * @return
	 */
	private List<Contact> getSameContactForDb(List<String> contactFigureIdList) {
		if (contactFigureIdList == null) {
			return null;
		}
		List<Contact> contactList = new ArrayList<>();
		ContactDBHandler contactDBHandler = new ContactDBHandler(this);

		List<String> allFigureIdList = ContactManager.getInstance().getAllFigureIdList();
		if (allFigureIdList == null || allFigureIdList.isEmpty()) {
			return null;
		}
		// 获取本地所有联系人列表
		LinkedList<Contact> allContactList = contactDBHandler.queryAllFigureCommonContact();
		for (Contact contact:
				allContactList) {
			for (String contactFigureId :
					contactFigureIdList) {
				if (contactFigureId.equals(contact.figureUsersId) && !contactList.contains(contact)) {
					contactList.add(contact);
					break;
				}
			}
		}


		// 排序 必须要排序才能去重
		Collections.sort(contactList, new Comparator<Contact>() {
			@Override
			public int compare(Contact lhs, Contact rhs) {
				if (lhs.pinying.equals(rhs.pinying)) {
					return lhs.getUIName().compareTo(rhs.getUIName());
				} else {
					if ("#".equals(lhs.pinying)) {
						return 1;
					} else if ("#".equals(rhs.pinying)) {
						return -1;
					}
					return lhs.pinying.compareTo(rhs.pinying);
				}
			}
		});


		return contactList;
	}


	/**
	 * 获取相同联系群
	 */
	@Background
	void getSameGroupData() {
		SyncApi.getInstance().sameGroups(
				toChatId,
				this,
				sameGroupCallBack

		);
	}

	/**
	 * 相同联系群回调
	 */
	private SyncApi.CallBack sameGroupCallBack = new SyncApi.CallBack<List<String>>() {
		@Override
		public void success(List<String> mode) {
			setSameGroupView(mode);
		}

		@Override
		public void failed(String errTip, int errCode) {
			tip(errTip);
			setSameGroupView(null);
		}
	};

	/**
	 * 设置相同群组数据
	 */
	@UiThread
	void setSameGroupView(List<String> mode) {
		if (mode == null || mode.size() == 0) {
			sameGroupView1.setVisibility(View.GONE);
			sameGroupView2.setVisibility(View.GONE);
			sameGroupView3.setVisibility(View.GONE);
			same_group_arrow.setVisibility(View.GONE);
			return;
		}
		same_group_arrow.setVisibility(View.VISIBLE);
		if (mode.size() == 1) {
			sameGroupView1.setImageResource(R.drawable.group_icon);
			sameGroupView1.setVisibility(View.VISIBLE);
			sameGroupView2.setVisibility(View.GONE);
			sameGroupView3.setVisibility(View.GONE);
		} else if (mode.size() == 2) {
			sameGroupView1.setImageResource(R.drawable.group_icon);
			sameGroupView2.setImageResource(R.drawable.group_icon);
			sameGroupView1.setVisibility(View.VISIBLE);
			sameGroupView2.setVisibility(View.VISIBLE);
			sameGroupView3.setVisibility(View.GONE);
		} else if (mode.size() >= 3) {
			sameGroupView1.setImageResource(R.drawable.group_icon);
			sameGroupView2.setImageResource(R.drawable.group_icon);
			sameGroupView3.setImageResource(R.drawable.group_icon);
			sameGroupView1.setVisibility(View.VISIBLE);
			sameGroupView2.setVisibility(View.VISIBLE);
			sameGroupView3.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 查看共同联系人
	 */
	@Click(R.id.same_contact_layout)
	void goToSameContact() {
		if (same_contact_arrow.isShown()) {
			SameContactActivity_.intent(this).otherFigureId(toChatId).start();
		}
	}

	/**
	 * 查看共同群
	 */
	@Click(R.id.same_group_layout)
	void goToSameGroup() {
		if (same_group_arrow.isShown()) {
			SameGroupActivity_.intent(this).otherFigureId(toChatId).start();
		}
	}


	@Click(R.id.btnSend)
	void sendMessage () {
		// 全部
		if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())){
			if(BorrowConstants.MINGLU.equals(goWhere)
					|| BorrowConstants.NEW_CONTACT.equals(goWhere)){// 去聊天（名录或者新联系人）
				Contact mContact = (Contact)mSerializable;
				if(mContact == null){
					return;
				}
				ArrayList<FigureMode> mList_FigureMode = mContact.figureGroup;
				if(mList_FigureMode == null || mList_FigureMode.isEmpty()){
					return;
				}
				FigureMode mFigureMode;

				if(mList_FigureMode.size()>1) {

					FigureMode[] mFigureModeArray = (FigureMode[]) mList_FigureMode
							.toArray(new FigureMode[mList_FigureMode.size()]);

					// 找出此人跟我的哪个角色是最近聊天的
					ContactAdapter.bigToSmallSort(mFigureModeArray);

					mFigureMode= mFigureModeArray[0];

				}else{
					mFigureMode=mList_FigureMode.get(0);
				}

				ChatMainActivity_//
						.intent(this)//
						.currentFigureId(mFigureMode.getFigureUsersid())//
						.toChatXlId(mContact.xlUserID)
						.toChatId(mContact.figureUsersId)//
						.titleName(mContact.xlUserName)//
						.headerImgId(mContact.file_id)//
						.toChatName(mContact.xlUserName)//
						.chatType(BorrowConstants.CHATTYPE_SINGLE)//
						.start();
				animLeftToRight();
			}else{
				// 选择角色
				ChooseRoleActivity_//
						.intent(this)//
						.extra("gowhere", goWhere)//
						.extra("serializable", mSerializable)//
						.start();
				animBottomToTop();
			}
		}else{
			// 某个角色
			ChatMainActivity_//
					.intent(this)//
					.currentFigureId(ContactManager.getInstance().getCurrentFigureID())// 当前角色
					.toChatXlId(toChatXlId)// 附近的人
					.toChatId(toChatId)//
					.titleName(titleName)//
					.headerImgId(headerImgId)//
					.toChatName(toChatName)//
					.chatType(BorrowConstants.CHATTYPE_SINGLE)//
					.start();
			animLeftToRight();
		}

	}



}

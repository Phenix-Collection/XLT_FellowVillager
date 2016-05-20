package com.xianglin.fellowvillager.app.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.ChooseRoleAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.ThreadPool;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 角色选择
 */
@EActivity(R.layout.activity_choose_role)
public class ChooseRoleActivity extends BaseActivity {

	@ViewById(R.id.top_bar)
	TopView topView;

	@ViewById(R.id.rv_choose_role)
	RecyclerView rv_choose_role;
	// 当前用户的所有角色
	private List<FigureMode> mList_FigureMode = new ArrayList<>();;

	// 选择角色后去哪里
	private String goWhere;
	// 传递的对象
	private Serializable mSerializable;

	private String figureIdList;
	private String from;//从哪个页面跳转过来

	@AfterInject
	public void initData () {
		if (getIntent() == null) {
			goWhere = "";
			return;
		}
		goWhere = getIntent().getStringExtra("gowhere");
		mSerializable = getIntent().getSerializableExtra("serializable");
		figureIdList=getIntent().getStringExtra("figureIdList");
		from=getIntent().getStringExtra("from");
		if(figureIdList!=null){
			String[] idList=figureIdList.split(",");
			for(int i=0;i<idList.length;i++){
                FigureMode figureMode=ContactManager.getInstance().getCurrentFigure(idList[i]);
				mList_FigureMode.add(figureMode);
			}
		}
	}

	@AfterViews
	public void initView () {

		topView.setAppTitle("角色选择");
		topView.setLeftImageResource(R.drawable.icon_back);
		topView.setLeftImgOnClickListener();

		topView.setRightTextViewText("取消");
		topView.getRightText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				animTopToBottom();
			}
		});


		if(figureIdList==null){
			ThreadPool.getCachedThreadPool().submit(new Runnable() {
				@Override
				public void run() {
					LogCatLog.i("TAG", "ChooseRoleActivity中的线程在执行...");
					Map<String, FigureMode> map = ContactManager.getInstance().getFigureTable();
					if (map == null) {
						return;
					}
					Set<Map.Entry<String, FigureMode>> set = map.entrySet();
					Iterator<Map.Entry<String, FigureMode>> iter = set.iterator();
					while (iter.hasNext()) {
						Map.Entry<String, FigureMode> map_ = iter.next();
						mList_FigureMode.add(map_.getValue());
					}
				}
			});
		}
		ChooseRoleAdapter mChooseRoleAdapter = new ChooseRoleAdapter(ChooseRoleActivity.this, mList_FigureMode, goWhere, mSerializable);
		mChooseRoleAdapter.setFrom(from);
		rv_choose_role.setLayoutManager(new LinearLayoutManager(ChooseRoleActivity.this));
		rv_choose_role.setAdapter(mChooseRoleAdapter);
	}

	/*private List<FigureMode> createData () {
		List<FigureMode> mList_ = new ArrayList<>();
		FigureMode fm = new FigureMode();
		for (int i = 0; i < 24; i++) {
			fm.setCreateDate("2016/2/27");
			//			fm.setFigureGender("男");
			fm.setFigureGroup("天堂");
			fm.setFigureImageid("5210");
			fm.setFigureInfo("weidi5858258组");
			fm.setFigureRelationship("扫一扫");
			//			fm.setFigureStatus("");
			fm.setFigureUsersid("" + (52101 + i));
			fm.setFigureXlremarks("今天星期天");
			fm.setImagePathThumbnail("null");
			fm.setImagePpath("null");
			fm.setFigureName("中国人");
			fm.setUpdateDate(System.currentTimeMillis());
			fm.setXlId("" + (10431 + i));
			mList_.add(fm);
		}
		return mList_;
	}*/

	/*private void getListFromWeb () {
		List<FigureDTO> mList_FigureDTO = SyncApi.getInstance().list();
		if (mList_FigureDTO == null) {
			// 提示信息
			return;
		}
		if (mList_FigureDTO.isEmpty()) {

		} else {
			*//**
			 private String partyId;
			 private String figureId;
			 private String nickName;
			 private String avatarUrl;
			 private String gender;
			 private String sexualOrientation;
			 private String individualitySignature;
			 private String status;
			 private boolean open;
			 *//*
			List<FigureMode> mList_FigureMode = new ArrayList<>();
			for (int i = 0; i < mList_FigureDTO.size(); i++) {
				FigureDTO mFigureDTO = mList_FigureDTO.get(i);
				FigureMode mFigureMode = new FigureMode();
				mFigureMode.setXlId("");
				mFigureMode.setUpdateDate(System.currentTimeMillis());
				mFigureMode.setCreateDate("");
				mFigureMode.setFigureName("");
				mFigureMode.setImagePathThumbnail("");
				//				mFigureMode.setFigureStatus("");
				mFigureMode.setIsOpen("");
				mFigureMode.setFigureImageid("");
				//				mFigureMode.setFigureGender("");
			}
		}
	}*/

}

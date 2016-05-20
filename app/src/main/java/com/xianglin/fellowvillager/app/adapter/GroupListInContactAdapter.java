package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.group.GroupListInContactActivity;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 群聊列表展示的Adapter
 author:王力伟 time：2016.2.27
 */
public class GroupListInContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private Context mContext;
	/**与该角色相关的所有群*/
	private List<Group> mListData_All;
	/**管理的群*/
	private List<Group> mListData_Manager;
	/**我参与的群*/
	private List<Group> mListData_Join;
	/**已解散的群*/
	private List<Group> mListData_Disband;
	private List<Group> mListData;
	public static final String GROUP_TYPE_NORMAL = "NORMARL";
	public static final String GROUP_TYPE_BLACK = "BLACK";
	/**群组类别类型的View类型*/
	private static final int VIEW_TYPE_KIND = 0;
	/**item的view类型*/
	private static final int VIEW_TYPE_ITEM_NORMAL = 1;
	private static final int VIEW_TYPE_ITEM_DISBAND = 2;

	// List<Group>:代表群的集合
	private Map<String, List<Group>> mMap_GroupList;

	public GroupListInContactAdapter(Context context, Map<String, List<Group>> map) {
		this.mContext = context;
		this.mMap_GroupList = map;

		if(mMap_GroupList == null){
			return;
		}
		Iterator<Map.Entry<String, List<Group>>> iterator = mMap_GroupList.entrySet().iterator();
		if(iterator == null){
			return;
		}
		mListData = new ArrayList<>();
		while (iterator.hasNext()) {

			Map.Entry<String, List<Group>> entry = iterator.next();
			if(GROUP_TYPE_NORMAL.equals(entry.getKey())){
				mListData_All = entry.getValue();
			}
		}

		mListData_Manager = getManagingGroupList(mListData_All);
		mListData_Join = getJoiningGroupList(mListData_All);
		mListData_Disband = getDisbandedGroupList(mListData_All);


		if (mListData_Manager != null && mListData_Manager.size() != 0) {
			for(int i=0;i<mListData_Manager.size();i++){
				mListData.add(mListData_Manager.get(i));
			}
		}
		if (mListData_Join != null && mListData_Join.size() != 0) {
			for(int i=0;i<mListData_Join.size();i++){
				mListData.add(mListData_Join.get(i));
			}
		}
		if (mListData_Disband != null && mListData_Disband.size() != 0) {
			for(int i=0;i<mListData_Disband.size();i++){
				mListData.add(mListData_Disband.get(i));
			}
		}

	}

	/**
	 * 获得管理的群
	 * @param list 与该角色相关的所有群
	 * @return 管理的群
     */
	private List<Group> getManagingGroupList(List<Group> list) {
		List<Group> groupList = new ArrayList<>();
		if (list == null) {
			return null;
		}
		// 当前为全部角色
		if (TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())) {
			for (Group group:
					list) {
				if (group == null) {
					continue;
				}
				// 判断是否是群主
				if (group.status.equals("ACTIVE")
						&& ContactManager.getInstance().getCurrentFigure(group.ownerFigureId) != null) {
					group.groupCustomType = "A";
					groupList.add(group);
				}
			}
		} else { // 单角色
			for (Group group:
					list) {
				if (group == null) {
					continue;
				}
				// 判断是否是群主
				if (group.status.equals("ACTIVE")
						&& group.ownerFigureId.equals(ContactManager.getInstance().getCurrentFigureID())) {
					group.groupCustomType = "A";
					groupList.add(group);
				}
			}
		}

		if (!groupList.isEmpty()) {
			Group kindGroup = new Group.Builder().build();
			kindGroup.groupCustomType = "MANAGE";
			groupList.add(0, kindGroup);
		}
		return groupList;
	}

	/**
	 * 获得参与的群
	 * @param list 与该角色相关的所有群
	 * @return 参与的群
	 */
	private List<Group> getJoiningGroupList(List<Group> list) {
		List<Group> groupList = new ArrayList<>();
		if (list == null) {
			return null;
		}

		// 当前为全部角色
		if (TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())) {
			for (Group group:
					list) {
				if (group == null) {
					continue;
				}
				// 判断是否是群主
				if (group.status.equals("ACTIVE")
						&& ContactManager.getInstance().getCurrentFigure(group.ownerFigureId) == null) {
					group.groupCustomType = "A";
					groupList.add(group);
				}
			}
		} else { // 单角色
			for (Group group:
					list) {
				if (group == null) {
					continue;
				}
				// 判断是否是群主
				if (group.status.equals("ACTIVE")
						&& !group.ownerFigureId.equals(ContactManager.getInstance().getCurrentFigureID())) {
					group.groupCustomType = "B";
					groupList.add(group);
				}
			}
		}


		if (!groupList.isEmpty()) {
			Group kindGroup = new Group.Builder().build();
			kindGroup.groupCustomType = "JOIN";
			groupList.add(0, kindGroup);
		}
		return groupList;
	}

	/**
	 * 获得解散的群
	 * @param list 与该角色相关的所有群
	 * @return 解散的群
	 */
	private List<Group> getDisbandedGroupList(List<Group> list) {
		List<Group> groupList = new ArrayList<>();
		if (list == null) {
			return null;
		}

		for (Group group:
				list) {
			if (group == null) {
				continue;
			}
			// 判断是否是已解散的群
			if (group.status.equals("DISMISS")) {
				group.groupCustomType = "C";
				groupList.add(group);
			}
		}
		if (!groupList.isEmpty()) {
			Group kindGroup = new Group.Builder().build();
			kindGroup.groupCustomType = "DISBAND";
			groupList.add(0, kindGroup);
		}
		return groupList;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int viewType) {
		if (viewType == VIEW_TYPE_KIND) {
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(
					R.layout.item_group_kind,
					viewGroup,
					false
			);
			return new GroupKindViewHolder(v);
		}

		if(viewType ==VIEW_TYPE_ITEM_NORMAL){
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group_in_contact, viewGroup, false);

			return new ViewHolder(v);
		}

		if(viewType ==VIEW_TYPE_ITEM_DISBAND){
			View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group_in_contact_diss, viewGroup, false);

			return new ViewHolderDisband(v);
		}

		return null;
	}

	@Override
	public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
			case VIEW_TYPE_ITEM_NORMAL: {
				showNormalInfo((ViewHolder) holder, position);
			}
			break;
			case VIEW_TYPE_ITEM_DISBAND: {
				showDisbandInfo((ViewHolderDisband) holder, position);
			}
			break;
			case VIEW_TYPE_KIND: {
				showKind((GroupKindViewHolder)holder, position);
			}
			break;
		}

	}

	@Override
	public int getItemViewType(int position) {
		if ("A".equals(mListData.get(position).groupCustomType)
				|| "B".equals(mListData.get(position).groupCustomType)

				) {
			return VIEW_TYPE_ITEM_NORMAL;
		}
		if( "C".equals(mListData.get(position).groupCustomType)){
			return VIEW_TYPE_ITEM_DISBAND;
		}

		return VIEW_TYPE_KIND;
	}

	@Override
	public int getItemCount () {
		return mListData.size();
	}

	// 重写的自定义ViewHolder
	 class ViewHolder extends RecyclerView.ViewHolder {
		// 前三个群成员图片
		CircleImage ci_minglu_contact_head1;
		CircleImage ci_minglu_contact_head2;
		CircleImage ci_minglu_contact_head3;
		// 群名称
		TextView tv_minglu_contact_nick;
		//拉黑
		TextView tv_disband;
		// 角色小图片
		CircleImage ci_role_1;
		LinearLayout itemLayout;

		public ViewHolder (View view) {
			super(view);
			ci_minglu_contact_head1 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head1);
			ci_minglu_contact_head2 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head2);
			ci_minglu_contact_head3 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head3);
			tv_minglu_contact_nick = (TextView) view.findViewById(R.id.tv_minglu_contact_nick);
			itemLayout = (LinearLayout) view.findViewById(R.id.ll_minglu_contact);
			tv_disband = (TextView) view.findViewById(R.id.tv_disband);
			ci_role_1 = (CircleImage) view.findViewById(R.id.ci_role_3);
		}
	}
	// 重写的自定义ViewHolder
	class ViewHolderDisband extends RecyclerView.ViewHolder {
		// 前三个群成员图片
		CircleImage ci_minglu_contact_head1;
		CircleImage ci_minglu_contact_head2;
		CircleImage ci_minglu_contact_head3;
		// 群名称
		TextView tv_minglu_contact_nick;

		// 角色小图片
		CircleImage ci_role_1;
		LinearLayout itemLayout;

		public ViewHolderDisband (View view) {
			super(view);
			ci_minglu_contact_head1 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head1);
			ci_minglu_contact_head2 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head2);
			ci_minglu_contact_head3 = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head3);
			tv_minglu_contact_nick = (TextView) view.findViewById(R.id.tv_minglu_contact_nick);

			itemLayout = (LinearLayout) view.findViewById(R.id.ll_minglu_contact);
			ci_role_1 = (CircleImage) view.findViewById(R.id.ci_role_3);
		}
	}

	/**
	 * 群组类别ViewHolder
	 */
	class GroupKindViewHolder extends RecyclerView.ViewHolder {

		private TextView tv_group;

		public GroupKindViewHolder(View view) {
			super(view);
			tv_group = (TextView) view.findViewById(R.id.front);
		}
	}



	/**
	 * 显示群类别
	 * @param holder
	 * @param position
     */
	private void showKind(GroupKindViewHolder holder, int position) {
		if (mListData == null || mListData.isEmpty()) {
			return;
		}
		Group group = mListData.get(position);
		if (group == null) {
			return;
		}
		if ("MANAGE".equals(group.groupCustomType)) {
			holder.tv_group.setText("我管理的群");
		} else if ("JOIN".equals(group.groupCustomType)) {
			holder.tv_group.setText("我参与的群");
		} else if ("DISBAND".equals(group.groupCustomType)) {
			holder.tv_group.setText("已解散的群");
		}
	}





	/**
	 填充数据，根据条件显示不同的群
	 @param holder
	 @param position
	 */
	private void showNormalInfo (ViewHolder holder, int position) {
		if(mListData.size() == 0){
			return;
		}
		final Group group1 = mListData.get(position);
		if(group1 == null){
			return;
		}

		holder.ci_minglu_contact_head1.setImageResource(R.drawable.head);
		holder.ci_minglu_contact_head2.setImageResource(R.drawable.head);
		holder.ci_minglu_contact_head3.setImageResource(R.drawable.head);

			holder.tv_minglu_contact_nick.setTextColor(mContext.getResources().getColor(R.color.black));

		holder.tv_minglu_contact_nick.setText(group1.xlGroupName);

		if (ContactManager.getInstance().getCurrentFigure() == null) {
			ImageUtils.showCommonImage(
					(Activity) mContext,
					holder.ci_role_1,
					FileUtils.IMG_CACHE_HEADIMAGE_PATH,
					ContactManager.getInstance().getCurrentFigure(group1.figureId).getFigureImageid(),
					R.drawable.head
			);
			holder.ci_role_1.setVisibility(View.VISIBLE);
		} else {
			holder.ci_role_1.setVisibility(View.GONE);
		}


		holder.tv_disband.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moveToBlackList(group1);
			}
		});

		holder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClick_(group1);
			}
		});

	}

	/**
	 填充数据，根据条件显示不同的群
	 @param holder
	 @param position
	 */
	private void showDisbandInfo (ViewHolderDisband holder, int position) {
		if(mListData.size() == 0){
			return;
		}
		final Group group1 = mListData.get(position);
		if(group1 == null){
			return;
		}

		holder.ci_minglu_contact_head1.setImageResource(R.drawable.head);
		holder.ci_minglu_contact_head2.setImageResource(R.drawable.head);
		holder.ci_minglu_contact_head3.setImageResource(R.drawable.head);
		// 群名称

			holder.tv_minglu_contact_nick.setTextColor(mContext.getResources().getColor(R.color.btn_gray_pressed_status));

		holder.tv_minglu_contact_nick.setText(group1.xlGroupName);

		if (ContactManager.getInstance().getCurrentFigure() == null) {
			ImageUtils.showCommonImage(
					(Activity) mContext,
					holder.ci_role_1,
					FileUtils.IMG_CACHE_HEADIMAGE_PATH,
					ContactManager.getInstance().getCurrentFigure(group1.figureId).getFigureImageid(),
					R.drawable.head
			);
			holder.ci_role_1.setVisibility(View.VISIBLE);
		} else {
			holder.ci_role_1.setVisibility(View.GONE);
		}


		holder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClick_(group1);
			}
		});
	}

	private void moveToBlackList(final Group group) {

		if(group == null){
			return;
		}

		BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
			@Override
			public void execute() {
				try {
					SyncApi.getInstance().moveToBlackList(group.figureId, group.xlGroupID, mContext, new SyncApi.CallBack() {
						@Override
						public void success(Object mode) {

							if(GroupManager.getInstance().moveToBlackList(group.localGroupId)){
								((GroupListInContactActivity)mContext).tip("拉黑成功");
								((GroupListInContactActivity)mContext).loadData();
							};

						}

						@Override
						public void failed(String errTip, int errCode) {
							((BaseActivity)mContext).tip(errCode);
						}
					});

				} catch (Throwable e) {
					Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread
							.currentThread(), e);
				}
			}
		});



	}

	private void onClick_(Group group){
		if(group == null){
			return;
		}

		ChatMainActivity_//
				.intent(mContext)//
				.currentFigureId(group.figureId)
				.toChatId(group.xlGroupID)
				.titleName(group.xlGroupName)//
				.chatType(BorrowConstants.CHATTYPE_GROUP)//
				.start();



		if(mContext instanceof BaseActivity){
			((BaseActivity)mContext).animLeftToRight();
		}

	}

}

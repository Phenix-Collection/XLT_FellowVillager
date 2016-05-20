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
import com.xianglin.fellowvillager.app.activity.UserDetailBeforeChatActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import java.util.ArrayList;
import java.util.List;

/**
 新联系人的Adapter
 author:王力伟 time：2016.2.27
 */
public class NewContactAdapter extends RecyclerView.Adapter<NewContactAdapter.ViewHolder> {

	private Context mContext;
	private List<Contact> mList_Contact;
	private OnItemClickListener mOnItemClickListener;

	public NewContactAdapter (Context context, List<Contact> list) {
		this.mContext = context;
		this.mList_Contact = list;
	}

	@Override
	public ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int viewType) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item_minglu, viewGroup, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder (ViewHolder holder, int position) {

		showInfo(holder, position);

	}

	@Override
	public int getItemCount () {
		return mList_Contact == null || mList_Contact.size() == 0 ? 0 : mList_Contact.size();
	}

	// 重写的自定义ViewHolder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		LinearLayout ll_minglu_contact;
		// 角色头像
		CircleImage ci_minglu_contact_head;
		// 角色名称
		TextView tv_minglu_contact_nick;
		// 角色小图片
		CircleImage ci_role_1;
		CircleImage ci_role_2;
		CircleImage ci_role_3;

		public ViewHolder (View view) {
			super(view);
			ll_minglu_contact = (LinearLayout) view.findViewById(R.id.ll_minglu_contact);
			ci_minglu_contact_head = (CircleImage) view.findViewById(R.id.ci_minglu_contact_head);
			tv_minglu_contact_nick = (TextView) view.findViewById(R.id.tv_minglu_contact_nick);
			ci_role_1 = (CircleImage) view.findViewById(R.id.ci_role_1);
			ci_role_2 = (CircleImage) view.findViewById(R.id.ci_role_2);
			ci_role_3 = (CircleImage) view.findViewById(R.id.ci_role_3);
		}
	}

	public interface OnItemClickListener {
		void onItemClick ();
	}

	public void setOnItemClickListener (OnItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	/**
	 填充数据，根据条件显示不同的群
	 @param holder
	 @param position
	 */
	private void showInfo (ViewHolder holder, final int position) {
		if(mList_Contact == null || mList_Contact.isEmpty()){
			return;
		}
		Contact mContact = mList_Contact.get(position);
		if(mContact == null){
			return;
		}

		// 注册点击事件
		if (mOnItemClickListener == null) {
			holder.ll_minglu_contact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick (View v) {
					onClick_(position);
				}
			});
		} else {
			holder.ll_minglu_contact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick (View v) {
					mOnItemClickListener.onItemClick();
				}
			});
		}

		// 联系人头像
		ImageUtils.showCommonImage((Activity) mContext, holder.ci_minglu_contact_head, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mContact.file_id, R.drawable.head);
		// 联系人名称
		String name =mContact.getXlUserName();
		int  nameLen = name.length();
		if(nameLen > 12){

			name = name.substring(0,12)+"...";

		}
		holder.tv_minglu_contact_nick.setText(name);

		holder.ci_role_1.setVisibility(View.GONE);
		holder.ci_role_2.setVisibility(View.GONE);
		holder.ci_role_3.setVisibility(View.GONE);

		// “全部”时显示右边的小图像
		if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())){
			ArrayList<FigureMode> mList = mContact.figureGroup;
			if(mList == null){
				return;
			}
			if(mList.size() == 1){
				holder.ci_role_1.setVisibility(View.VISIBLE);
				FigureMode mFigureMode = mList.get(0);
				if(mFigureMode != null){
					ImageUtils.showCommonImage((Activity) mContext, holder.ci_role_1, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode.getFigureImageid(), R.drawable.head);
				}else{
					holder.ci_role_1.setImageResource(R.drawable.head);
				}

				// 测试使用
//				holder.ci_role_1.setImageResource(R.drawable.head);
			}else if(mList.size() == 2){
				holder.ci_role_1.setVisibility(View.VISIBLE);
				holder.ci_role_2.setVisibility(View.VISIBLE);
				FigureMode mFigureMode1 = mList.get(0);
				FigureMode mFigureMode2 = mList.get(1);
				if(mFigureMode1 != null && mFigureMode2 != null){
					ImageUtils.showCommonImage((Activity) mContext, holder.ci_role_1, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode1.getFigureImageid(), R.drawable.head);
					ImageUtils.showCommonImage((Activity) mContext, holder.ci_role_2, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode2.getFigureImageid(), R.drawable.head);
				}else{
					holder.ci_role_1.setImageResource(R.drawable.head);
					holder.ci_role_2.setImageResource(R.drawable.head);
				}

				// 测试使用
//				holder.ci_role_1.setImageResource(R.drawable.head);
//				holder.ci_role_2.setImageResource(R.drawable.head);
			}else{
				holder.ci_role_1.setVisibility(View.VISIBLE);
				holder.ci_role_2.setVisibility(View.VISIBLE);
				holder.ci_role_3.setVisibility(View.VISIBLE);
				holder.ci_role_1.setImageResource(R.drawable.more);

				FigureMode[] mFigureModeArray = (FigureMode[]) mList.toArray(new FigureMode[mList.size()]);

				// 找出此人跟我的哪个角色是最近聊天的
				ContactAdapter.bigToSmallSort(mFigureModeArray);

				FigureMode mFigureMode1 = mFigureModeArray[0];
				FigureMode mFigureMode2 = mFigureModeArray[1];

				if(mFigureMode1 != null && mFigureMode2 != null){
					ImageUtils.showCommonImage((Activity) mContext, holder.ci_role_2, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode2.getFigureImageid(), R.drawable.head);
					ImageUtils.showCommonImage((Activity) mContext, holder.ci_role_3, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureMode1.getFigureImageid(), R.drawable.head);
				}else{
					holder.ci_role_2.setImageResource(R.drawable.head);
					holder.ci_role_3.setImageResource(R.drawable.head);
				}

				// 测试使用
//				holder.ci_role_2.setImageResource(R.drawable.head);
//				holder.ci_role_3.setImageResource(R.drawable.head);
			}
		}

	}

	private void onClick_(int position){
		// 新联系人点击进入后不需要选择角色，直接进入个人主页
		Contact mContact = mList_Contact.get(position);
		if(mContact == null){
			return;
		}
		UserDetailBeforeChatActivity_//
				.intent(mContext)//
				.extra("gowhere", BorrowConstants.NEW_CONTACT)//
				.extra("serializable", mContact)//
				.toChatId(mContact.figureUsersId)//
				.toChatXlId(mContact.xlUserID)
				.titleName(mContact.xlUserName)//
				.headerImgId(mContact.file_id)//
				.toChatName(mContact.xlUserName)//
				.chatType(BorrowConstants.CHATTYPE_SINGLE)//
				.contactId(mContact.contactId)
				.start();
		if (mContext instanceof BaseActivity) {
			((BaseActivity) mContext).animLeftToRight();
		}
	}

}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.UserDetailBeforeChatActivity_;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import java.util.List;

/**
 * 附近联系人列表
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/18 15:09  XLXZ Exp $
 */
public class NearbyListAdapter extends BaseAdapter {
    protected Context mContext;
    private List<UserFigureDTO> mList;
    protected LayoutInflater mInflater;

    public NearbyListAdapter(List<UserFigureDTO> list, Context context) {
        if (context == null) {
            return;
        }
        this.mList = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 设置adapter数据
     * @param list
     */
    public void setData(List<UserFigureDTO> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null || mList.isEmpty() ? 0 : mList.size();
    }

    @Override
    public UserFigureDTO getItem(int position) {
        if (mList == null) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewH;

        if (convertView == null) {
            viewH = new ViewHolder();
            convertView = mInflater.inflate(R.layout.contact_item_minglu, parent, false);
            viewH.ll_minglu_contact = (LinearLayout) convertView.findViewById(R.id.ll_minglu_contact);
            viewH.ci_minglu_contact_head = (CircleImage) convertView.findViewById(R.id.ci_minglu_contact_head);
            viewH.tv_minglu_contact_nick = (TextView) convertView.findViewById(R.id.tv_minglu_contact_nick);
            convertView.setTag(viewH);
        } else {
            viewH = (ViewHolder) convertView.getTag();
        }

        viewH.ll_minglu_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_(position);
            }
        });
        UserFigureDTO mUserFigureDTO = getItem(position);
        if (mUserFigureDTO != null) {
            String avatarUrl = mUserFigureDTO.getAvatarUrl();
            if (mContext instanceof BaseActivity) {
                ImageUtils.showCommonImage((BaseActivity) mContext, viewH.ci_minglu_contact_head, FileUtils.IMG_CACHE_HEADIMAGE_PATH, avatarUrl, R.drawable.head);
            }
            viewH.tv_minglu_contact_nick.setText(mUserFigureDTO.getNickName());
        }

        return convertView;
    }

    class ViewHolder {
        protected LinearLayout ll_minglu_contact;
        protected CircleImage ci_minglu_contact_head;
        protected TextView tv_minglu_contact_nick;
    }

    private void onClick_(int position) {
        if (mList == null) {
            return;
        }
        // 附近的人
        UserFigureDTO mUserFigureDTO = mList.get(position);
        if (mUserFigureDTO == null) {
            return;
        }

        // 个人主页
        UserDetailBeforeChatActivity_//
                .intent(mContext)//
                .extra("gowhere", BorrowConstants.NEAR_PEOPLE)//
                .extra("serializable", mUserFigureDTO)//
                .toChatXlId(mUserFigureDTO.getUserId())//
                .toChatId(mUserFigureDTO.getFigureId())//
                .titleName(mUserFigureDTO.getNickName())//
                .gender(mUserFigureDTO.getGender())
                .description(mUserFigureDTO.getIndividualitySignature())
                .headerImgId(mUserFigureDTO.getAvatarUrl())//
                .toChatName(mUserFigureDTO.getNickName())//
                .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                .start();

        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).animLeftToRight();
        }
    }

}

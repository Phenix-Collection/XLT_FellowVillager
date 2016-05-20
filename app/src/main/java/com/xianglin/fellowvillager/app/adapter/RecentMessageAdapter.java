/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.RecentMessageBean;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

public class RecentMessageAdapter extends BaseCursorAdapter {

    private int mRightWidth = 0;
    MessageDBHandler.RecentMessageCursor mRecentMessageCursor;

    public RecentMessageAdapter(Context context, Cursor c, int rightWidth) {
        super(context, c);
        mRightWidth = rightWidth;
        mRecentMessageCursor = new MessageDBHandler.RecentMessageCursor(c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View convertView = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_contact_list_item, parent, false);

        holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
        holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
        holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
        holder.tv_number = (TextView) convertView.findViewById(R.id.unread_msg_number);
        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
        holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        holder.item_right_txt = (TextView) convertView.findViewById(R.id.item_right_txt);
        holder.item_figure_icon = (CircleImage) convertView.findViewById(R.id.iv_figure_icon);
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public RecentMessageBean getItem(int position) {
        return mRecentMessageCursor.getMessage();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        mRecentMessageCursor = new MessageDBHandler.RecentMessageCursor(cursor);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final RecentMessageBean msg = mRecentMessageCursor.getMessage();

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth,
                LinearLayout.LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);
        holder.tv_title.setText(msg.getXlListTitle());
        holder.tv_msg.setText(msg.getXlLastMsg());
        holder.tv_time.setText(msg.getXlLastTime());

        if ("" == ContactManager.getInstance().getCurrentFigureID()) {
            FigureMode mCurFigureMode = ContactManager.getInstance().
                    getCurrentFigure(msg.getFigureId());
            ImageUtils.showCommonImage((Activity) mContext,
                    holder.item_figure_icon,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    mCurFigureMode.getFigureImageid(),
                    R.drawable.head);
        } else {
            holder.item_figure_icon.setVisibility(View.GONE);
        }

        holder.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightItemClick(msg);
                }
            }
        });

    }


    static class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;
        TextView tv_title;
        TextView tv_msg;
        TextView tv_time;
        ImageView iv_icon;
        TextView tv_number;
        TextView item_right_txt;
        CircleImage item_figure_icon;
    }

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;

    public void setOnRightItemClickListener(onRightItemClickListener listener) {
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(RecentMessageBean recentMessageBean);
    }

    @Override
    public void onContentChanged() {
        this.notifyDataSetChanged();
    }
}

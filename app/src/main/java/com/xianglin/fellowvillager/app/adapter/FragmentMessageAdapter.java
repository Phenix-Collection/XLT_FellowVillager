package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.utils.SmileUtils;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.RecentMessageBean;
import com.xianglin.fellowvillager.app.utils.CustomToast;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.CircleImage;

import java.util.ArrayList;
import java.util.List;

public class FragmentMessageAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    private Context mContext = null;

    private static final String TAG = FragmentMessageAdapter.class.getSimpleName();

    public void setData(List<RecentMessageBean> data) {
        mData = data;
    }

    private List<RecentMessageBean> mData;
    private List<RecentMessageBean> messageBeans = new ArrayList<RecentMessageBean>();
    private int mRightWidth = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ViewHolder holder = (ViewHolder) msg.obj;
            Bundle bundle = msg.getData();
            String fileName = bundle.getString("fileName");
            ImageLoader.getInstance().displayImage("file://" +
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileName, holder.iv_icon);
        }
    };

    /**
     * @param
     */
    public FragmentMessageAdapter(Context ctx, List<RecentMessageBean> data, int rightWidth) {
        mContext = ctx;
        this.mData = data;
        mRightWidth = rightWidth;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_contact_list_item, parent, false);
            holder = new ViewHolder();
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
        } else {// 有直接获得ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        RecentMessageBean msg = mData.get(position);

        LayoutParams lp1 = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        holder.item_left.setLayoutParams(lp1);
        LayoutParams lp2 = new LayoutParams(mRightWidth,
                LayoutParams.MATCH_PARENT);
        holder.item_right.setLayoutParams(lp2);
        holder.tv_title.setText(msg.getXlListTitle());

        if (msg.getXlListType() == BorrowConstants.CHATTYPE_GROUP) {
            holder.iv_icon.setImageResource(R.drawable.group_icon);
        } else if (msg.getXlListType() == BorrowConstants.CHATTYPE_SINGLE) {
            String headerImgId = !TextUtils.isEmpty(msg.getXlImagePath()) ? msg.getXlImagePath() :
                    msg.getFile_id();

            ImageUtils.showCommonImage((Activity) mContext, holder.iv_icon,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH, headerImgId, R.drawable.head);
        } else {
            holder.iv_icon.setImageResource(R.drawable.head);
        }

        String strmsg = msg.getXlLastMsg();
        if (TextUtils.isEmpty(strmsg)) {
            strmsg = "";
        }

        if(msg.isPrivate()){
            holder.tv_msg.setText("正在私密聊天");
            holder.tv_msg.setTextColor(mContext.getResources().getColor(R.color.line_chat_bg));
        }else {
            holder.tv_msg.setTextColor(mContext.getResources().getColor(R.color.app_text_color3));
            if (msg.getMsg_type().equals(MessageChatAdapter.VOICE + "")) {
                holder.tv_msg.setText("[语音]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.IMAGE + "")) {
                holder.tv_msg.setText("[图片]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.IDCARD + "")) {
                holder.tv_msg.setText("[名片]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.NEWSCARD + "")) {
                holder.tv_msg.setText("[外部网页]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.WEBSHOPPING + "")) {
                holder.tv_msg.setText("[商品]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.REDBUNDLE + "")) {
                holder.tv_msg.setText("[红包]");
            } else if (msg.getMsg_type().equals(MessageChatAdapter.TEXT + "")) {
                Spannable span = SmileUtils
                        .getSmiledText(mContext, strmsg, 15);
                holder.tv_msg.setText(span, TextView.BufferType.SPANNABLE);
            } else {
                holder.tv_msg.setText(strmsg);
            }
        }
        holder.tv_time.setText(CustomToast.formatDateTime(msg.getXlLastTime()));

        int msgNum = Integer.parseInt(msg.getXlMsgNum() == null ? "0" : msg.getXlMsgNum() + "");

        if (msgNum > 99) {
            holder.tv_number.setVisibility(View.VISIBLE);
            holder.tv_number.setBackgroundResource(R.drawable.aii2);
            holder.tv_number.setText("99+");
            ViewGroup.LayoutParams params = holder.tv_number.getLayoutParams();
            params.width = Utils.dipToPixel(mContext, 20);
            holder.tv_number.setLayoutParams(params);
        } else if (msgNum > 0) {
            holder.tv_number.setBackgroundResource(R.drawable.aii);
            ViewGroup.LayoutParams params = holder.tv_number.getLayoutParams();
            params.width = Utils.dipToPixel(mContext, 15);
            holder.tv_number.setLayoutParams(params);
            holder.tv_number.setVisibility(View.VISIBLE);
            holder.tv_number.setText(msgNum + "");
        } else {
            holder.tv_number.setVisibility(View.GONE);
        }

        if ("" == ContactManager.getInstance().getCurrentFigureID()) {
            if (msg == null || msg.getFigureId() == null) return convertView;
            FigureMode mCurFigureMode = ContactManager.getInstance().
                    getCurrentFigure(msg.getFigureId());
            if (mCurFigureMode != null)
                ImageUtils.showCommonImage((Activity) mContext,
                        holder.item_figure_icon,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        mCurFigureMode.getFigureImageid(),
                        R.drawable.head);
        } else {
            holder.item_figure_icon.setVisibility(View.GONE);
        }

        holder.item_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightItemClick(v, position);
                }
            }
        });
        return convertView;
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
        void onRightItemClick(View v, int position);
    }

}





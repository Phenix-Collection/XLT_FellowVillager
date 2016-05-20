package com.xianglin.fellowvillager.app.chat.adpter.viewholder;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xianglin.fellowvillager.app.widget.CircleProgressView;

/**
 * Created by zhanglisan on 16/3/31.
 */
public class MessageViewHolder {
    public LinearLayout itemMessage;
    public ImageView iv;
    public RelativeLayout rl_voice;//语音长度控制
    public SimpleDraweeView chatImageView;
    public ImageView iv_loading;
    public RelativeLayout rl_picture;
    public TextView tvSubTitle;
    public TextView webTitle;//视图显示标题
    public ImageView link_icon;//外部链接标识
    public TextView webRealPrice;//真实价格
    public TextView webOldPrice;//原始价格
    public ImageView cardImage;//卡片图片
    public LinearLayout ll_chatcontent;//显示内容
    public TextView tv;
    public ProgressBar pb;
    public ImageView staus_iv;
    public ImageView head_iv;
    public TextView tv_userId;
    public ImageView iv_read_status;

    public TextView tv_sys_msg;
    public CircleProgressView circleProgressView;
}

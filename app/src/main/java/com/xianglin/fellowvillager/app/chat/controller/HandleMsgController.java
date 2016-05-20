package com.xianglin.fellowvillager.app.chat.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.WebviewActivity_;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.ShowBigImageActivity_;
import com.xianglin.fellowvillager.app.chat.ShowGifImageActivity_;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.adpter.viewholder.MessageViewHolder;
import com.xianglin.fellowvillager.app.chat.model.VoicePlayStatus;
import com.xianglin.fellowvillager.app.chat.utils.SmileUtils;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.NoUnderlineSpan;
import com.xianglin.fellowvillager.app.utils.SoundUtil;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.audio.AlipayVoiceRecorder;
import com.xianglin.mobile.common.filenetwork.model.AddressManager;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.api.BackgroundExecutor;

import java.util.List;

/**
 * 消息处理工具类
 * Created by zhanglisan on 16/3/31.
 */
public class HandleMsgController {

    private static final String TAG = "HandleMsgController";

    private Activity activity;
    private Context context;
    private String currentFigureId;
    private AlipayVoiceRecorder mAlipayVoiceRecorder;
    private int mCurrentPlayingIndex = -1;
    private int chatType;
    private String toChatId;

    private HandleMsgCallBack callBack;

    private volatile static HandleMsgController instance;

    private HandleMsgController() {
    }

    public static HandleMsgController getInstance() {
        if (instance == null) {
            synchronized (HandleMsgController.class) {
                if (instance == null) {
                    instance = new HandleMsgController();
                }
            }
        }
        return instance;
    }

    public void init(
            Activity activity,
            String currentFigureId,
            AlipayVoiceRecorder alipayVoiceRecorder,
            int chatType,
            String toChatId,
            HandleMsgCallBack callBack
    ) {
        this.activity = activity;
        this.context = activity;
        this.currentFigureId = currentFigureId;
        this.mAlipayVoiceRecorder = alipayVoiceRecorder;
        this.chatType = chatType;
        this.toChatId = toChatId;
        this.callBack = callBack;
    }

    /**
     * 处理消息回调
     */
    public interface HandleMsgCallBack {
        void refreshUI(final MessageViewHolder holder, MessageBean messageBean);
    }

    public void handlerTextMsg(final MessageViewHolder holder, final MessageBean messageBean) {
        if (messageBean.msgContent == null) return;
        int msgStatus = messageBean.msgStatus;
        Spannable span = SmileUtils.getSmiledText(context, messageBean.msgContent, 23);

        showMsgState(holder, messageBean);

        if (holder.staus_iv != null)
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMsgController.getInstance().sendChatText(messageBean, true);
                }
            });
        holder.tv.setText(span, TextView.BufferType.SPANNABLE);

        NoUnderlineSpan mNoUnderlineSpan = new NoUnderlineSpan(context.getResources().getColor(R.color
                .app_text_color), false);
        if (holder.tv.getText() instanceof Spannable) {
            Spannable s = (Spannable) holder.tv.getText();
            s.setSpan(mNoUnderlineSpan, 0, s.length(), Spanned.SPAN_MARK_MARK);
        }
    }

    /**
     * 处理显示图片消息
     *
     * @param holder
     * @param messageBean
     */
    public void handlerImageMsg(final MessageViewHolder holder,
                                final MessageBean messageBean) {
        //图片路径或ID
        String msgContent = messageBean.msgContent;
        String imageSize = messageBean.imageSize;
        int msgStatus = messageBean.msgStatus;
        if (holder.chatImageView == null) return;
        holder.chatImageView.setImageBitmap(null);
        holder.chatImageView.setTag(messageBean.msgContent);
        showMsgState(holder, messageBean);
        setImageSize(imageSize, holder.chatImageView);//设置图片显示大小
        if (msgStatus < BorrowConstants.MSGSTATUS_READ) {//发送
            if (FileUtils.getInstance().isExists(msgContent)) {
                holder.chatImageView.setImageResource(R.drawable.common_loading3);
                ImageUtils.showCommonImage(
                        (Activity) context,
                        holder.chatImageView,
                        FileUtils.IMG_SAVE_PATH,
                        messageBean.msgContent,
                        R.drawable.ic_picture_loadfailed
                );

            } else {//本地图片被删除显示默认图片
                holder.chatImageView.setImageResource(R.drawable.ic_picture_loadfailed);
            }
        } else {//接收
            //接收是msgContent 放置的是图片ID
            holder.chatImageView.setImageResource(R.drawable.common_loading3);
            if (messageBean.msgStatus == BorrowConstants.MSGSTATUS_INPROGRESS) {
                holder.chatImageView.setImageResource(R.drawable.common_loading3);
            } else if (messageBean.msgStatus == BorrowConstants.MSGSTATUS_RECEIVE_FAIL) {
                holder.chatImageView.setImageResource(R.drawable.fail_image);
            } else {
                ImageUtils.showCommonImage((Activity) context, holder.chatImageView,
                        FileUtils.IMG_SAVE_PATH, msgContent, R.drawable.ic_picture_loadfailed);
            }
        }

        holder.chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpPath = messageBean.msgContent;
                if (TextUtils.isEmpty(tmpPath)) {
                    return;
                }
                if (tmpPath.contains(".") && tmpPath.endsWith(".gif")) { // 全路径
                    ShowGifImageActivity_.intent(context)
                            .imgPath(tmpPath.replace("/thumbnail/", "/")).start();
                    return;
                }

                if (!tmpPath.contains(".")) { // 非全路径
                    String uriPath = ImageUtils.getLocalImagePath(FileUtils.IMG_SAVE_PATH, tmpPath);
                    if (TextUtils.isEmpty(uriPath)) {
                        return;
                    }
                    if (uriPath.endsWith(".gif")) {
                        ShowGifImageActivity_.intent(context)
                                .imgPath(tmpPath.replace("/thumbnail/", "/")).start();
                        return;
                    }
                }

                suspendOtherPrivateMsg(messageBean);

                ShowBigImageActivity_.intent(context).chatType(chatType)
                        .toChatId(toChatId)
                        .currentFigureId(currentFigureId)
                        .msgKey(messageBean.msgKey)
                        .imgPath(tmpPath.replace("/thumbnail/", "/")).start();
            }
        });

        if (holder.staus_iv != null)
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMsgController.getInstance().sendChatImage(null, messageBean, true);
                }
            });
    }

    public void handlerVoiceMsg(final MessageViewHolder holder,
                                final MessageBean messageBean, final int position) {

        final int msgStatus = messageBean.msgStatus;
        final String voicePath = messageBean.msgContent;

        String recordlength = messageBean.recordlength;
        setVoiceWidth(holder.rl_voice, recordlength);
        holder.tv.setText(recordlength + "\"");

        showMsgState(holder, messageBean);
        if (msgStatus < BorrowConstants.MSGSTATUS_READ) {//发送
            if (holder.staus_iv != null)
                holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SendMsgController.getInstance().sendChatVoice(messageBean, true);
                    }
                });

            if (!FileUtils.getInstance().isExists(voicePath)) {//本地不存在重新下载
                String fileId = messageBean.file_id;
                FileUtils.downloadFile(context, fileId, FileUtils.VOICE_CACHE_PATH);
            }
        } else {//接收
            if (messageBean.isplayed == VoicePlayStatus.UN_PLAY.value()) {//未读
                holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
                holder.tv.setTextColor(context.getResources().getColor(R.color.app_title_bg));
                holder.rl_voice.setBackgroundResource(R.drawable.right_voice_bg);
            } else {//已读
                holder.iv.setImageResource(R.drawable.voice_lf_p3);
                holder.tv.setTextColor(context.getResources().getColor(R.color.app_text_color3));
                holder.rl_voice.setBackgroundResource(R.drawable.chatfrom_bg_normal);
            }

            if (FileUtils.getInstance().isExists(FileUtils.VOICE_CACHE_PATH
                    + AddressManager.addressManager.env + "_" + voicePath + ".amr")) {
            } else {
                FileUtils.downloadFile(context, voicePath, FileUtils.VOICE_CACHE_PATH);
            }
        }

        if (holder.rl_voice != null)
            holder.rl_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    suspendOtherPrivateMsg(messageBean);
                    if (msgStatus < BorrowConstants.MSGSTATUS_READ) { // 发送方
                        holder.iv.setImageResource(R.drawable.voice_to_icon);
                    } else { // 接收方
                        holder.iv.setImageResource(R.drawable.voice_lf_bg);
                        holder.tv.setTextColor(context.getResources().getColor(R.color.app_text_color3));
                        holder.rl_voice.setBackgroundResource(R.drawable.chatfrom_bg_normal);
                    }
                    AnimationDrawable animator = (AnimationDrawable) holder.iv.getDrawable();
                    animator.start();
                    if (mCurrentPlayingIndex == position && SoundUtil.getInstance().isPlaying()) {
                        SoundUtil.getInstance().stopPlayer();
                    } else {
                        if (SoundUtil.getInstance().isPlaying()) {
                            SoundUtil.getInstance().stopPlayer();
                        }
                        if (FileUtils.getInstance().isExists(voicePath)) {//本地路径
                            SoundUtil.getInstance().playRecorder(v.getContext(), voicePath);
                        } else if (voicePath.contains(".")) {//本地语音不存在
                            String recicePath = FileUtils.VOICE_CACHE_PATH + AddressManager.addressManager.env
                                    + "_" + messageBean.file_id + ".amr";
                            SoundUtil.getInstance().playRecorder(v.getContext(), recicePath);
                        } else {
                            String recicePath = FileUtils.VOICE_CACHE_PATH + AddressManager.addressManager.env
                                    + "_" + voicePath + ".amr";
                            SoundUtil.getInstance().playRecorder(v.getContext(), recicePath);
                        }
                    }
                    MessageDBHandler messageDBHandler = new MessageDBHandler(context);
                    messageDBHandler.updateIsPlayed(messageBean.msgKey);//更新为已读
                    messageBean.isplayed = VoicePlayStatus.PLAYED.value();
                    mCurrentPlayingIndex = position;
                    //播放语音

                    SoundUtil.getInstance().setVoiceCompletionListener(
                            new SoundUtil.VoiceCompletionListener() {
                                @Override
                                public void complete() {
                                    messageBean.isplayed = VoicePlayStatus.PLAY_DONE.value();
                                    resumeOtherPrivateMsg(messageBean); // 恢复其他计数器
                                    if (msgStatus < BorrowConstants.MSGSTATUS_READ) { // 发送方
                                        holder.iv.setImageResource(R.drawable.chatto_voice_playing_f3);
                                    } else { // 接收方
                                        holder.iv.setImageResource(R.drawable.voice_lf_p3);
                                    }
                                    if (messageBean.isPrivate() && messageBean.currentlifetime <= 0) {
                                        // 倒计时已结束的话,销毁此条私密消息
                                        callBack.refreshUI(holder, messageBean);
                                    }
                                }
                            });
                }
            });
        if (holder.iv != null)
            holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.initVoicePopMenu(holder.iv, context, mAlipayVoiceRecorder);
                    return true;
                }
            });
    }


    /**
     * 暂停其他私密消息计数
     *
     * @param messageBean 当前消息messagebean
     */
    private void suspendOtherPrivateMsg(MessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        List<MessageBean> allMessages = ChatManager.getInstance().getAllMessageAboutSomeOne(
                toChatId,
                currentFigureId
        );

        if (allMessages == null) {
            return;
        }
        for (MessageBean bean :
                allMessages) {
            if (bean == null) {
                continue;
            }
            if (!bean.isPrivate()) {
                continue;
            }
            if (messageBean.msgKey.equals(bean.msgKey)) {
                continue;
            }
            bean.isPrivatePause = true;
        }
    }

    /**
     * 恢复其他所有私密消息计时
     *
     * @param messageBean
     */
    private void resumeOtherPrivateMsg(MessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        List<MessageBean> allMessages = ChatManager.getInstance().getAllMessageAboutSomeOne(
                toChatId,
                currentFigureId
        );

        if (allMessages == null) {
            return;
        }
        for (MessageBean bean :
                allMessages) {
            if (bean == null) {
                continue;
            }
            if (!bean.isPrivate()) {
                continue;
            }
            if (messageBean.msgKey.equals(bean.msgKey)) {
                continue;
            }
            bean.isPrivatePause = false;
        }
    }


    /**
     * 处理接收到的系统消息
     *
     * @param holder      视图持有者
     * @param messageBean 消息实体
     */
    public void handlerSysMsg(final MessageViewHolder holder,
                              final MessageBean messageBean) {
        if (messageBean == null || messageBean.msgContent == null) {
            return;
        }
        String content = messageBean.msgContent;
        if (TextUtils.isEmpty(content)) {
            holder.tv_sys_msg.setVisibility(View.GONE);
            return;
        }
        if (holder.tv_sys_msg != null) {
            holder.tv_sys_msg.setText(content);
        }
    }

    public void handlerIDCARD(final MessageViewHolder holder,
                              final MessageBean messageBean) {
        final NameCardBean nameCardBean = (NameCardBean) messageBean.getMsgTypeBean();
        holder.tv.setText(nameCardBean.getName());
        holder.tvSubTitle.setText("乡邻号:" + messageBean.idCard.getFigureId());
        ImageUtils.showCommonImage((Activity) context, holder.cardImage,
                FileUtils.IMG_CACHE_HEADIMAGE_PATH, nameCardBean.getImgId(), R.drawable.head);
        if (holder.ll_chatcontent != null)
            holder.ll_chatcontent.setOnClickListener(new View.OnClickListener() {
                /**
                 * @param v
                 */
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(currentFigureId)) {
                        return;
                    }
                    if (currentFigureId.equals(messageBean.idCard.getFigureId())
                            || (chatType == BorrowConstants.CHATTYPE_SINGLE && ContactManager.getInstance()
                            .getContact(ContactDBHandler.getContactId(messageBean)).figureUsersId.equals(messageBean.idCard.getFigureId()))) {
                        return;
                    }

                    BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                        @Override
                        public void execute() {
                            new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.IDCARD, nameCardBean.getMsg_key());
                        }
                    });

                    ChatMainActivity_//
                            .intent(activity)//
                                    //// TODO: 2016/3/9  收到名片后 是否可以选择身份去聊天
                            .currentFigureId(currentFigureId)// 当前角色
                            .toChatXlId(messageBean.xlID)// 附近的人 xluserid
                            .toChatId(messageBean.idCard.getFigureId())//figureUserId
                            .titleName(messageBean.idCard.getName())
                            .headerImgId(messageBean.idCard.getImgId())
                            .toChatName(messageBean.idCard.getName())
                            .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                            .start();

                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).animLeftToRight();
                    }
                }
            });
        showMsgState(holder, messageBean);
        if (holder.staus_iv != null)
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMsgController.getInstance().sendChatIDCard(messageBean, null, true);
                }
            });
    }

    public void handlerWEBSHOPPING(final MessageViewHolder holder,
                                   final MessageBean messageBean) {
        GoodsDetailBean goodsDetailBean = (GoodsDetailBean) messageBean.getMsgTypeBean();
        if (holder.tv != null) holder.tv.setText(goodsDetailBean.getAbstraction());
        holder.cardImage.setVisibility(View.VISIBLE);
        ImageUtils.showUrlImage(holder.cardImage, goodsDetailBean.getImgURL());
        if (holder.webRealPrice != null) {
            holder.webRealPrice.setVisibility(View.VISIBLE);

            double price = Utils.parseDouble(goodsDetailBean.getPrice());//多少分
            holder.webRealPrice.setText("￥" + Utils.formatDecimal(price / 100, 2));
        }
        if (holder.webTitle != null) {
            holder.webTitle.setText("商品");
            holder.webTitle.setBackgroundResource(R.drawable.bg_top_round_red_txt);
        }
        if (holder.link_icon != null) holder.link_icon.setVisibility(View.INVISIBLE);

        if (holder.ll_chatcontent != null)
            holder.ll_chatcontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                        @Override
                        public void execute() {
                            new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.WEBSHOPPING, messageBean.msgKey);
                        }
                    });
                    Intent intent = new Intent(activity, WebviewActivity_.class);
                    intent.putExtra("url", messageBean.goodsCard.getUrl());
                    activity.startActivity(intent);
                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).animLeftToRight();
                    }
                }
            });

        showMsgState(holder, messageBean);

        if (holder.staus_iv != null)
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMsgController.getInstance().sendChatGoods(messageBean, "", "", "", "", "", "", true);
                }
            });
    }

    public void handlerNewsCard(final MessageViewHolder holder,
                                final MessageBean messageBean) {
        NewsCard newsCard = (NewsCard) messageBean.getMsgTypeBean();
        if (holder.tv != null) holder.tv.setText(newsCard.getSummary());
        if (TextUtils.isEmpty(newsCard.getImgurl())) {
            holder.cardImage.setVisibility(View.GONE);
        } else {
            holder.cardImage.setVisibility(View.VISIBLE);
            ImageUtils.showUrlImage(holder.cardImage, newsCard.getImgurl());
        }
        if (holder.webRealPrice != null) {
            holder.webRealPrice.setVisibility(View.INVISIBLE);
            holder.webRealPrice.setText("");
        }
        if (holder.webTitle != null) {
            holder.webTitle.setText("外部网页");
            holder.webTitle.setBackgroundResource(R.drawable.bg_top_round_grey_txt);
        }
        if (holder.link_icon != null) holder.link_icon.setVisibility(View.VISIBLE);

        if (holder.ll_chatcontent != null)
            holder.ll_chatcontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
                        @Override
                        public void execute() {
                            new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.NEWSCARD, messageBean.msgKey);
                        }
                    });

                    Intent intent = new Intent(activity, WebviewActivity_.class);
                    intent.putExtra("url", messageBean.newsCard.getUrl());
                    activity.startActivity(intent);
                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).animLeftToRight();
                    }
                }
            });

        showMsgState(holder, messageBean);

        if (holder.staus_iv != null)
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMsgController.getInstance().sendChatNews(messageBean, "", "", "", "", "", true);
                }
            });
    }

    private void showMsgState(
            final MessageViewHolder holder,
            final MessageBean messageBean
    ) {
        if (holder.circleProgressView != null) {
            holder.circleProgressView.setTag(messageBean.msgLocalKey);
        }
        if (messageBean.direct == MessageBean.Direct.SEND) { // 发送消息
            //// TODO: 2016/2/24  接收消息布局缺少 staus_iv
            if (holder.pb == null || holder.staus_iv == null) {
                return;
            }
            //发送
            setMessageSendCallback(holder, messageBean);
            setPrivateMessageReceiveCallback(holder, messageBean);
            switch (messageBean.msgStatus) {
                case BorrowConstants.MSGSTATUS_SEND:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.INVISIBLE);
                    // 发送消息
                    //                sendMsgInBackground(message);
                    break;
                case BorrowConstants.MSGSTATUS_OK: // 发送成功
                    holder.pb.setVisibility(View.INVISIBLE);
                    holder.staus_iv.setVisibility(View.INVISIBLE);
                    break;
                case BorrowConstants.MSGSTATUS_FAIL: // 发送失败
                    holder.pb.setVisibility(View.INVISIBLE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case BorrowConstants.MSGSTATUS_INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.INVISIBLE);
                    break;
                default:
                    holder.pb.setVisibility(View.INVISIBLE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
            }
        } else { // 接收消息
            setPrivateMessageReceiveCallback(holder, messageBean);
            //// TODO: 2016/2/24  接收消息布局缺少 staus_iv
            if (holder.pb == null) {
                return;
            }
            //接受回调
            setMessageReceiveCallback(holder, messageBean);
        }

    }

    /**
     * 设置消息接收callback
     */
    protected void setMessageReceiveCallback(final MessageViewHolder holder,
                                             final MessageBean messageBean) {

        MessageCallBack messageReceiveCallback = new MessageCallBack() {

            @Override
            public void onSuccess() {
                LogCatLog.d(TAG, "下载成功:" + messageBean.msgContent);
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        if (messageBean.msgType == MessageChatAdapter.IMAGE) {

                            if (messageBean.msgContent.equals(String.valueOf(holder.chatImageView.getTag()))) {

                                ImageUtils.showCommonImage((Activity) context, holder.chatImageView,
                                        FileUtils.IMG_SAVE_PATH, messageBean.msgContent, R.drawable.ic_picture_loadfailed);

                   /*             Bitmap bmp = ImageUtils.getBitmapById(FileUtils.IMG_SAVE_PATH, messageBean.msgContent);
                                // holder.chatImageView.setImageBitmap(null);
                                holder.chatImageView.setImageBitmap(bmp);
                                holder.chatImageView.setPadding(0, 0, 0, 0);
                                ImageCache.getInstance().put(messageBean.msgContent, bmp);*/
                            }
                        }
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        LogCatLog.d(TAG, "下载中" + progress);
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                LogCatLog.d(TAG, "下载失败:" + code);
                if (messageBean.msgType == MessageChatAdapter.IMAGE) {

                    if (messageBean.msgContent.equals(String.valueOf(holder.chatImageView.getTag()))) {
                        holder.chatImageView.setImageBitmap(null);
                        holder.chatImageView.setImageResource(R.drawable.fail_image);
                    }
                }
            }
        };

        messageBean.setMessageStatusCallback(messageReceiveCallback);
    }

    /**
     * 设置消息发送callback
     */
    protected void setMessageSendCallback(final MessageViewHolder holder,
                                          MessageBean messageBean) {
        MessageCallBack messageSendCallback = new MessageCallBack() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  /*          if(percentageView != null)
                                percentageView.setText(progress + "%");*/
                        holder.pb.setVisibility(View.INVISIBLE);
                        holder.staus_iv.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                  /*          if(percentageView != null)
                                percentageView.setText(progress + "%");*/
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                holder.pb.setVisibility(View.INVISIBLE);
                holder.staus_iv.setVisibility(View.VISIBLE);
            }
        };

        messageBean.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * 设置私密聊天接收回调接口
     *
     * @param holder
     * @param messageBean
     */
    private void setPrivateMessageReceiveCallback(
            final MessageViewHolder holder,
            final MessageBean messageBean
    ) {
        PrivateMessageCallBack p = new PrivateMessageCallBack() {
            @Override
            public void onStart() {
                LogCatLog.d(TAG, "结束计时,onStart:MessageBean=" + messageBean.msgContent
                        + " lifetime=" + messageBean.lifetime + "s");
            }

            @Override
            public void onPause(MessageBean messageBean) {
                LogCatLog.d(TAG, "结束计时,onPause:MessageBean=" + messageBean.msgContent
                        + " lifetime=" + messageBean.lifetime + "s");

            }

            @Override
            public void onEnd(MessageBean messageBean) {
                if (messageBean != null) {
                    LogCatLog.d(
                            TAG,
                            "结束计时,onEnd:MessageBean=" + messageBean.msgContent
                                    + " lifetime=" + messageBean.lifetime + "s"
                    );
                }

                handleTimeEndByMsgType(holder, messageBean);
//                if(currentFigureId.equals(messageBean.figureId)
//                        &&toChatId.equals(messageBean.figureUsersId)){
//                    context.sendBroadcast(new Intent(BorrowConstants.SECRET_END_ACTION));
//                }
            }

            /**
             * 执行中回调方法
             * @param lefttime 总生存时间
             * @param percentTime 百分比时间
             * @param pictime 图片秒数时间
             * @param messageBean
             */
            @Override
            public void onProgress(
                    final int lefttime,
                    final int percentTime,
                    final int pictime,
                   final MessageBean messageBean) {
                LogCatLog.d(
                        TAG,
                        "结束计时,onProgress:MessageBean=" + messageBean.msgContent
                                + " lifetime=" + messageBean.lifetime + "s"
                );
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Object tag = holder.circleProgressView.getTag() == null ? "" : holder.circleProgressView.getTag();
                        if (messageBean.msgLocalKey.equals(String.valueOf(tag))) {
                            holder.circleProgressView.setProgress(percentTime);
//                            holder.tv.setText("time:" + percentTime);
                        }
                    }
                });
            }
        };
        messageBean.setPrivateMessageCallBack(p);
    }

    /**
     * 根据消息类型处理倒计时结束时事件
     *
     * @param messageBean
     */
    private void handleTimeEndByMsgType(final MessageViewHolder holder, final MessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        if (messageBean.chatType != MessageBean.ChatType.Chat) { // 点对点聊天
            return;
        }
        switch (messageBean.msgType) {
            case MessageChatAdapter.VOICE:// 播放音频
                if (messageBean.isplayed == VoicePlayStatus.UN_PLAY.value() // 未播放
                        || messageBean.isplayed == VoicePlayStatus.PLAY_DONE.value()) { // 播放完毕
                    callBack.refreshUI(holder, messageBean);
                }
                break;
            case MessageChatAdapter.TEXT:
            case MessageChatAdapter.IMAGE:
            case MessageChatAdapter.IDCARD:
            case MessageChatAdapter.WEBSHOPPING:
            case MessageChatAdapter.NEWSCARD:
                callBack.refreshUI(holder, messageBean);
                break;
            case MessageChatAdapter.SYS:
                break;
        }
    }

    private void setImageSize(String imageSize, View chatImageView) {
        int reqWidth = DeviceInfoUtil.dip2px(100), reqHeight = DeviceInfoUtil.dip2px(100);
        int padding_width, padding_height;
        if (imageSize != null && imageSize.contains("_")) {
            try {
                String[] size = imageSize.split("_");
                int width = Integer.parseInt(size[0]);
                int height = Integer.parseInt(size[1]);
                if (width > height) {
                    if (width >= DeviceInfo.getInstance().getScreenWidth()) {
                        reqWidth = DeviceInfoUtil.dip2px(180);
                    } else {
                        reqWidth = DeviceInfoUtil.dip2px(120);
                    }
                    reqHeight = reqWidth * height / width;

                } else {
                    if (height >= DeviceInfo.getInstance().getScreenHeight()) {
                        reqHeight = DeviceInfoUtil.dip2px(180);
                    } else {
                        reqHeight = DeviceInfoUtil.dip2px(120);
                    }
                    reqWidth = reqHeight * width / height;
                }

            } catch (Exception e) {

            }
        }
        ViewGroup.LayoutParams params = chatImageView.getLayoutParams();
        //if(params.width!=reqWidth){
        params.width = reqWidth;
        params.height = reqHeight;
        chatImageView.setLayoutParams(params);
        // }
        padding_width = (reqWidth - DeviceInfoUtil.dip2px(15)) / 2;
        padding_height = (reqHeight - DeviceInfoUtil.dip2px(15)) / 2;
        chatImageView.setPadding(padding_width, padding_height, padding_width, padding_height);
    }

    private void setVoiceWidth(RelativeLayout rl_voice, String voiceLength) {
        try {
            int recordlength = Integer.parseInt(voiceLength);
            ViewGroup.LayoutParams params = rl_voice.getLayoutParams();
            params.width = Utils.dipToPixel(context, 60 + 200 * recordlength / 60);
            LogCatLog.e(TAG, "layout width=" + params.width);
            rl_voice.setLayoutParams(params);
        } catch (Exception e) {
            LogCatLog.e(TAG, "setVoiceWidth -> e = " + e);
        }
    }


}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.adpter.viewholder.MessageViewHolder;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.chat.controller.HandleMsgController;
import com.xianglin.fellowvillager.app.chat.controller.PrivateMessageCallBack;
import com.xianglin.fellowvillager.app.chat.model.MessageType;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.DateUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.audio.AlipayVoiceRecorder;
import com.xianglin.fellowvillager.app.utils.messagetask.MessageEventTaskManager;
import com.xianglin.fellowvillager.app.utils.messagetask.PrivateMessageTask;
import com.xianglin.fellowvillager.app.widget.CircleProgressView;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.LinkedList;
import java.util.List;

/**
 * @author chengshengli
 * @version v 1.0.0 2015/11/6 16:22 XLXZ Exp $
 */
public class MessageChatAdapter extends BaseAdapter implements PrivateMessageCallBack {
    private final static String TAG = "MessageChatAdapter";

    //消息类型
    public static final int SYS = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int VOICE = 3;
    public static final int VIDEO = 4;
    public static final int IDCARD = 5;
    public static final int REDBUNDLE = 6;
    public static final int WEBSHOPPING = 7;
    public static final int NEWSCARD = 8;

    public static final int FILE = 111;
    public static final int LOCATION = 1112;

    //消息状态
    public static final int SEND_MESSAGE = BorrowConstants.MSGSTATUS_FAIL;//0成功 1失败
    public static final int RECEIVE_MESSAGE = 2;//2,3

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 10;
    private static final int HANDLER_MESSAGE_REFRESH_LIST_AND_REMOVE = 14;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 11;
    private static final int HANDLER_MESSAGE_SEEK_TO = 12;

    private String toChatId;
    private LayoutInflater inflater;
    private Activity activity;
    ContactDBHandler contactDBHandler;

    private Context context;
    private int chatType;//单聊 群聊
    private String headerImgId;//对方头像ID
    private String currentFigureId;//本人角色id
    private ListView mListView;

    AlipayVoiceRecorder mAlipayVoiceRecorder;

    private XLConversation conversation;
    /**
     * 消息类型  文本 语音 图片 文件等等
     **/
    private List<MessageBean> listData = new LinkedList<>();

    /**
     * @param listView
     * @param context
     * @param toChatId        接收方 联系人id ContactDBHandler.getContactId()
     * @param chatType        单聊 群聊
     * @param headerImgId
     * @param currentFigureId
     */
    public MessageChatAdapter(
            ListView listView,
            Context context,
            String toChatId,
            int chatType,
            String headerImgId,
            String currentFigureId
    ) {
        mListView = listView;
        contactDBHandler = new ContactDBHandler(context);

        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        this.chatType = chatType;
        this.headerImgId = headerImgId;
        this.currentFigureId = currentFigureId;

        this.toChatId = toChatId;
        String contactId;
        if (BorrowConstants.CHATTYPE_SINGLE == chatType) {
            contactId = ContactDBHandler.getContactId(toChatId, currentFigureId);
        } else {
            contactId = GroupDBHandler.getGroupId(toChatId, currentFigureId);
        }
        this.conversation = ChatManager.getInstance().getConversation(contactId,
                chatType == BorrowConstants.CHATTYPE_SINGLE ? false : true);
        HandleMsgController.getInstance().init(
                activity,
                currentFigureId,
                mAlipayVoiceRecorder,
                chatType,
                toChatId,
                new HandleMsgController.HandleMsgCallBack() {
                    @Override
                    public void refreshUI(final MessageViewHolder holder, final MessageBean messageBean) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    removeListItem(holder.itemMessage, messageBean);
                                } catch (Exception e) {
                                    LogCatLog.e(TAG, "refreshUI -> e = " + e);
                                } finally {
                                    refreshAndRemove(messageBean);
                                }
                            }
                        });
                    }
                }
        );
    }

    public void setAlipayVoiceRecorder(AlipayVoiceRecorder mAlipayVoiceRecorder) {
        this.mAlipayVoiceRecorder = mAlipayVoiceRecorder;
    }

    public void setChatData(List<MessageBean> listData) {
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("handlerleak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    Log.e("tag", "HANDLER_MESSAGE_REFRESH_LIST");
                    refreshList(false);
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (listData.size() > 0) {
                        mListView.setSelection(listData.size() - 1);
                        // mListView.setSelection(mListView.getBottom());
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:

                    int leave_position=-1;
                    String msgKey= DataDealUtil.getChatLeavePosition(currentFigureId, toChatId);
                    for(int i=0;i<getCount();i++){
                        if(((MessageBean)getItem(i)).msgKey.equals(msgKey)){
                            leave_position=i;
                            break;
                        }
                    }
                    if(leave_position==-1){//处理上面是私密消息的情况
                        String msgBeforeKey= DataDealUtil.getChatLeaveNoSecretPosition(currentFigureId, toChatId);
                        for(int i=0;i<getCount();i++){
                            if(((MessageBean)getItem(i)).msgKey.equals(msgBeforeKey)){
                                leave_position=i;
                                break;
                            }
                        }
                    }
                    LogCatLog.e(TAG, "leave position=" + leave_position);
                    int value=message.arg1;
                    if(value>0){
                        mListView.setSelection(value);
                    }else{//处理第一次进入定位到上次离开的情况
                        if(leave_position==-1||leave_position>=mListView.getCount()){
                            mListView.setSelection(mListView.getCount()-1);
                        }else{
                            mListView.setSelection(leave_position);
                        }
                    }
                    break;
                case HANDLER_MESSAGE_REFRESH_LIST_AND_REMOVE:
                    refreshList(true);
                    break;
                default:
                    break;
            }
        }
    };

    private void refreshList(boolean isRemove) {
        // UI线程不能直接使用conversation.getAllMessages()
        // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
        listData = conversation.getAllMessages();

        for (int i = 0; i < listData.size(); i++) {
            MessageBean messageBean = listData.get(i);
            if (messageBean.isExpired) {
                conversation.getAllMessages().remove(i);
                --i;
                continue;
            }

            //添加私密消息监听
            if (messageBean.isPrivate())
                messageBean.setPrivateMessageCallBack(this);

            if (messageBean.direct == MessageBean.Direct.RECEIVE
                    && messageBean.msgStatus == BorrowConstants.MSGSTATUS_UNREAD) {
                messageBean.msgStatus = BorrowConstants.MSGSTATUS_READ;
            }
            MessageEventTaskManager.getInstance()
                    .addMessageEventTaskTask(new PrivateMessageTask(messageBean));
        }

        conversation.setUnreadMsgCount(0);
        if (!isRemove)
            notifyDataSetChanged();
    }

    protected void removeListItem(View rowView, final MessageBean messageBean) {
        final Animation animation = (Animation) AnimationUtils.loadAnimation(context,
                messageBean.direct == MessageBean.Direct.SEND ? R.anim.anim_right_translate
                        : R.anim.anim_left_translate);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                conversation.getAllMessages().remove(messageBean);
                notifyDataSetChanged();
                animation.cancel();
            }
        });
        rowView.startAnimation(animation);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面
     */
    public void refreshAndRemove(MessageBean messageBean) {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST_AND_REMOVE)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST_AND_REMOVE);
        msg.obj = messageBean;
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;

        handler.sendMessage(msg);
    }

    /**
     * 获取item类型  多种类型item必须，不然会数据错乱
     */
    @Override
    public int getItemViewType(int position) {
        MessageBean message = listData.get(position);
        int msgType = message.msgType;
        if (msgType == TEXT) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_TXT.value()
                    : MessageType.MESSAGE_TYPE_SENT_TXT.value();
        }
        if (msgType == IMAGE) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_IMAGE.value()
                    : MessageType.MESSAGE_TYPE_SENT_IMAGE.value();
        }
        if (msgType == VOICE) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_VOICE.value()
                    : MessageType.MESSAGE_TYPE_SENT_VOICE.value();
        }
        if (msgType == IDCARD) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_IDCARD.value()
                    : MessageType.MESSAGE_TYPE_SENT_IDCARD.value();
        }
        if (msgType == WEBSHOPPING || msgType == NEWSCARD) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_WEBSHOPPING.value()
                    : MessageType.MESSAGE_TYPE_SENT_WEBSHOPPING.value();
        }
        if (msgType == SYS) {
            return message.msgStatus > SEND_MESSAGE ? MessageType.MESSAGE_TYPE_RECV_SYS.value()
                    : MessageType.MESSAGE_TYPE_SENT_SYS.value();
        }

        return -1;// invalid
    }

    /**
     * 多种类型item必须，不然会数据错乱
     **/
    public int getViewTypeCount() {
        return 14;
    }

    private View createViewByMessage(int MsgType, int direct, int position) {
        switch (MsgType) {
            case IMAGE:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_picture, null) : inflater
                        .inflate(R.layout.row_sent_picture, null);
            case VOICE:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_voice, null) : inflater
                        .inflate(R.layout.row_sent_voice, null);
            case IDCARD:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_id_card, null) : inflater
                        .inflate(R.layout.row_sent_id_card, null);
            case WEBSHOPPING:
            case NEWSCARD:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_web_shopping, null) : inflater
                        .inflate(R.layout.row_sent_web_shopping, null);
            case SYS:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_sysmsg, null) : inflater
                        .inflate(R.layout.row_send_sysmsg, null);
            default:
                return direct > SEND_MESSAGE ? inflater
                        .inflate(R.layout.row_received_message, null) : inflater
                        .inflate(R.layout.row_sent_message, null);
        }
    }

    //int MsgType;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MessageViewHolder holder;
        MessageBean bean = listData.get(position);
        if (bean != null) {
            if (convertView == null) {
                holder = new MessageViewHolder();
                convertView = createViewByMessage(bean.msgType, bean.msgStatus, position);
                initHolder(bean, holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (MessageViewHolder) convertView.getTag();
            }

            try {
                handlerItemByType(holder, bean, position);
            } catch (NullPointerException e) {
                LogCatLog.e(TAG, "NullPointerException=" + e.getMessage());
            } catch (Exception e2) {
                LogCatLog.e(TAG, "Exception=" + e2.getMessage());
            }
            if (bean.msgType == SYS) {
                if (TextUtils.isEmpty(bean.msgContent)) {
                    convertView.setVisibility(View.GONE);
                }
                return convertView;
            }

            TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

            if (timestamp != null) {
                timestamp.setText(DateUtil.getDateFormat(bean.msgDate));
                timestamp.setVisibility(View.VISIBLE);
            }
            //显示或下载头像
            if (holder.head_iv != null) {
                if (chatType == BorrowConstants.CHATTYPE_GROUP) {//群聊
                    holder.head_iv.setTag(bean.file_id);
                    String imageId = bean.file_id;
                    if (MessageBean.Direct.SEND == bean.direct) {
                        showPersonImage(holder.head_iv, currentFigureId);
                    }
                    GroupMember groupMember = GroupManager.getInstance().getMember(bean.xlgroupmemberid);
                    if (groupMember != null) {
                        holder.tv_userId.setText(groupMember.getUIName());
                        ImageUtils.showCommonImage((Activity) context, holder.head_iv,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH, groupMember.file_id, R.drawable.head);
                    } else {
                        holder.tv_userId.setText("");
                        LogCatLog.e(TAG, "groupmemberid :" + bean.xlgroupmemberid + " is null");
                    }

                } else {//点对点
                    holder.head_iv.setTag(headerImgId);
                    if (MessageBean.Direct.SEND == bean.direct) {
                        showPersonImage(holder.head_iv, currentFigureId);
                    } else {
                        ImageUtils.showCommonImage((Activity) context, holder.head_iv,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH, headerImgId, R.drawable.head);
                        LogCatLog.d(TAG, "单聊头像id headerImgId:" + headerImgId);
                    }
                    holder.tv_userId.setText(bean.xlName);
                }
                holder.tv_userId.setSingleLine(true);
                holder.tv_userId.setEllipsize(TextUtils.TruncateAt.END);
                holder.tv_userId.setMaxEms(6);
            }
            //头像进度条-私密消息可见
            holder.circleProgressView.setVisibility(bean.isPrivate() ? View.VISIBLE : View.GONE);
        }
        return convertView;
    }

    private void showPersonImage(ImageView image, String currentFigureId) {
        if (TextUtils.isEmpty(currentFigureId)) return;

        FigureMode figureMode = ContactManager.getInstance().getCurrentFigure(currentFigureId);
        if (figureMode != null) {
            ImageUtils.showCommonImage((Activity) context, image, FileUtils
                    .IMG_CACHE_HEADIMAGE_PATH, figureMode.getFigureImageid(), R.drawable.head);
        }
    }

    private void handlerItemByType(final MessageViewHolder holder,
                                   final MessageBean messageBean, final int position) {
        LogCatLog.e(TAG, "MsgType=" + messageBean.msgType + ",position=" + position + ",content="
                + messageBean.msgContent + ",msgStatus=" + messageBean.msgStatus);
        // 设置内容
        final int msgStatus = messageBean.msgStatus;

        switch (messageBean.msgType) {
            case TEXT:
                HandleMsgController.getInstance().handlerTextMsg(holder, messageBean);
                break;
            case IMAGE:
                HandleMsgController.getInstance().handlerImageMsg(holder, messageBean);
                break;
            case VOICE:// 播放音频
                HandleMsgController.getInstance().handlerVoiceMsg(holder, messageBean, position);
                break;
            case IDCARD:
                HandleMsgController.getInstance().handlerIDCARD(holder, messageBean);
                break;
            case WEBSHOPPING:
                HandleMsgController.getInstance().handlerWEBSHOPPING(holder, messageBean);
                break;
            case NEWSCARD:
                HandleMsgController.getInstance().handlerNewsCard(holder, messageBean);
                break;
            case SYS:
                HandleMsgController.getInstance().handlerSysMsg(holder, messageBean);
                break;
        }
    }

    private void initHolder(MessageBean bean, MessageViewHolder holder, View convertView) {
        try {
            int MsgType = bean.msgType;

            holder.itemMessage = (LinearLayout) convertView.findViewById(R.id.item_message);
            if (MsgType == SYS) {
                holder.tv_sys_msg = (TextView) convertView.findViewById(R.id.tv_sys_msg);
            } else {
                holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                holder.circleProgressView = (CircleProgressView) convertView
                        .findViewById(R.id.iv_countdown);
                holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                if (MsgType == TEXT) {
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    // 这里是文字内容
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                } else if (MsgType == IMAGE) {
                    holder.chatImageView = ((SimpleDraweeView) convertView.findViewById(R.id.iv_sendPicture));
                    holder.iv_loading = (ImageView) convertView.findViewById(R.id.iv_loading);
                    holder.rl_picture = (RelativeLayout) convertView.findViewById(R.id.rl_picture);
                    holder.chatImageView.setTag(bean.msgContent);
                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
                } else if (MsgType == VOICE) {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                    holder.rl_voice = (RelativeLayout) convertView.findViewById(R.id.rl_voice);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                } else if (MsgType == IDCARD) {
                    holder.ll_chatcontent = (LinearLayout) convertView.findViewById(R.id.tv_chatcontent);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.tv = (TextView) convertView.findViewById(R.id.idName);//个人名称
                    holder.tvSubTitle = (TextView) convertView.findViewById(R.id.idXLID);//个人乡邻信息
                    holder.cardImage = (ImageView) convertView.findViewById(R.id.idImg);
                } else if (MsgType == WEBSHOPPING || MsgType == NEWSCARD) {
                    holder.ll_chatcontent = (LinearLayout) convertView.findViewById(R.id.tv_chatcontent);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.tv = (TextView) convertView.findViewById(R.id.shopping_title);//标题
                    holder.tvSubTitle = (TextView) convertView.findViewById(R.id.shopping_sub_title);//副标题
                    holder.cardImage = (ImageView) convertView.findViewById(R.id.shopping_image);
                    holder.webTitle = (TextView) convertView.findViewById(R.id.webTitle);
                    holder.link_icon = (ImageView) convertView.findViewById(R.id.link_icon);
                    holder.webRealPrice = (TextView) convertView.findViewById(R.id.shopping_real_price);
                    holder.webOldPrice = (TextView) convertView.findViewById(R.id.shopping_old_price);
                    holder.webOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中间横线
                }
            }
        } catch (Exception e) {
            LogCatLog.e(TAG, "initHolder fail! ->e = " + e);
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onPause(MessageBean messageBean) {
    }

    @Override
    public void onEnd(MessageBean messageBean) {
        refreshAndRemove(messageBean);
        if (currentFigureId.equals(messageBean.figureId)
                && toChatId.equals(messageBean.figureUsersId)) {
            context.sendBroadcast(new Intent(BorrowConstants.SECRET_END_ACTION));
        }
    }

    @Override
    public void onProgress(int lefttime, int progress, int picTime, MessageBean messageBean) {

    }
}
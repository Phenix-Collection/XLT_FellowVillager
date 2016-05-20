/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.NewCardActivity;
import com.xianglin.fellowvillager.app.activity.PersonDetailActivity_;
import com.xianglin.fellowvillager.app.activity.group.GroupManagerActivity_;
import com.xianglin.fellowvillager.app.adapter.GroupListInContactAdapter;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.chat.utils.PhotoUtil;
import com.xianglin.fellowvillager.app.chat.controller.SendMsgController;
import com.xianglin.fellowvillager.app.chat.utils.SmileUtils;
import com.xianglin.fellowvillager.app.chat.widget.PasteEditText;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.fragment.ChatCardFragment_;
import com.xianglin.fellowvillager.app.fragment.ChatFaceFragment;
import com.xianglin.fellowvillager.app.fragment.ChatFaceFragment_;
import com.xianglin.fellowvillager.app.fragment.ChatPictureFragment_;
import com.xianglin.fellowvillager.app.fragment.ChatVoiceFragment_;
import com.xianglin.fellowvillager.app.loader.MessageDialogueLoader;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.longlink.XLNotifierEvent;
import com.xianglin.fellowvillager.app.longlink.listener.XLEventListener;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.Extras;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.NoticeGetGroupOrContactInfoUtil;
import com.xianglin.fellowvillager.app.utils.SoundUtil;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.audio.AlipayVoiceRecorder;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.XListView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 聊天主界面
 *
 * @author songdiyuan
 * @version $Id: ChatMainActivity.java, v 1.0.0 2015-11-20 下午2:36:04 xl Exp $
 */
@EActivity(R.layout.activity_chat)
public class ChatMainActivity extends BaseActivity
        implements XListView.IXListViewListener, XLEventListener {

    public static final int PIC_FRAGMENT_INDEX = 0x000;
    public static final int VOICE_FRAGMENT_INDEX = 0x001;
    public static final int FACE_FRAGMENT_INDEX = 0x002;
    public static final int CARD_FRAGMENT_INDEX = 0x003;

    private static final String SMILE_UTILS_CLASS
            = "com.xianglin.fellowvillager.app.chat.utils.SmileUtils";

    @ViewById(R.id.rl_bottom)
    RelativeLayout rlBottom;

    @ViewById(R.id.mask_top)
    RelativeLayout mask_top;


    @ViewById(R.id.et_sendmessage)
    PasteEditText mPasteEditText;

    @ViewById(R.id.btn_send)
    Button btnSend;

    @ViewById(R.id.ll_bottom_menu)
    LinearLayout ll_bottom_menu;

    @ViewById(R.id.list)
    XListView chatList;

    @ViewById(R.id.top_bar)
    TopView topView;

    @ViewById(R.id.ll_menu_container)
    LinearLayout ll_menu_container;

    @ViewById(R.id.iv_pic_icon)
    ImageView mPicIconIv;

    @ViewById(R.id.iv_voice_icon)
    ImageView mVoiceIconIv;

    @ViewById(R.id.iv_face_icon)
    ImageView mFaceIconIv;

    @ViewById(R.id.iv_card_icon)
    ImageView mCardIconIv;

    @ViewById(R.id.net_none)
    View net_none;

    @ViewById(R.id.iv_chat_pvi)
    Button iv_chat_pvi;//私密图标设置
    @ViewById(R.id.chat_line)
    View chat_line;//聊天输入框上面的分割线

    @ViewById(R.id.tv_unRead)
    TextView tv_unRead;//未读消息数
    XLConversation conversation;

    @Click(R.id.iv_chat_pvi)
    void clickSecret(){
        String contactId=ContactDBHandler.getContactId(toChatId,currentFigureId);
        Contact contact = ContactManager.getInstance().getContact(contactId);
        if(contact==null||contact.contactLevel== Contact.ContactLevel.UMKNOWN){
            return;
        }
        //判断当前是否是私密模式
        boolean isSecret=DataDealUtil.isSecretMode(currentFigureId, toChatId);
         int secret_index= DataDealUtil.getSecretIndex(currentFigureId, toChatId);
        int time_count=DataDealUtil.TIME_COUNT[secret_index];
        if(isSecret){
            setSecretClose();
            mCardIconIv.setClickable(true);
            DataDealUtil.setIsSecretMode(currentFigureId, toChatId, false);
            contactDBHandler.setPrivateMsgDate(toChatId, contactId, currentFigureId, false, time_count);
        }else{
            setSecretOpen();
            mCardIconIv.setClickable(false);
            DataDealUtil.setIsSecretMode(currentFigureId, toChatId, true);
            contactDBHandler.setPrivateMsgDate(toChatId, contactId, currentFigureId, true, time_count);
        }
    }

    void setSecretOpen(){
        chat_line.setBackgroundResource(R.color.line_chat_bg);
        ViewGroup.LayoutParams params=chat_line.getLayoutParams();
        params.height=3;
        chat_line.setLayoutParams(params);
        int secret_index= DataDealUtil.getSecretIndex(currentFigureId, toChatId);
        if(secret_index>=0){
            iv_chat_pvi.setBackgroundResource(time_res[secret_index]);
        }
        else{
            iv_chat_pvi.setBackgroundResource(time_res[2]);
        }

    }
    void setSecretClose(){

        iv_chat_pvi.setBackgroundResource(R.drawable.chat_pri_icon);
        chat_line.setBackgroundResource(R.color.c_divider);
        ViewGroup.LayoutParams params=chat_line.getLayoutParams();
        params.height=1;
        chat_line.setLayoutParams(params);
    }

    int time_res[]={R.drawable.chat_10s,R.drawable.chat_20s,
            R.drawable.chat_30s,R.drawable.chat_1m};

    @Extra
    String titleName;
    @Extra
    String headerImgId;//发消息人的头像ID
    @Extra
    String toChatName;//发消息的人名称

    @Extra
    String toChatId;//角色id或群id
    @Extra
    String toChatXlId;//聊天人的userId
    @Extra
    String currentFigureId;


    /**
     * 0单聊1群聊
     **/
    @Extra
    int chatType;


    private MessageDialogueLoader mMomentDialogueLoader;
    private ExecutorService msgCountThreadPool;

    MessageChatAdapter adapter;
    private List<MessageBean> listData = new ArrayList<>();
    //private String toChatID = "2";
    // private int chatType = 0;//0单聊 1群聊
    AlipayVoiceRecorder mAlipayVoiceRecorder;

    private MessageDBHandler messageDBHandler;
    ContactDBHandler contactDBHandler;

    GroupDBHandler mGroupDBHandler;

    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
    public static final int REQUEST_CODE_ID_CARD = 26;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final String COPY_IMAGE = "EASEMOBIMG";

    public int new_msg_count=0;//记录浏览历史记录时新来的消息条数

    private Handler mHandler;

    private void showMenu() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isToBottom=true;
                isInBottom=true;
                ll_menu_container.setVisibility(View.VISIBLE);
                chatList.requestLayout();
            }
        }, 300);
        if (chatList.getCount() > 0) chatList.setSelection(chatList.getCount() - 1);
    }

    @Click(R.id.iv_pic_icon)
    void menu_picture_click() {
        setIconHilite(PIC_FRAGMENT_INDEX);
        mPasteEditText.clearFocus();
        Utils.hideSoftKeyboard(mPasteEditText);
        ChatPictureFragment_ pictureFragment_ = new ChatPictureFragment_();
        changeFragment(pictureFragment_, true);
        showMenu();

    }

    @Click(R.id.iv_voice_icon)
    void menu_voice_click() {
        setIconHilite(VOICE_FRAGMENT_INDEX);
        mPasteEditText.clearFocus();
        Utils.hideSoftKeyboard(mPasteEditText);
        ChatVoiceFragment_ voiceFragment_ = new ChatVoiceFragment_();
        changeFragment(voiceFragment_, true);
        showMenu();
    }

    @Click(R.id.iv_face_icon)
    void menu_face_click() {
        setIconHilite(FACE_FRAGMENT_INDEX);
        mPasteEditText.clearFocus();
        Utils.hideSoftKeyboard(mPasteEditText);
        ChatFaceFragment_ faceFragment_ = new ChatFaceFragment_();
        changeFragment(faceFragment_, true);
        showMenu();
        faceFragment_.setEmoSelectListener(new ChatFaceFragment.EmoSelectListener() {
            @Override
            public void addEmoToSend(String fileName) {
                if (fileName != "delete_expression") { // 不是删除键，显示表情
                    String content = mPasteEditText.getText().toString().trim();
                    // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                    Class clz;
                    try {
                        clz = Class.forName(SMILE_UTILS_CLASS);
                        Field field = clz.getField(fileName);
                        String expressionStr = field.get(null).toString();
                        /*判断添加表情后是否超出250字符,防止表情显示不完整*/
                        if (TextUtils.isEmpty(expressionStr)) {
                            return;
                        }
                        if (content.length() + expressionStr.length() > 250) {
                            tip("请输入250字以内");
                            return;
                        }

                        mPasteEditText.append(
                                SmileUtils.getSmiledText(
                                        context,
                                        expressionStr,
                                        23
                                )
                        );
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                 /* 删除文字或者表情*/
                if (TextUtils.isEmpty(mPasteEditText.getText())) {
                    return;
                }

                int selectionStart = mPasteEditText
                        .getSelectionStart();// 获取光标的位置
                if (selectionStart <= 0) {
                    return;
                }
                String body = mPasteEditText.getText()
                        .toString();
                String tempStr = body.substring(0,
                        selectionStart);
                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                if (i != -1) {
                    CharSequence cs = tempStr.substring(i,
                            selectionStart);
                    if (SmileUtils.containsKey(cs
                            .toString()))
                        mPasteEditText.getEditableText()
                                .delete(i, selectionStart);
                    else
                        mPasteEditText.getEditableText()
                                .delete(selectionStart - 1,
                                        selectionStart);
                } else {
                    mPasteEditText.getEditableText()
                            .delete(selectionStart - 1,
                                    selectionStart);
                }
            }
        });
    }

    @Click(R.id.iv_card_icon)
    void menu_card_click() {
        setIconHilite(CARD_FRAGMENT_INDEX);
        mPasteEditText.clearFocus();
        Utils.hideSoftKeyboard(mPasteEditText);
        ChatCardFragment_ cardFragment_ = new ChatCardFragment_();
        cardFragment_.setResume(true);
        Bundle bundle = new Bundle();
        bundle.putString(NewCardActivity.KEYNAME, titleName);
        bundle.putString("toChatName", toChatName);
        cardFragment_.setArguments(bundle);
        changeFragment(cardFragment_, true);
        showMenu();
    }

    private void changeFragment(Fragment f, boolean init) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ll_menu_container, f);
        if (!init)
            ft.addToBackStack(null);
        ft.commit();
    }

    public void hideInput4Voice() {
        mask_top.setVisibility(View.VISIBLE);
        rlBottom.setVisibility(View.GONE);
        ll_bottom_menu.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = ll_menu_container.getLayoutParams();
        params.height = DeviceInfoUtil.dip2px(312);
        ll_menu_container.setLayoutParams(params);
    }

    public void showInput4Voice() {
        mask_top.setVisibility(View.GONE);
        rlBottom.setVisibility(View.VISIBLE);
        ll_bottom_menu.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = ll_menu_container.getLayoutParams();
        params.height = DeviceInfoUtil.dip2px(235);
        ll_menu_container.setLayoutParams(params);
        //mPasteEditText.requestFocus();
    }

    public void addToMessageDB(boolean isNeedAdd) {
        //创建消息
        MessageBean message = MessageBean.createTxtSendMessage(
                mPasteEditText.getText().toString().trim());

        //标记状态
        message.msgStatus = BorrowConstants.MSGSTATUS_SEND;
        //网络发送
        SendMsgController.getInstance().sendMessage(message, toChatXlId, toChatId, currentFigureId);
        //清理输入框
        mPasteEditText.setText("");
        LogCatLog.e(TAG, "send button: 发送消息 " + message.toString());
    }


    static class MyHandler extends Handler {
        WeakReference<ChatMainActivity> mActivityReference;

        MyHandler(ChatMainActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ChatMainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case 1:
                    activity.chatList.stopRefresh();
                    activity.mMomentDialogueLoader.getNextPage();
                    break;
                case 2:
                    activity.adapter.notifyDataSetChanged();
                    break;
                case 3:
                    String name = (String) msg.obj;
                    if (!TextUtils.isEmpty(name)) {
                        activity.topView.setAppTitle(name);
                    } else {
                        activity.topView.setAppTitle(
                                activity.titleName == null
                                        ?
                                        "聊天"
                                        :
                                        activity.titleName
                        );
                    }
                    break;
            }
        }
    }

    private static final Long SYNCTIME = 800L;
    private static final String LASTTIMESYNC = "DATE_CHAT";
    public static final int GOTOPERSONDETAILACTIVITY = 100;

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                        (Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

                if (ni != null && ni.isConnected()) {

                    if (System.currentTimeMillis() - Utils.getLongValue(LASTTIMESYNC, 0) >= SYNCTIME) {
                        Utils.putLongValue(LASTTIMESYNC, System.currentTimeMillis());
                        if (net_none != null) net_none.setVisibility(View.GONE);
                    }
                } else {
                    if (net_none != null) net_none.setVisibility(View.VISIBLE);
                }
            }else if(intent.getAction().equals(BorrowConstants.SECRET_END_ACTION)){//私密消息结束
                LogCatLog.e(TAG,"secret end action response");
                new_msg_count--;
                showUnReadMsg();
            }
        }
    };

    //注解完成执行
    @AfterViews
    void assignViews() {
        //处理单聊未传头像问题
        if(TextUtils.isEmpty(headerImgId)&&chatType==BorrowConstants.CHATTYPE_SINGLE){
            Contact contact=ContactManager.getInstance().getContact(
                    ContactDBHandler.getContactId(toChatId,currentFigureId));
            if(contact!=null)
                 headerImgId=contact.file_id;
        }
        SendMsgController.getInstance().init(
                this,
                chatType,
                toChatId,
                toChatXlId,
                currentFigureId,
                new SendMsgController.SendMsgCallBack() {
                    @Override
                    public void refreshUI() {
                        if (adapter == null) {
                            return;
                        }
                        adapter.refreshSelectLast();

                    }
                }
        );
        PhotoUtil.initBitmap(this);
        if (DeviceInfoUtil.isNetAvailble(context)) {
            net_none.setVisibility(View.GONE);
        } else {
            net_none.setVisibility(View.VISIBLE);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(BorrowConstants.SECRET_END_ACTION);
        registerReceiver(netReceiver, intentFilter);

        messageDBHandler = new MessageDBHandler(this);//// TODO: 2015/11/24  仅作模拟
        contactDBHandler = new ContactDBHandler(this);
        mGroupDBHandler = new GroupDBHandler(ChatMainActivity.this);
        this.msgCountThreadPool = Executors.newSingleThreadExecutor();

        autoReadMsg();
        String conversitionId=ContactDBHandler.getContactId(toChatId,currentFigureId);
        if(BorrowConstants.CHATTYPE_GROUP==chatType){
            conversitionId = GroupDBHandler.getGroupId(toChatId,currentFigureId) ;
        }
        conversation = ChatManager.getInstance()
                .getConversation(conversitionId, chatType == BorrowConstants.CHATTYPE_SINGLE ? false : true);


        mHandler = new MyHandler(this);

        topView.setAppTitle(titleName == null ? "聊天" : titleName);
        topView.setLeftImageResource(R.drawable.icon_back);
        topView.setLeftImgOnClickListener();

        topView.setRightImageDrawable(R.drawable.persondetail);
        topView.getRightLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(toChatName)) {
                    toChatName = titleName;
                }
                Intent intent = new Intent(ChatMainActivity.this, PersonDetailActivity_.class);
                intent.putExtra("headerImgId", headerImgId);
                intent.putExtra("toChatName", toChatName);
                intent.putExtra("currentFigureId", currentFigureId);
                intent.putExtra("figureId", toChatId);
                intent.putExtra("toChatId", ContactDBHandler.getContactId(toChatId, currentFigureId));
                startActivityForResult(intent, GOTOPERSONDETAILACTIVITY);
                animLeftToRight();
            }
        });

        if (chatType == BorrowConstants.CHATTYPE_GROUP) {
          if(currentFigureId.equals("-1")){
               Extras mExtras =new Extras();
              mExtras.setGid(toChatId);
              mExtras.setToid(toChatXlId);
              NoticeGetGroupOrContactInfoUtil   noticeGetGroupOrContactInfoUtil=new NoticeGetGroupOrContactInfoUtil(context,mHandler);
              noticeGetGroupOrContactInfoUtil.getLocalGroupListAndBubbleSort(mExtras);
          }
           final Group group = GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(toChatId,currentFigureId));

            if (group == null ||BorrowConstants.IS_NO_JOIN_GROUP.equals(group.isJoin)|| group.groupType.equals(GroupListInContactAdapter.GROUP_TYPE_BLACK)) {
                showOrHideChatBar(false);
            } else {
                topView.setRightImageDrawable(R.drawable.icon_groupinfo);
                topView.getRightLayout().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if ("0".equals(group.isJoin)) {
                            //
                            tip("无法获得群信息"); //
                            // 当数据库中没有此群信息
                            //1.退群
                            //2.
                            return;
                        }
                        GroupManagerActivity_.intent(context)
                                .currentFigureId(currentFigureId)
                                .toGroupId(toChatId + "")
                                .toGroupName(
                                       GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(toChatId,currentFigureId)).xlGroupName
                                )
                                .start();
                    }
                });
            }
        } else {
            String titleNames = titleName == null ? "聊天" : titleName;
            FigureMode figureMode = ContactManager.getInstance().getCurrentFigure(currentFigureId);
            if (figureMode != null) {
                String figureName = figureMode.getFigureName();
                int figureNameSize= figureName.length();
                int titleNamesSize= titleNames.length();
                if (figureNameSize > 3){
                    figureName = figureName.substring(0,3)+"...";
                }
                if(titleNamesSize > 3){
                    titleNames = titleNames.substring(0,3)+"...";
                }
                if (ContactManager.getInstance().getCurrentFigure() != null) {
                    titleNames = titleNames + "-" + figureName;
                }
            }

            topView.setAppTitle(titleNames);
        }



        LogCatLog.e(TAG, "toChatId=" + toChatId + ",chatType=" + chatType);

        XLApplication.toChatId = toChatId;//保存正在聊天的id


        mMomentDialogueLoader = new MessageDialogueLoader(context, toChatId, chatType, 1);

        adapter = new MessageChatAdapter(chatList,
                context,
                toChatId,
                chatType,
                headerImgId,
                currentFigureId
        );
        adapter.setChatData(listData);
        chatList.setAdapter(adapter);
        adapter.setAlipayVoiceRecorder(mAlipayVoiceRecorder);
        chatList.hideTopTxt();
        chatList.setPullRefreshEnable(true);
        chatList.setPullLoadEnable(true);
        chatList.hideLoadMore();
        chatList.setXListViewListener(ChatMainActivity.this);
        //定位上次离开位置
        //adapter.refreshSeekTo(-1);
        //adapter.refreshSelectLast();

        mAlipayVoiceRecorder = new AlipayVoiceRecorder(this);
        registEvent();

        if (chatType == BorrowConstants.CHATTYPE_GROUP) {
            //  LogCatLog.d(TAG, "status==" + status);
            Group group = GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(toChatId,currentFigureId));
            if(group == null){
                showOrHideChatBar(true);
                return;
            }
            if (group.status != null) {
                if (group.status.equals("DISMISS")||group.groupType.equals(GroupListInContactAdapter.GROUP_TYPE_BLACK)) {
                    showOrHideChatBar(false);
                } else {
                    showOrHideChatBar(true);
                }
            }else{
                showOrHideChatBar(true);
            }
        }
    }

   //未读消息点击
    @Click(R.id.ll_unRead)
    void clickUnRead() {

        if (new_msg_count > 0 && new_msg_count < chatList.getCount()) {
            int position = chatList.getCount() - 1 - new_msg_count;
            chatList.setSelection(position);
            new_msg_count = 0;
            showUnReadMsg();
        }

    }
    @Click(R.id.btn_send)
    void clickSend() {
        // 点击发送按钮(发文字和表情)

        String content = mPasteEditText.getText().toString().trim();
        if (content.length() > 250) {
            tip("请输入250字以内");
        } else if (content.length() > 0) {
            isToBottom = true;
            isInBottom=true;
            addToMessageDB(true);// 添加消息到数据库
        }
    }

    /**
     * 注册事件
     */
    private void registEvent() {

        // 监听文字框
        mPasteEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                if (!TextUtils.isEmpty(s)) {
                    //btnMore.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    //btnMore.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mPasteEditText.setHint("");
                else
                    mPasteEditText.setHint(getString(R.string.tip_send));
            }
        });
        mPasteEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setIconHilite(-1);
                LogCatLog.e(TAG, "ontouch event");
                ll_menu_container.setVisibility(View.GONE);
                // Utils.showSoftKeyboard(mPasteEditText);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (chatList.getCount() > 0) chatList.setSelection(chatList.getCount() - 1);
                        chatList.requestLayout();
                    }
                }, 200);
                return false;
            }
        });

        chatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setIconHilite(-1); // 设置默认不选中任何选项卡
                mPasteEditText.clearFocus();
                ll_menu_container.setVisibility(View.GONE);
                Utils.hideSoftKeyboard(mPasteEditText);
                return false;
            }
        });
        //chatList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        chatList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_FLING) {
                } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {

                } else if (scrollState == SCROLL_STATE_IDLE) {//停止滚动
                    //第一条未读消息显示出来时气泡消失
                    LogCatLog.e(TAG, "new_msg_count=" + new_msg_count
                            + ",chatList size=" + chatList.getCount()
                            + ",last visiable position=" + chatList.getLastVisiblePosition());
//                    int leave_position=chatList.getFirstVisiblePosition();
//                    if(leave_position>=0){
//                        LogCatLog.e(TAG, "leave position=" + chatList.getFirstVisiblePosition());
//                        DataDealUtil.saveChatLeavePosition(currentFigureId,toChatId,
//                                (MessageBean)adapter.getItem(leave_position));
//                        //保存当前离开位置不是私密消息的最新记录
//                        for(int i=leave_position+1;i>=0&&i<adapter.getCount();i--){
//                            MessageBean bean= (MessageBean)adapter.getItem(i);
//                            if(!bean.isPrivate()){
//                                DataDealUtil.saveChatLeaveNoSecretPosition(currentFigureId,toChatId,
//                                        (MessageBean)adapter.getItem(i));
//                                break;
//                            }
//                        }
//                    }
                    if(chatList.getCount()-new_msg_count-1<=chatList.getLastVisiblePosition()){
                        new_msg_count=0;
                        showUnReadMsg();
                    }
                }
            }

            @Override
            public void onScroll(
                    AbsListView view,
                    int firstVisibleItem,
                    int visibleItemCount,
                    int totalItemCount
            ) {
                    if ((firstVisibleItem + visibleItemCount >= totalItemCount-1
                    )
                            && totalItemCount > 0
                            ) {
                        isInBottom = true;
                    } else {
                        isInBottom = false;
                    }
                    saveLeavePosition = firstVisibleItem;
                                        LogCatLog.e(TAG, "saveLeavePosition=" + saveLeavePosition + ",isInBottom="
                     + isInBottom);
            }
        });

        //chatList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(),true, true ));


    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }


        /** 清空消息**/
        switch (requestCode) {
            case REQUEST_CODE_ADD_TO_BLACKLIST: { // 移入黑名单
                //addUserToBlacklist(deleteMsg.getFrom());
            }
            break;
            case REQUEST_CODE_GROUP_DETAIL: {
                adapter.refresh();
            }
            break;
            case REQUEST_CODE_ID_CARD: {
                Contact businessCardBean = (Contact) data.getSerializableExtra(NewCardActivity.KEYID);
                String localMsgKey = System.currentTimeMillis() + "";//本地消息的key
                MessageBean bean = new MessageBean.Builder()
                        .msgType(MessageChatAdapter.IDCARD)
                        .msgStatus(BorrowConstants.MSGSTATUS_SEND)
                        .msgLocalKey(localMsgKey)
                        .msgKey(localMsgKey)
                        .msgDate(
                                Utils.timeStamp2Date(
                                        System.currentTimeMillis(),
                                        "yyyy-MM-dd HH:mm"
                                )
                        )
                        .build();
                SendMsgController.getInstance().sendChatIDCard(bean, businessCardBean, false);

            }
            break;
            case REQUEST_CODE_TEXT:// 重发消息
            case REQUEST_CODE_VOICE:
            case REQUEST_CODE_PICTURE:
            case REQUEST_CODE_LOCATION:
            case REQUEST_CODE_VIDEO:
            case REQUEST_CODE_FILE: {
                //resendMessage();
            }
            break;
        }
        if (data == null) return;
        MessageBean mMessageBean = (MessageBean) data.getSerializableExtra("MessageBean");
        if (mMessageBean != null) {
            if (mMessageBean.msgType == MessageChatAdapter.IDCARD) {
                Contact nameCard= new Contact.Builder(Contact.ITEM)
                        .xlUserName( mMessageBean.idCard.getName())
                        .figureUsersId( mMessageBean.idCard.getFigureId())
                        .file_id( mMessageBean.idCard.getImgId())
                        .build();

                SendMsgController.getInstance().sendChatIDCard(mMessageBean ,nameCard, false);
            } else if (mMessageBean.msgType == MessageChatAdapter.WEBSHOPPING) {
                SendMsgController.getInstance().sendChatGoods(mMessageBean,
                        mMessageBean.goodsCard.getGoodsId(),
                        mMessageBean.goodsCard.getName(),
                        mMessageBean.goodsCard.getImgURL(),
                        mMessageBean.goodsCard.getPrice(),
                        mMessageBean.goodsCard.getAbstraction(),
                        mMessageBean.goodsCard.getUrl(),
                        false);
            } else if (mMessageBean.msgType == MessageChatAdapter.NEWSCARD) {
                SendMsgController.getInstance().sendChatNews(mMessageBean,
                        mMessageBean.newsCard.getNewsid(),
                        mMessageBean.newsCard.getTitle(),
                        mMessageBean.newsCard.getImgurl(),
                        mMessageBean.newsCard.getSummary(),
                        mMessageBean.newsCard.getUrl(),
                        false);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mMomentDialogueLoader.onContentChanged();
        mAlipayVoiceRecorder.onForeground();
        XLApplication.isHome = false;

        if (chatType == BorrowConstants.CHATTYPE_GROUP) {

            Group group = GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(toChatId,currentFigureId));
            if (group != null) {
                String groupname = group.xlGroupName;
                String figureName = "";
                FigureMode figureMode = ContactManager.getInstance().getCurrentFigure(currentFigureId);
                if (figureMode != null) {
                    figureName = figureMode.getFigureName();
                }
                if (figureName.length() > 3) {
                    figureName = figureName.substring(0, 3) + "...";
                }
                if (groupname.length() > 3) {
                    groupname = groupname.substring(0, 3) + "...";
                }
                if (ContactManager.getInstance().getCurrentFigure() == null) {
                    topView.setAppTitle(groupname + "-" + figureName);
                } else {
                    topView.setAppTitle(groupname);
                }
            } else {
                topView.setAppTitle(titleName);
            }

            if(group!=null&&group.isJoin!=null&&group.isJoin.equals(BorrowConstants.IS_NO_JOIN_GROUP)){
                showOrHideChatBar(false);
            }
            iv_chat_pvi.setVisibility(View.GONE);

        }else{
            iv_chat_pvi.setVisibility(View.VISIBLE);
            if(DataDealUtil.isSecretMode(currentFigureId,toChatId)){

                setSecretOpen();
            }else{
                setSecretClose();
            }
        }

        adapter.refresh();
        ChatManager.getInstance().registerEventListener(
                this,
                new XLNotifierEvent.Event[]{XLNotifierEvent.Event.EventNewMessage,
                });
    }

        @Override
        public void onStop () {
            super.onStop();
            ChatManager.getInstance().unregisterEventListener(this);

        }

        @Override
        public void onEvent (XLNotifierEvent xlNotifierEvent){

            switch (xlNotifierEvent.getEvent()) {
                case EventNewMessage:
                    // 获取到message
                    final MessageBean message = (MessageBean) xlNotifierEvent.getData();

                    String toChatId = null;
                    // 群组消息
                    if (message.getChatType() == MessageBean.ChatType.GroupChat) {
                        toChatId = message.getFrom();
                    } else {
                        // 单聊消息
                        toChatId = message.getFrom();
                    }

                    // 如果是当前会话的消息，刷新聊天页面
                    if (toChatId.equals(this.toChatId)) {

                        //  adapter.refreshSelectLast();
                        adapter.refresh();
                        if(!isInBottom){//浏览历史记录
                            new_msg_count++;
                        }else{
                            new_msg_count=0;
                        }
                        LogCatLog.e(TAG,"isInBottom="+isInBottom+",new_msg_count="+new_msg_count);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                     showUnReadMsg();
                            }
                        });

                    } else {
                        // 如果消息不是和当前聊天ID的消息
                        onNewMsg(message);
                    }

                    break;
                default:
                    break;
            }

        }

    private void showUnReadMsg(){
        if(new_msg_count>99){
            tv_unRead.setText("99+");
            tv_unRead.setVisibility(View.VISIBLE);
        }else if(new_msg_count>0){
            tv_unRead.setText(new_msg_count+"");
            tv_unRead.setVisibility(View.VISIBLE);
        }else{
            new_msg_count=0;
            tv_unRead.setVisibility(View.GONE);
        }
    }


        /**
         * 处理新收到的消息，然后发送通知
         *
         * @param message
         */

    public synchronized void onNewMsg(MessageBean message) {


    }

    @Override
    protected void onPause() {
        SoundUtil.getInstance().stopPlayer();
        beforeCount = 0;
        Utils.hideSoftKeyboard(mPasteEditText);
        mAlipayVoiceRecorder.onBackgound();
        //        if (listData.size() > saveLeavePosition) {
        //            int before = leavePosition >= 0 ? leavePosition : 0;
        //            LogCatLog.e(TAG, "pause on leave saveLeavePosition=" + saveLeavePosition +
        //                    ",before=" + before);
        //            messageDBHandler.updateMsgCurrent(listData.get(saveLeavePosition).msgKey,
        //                    listData.get(before).msgKey);
        //            leavePosition = saveLeavePosition;//保存最新离开位置
        //        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        XLApplication.toChatId = "";//保存正在聊天的id

        unregisterReceiver(netReceiver);
        //autoReadMsg();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.hideSoftKeyboard(mPasteEditText);
        return super.onTouchEvent(event);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showSoftKeyboard(mPasteEditText);
                if (chatList.getCount() > 0) chatList.setSelection(chatList.getCount() - 1);
                chatList.stopLoadMore();
            }
        }, 200);
    }

    private int beforeCount;

    private int leavePosition = 0;//上次离开时的位置 从0开始
    private int unReadPosition = 0;//未读消息的位置  从0开始
    private int saveLeavePosition = 0;//保存最新离开的的位置
    public boolean isInBottom = false;//是否在底部
    public boolean isToBottom = true;//是否定位到底部
    private final int loadMsgCount = 30;//一次加载的消息数目


    /**
     * 显示聊天工具条和右上角详情按钮
     *
     * @param show 显示还是隐藏
     */
    private void showOrHideChatBar(final boolean show) {
        if (show) {
            rlBottom.setVisibility(View.VISIBLE);
            topView.getRightLayout().setVisibility(View.VISIBLE);
            ll_bottom_menu.setVisibility(View.VISIBLE);
        } else {
            rlBottom.setVisibility(View.GONE);
            topView.getRightLayout().setVisibility(View.GONE);
            ll_bottom_menu.setVisibility(View.GONE);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chatList.setPullLoadEnable(show);
            }
        }, 500);
    }



    /**
     * 设置输入选项卡图标高亮
     *
     * @param index 选项卡编号(从0开始)
     */
    private void setIconHilite(int index) {
        mPicIconIv.setImageResource(R.drawable.chat_pic_def);
        mVoiceIconIv.setImageResource(R.drawable.chat_voice_def);
        mFaceIconIv.setImageResource(R.drawable.chat_face_def);
        mCardIconIv.setImageResource(R.drawable.chat_card_def);
        switch (index) {
            case PIC_FRAGMENT_INDEX: {
                mPicIconIv.setImageResource(R.drawable.chat_pic_def_hilite);
            }
            break;
            case VOICE_FRAGMENT_INDEX: {
                mVoiceIconIv.setImageResource(R.drawable.chat_voice_def_hilite);
            }
            break;
            case FACE_FRAGMENT_INDEX: {
                mFaceIconIv.setImageResource(R.drawable.chat_face_def_hilite);
            }
            break;
            case CARD_FRAGMENT_INDEX: {
                mCardIconIv.setImageResource(R.drawable.chat_card_def_hilite);
            }
            break;
        }
    }

    public void autoReadMsg() {
        msgCountThreadPool.submit(new Runnable() {
            @Override
            public void run() {

                if (chatType == BorrowConstants.CHATTYPE_GROUP) {
                    messageDBHandler.autoReadMsg(GroupDBHandler.getGroupId(toChatId,currentFigureId));//标记为,已读在加载数据之前调用
                } else {
                    messageDBHandler.autoReadMsg(ContactDBHandler.getContactId(toChatId, currentFigureId));//标记为,已读在加载数据之前调用
                }
            }
        });
    }

}

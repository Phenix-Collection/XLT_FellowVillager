/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.group.GroupAddMemberActivity_;
import com.xianglin.fellowvillager.app.activity.personal.PersonalInfoActivity_;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.constants.SyncApiConstans;
import com.xianglin.fellowvillager.app.db.FigureDbHandler;
import com.xianglin.fellowvillager.app.fragment.MainContactFragment;
import com.xianglin.fellowvillager.app.fragment.MainMeFragment;
import com.xianglin.fellowvillager.app.fragment.MainMessageFragment;
import com.xianglin.fellowvillager.app.fragment.MainWebFragment;
import com.xianglin.fellowvillager.app.fragment.MainWebFragment_;
import com.xianglin.fellowvillager.app.longlink.LongLinkUtils;
import com.xianglin.fellowvillager.app.longlink.XLNotifierEvent;
import com.xianglin.fellowvillager.app.longlink.listener.XLEventListener;
import com.xianglin.fellowvillager.app.model.Extras;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.JpushUtil;
import com.xianglin.fellowvillager.app.utils.NoticeGetGroupOrContactInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.MenuPopuWindow;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.tab.MainTab;
import com.xianglin.fellowvillager.app.widget.tab.MyFragmentTabHost;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.ContactVo;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity
        implements TabHost.OnTabChangeListener, XLEventListener {

    /**
     * 首页选项卡
     */
    public static final int TAB_HOME = 0;
    /**
     * 消息选项卡
     */
    public static final int TAB_MESSAGE = 1;
    /**
     * 角色选项卡
     */
    public static final int TAB_FIGURE = 2;
    /**
     * 名录选项卡
     */
    public static final int TAB_CONTACT = 3;
    /**
     * 我选项卡
     */
    public static final int TAB_ME = 4;

    public static boolean isForeground = false;//Jpush
    private MainTab[] tabs;
    @ViewById(R.id.topview)
    TopView mTopView;

    @ViewById(android.R.id.tabhost)
    public MyFragmentTabHost mTabHost;
    @ViewById(R.id.img_right)
    public ImageView ivRight;

    @ViewById(R.id.figureBtn)
    public CircleImage mFigureBtn;

    @ViewById(R.id.red_dot)
    ImageView mRedDot;

    int currentTabIndex;// 当前fragment的index

    private boolean isFirstLoadedContact = true; //通讯录只在第一次加载时弹进度

    private Animation mDismissAnim, mShowAnim;
    private MenuPopuWindow mMenuPopuWindow = null;
    private String figureName = null;
    private boolean mIsExit; // 是否退出App
    private static final Long SYNCTIME = 800L;
    private static final String LASTTIMESYNC = "DATE_MAIN";

    public static final int REQUEST_CODE_FIGURE = 0x0001;
    public static final int REQUEST_CODE_PERSONAL = 0x0002;
    public NoticeGetGroupOrContactInfoUtil noticeGetGroupOrContactInfoUtil;

    private String name = null;//发送人name
    private String xlID = null;//发送人id

    private String headImgId;//群对话中发送人的ID

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @AfterInject
    void init(){

        // 保存当前的环境
        new LongLinkUtils().longLinkStart();

        registerMessageReceiver();  // used for receive msg
        registerNetwrokReceiver();


        JpushUtil.setAlias(MainActivity.this, PersonSharePreference.getUserID() + "");
        noticeGetGroupOrContactInfoUtil = new NoticeGetGroupOrContactInfoUtil(MainActivity.this,handler);
        if (getIntent().getExtras() != null) {
            String extra = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
            Extras mExtras = parseJSONString(extra);
            if (mExtras != null) {
                if ("0".equals(mExtras.getType())) {
                    noticeGetGroupOrContactInfoUtil.getDBContact(mExtras);
                } else if ("1".equals(mExtras.getType())) {
                    noticeGetGroupOrContactInfoUtil.getLocalGroupListAndBubbleSort(mExtras);
                }
            } else {
                LogCatLog.e(TAG, "解析通知数据失败");
            }
        }
    }


    //注解完成执行
    @AfterViews
    void initView() {

        loadCurrentFigure();

        mTopView.setRightImageDrawable(R.drawable.icon_new_group_chat);
        mTopView.setRightImage1Visibility(View.VISIBLE);

        initTabs();

        mMenuPopuWindow = new MenuPopuWindow(this);
        mShowAnim = new RotateAnimation(0, 45,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mShowAnim.setInterpolator(new LinearInterpolator());
        mShowAnim.setDuration(250);
        mShowAnim.setFillAfter(true);

        mDismissAnim = new RotateAnimation(45, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mDismissAnim.setInterpolator(new LinearInterpolator());
        mDismissAnim.setDuration(250);
        mDismissAnim.setFillAfter(true);
    }


    @SuppressWarnings("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Extras mExtras = (Extras) msg.obj;
            if (msg.what == 1) {

                if ("0".equals(mExtras.getType())) {     //点对点对话
                    ChatMainActivity_.intent(context)
                            .titleName(name)
                            .headerImgId(headImgId)
                            .toChatId(mExtras.getFfid()) // contact figure id && group id
                            .currentFigureId(mExtras.getTfid())// this user  figure id
                            .toChatXlId(mExtras.getFid())// contact user id
                            .chatType(BorrowConstants.CHATTYPE_SINGLE)
                            .start();
                } else if ("1".equals(mExtras.getType())) {      //群对话
                    noticeGetGroupOrContactInfoUtil.getLocalGroupListAndBubbleSort(mExtras);

                }

            } else if (msg.what == 2) {

                Bundle bundle = msg.getData();
                Long figureId = bundle.getLong("figureId");
                String groupName = bundle.getString("groupName");
                ChatMainActivity_.intent(MainActivity.this)
                        .titleName(groupName)
                        .toChatId(mExtras.getGid()) // contact figure id && group id
                        .currentFigureId(figureId + "")// this user  figure id//TODO Ok
                        .toChatXlId(mExtras.getFid())// contact user id
                        .chatType(BorrowConstants.CHATTYPE_GROUP)
                        .start();

            }else if(msg.what == 3){

                ChatMainActivity_.intent(MainActivity.this)
                        .toChatId(mExtras.getGid()) // contact figure id && group id
                        .currentFigureId("－1")// this user  figure id//TODO Ok
                        .toChatXlId(mExtras.getFid())// contact user id
                        .chatType(BorrowConstants.CHATTYPE_GROUP)
                        .start();


            }
        }
    };


    @Click(R.id.figureBtn)
    void switchFigure() {
        startActivityForResult(new Intent(MainActivity.this, SwitchFigureActivity.class),
                REQUEST_CODE_FIGURE);
    }

    private Fragment getFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * 初始化显示角色未读消息底部选项卡红点
     */
    private void initRedDot() {
        FigureDbHandler figureDbHandler = new FigureDbHandler(this);
        HashMap<String, Long> unReadMsgMap = figureDbHandler.queryFigureWithMsgCount();
        if (unReadMsgMap == null) {
            return;
        }
        if (ContactManager.getInstance().getCurrentFigure() == null) { // 全部角色
            for (Map.Entry<String, Long> entry :
                    unReadMsgMap.entrySet()) {
                if (entry.getValue() <= 0L) {
                    continue;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRedDot.setVisibility(View.VISIBLE);
                        return;
                    }
                });
            }
        } else { // 单个角色
            for (Map.Entry<String, Long> entry :
                    unReadMsgMap.entrySet()) {
                if (entry == null || entry.getKey() == null) {
                    continue;
                }
                if (entry.getKey().equals(ContactManager.getInstance().getCurrentFigureID())) {
                    continue;
                }
                if (entry.getValue() <= 0L) {
                    continue;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRedDot.setVisibility(View.VISIBLE);
                        return;
                    }
                });
            }
        }

    }




    /**
     * 加载当前角色
     */
    public void loadCurrentFigure() {
        FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
        if (currentFigure == null) {
            mFigureBtn.setImageResource(R.drawable.all_figure);
        } else {
            ImageUtils.showCommonImage(
                    this,
                    mFigureBtn,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    currentFigure.getFigureImageid(),
                    R.drawable.head
            );
        }
    }

    private void initTabs() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }

        tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            if (i == TAB_FIGURE) {
                title.setEnabled(false);
            }
            if (mainTab.getResIcon() != -1) {
                Drawable drawable = this.getResources().getDrawable(
                        mainTab.getResIcon());
                title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                        null);
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
        }

        mTabHost.setCurrentTab(TAB_MESSAGE);

        mTabHost.setOnTabChangedListener(this);
        FigureMode figureMode = ContactManager.getInstance().getCurrentFigure();
        if (figureMode != null) {
            String title = figureMode.getFigureName();
            figureName = title;
            mTopView.setAppTitle(title);
        }
    }

    /**
     * 点击底部选项卡事件
     */
    private void onTabClicked() {
        FigureMode figureMode = ContactManager.getInstance().getCurrentFigure();
        if (figureMode != null) {
            String title = figureMode.getFigureName();
            figureName = title;
        } else {
            figureName = null;
        }
        switch (currentTabIndex) {
            case TAB_HOME:
                mTopView.setAppTitle(R.string.menu_home);
                mTopView.setRightImageVisibility(View.GONE);
                mTopView.setRightImage1Visibility(View.GONE);
                break;
            case TAB_MESSAGE:
                setTopViewTitle(figureName);
                mTopView.setRightImageDrawable(R.drawable.icon_new_group_chat);
                mTopView.setRightImage1Visibility(View.VISIBLE);
                break;
            case TAB_CONTACT:
                if (isFirstLoadedContact) {
                    showLoadingDialog();
                    isFirstLoadedContact = false;
                }
                setTopViewTitle(figureName);
                mTopView.setRightImageDrawable(R.drawable.add_contact);
                mTopView.setRightImage1Visibility(View.GONE);
                break;
            case TAB_ME:
                mTopView.setAppTitle(R.string.menu_me);
                mTopView.setRightImageVisibility(View.GONE);
                mTopView.setRightImage1Visibility(View.GONE);
                mTopView.setRightTextViewText("新建角色");
                break;
        }
    }

    public void setTopViewTitle(String title) {

        if (currentTabIndex == TAB_CONTACT || currentTabIndex == TAB_MESSAGE) {
            if (title == null) {
                FigureMode figureMode = ContactManager.getInstance().getCurrentFigure();
                if (figureMode != null) {
                    title = figureMode.getFigureName();
                    figureName = title;
                    mTopView.setAppTitle(title);
                } else {
                    // 全部角色
                    if (currentTabIndex == TAB_CONTACT) {
                        mTopView.setAppTitle(R.string.menu_contact);
                    } else if (currentTabIndex == TAB_MESSAGE) {
                        mTopView.setAppTitle(R.string.xianglintong);
                    }
                }
            } else {
                if (!title.equals(mTopView.getAppTitle())) {

                    mTopView.setAppTitle(title);
                }

            }
        }


    }

    @Click(R.id.ll_right_layout_1)
    void clickSwipQrcode() {
        startActivity(new Intent(this, CaptureActivity.class));
    }

    @Click(R.id.ll_right_layout)
    void clickRight() {
        switch (currentTabIndex) {
            case 0:
                GroupAddMemberActivity_.intent(this)
                        .addOrJoin(BorrowConstants.CHATTYPE_ADD).start();
                break;
            case TAB_MESSAGE:
                GroupAddMemberActivity_.intent(this)
                        .addOrJoin(BorrowConstants.CHATTYPE_ADD).start();
                break;
            case TAB_CONTACT:
                AddFriendsActivity_.intent(MainActivity.this).start();
                animLeftToRight();
                break;
            case TAB_ME:

                startActivityForResult(
                        new Intent(this, PersonalInfoActivity_.class)
                                .putExtra("operateType", BorrowConstants.TYPE_ADD),
                        REQUEST_CODE_PERSONAL);
                break;
            default:
                break;
        }
    }

    @Click(R.id.txt_right)
    void clicktxtRight() {
        switch (currentTabIndex) {
            case TAB_ME:
                if (ContactManager.getInstance().getFigureTable().size() < 5) {
                    startActivityForResult(
                            new Intent(this, PersonalInfoActivity_.class)
                                    .putExtra("operateType", BorrowConstants.TYPE_ADD),
                            REQUEST_CODE_PERSONAL);
                } else {
                    tip("当前活跃角色已超过5个,请冻结角色后再创建!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMenuPopuWindow != null && mMenuPopuWindow.mPopupWindow.isShowing()) {
                menuPopuWindowControl();
                return true;
            }
            if (mIsExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 消息主界面右上角菜单控制
     */
    private void menuPopuWindowControl() {
        if (mMenuPopuWindow != null && mMenuPopuWindow.mPopupWindow.isShowing()) {
            ivRight.clearAnimation();
            ivRight.startAnimation(mDismissAnim);
            mMenuPopuWindow.dismissPopup();
        } else {
            ivRight.clearAnimation();
            ivRight.startAnimation(mShowAnim);
            mMenuPopuWindow.mPopupWindow.showAsDropDown(mTopView, 0, 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && data != null) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result") == null
                    ?
                    null
                    :
                    bundle.getString("result").trim();
            if (!TextUtils.isEmpty(scanResult)) {
                if (scanResult.startsWith("http")) {
                    if (tabs[currentTabIndex].getClz().equals(MainWebFragment_.class)) {
                        MainWebFragment.setWebViewUrl(scanResult);
                    }
                    ToastUtils.showCenterToast(scanResult, this);
                } else {
                    ToastUtils.showCenterToast(scanResult, this);
                }
            } else {
                ToastUtils.showCenterToast("扫描失败，请重试！", this);
            }
        } else if (requestCode == REQUEST_CODE_FIGURE || requestCode == REQUEST_CODE_PERSONAL) {
            // 切换角色返回结果处理
            if (resultCode != SwitchFigureActivity.RESULT_CODE_OK) {
                return;
            }
            FigureMode figureMode = ContactManager.getInstance().getCurrentFigure();
            if (figureMode == null) {
                mFigureBtn.setImageResource(R.drawable.all_figure);
            } else {
                ImageUtils.showCommonImage(
                        this,
                        mFigureBtn,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureMode.getFigureImageid(),
                        R.drawable.head);
            }
            refreshFragmentByFigure(figureMode);
        }
    }

    /**
     * 切换角色后更新主界面各个fragment
     *
     * @param figureMode 角色model,为null时是全部角色状态
     */
    private void refreshFragmentByFigure(FigureMode figureMode) {

        String figureId;
        if (figureMode == null) {
            figureId = "";
            figureName = null;
            setTopViewTitle(null);//全部
        } else {
            figureId = figureMode.getFigureUsersid();
            figureName = figureMode.getFigureName();
            setTopViewTitle(figureName);// 单个角色

        }

        Fragment messageFragment = getFragment(getString(R.string.menu_message));
        Fragment contactFragment = getFragment(getString(R.string.menu_contact));
        Fragment meFragment = getFragment(getString(R.string.menu_mine));
        if (messageFragment instanceof MainMessageFragment) {
            ((MainMessageFragment) messageFragment).onContentChanged();
        }

        if (contactFragment instanceof MainContactFragment) {
            ((MainContactFragment) contactFragment).refreshUI();
        }

        if (meFragment instanceof MainMeFragment) {
            ((MainMeFragment) meFragment).setDataByFigureId(figureId);
        }

    }

    /**
     * 接收到消息监听器
     *
     * @param xlNotifierEvent
     */
    @Override
    public void onEvent(XLNotifierEvent xlNotifierEvent) {
        switch (xlNotifierEvent.getEvent()) {
            case EventNewMessage:
                // 获取到message
                MessageBean message = (MessageBean) xlNotifierEvent.getData();
                if (message == null) {
                    return;
                }
                if (ContactManager.getInstance().getCurrentFigure(message.figureId).getFigureStatus()
                        == FigureMode.Status.FREEZE) {
                    return;
                }
                FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
                if (currentFigure == null) { // 全部角色
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRedDot.setVisibility(View.VISIBLE);
                        }
                    });
                } else if ((currentFigure.getFigureUsersid() != null)
                        && (!currentFigure.getFigureUsersid().equals(message.figureId))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRedDot.setVisibility(View.VISIBLE);
                        }
                    });
                }

                break;
            default:
                break;
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!JpushUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                LogCatLog.i("test", showMsg.toString());
                if (extras != null) {
                    Extras mExtras = parseJSONString(extras);
                    getDetailContact(xlID, mExtras);
                }
            }
        }
    }


    private Extras parseJSONString(String extras) {
        try {
            Extras mExtras = JSON.parseObject(extras, Extras.class);
            LogCatLog.d(TAG, mExtras.getId());
            return mExtras;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onResume() {
        initRedDot();
        registerMessageReceListener();
        PersonSharePreference.setIsInNoticeTime("true");
        isForeground = true;
        super.onResume();
        LogCatLog.i(TAG, "---------------------clearAllNotifications------------------");
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (mMenuPopuWindow != null && !mMenuPopuWindow.mPopupWindow.isShowing()) {
            ivRight.clearAnimation();
        }
    }

    @Override
    protected void onPause() {
        isForeground = false;
        unRegisterMessageReceListener();
        mRedDot.setVisibility(View.GONE);
        super.onPause();
    }

    /**
     * 获取陌生人信息
     *
     * @param
     */
    @Background
    void getDetailContact(final String xlId, final Extras mExtras) {
        final CommonReq commonReq = new CommonReq();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("contactXlid", xlId);
                put("operateType", SyncApiConstans.DETAIL);
            }
        });
        SyncApi.getInstance().contactManage(this, commonReq, new SyncApi.CallBack<ContactVo>() {
            @Override
            public void success(ContactVo mode) {
                name = mode.getRemarkName();
                headImgId = mode.getImgId() + "";
                Message message = new Message();
                message.what = 1;
                message.obj = mExtras;
                handler.sendMessage(message);

            }

            @Override
            public void failed(String err, int type) {
                tip("");
            }

        });
    }

    @Override
    public void onTabChanged(String tabId) {

        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
                currentTabIndex = i;
                onTabClicked();
            } else {
                v.setSelected(false);
            }
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mNetwrokReceiver);
        super.onDestroy();
    }


    /**
     * 注册未读消息的接收器
     */
    private void registerMessageReceListener() {
        ChatManager.getInstance().registerEventListener(
                MainActivity.this,
                new XLNotifierEvent.Event[]{XLNotifierEvent.Event.EventNewMessage,
                });
    }

    /**
     * 取消未读消息的接收器
     */
    private void unRegisterMessageReceListener() {
        ChatManager.getInstance().unregisterEventListener(
                MainActivity.this
        );
    }


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    private void registerNetwrokReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetwrokReceiver, intentFilter);
    }


    private BroadcastReceiver mNetwrokReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Fragment fragment = getFragment(getString(R.string.menu_message));
            LogCatLog.e("Test", "action=" + intent.getAction());
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                final ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

                if (ni != null && ni.isConnected()) {
                    if (System.currentTimeMillis() - Utils.getLongValue(LASTTIMESYNC, 0) >= SYNCTIME) {
                        Utils.putLongValue(LASTTIMESYNC, System.currentTimeMillis());
                        if (fragment != null && fragment instanceof MainMessageFragment) {
                            ((MainMessageFragment) fragment).showNetNoneState(false);
                        }
                    }
                } else {
                    if (fragment != null && fragment instanceof MainMessageFragment) {
                        ((MainMessageFragment) fragment).showNetNoneState(true);
                    }
                }
            }
        }
    };


}

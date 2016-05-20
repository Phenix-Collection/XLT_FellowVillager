/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.amap.api.location.AMapLocation;
import com.xianglin.appserv.common.service.facade.model.LocationInfoDTO;
import com.xianglin.cif.common.service.facade.model.DeviceInfo;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.cif.common.service.facade.model.LoginInfo;
import com.xianglin.cif.common.service.facade.model.enums.DevicePlatform;
import com.xianglin.cif.common.service.facade.model.enums.SystemType;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.MainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.db.UserDBHandler;
import com.xianglin.fellowvillager.app.model.User;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.lbs.v2.LBSLocationManagerProxy;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 启动页
 * 1.版本检查
 * 2.数据初始化
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/11 14:18  XLXZ Exp $
 */
@EActivity(R.layout.launch_layout)
public class LaunchActivity extends BaseActivity
        implements LBSLocationManagerProxy.LocationListener {
    private long beginTime;
    private long endTime;

    @ViewById(R.id.ll_root)
    View mContentView;

    private String deviceId;
    private DeviceInfo baseDeviceInfo = new DeviceInfo();
    private FigureDTO mFigureDTO = new FigureDTO();

    private String figureId;

    double longitude = com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLongitudeDoube();
    double latitude = com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLatitudeDoube();

    /**
     * 登录成功与否
     */
    private boolean loginSuccess = false;

    //setContentView();之前执行
    @AfterInject
    void init() {
        //需要一些权限,放在Activity中才能处理,权限请求的回调
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LBSLocationManagerProxy.getInstance().setUIListener(this);
        // 防止有时候按home键将app压到后台后,点击app后显示launch页面的情况
        if (!isTaskRoot()) {
            String intentAction = getIntent().getAction();
            if (getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                    && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
    }

    //注解完成执行
    @AfterViews
    void initView() {
        // 启动本地通知
        PersonSharePreference.setIsInNoticeTime("true");
        LogCatLog.i(TAG, "----IsInNoticeTime----" + PersonSharePreference.getIsInNoticeTime());
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(500);
        BackgroundExecutor.execute(
                new BackgroundExecutor.Task("", 0, "") {
                    @Override
                    public void execute() {
                        try {
                            //耗时的数据库升级操作,显示进度 需要根据版本号分别升级
/*                            if (BaseDBHelper.oldVersion != 0 && BaseDBHelper.oldVersion <
                                    XLApplication.xlDBVer) {
                                new UpgradeDBHandler
                                        (XiangLinApplication.getInstance()).addConstraintUNIQUE
                                        (new int[]{3, 5},
                                                new String[]{"MSG_LOCAL_KEY", "MSG_LOCAL_KEY"});
                            }*/
                            // 把发送中的标记为发送失败
                            new MessageDBHandler(getApplicationContext()).updateMsgState();
                        } catch (Throwable e) {
                            Thread.getDefaultUncaughtExceptionHandler()
                                    .uncaughtException(Thread.currentThread(), e);
                        }
                    }

                }
        );

        mContentView.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        setDeviceInfo();//设备信息
    }

    /**
     * 跳转到...注册or主界面
     */
    private void redirectTo() {
        beginTime = System.currentTimeMillis();
        final User user = new UserDBHandler(LaunchActivity.this).query();
        endTime = System.currentTimeMillis();
        LogCatLog.i("LanuchPage 用户数据库查询时间", (endTime - beginTime) + "ms");

        if (user == null) {
            firstLaunchActivity();
        } else {
            beginTime = System.currentTimeMillis();

            PersonSharePreference.setUserID(user.xlID);
            PersonSharePreference.setUserNickName(user.xlUserName);
            PersonSharePreference.setLogin(true);

            if (PersonSharePreference.getDeviceId() != "") {
                deviceId = PersonSharePreference.getDeviceId();
                com.xianglin.mobile.common.info.DeviceInfo.getInstance().setmDid(deviceId);
                LogCatLog.e(TAG, "111111 http head -- did  = " +
                        com.xianglin.mobile.common.info.DeviceInfo.getInstance().getmDid());
            }

            autoLogin(user.figureId, "");

            //初始化联系人,和当前使用角色
            ContactManager.getInstance().init(this, user.figureId);
            //初始化群信息
            GroupManager.getInstance().init(this, null);
            //加载聊天信息
            ChatManager.getInstance().loadConversation(null);
        }
    }

    public void setDeviceInfo() {
        baseDeviceInfo.setPlatform(DevicePlatform.androidPhone.name());
        baseDeviceInfo.setSystemType(SystemType.ANDROID.name());
        baseDeviceInfo.setSystemVersion(
                com.xianglin.mobile.common.info.DeviceInfo.getInstance().getmSystemVersion());
        baseDeviceInfo.setWifiMac(PersonSharePreference.getAndroidMac());
        baseDeviceInfo.setBluetoothMac(PersonSharePreference.getAndroidBtMac());
        baseDeviceInfo.setImei(PersonSharePreference.getAndroidImei());
        baseDeviceInfo.setImsi(PersonSharePreference.getAndroidImsi());
    }

    /**
     * 首次使用应用
     */
    @Background
    void firstLaunchActivity() {
        activateDevice();
        getUnusedFigureIds();
        register();
    }

    /**
     * 设备激活
     */
    void activateDevice() {
        if (PersonSharePreference.getDeviceId() != "") {
            deviceId = PersonSharePreference.getDeviceId();
        } else {
            SyncApi.getInstance().activateDevice(baseDeviceInfo, LaunchActivity.this,
                    new SyncApi.CallBack<String>() {
                        @Override
                        public void success(String mode) {
                            deviceId = mode;
                            com.xianglin.mobile.common.info.DeviceInfo.getInstance().setmDid(mode);
                            PersonSharePreference.setDeviceId(mode);
                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip(errTip);
                        }
                    }
            );
        }
    }

    /**
     * 获取figureID
     */
    void getUnusedFigureIds() {
        SyncApi.getInstance().getUnusedFigureIds(1, LaunchActivity.this,
                new SyncApi.CallBack<List<String>>() {
                    @Override
                    public void success(List<String> mode) {
                        figureId = mode.get(0);
                        PersonSharePreference.setFigureID(figureId);
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    /**
     * 注册
     */
    void register() {
        SyncApi.getInstance().register(figureId, "", LaunchActivity.this,
                new SyncApi.CallBack<FigureDTO>() {
                    @Override
                    public void success(FigureDTO mode) {
                        mFigureDTO = mode;
                        LogCatLog.e(TAG, "111111 register mFigureDTO = " + mFigureDTO);
                        SetUserNameActivity_.intent(LaunchActivity.this)
                                .mDeviceInfo(baseDeviceInfo)
                                .mDeviceId(deviceId)
                                .mFigureId(figureId)
                                .mFigureDTO(mFigureDTO).start();
                        finish();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    @Background
    void autoLogin(String figureID, String password) {
        LoginInfo mLoginInfo = new LoginInfo();
        mLoginInfo.setFigureId(figureID);
        mLoginInfo.setPassword("");
        mLoginInfo.setClientId("1");
        mLoginInfo.setClientVersion(DeviceInfoUtil.getVersionName(this));
        SyncApi.getInstance().autoLogin(mLoginInfo, baseDeviceInfo, LaunchActivity.this,
                new SyncApi.CallBack<String>() {
                    @Override
                    public void success(String mode) {
                        loginSuccess = true;
                        if (!Utils.isValidLatAndLon(longitude, latitude)) {
                            LBSLocationManagerProxy.getInstance().setUIListener(LaunchActivity.this);
                        } else {
                            reportLocationInfo();
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    /**
     * 上报地理位置信息
     */
    private void reportLocationInfo() {
        if (!loginSuccess) {
            return;
        }
        LocationInfoDTO lidto = new LocationInfoDTO();
        lidto.setPosition(null);
        lidto.setLongitude(com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLongitudeDoube());
        lidto.setLatitude(com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLatitudeDoube());
        SyncApi.getInstance().reportLocation(lidto, LaunchActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });

        MainActivity_.intent(LaunchActivity.this).start();
        endTime = System.currentTimeMillis();
        LogCatLog.i(TAG, "111111 launchtime = " + (endTime - beginTime) + "ms");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(context);
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(context);
        super.onPause();
    }

    @Override
    public void onLocationSuccess(AMapLocation location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        if (Utils.isValidLatAndLon(longitude, latitude)) {
            reportLocationInfo();
        }
    }
}

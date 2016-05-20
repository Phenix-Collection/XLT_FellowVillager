package com.xianglin.fellowvillager.app.activity.login;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xianglin.cif.common.service.facade.model.DeviceInfo;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.cif.common.service.facade.model.LoginInfo;
import com.xianglin.cif.common.service.facade.model.enums.DevicePlatform;
import com.xianglin.cif.common.service.facade.model.enums.SystemType;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import com.xianglin.mobile.common.rpc.transport.http.HttpCaller;

/**
 * Created by ex-zhangxiang on 2016/1/28.
 */
@EActivity(R.layout.activity_test)
public class TestActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.topview)
    TopView topview;

    @ViewById(R.id.btn_getUnusedLoginNames_test)
    Button btn_getUnusedLoginNames_test;

    @ViewById(R.id.btn_logout_test)
    Button btn_logout_test;

    @ViewById(R.id.btn_login_test)
    Button btn_login_test;

    @ViewById(R.id.btn_register_test)
    Button btn_register_test;

    @ViewById(R.id.btn_autoLogin_test)
    Button btn_autoLogin_test;

    @ViewById(R.id.btn_activateDevice_test)
    Button btn_activateDevice_test;
    @ViewById(R.id.btn_activateDevice_create)
    Button btn_activateDevice_create;
    @ViewById(R.id.btn_activateDevice_update)
    Button btn_activateDevice_update;
    @ViewById(R.id.btn_activateDevice_detail)
    Button btn_activateDevice_detail;
    @ViewById(R.id.btn_activateDevice_list)
    Button btn_activateDevice_list;

    public static int GETUNUSEDLOGINNAMES = 1;
    public static int REGISTER = 2;
    public static int LOGIN = 3;
    public static int AUTOLOGIN = 4;
    public static int LOGOUT = 5;

    public String deviceId;
    public List<String> figureIdList;
    String figureId;
    String xlId;


    @AfterViews
    void initView() {
        topview.setAppTitle("登录注册测试");

//        setDeviceInfo();
        btn_getUnusedLoginNames_test.setOnClickListener(this);
        btn_logout_test.setOnClickListener(this);
        btn_login_test.setOnClickListener(this);
        btn_register_test.setOnClickListener(this);
        btn_autoLogin_test.setOnClickListener(this);
        btn_activateDevice_test.setOnClickListener(this);
        btn_activateDevice_create.setOnClickListener(this);
        btn_activateDevice_update.setOnClickListener(this);
        btn_activateDevice_detail.setOnClickListener(this);
        btn_activateDevice_list.setOnClickListener(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOGIN) {
                //保存id和用户昵称
                String result = (String) msg.obj;
                LogCatLog.i(TAG, result);
            } else if (msg.what == REGISTER) {
            } else if (msg.what == LOGOUT) {
            } else if (msg.what == AUTOLOGIN) {
            } else if (msg.what == GETUNUSEDLOGINNAMES) {
            }
        }
    };

    private List<FigureDTO> figureDTOList;
    private FigureDTO curFigureDTO;
    private Boolean isUpdateSuccess;
    private List<String> curFigureIDList;
    private String curFigureID;
//
    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_getUnusedLoginNames_test:
//                tip("getUnusedLoginNames");
//                getUnusedFigureIds();
//                break;
//            case R.id.btn_logout_test:
//                tip("logout");
//                logout();
//                break;
//            case R.id.btn_login_test:
//                tip("login");
//                login();
//                break;
//            case R.id.btn_register_test:
//                tip("register");
//                register();
//                break;
//            case R.id.btn_autoLogin_test:
//                tip("autoLogin");
//                autoLogin();
//                break;
//            case R.id.btn_activateDevice_test:
//                tip("autoLogin");
//                activateDevice();
//                break;
//            case R.id.btn_activateDevice_create:
//                tip("create");
//                create();
//                break;
//            case R.id.btn_activateDevice_update:
//                tip("update");
//                curFigureDTO.setNickName("11111111");
//                update();
//                Log.e("111111", "111111 update-> isUpdateSuccess = " + isUpdateSuccess);
//                break;
//            case R.id.btn_activateDevice_detail:
//                tip("detail");
//                Log.e("111111", "111111 detail-> curFigureID = " + curFigureID);
//                detail();
//                Log.e("111111", "111111 detail-> curFigureDTO = " + curFigureDTO);
//                break;
//            case R.id.btn_activateDevice_list:
//                tip("list");
//                list();
//                Log.e("111111", "111111 list-> figureDTOList = " + figureDTOList);
//
//                break;
//        }
    }
//
//    DeviceInfo baseDeviceInfo = new DeviceInfo();
//
//    public void setDeviceInfo() {
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
////                != PackageManager.PERMISSION_GRANTED){
////            Log.e("111111", "222222 ");
////        }else {
////            ActivityCompat.requestPermissions(this,
////                    new String[]{Manifest.permission.BLUETOOTH}, 1);
////        }
//        baseDeviceInfo.setPlatform(DevicePlatform.androidPhone.name());
//        baseDeviceInfo.setSystemType(SystemType.ANDROID.name());
//        baseDeviceInfo.setSystemVersion("1.0.0");
//        baseDeviceInfo.setWifiMac(PersonSharePreference.getAndroidMac());
//        baseDeviceInfo.setBluetoothMac(PersonSharePreference.getAndroidBtMac());
//        baseDeviceInfo.setImei(PersonSharePreference.getAndroidImei());
//        Log.e("111111", "111111 imei = " + PersonSharePreference.getAndroidImei());
//        baseDeviceInfo.setImsi(PersonSharePreference.getAndroidImsi());
//    }
//
//    public LoginInfo getLoginInfo() {
//        LoginInfo baseLoginInfo = new LoginInfo();
//        baseLoginInfo.setFigureId(PersonSharePreference.getFigureID());
//        baseLoginInfo.setPassword("");
//        baseLoginInfo.setClientId("1");
//        baseLoginInfo.setClientVersion(DeviceInfoUtil.getVersionName(this));
//        Log.e("111111", "111111 baseLoginInfo.getFigureId = " + baseLoginInfo.getFigureId());
//        Log.e("111111", "111111 baseLoginInfo.getClientVersion = " + baseLoginInfo.getClientVersion());
//
//        return baseLoginInfo;
//    }
//
//    @Background
//    void activateDevice() {
////        if (PersonSharePreference.getDeviceId() != ""){
////            deviceId = PersonSharePreference.getDeviceId();
////            Log.e("111111", "111111 111111 activateDevice");
////
////        }else {
//        deviceId = SyncApi.getInstance().activateDevice(baseDeviceInfo);
////            Log.e("111111", "111111 2222222 activateDevice");
////            PersonSharePreference.setDeviceId(deviceId);
////        }
//        com.xianglin.mobile.common.info.DeviceInfo.getInstance().setmDid(deviceId);
//    }
//
//    @Background
//    void getUnusedFigureIds() {
//        Log.e("111111", "111111 getUnusedFigureIds deviceId = " + deviceId);
//        figureIdList = SyncApi.getInstance().getUnusedFigureIds(1);
//        figureId = figureIdList.get(0);
//        Log.e("111111", "111111 getUnusedFigureIds = " + figureId);
//        PersonSharePreference.setFigureID(figureId);
//    }
//
//    @Background
//    void register() {
//        Log.e("111111", "111111 register deviceId = " + figureId);
//        FigureDTO mFigureDTO = new FigureDTO();
//        mFigureDTO = SyncApi.getInstance().register(figureId, "");
//        Log.e("111111", "111111 mFigureDTO = " + mFigureDTO);
//    }
//
//    @Background
//    void autoLogin() {
//        String result = SyncApi.getInstance().autoLogin(getLoginInfo(), baseDeviceInfo);
//
//        if (result != null) {
//            Message message = new Message();
//            message.what = AUTOLOGIN;
//            message.obj = result;
//            mHandler.sendMessage(message);
//        }
//    }
//
//    @Background
//    void logout() {
//        SyncApi.getInstance().logout();
//    }
//
//    @Background
//    void login() {
//        String result = SyncApi.getInstance().login(getLoginInfo(), baseDeviceInfo);
//
//        if (result != null) {
//            Message message = new Message();
//            message.what = LOGIN;
//            message.obj = result;
//            mHandler.sendMessage(message);
//        }
//    }
//
//    @Background
//    void create() {
//        Log.e("111111", "111111 create deviceId = " + deviceId);
//        curFigureIDList = SyncApi.getInstance().getUnusedFigureIds(1);
//        curFigureID = curFigureIDList.get(0);
//        Log.e("111111", "111111 create-> curFigureID = " + curFigureID);
//        curFigureDTO = SyncApi.getInstance().create(curFigureID, true);
//        Log.e("111111", "111111 create-> curFigureDTO = " + curFigureDTO);
//    }

//    @Background
//    void update() {
//        isUpdateSuccess = SyncApi.getInstance().update(curFigureDTO);
//    }
//
//    @Background
//    void detail() {
//        curFigureDTO = SyncApi.getInstance().detail(curFigureID);
//    }
//
//    @Background
//    void list() {
//        figureDTOList = SyncApi.getInstance().list();
//    }

}

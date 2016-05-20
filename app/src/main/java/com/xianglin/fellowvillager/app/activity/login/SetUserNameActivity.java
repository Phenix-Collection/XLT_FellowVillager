/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.xianglin.appserv.common.service.facade.model.LocationInfoDTO;
import com.xianglin.cif.common.service.facade.model.DeviceInfo;
import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.cif.common.service.facade.model.LoginInfo;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.CommonChooseActivity;
import com.xianglin.fellowvillager.app.activity.MainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.FigureDbHandler;
import com.xianglin.fellowvillager.app.db.UserDBHandler;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.User;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.SystemBarTintManager;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.WebPUtil;
import com.xianglin.fellowvillager.app.utils.crop.Crop;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.lbs.v2.LBSLocationManagerProxy;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册第二步->设置用户名
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/13 17:34  XLXZ Exp $
 */
// TODO: 2015/12/4   需要优化url
//@Fullscreen
@EActivity(R.layout.activity_set_user_name)
public class SetUserNameActivity extends BaseActivity
        implements LBSLocationManagerProxy.LocationListener {
    private static final int REQUEST_CAMERA = 111;
    private boolean isSaveedHead;

    @ViewById(R.id.tv_xlid)
    TextView mTvXLID;

    @ViewById(R.id.et_user_name)
    EditText mETNickName;

    @ViewById(R.id.iv_head)
    ImageView mIvHead;

    @ViewById(R.id.view_status_bar)
    View view;

    String imageId;

    @Extra
    String mPartyId; //party Id
    @Extra
    String mFigureId; //Figure Id
    @Extra
    FigureDTO mFigureDTO;
    @Extra
    DeviceInfo mDeviceInfo;
    @Extra
    String mDeviceId;

    Uri HeadImageUri;

    Uri headTempUri;

    File saveFile;//webp保持路径

    int imageX, imageY;

    public static int GETUNUSEDFIGUREID = 1;
    public static int REGISTER = 2;
    public static int LOGIN = 3;
    public static int AUTOLOGIN = 4;
    public static int LOGOUT = 5;

    double longitude = com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLongitudeDoube();
    double latitude = com.xianglin.mobile.common.info.DeviceInfo.getInstance().getLatitudeDoube();

    /**
     * 登录成功与否
     */
    private boolean loginSuccess = false;

    //注解完成执行
    @AfterViews
    void initView() {
        LBSLocationManagerProxy.getInstance().setUIListener(SetUserNameActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.app_title_bg);
        } else {
            view.setVisibility(View.GONE);
        }

        mTvXLID.setText(getString(R.string.prefix_id) + mFigureId);
        HeadImageUri = Uri.fromFile(new File(FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureId + ".jpg"));
        headTempUri = Uri.fromFile(new File(FileUtils.IMG_SAVE_PATH_CAP_CUT, mFigureId + ".jpg"));

        imageY = imageX = mIvHead.getWidth();
    }

    @Click(R.id.btn_sure)
    void sureUserName() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SetUserNameActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        if (!ToastUtils.isFastDoubleClick(2000)) {
            //检查数据
            if (checkData()) {
                if (isSaveedHead) {
                    uploadAvatar();
                } else {
                    PersonSharePreference.setUserImgID("nohead");
                    login(false);
                }
            }
        }
    }

    private void uploadAvatar() {
        try {
            saveFile = new File(FileUtils.IMG_CACHE_HEADIMAGE_PATH, mFigureId + ".webp");
            String head = FileUtils.IMG_CACHE_HEADIMAGE_PATH + mFigureId + ".jpg";
            File file = new File(head);
            Bitmap imgBmp = null;
            if (file.exists()) {
                imgBmp = BitmapFactory.decodeFile(head);
            }
            boolean isSuccess = WebPUtil.with(context).imageToWebp(imgBmp, saveFile);
            if (isSuccess) {
                FileUtils.uploadFile(context, Long.parseLong(mFigureId),
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH + mFigureId + ".webp", listener);
            }
        } catch (Exception e) {
            LogCatLog.e(TAG, "111111 uploadAvatar : e = " + e);

        }
    }

    void update(boolean isSaveedHead) {
        mFigureDTO.setNickName(mETNickName.getText().toString().trim());
        LogCatLog.e(TAG, "111111 update : isSaveedHead = " + isSaveedHead + " / imageId = " + imageId);
        if (isSaveedHead) {
            mFigureDTO.setAvatarUrl(imageId);
        }

        SyncApi.getInstance().update(mFigureDTO, SetUserNameActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        //保存figureDTO到本地数据库
                        FigureDbHandler f = new FigureDbHandler(SetUserNameActivity.this);
                        FigureMode fm = createFigure();
                        ContactManager.getInstance().addFigureTable(fm);
                        //保存USER信息
                        User usery = new User.Builder()
                                .xlID(mFigureDTO.getPartyId())
                                .deviceID(mDeviceId)
                                .xlUserName(mFigureDTO.getNickName())
                                .imagePath(HeadImageUri.getPath())
                                .figureId(fm.getFigureUsersid())
                                .build();
                        UserDBHandler userDBHandler = new UserDBHandler(context);
                        userDBHandler.add(usery);

                        PersonSharePreference.setUserID(mFigureDTO.getPartyId());
                        PersonSharePreference.setUserNickName(mFigureDTO.getNickName());
                        PersonSharePreference.setLogin(true);
                        //初始化联系人,和当前使用角色
                        ContactManager.getInstance().init(SetUserNameActivity.this, fm.getFigureUsersid());
                        //加载聊天信息
                          ChatManager.getInstance().loadConversation(null);

                        if (!Utils.isValidLatAndLon(longitude, latitude)) {
                            LBSLocationManagerProxy.getInstance().setUIListener(SetUserNameActivity.this);
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

    FigureMode createFigure() {
        FigureMode figureMode = new FigureMode();
        figureMode.setXlId(mFigureDTO.getPartyId());
        figureMode.setFigureUsersid(mFigureDTO.getFigureId());
        figureMode.setFigureName(mFigureDTO.getNickName());
        figureMode.setFigureImageid(mFigureDTO.getAvatarUrl());
        figureMode.setFigureGender(FigureMode.FigureGender.UNKNOWN);//valueOf(mFigureDTO.getGender()));
        figureMode.setSexualOrientation(FigureMode.SexualOrientation.UNKNOWN);//        valueOf(mFigureDTO.getSexualOrientation()));
        figureMode.setFigureInfo(mFigureDTO.getIndividualitySignature());
        figureMode.setFigureStatus(Enum.valueOf(FigureMode.Status.class, mFigureDTO.getStatus()));
        figureMode.setFigure_usersid_shortid(Utils.getUniqueMessageForFigureidId());
        figureMode.setCreateDate(mFigureDTO.getCreateTime());
        figureMode.setUpdateDate(mFigureDTO.getUpdateTime());
        figureMode.setIsOpen("1");
        return figureMode;
    }

    @Background
    void login(final boolean isSaveedHead) {
        SyncApi.getInstance().login(getLoginInfo(), mDeviceInfo,
                SetUserNameActivity.this, new SyncApi.CallBack<String>() {
                    @Override
                    public void success(String mode) {
                        LogCatLog.e(TAG, "111111 login : mode = " + mode);
                        loginSuccess = true;
                        update(isSaveedHead);
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
        lidto.setLongitude(longitude);
        lidto.setLatitude(latitude);
        SyncApi.getInstance().reportLocation(lidto, SetUserNameActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });

        MainActivity_.intent(SetUserNameActivity.this).start();
        setResult(Activity.RESULT_OK);
        finish();
    }

    public LoginInfo getLoginInfo() {
        LoginInfo baseLoginInfo = new LoginInfo();
        baseLoginInfo.setFigureId(PersonSharePreference.getFigureID());
        baseLoginInfo.setPassword("");
        baseLoginInfo.setClientId("1");
        baseLoginInfo.setClientVersion(DeviceInfoUtil.getVersionName(this));
        return baseLoginInfo;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //保存id和用户昵称
//                register(mETNickName.getText().toString().trim(), imageId);
                login(true);
            }
        }
    };

    @Click(R.id.iv_head)
    void selectHeadPic() {
        Utils.hideSoftKeyboard(mETNickName);

        String take = getString(R.string.takephoto);
        String choose = getString(R.string.choosephoto);
        CommonChooseActivity.show(this, new String[]{take, choose},
                new CommonChooseActivity.OnChooseListener() {
                    @Override
                    public void onChoose(int position) {
                        switch (position) {
                            case 0: // 相机
                                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 下面这句指定调用相机拍照后的照片存储的路径
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, headTempUri);
                                startActivityForResult(intent1, REQUEST_CAMERA);
                                break;
                            case 1: // 相册
                                Crop.pickImage(SetUserNameActivity.this);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            //拍照回来
            beginCrop(headTempUri);
        } else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            //图库
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mIvHead.setImageDrawable(null);
            isSaveedHead = true;
            mIvHead.setImageURI(result != null ? Crop.getOutput(result) : HeadImageUri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            tip(Crop.getError(result).getMessage());
            isSaveedHead = false;
        }
    }

    private void beginCrop(Uri source) {
        int x = getResources().getDimensionPixelSize(R.dimen.head_image_height);
        Crop.of(source, HeadImageUri).withMaxSize(x, x).asSquare().start(this);
    }

    FileMessageListener<FileTask> listener = new FileMessageListener<FileTask>() {
        @Override
        public void success(int statusCode, FileTask t) {
            PersonSharePreference.setUserImgID(t.fileID);
            imageId = t.fileID;
            mHandler.sendEmptyMessage(1);
        }

        @Override
        public void handleing(int statusCode, FileTask t) {
        }

        @Override
        public void failure(int statusCode, FileTask t) {
            tip(R.string.HeadImage_Upload_failed);
        }
    };

    /**
     * 输入内容检查
     */
    private boolean checkData() {

        if (TextUtils.isEmpty(mETNickName.getText().toString().trim())) {
            tip(R.string.set_username_tip);
            return false;
        } else if (!isTrueName(mETNickName.getText().toString().trim())) {
            tip(R.string.set_username_tip2);
            return false;
        } else if (mETNickName.getText().toString().trim().length() > 30) {
            tip(R.string.set_username_tip3);
            return false;
        } else if (mETNickName.getText().toString().trim().length() < 2) {
            tip(R.string.set_username_tip4);
            return false;
        }
        return true;
    }

    // 校验name只能是数字,英文字母和中文
    public static boolean isTrueName(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    @Override
    protected void onDestroy() {
        //todo 清理多余图片
        super.onDestroy();
        loginSuccess = false;
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

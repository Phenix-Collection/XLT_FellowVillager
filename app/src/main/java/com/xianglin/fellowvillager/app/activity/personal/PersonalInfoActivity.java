package com.xianglin.fellowvillager.app.activity.personal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.WebPUtil;
import com.xianglin.fellowvillager.app.utils.event.FrozenEvent;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.dialog.CommonDialog;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * 个人资料  新建角色
 *
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_info)
public class PersonalInfoActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.iv_header)
    CircleImage iv_header;//头像
    @ViewById(R.id.tv_name)
    TextView tv_name;//名称
    @ViewById(R.id.tv_sex)
    TextView tv_sex;//性别
    @ViewById(R.id.tv_figureId)
    TextView tv_figureId;//角色ID
    @ViewById(R.id.tv_describe)
    TextView tv_describe;//个人描述
    @ViewById(R.id.btnFigure)
    Button btnFigure;//冻结或解冻
    @ViewById(R.id.arrow1)
    ImageView mArrow1;
    @ViewById(R.id.arrow2)
    ImageView mArrow2;
    @ViewById(R.id.arrow3)
    ImageView mArrow3;
    @ViewById(R.id.arrow5)
    ImageView mArrow5;
    @ViewById(R.id.rela_header)
    RelativeLayout mRelaHeader;
    @ViewById(R.id.rela_name)
    RelativeLayout mRelaName;
    @ViewById(R.id.rela_sex)
    RelativeLayout mRelaSex;
    @ViewById(R.id.rela_describe)
    RelativeLayout mRelaDescribe;

    public static final int SETNAME = 1;
    public static final int SETSEX = 2;
    public static final int SETDESCRIBE = 3;
    public static final int SETHEADER = 4;
    public static final int RESULT_CODE_OK = 0X100;
    String operateType;

    String figureId;
    String figureImgId;
    FigureMode figureMode;
    /**
     * 头像改变与否
     */
    private boolean mAvatarChange = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tv_figureId.setText(figureId);
                    break;
                case 2:
                    finish();
                    break;
            }
        }
    };

    /**
     * 显示角色信息
     */
    void showFigureInfo() {
        mTopView.setAppTitle(getString(R.string.str_user_info));
        figureId = getIntent().getStringExtra("figureId");
        if (figureId.equals("")) {
            btnFigure.setVisibility(View.VISIBLE);
            figureId = getIntent().getStringExtra("selectFID");
        } else {
            btnFigure.setVisibility(View.GONE);
        }
        LogCatLog.e(TAG, "select figureId=" + figureId);
        figureMode = ContactManager.getInstance().getCurrentFigure(figureId);
        if (figureMode != null) {

            figureImgId = figureMode.getFigureImageid();
            if (TextUtils.isEmpty(figureImgId)) {
                iv_header.setImageResource(R.drawable.head);
            } else {
                ImageUtils.showCommonImage(this, iv_header,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH, figureImgId, R.drawable.head);
            }
            tv_name.setText(figureMode.getFigureName());
            tv_figureId.setText(figureId);
            tv_describe.setText(figureMode.getFigureInfo());

            DataDealUtil.setGender(btnFigure, figureMode.getFigureStatus());
            DataDealUtil.setGender(tv_sex, figureMode.getFigureGender());
        }
        mTopView.getLeftlayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateFigureMode();
            }
        }, 200);

        if (mAvatarChange) {
            setResult(RESULT_CODE_OK);
        }
        super.onBackPressed();

    }

    @Background
    void updateFigureMode() {
        if (BorrowConstants.TYPE_INFO.equals(operateType)) {
            figureMode = ContactManager.getInstance().getCurrentFigure(figureId);
            update(FigureMode.FigureModeToFigureDTO(figureMode), null, null, null, null);
        }
    }

    /**
     * 创建角色
     */
    void showAddFigure() {
        getUnusedFigureIds();
        FileUtils.getInstance().deleteFile(FileUtils.IMG_CACHE_HEADIMAGE_PATH + "temp.jpg");
        mTopView.setAppTitle(getString(R.string.str_add_role));
        mTopView.setRightTextViewText(getString(R.string.str_confirm));
        tv_sex.setText("不明");
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ToastUtils.isFastDoubleClick(1500)) {
                    String name = tv_name.getText().toString().trim();
                    if (name.equals("")) {
                        ToastUtils.toastForShort(context, "请输入正确名称");
                    } else if (name.length() < 2) {
                        ToastUtils.toastForShort(context, "请输入正确名称");
                    } else if (!DeviceInfoUtil.isNetAvailble(context)) {
                        ToastUtils.toastForShort(context, "网络异常,请联网后再试!");
                    } else {
                        showLoadingDialog();
                        if (figureId != null) {
                            String imgPath = FileUtils.IMG_CACHE_HEADIMAGE_PATH + "temp.jpg";
                            if (FileUtils.getInstance().isExists(imgPath)) {

                                Bitmap mImgBmp = ImageUtils.decodeThumbnailsBitmap(imgPath);
                                File saveFile = new File(FileUtils.IMG_CACHE_HEADIMAGE_PATH
                                        + figureId + ".webp");
                                WebPUtil.with(context).imageToWebp(mImgBmp, saveFile);//保存大图

                                FileUtils.uploadFile(context, Long.parseLong(figureId),
                                        FileUtils.IMG_CACHE_HEADIMAGE_PATH + figureId + ".webp",
                                        new FileMessageListener<FileTask>() {
                                            @Override
                                            public void success(int i, FileTask fileTask) {
                                                LogCatLog.e(TAG, "上传成功fileId=" + fileTask.fileID
                                                        + "----" + fileTask.fileName);
                                                figureImgId = fileTask.fileID;
                                                addFigure(tv_name.getText().toString(),
                                                        tv_sex.getText().toString(),
                                                        figureImgId,
                                                        tv_describe.getText().toString());
                                                figureImgId = "";
                                            }

                                            @Override
                                            public void handleing(int i, FileTask fileTask) {
                                            }

                                            @Override
                                            public void failure(int i, FileTask fileTask) {
                                            }
                                        });
                            } else {
                                figureImgId = "";
                                addFigure(tv_name.getText().toString(),
                                        tv_sex.getText().toString(),
                                        figureImgId,
                                        tv_describe.getText().toString());
                            }
                        }
                    }
                }
            }


        });

        btnFigure.setVisibility(View.GONE);
    }

    @AfterViews
    void initView() {
        operateType = getIntent().getStringExtra("operateType");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.getLeftlayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (operateType.equals(BorrowConstants.TYPE_INFO)) {
            showFigureInfo();
            refreshUi();
        } else {
            showAddFigure();
        }
    }

    private void refreshUi() {
        if (figureMode == null) {
            return;
        }
        boolean isFigureActive = (figureMode.getFigureStatus() == FigureMode.Status.ACTIVE);
        mArrow1.setVisibility(isFigureActive ? View.VISIBLE : View.GONE);
        mArrow2.setVisibility(isFigureActive ? View.VISIBLE : View.GONE);
        mArrow3.setVisibility(isFigureActive ? View.VISIBLE : View.GONE);
        mArrow5.setVisibility(isFigureActive ? View.VISIBLE : View.GONE);
        mRelaHeader.setClickable(isFigureActive);
        mRelaName.setClickable(isFigureActive);
        mRelaSex.setClickable(isFigureActive);
        mRelaDescribe.setClickable(isFigureActive);
        btnFigure.setText(isFigureActive ? getString(R.string.str_fix_role)
                : getString(R.string.str_free_role));
        btnFigure.setTextColor(isFigureActive ? getResources().getColor(R.color.red)
                : getResources().getColor(R.color.button_green));
    }

    @Background
    void changeFigureStatus(FigureDTO figureDTO) {
        LogCatLog.e(TAG, "figure status=" + figureMode.getFigureStatus());
        if (figureMode.getFigureStatus() == FigureMode.Status.ACTIVE) {//冻结
            LogCatLog.e(TAG, "响应冻结");
            figureDTO.setStatus(FigureMode.Status.FREEZE.name());
            SyncApi.getInstance().update(figureDTO, PersonalInfoActivity.this,
                    new SyncApi.CallBack() {
                        @Override
                        public void success(Object mode) {
                            figureMode.setFigureStatus(FigureMode.Status.FREEZE);
                            ContactManager.getInstance().freezeFigureTable(figureMode);
                            EventBus.getDefault().post(
                                    new FrozenEvent(figureMode.getFigureUsersid(), true));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshUi();
                                }
                            });
                        }

                        @Override
                        public void failed(String errTip, int errCode) {

                        }
                    });
        } else {//解冻
            figureDTO.setStatus(FigureMode.Status.ACTIVE.name());
            SyncApi.getInstance().update(figureDTO, PersonalInfoActivity.this,
                    new SyncApi.CallBack() {
                        @Override
                        public void success(Object mode) {
                            figureMode.setFigureStatus(FigureMode.Status.ACTIVE);
                            ContactManager.getInstance().addFigureTable(figureMode);
                            EventBus.getDefault().post(
                                    new FrozenEvent(figureMode.getFigureUsersid(), false));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshUi();
                                }
                            });
                        }

                        @Override
                        public void failed(String errTip, int errCode) {

                        }
                    });
        }
    }

    @Click(R.id.btnFigure)
    void figureOperate() {
        figureMode = ContactManager.getInstance().getCurrentFigure(figureId);
        new CommonDialog.Builder(this)
                .setTitle(figureMode.getFigureStatus() == FigureMode.Status.ACTIVE ?
                        "确认要冻结此角色?" : "确认要解冻此角色?")
                .setCancleButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (figureMode != null) {
                            if (figureMode.getFigureStatus() == FigureMode.Status.ACTIVE
                                    && ContactManager.getInstance().getFigureTable().size() == 1) {
                                tip("当前活跃角色只有1个,请先解冻一个角色后再冻结");
                            } else if (figureMode.getFigureStatus() == FigureMode.Status.FREEZE
                                    && ContactManager.getInstance().getFigureTable().size() == 5) {
                                tip("当前活跃角色已超过5个,请冻结角色后再解冻");
                            } else {
                                FigureDTO figureDTO = FigureMode.FigureModeToFigureDTO(figureMode);
                                changeFigureStatus(figureDTO);
                            }
                            dialog.cancel();
                        }
                    }
                }).create().show();
    }

    @Background
    void getUnusedFigureIds() {
        SyncApi.getInstance().getUnusedFigureIds(1, PersonalInfoActivity.this,
                new SyncApi.CallBack<List<String>>() {
                    @Override
                    public void success(List<String> mode) {
                        figureId = mode.get(0);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    @Background
    void addFigure(final String title, final String sex,
                   final String figureImgId, final String description) {
        SyncApi.getInstance().create(figureId, true, PersonalInfoActivity.this,
                new SyncApi.CallBack<FigureDTO>() {
                    @Override
                    public void success(FigureDTO mode) {
                        String mGender = sex;
                        LogCatLog.e(TAG, "111111 create success mode = " + mode);
                        if ("男".equals(mGender)) {
                            mGender = FigureMode.FigureGender.MALE.name();
                        } else if ("女".equals(mGender)) {
                            mGender = FigureMode.FigureGender.FEMALE.name();
                        } else if ("不公开".equals(mGender)) {
                            mGender = FigureMode.FigureGender.PRIVATE.name();
                        } else {
                            mGender = FigureMode.FigureGender.UNKNOWN.name();
                        }
                        if (mode != null) {
                            mode.setFigureId(figureId);
                            mode.setNickName(title);
                            mode.setGender(mGender);
                            mode.setAvatarUrl(figureImgId);
                            mode.setIndividualitySignature(description);
                            mode.setStatus("ACTIVE");

                            update(mode, title, mGender, figureImgId, description);
                            hideLoadingDialog();
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    void update(FigureDTO figureDTO, final String title, final String gender,
                final String figureImgId, final String description) {

        SyncApi.getInstance().update(figureDTO, PersonalInfoActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        if(title != null)
                        saveFigurMode(title, gender, figureImgId, description);
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                }
        );
    }

    void saveFigurMode(String title, String sex, String figureImgId, String description) {
        if (figureId == null) return;

        FigureMode fm = createFigure(figureId, title, sex, figureImgId, description);
        ContactManager.getInstance().addFigureTable(fm);

        ContactManager.getInstance().switchCurrentUserFigure(fm);
        setResult(RESULT_CODE_OK);
        finish();
    }

    FigureMode createFigure(String figureId, String title, String sex,
                            String figureImgId, String description) {

        FigureMode figureMode = new FigureMode();

        figureMode.setXlId(PersonSharePreference.getUserID() + "");
        figureMode.setCreateDate(System.currentTimeMillis());
        figureMode.setUpdateDate(System.currentTimeMillis());
        figureMode.setFigureGroup("");
        figureMode.setFigureInfo(description);
        figureMode.setFigureName(title);
        figureMode.setFigureStatus(FigureMode.Status.ACTIVE);
        figureMode.setFigureUsersid(figureId);
        figureMode.setFigureImageid(figureImgId);
        figureMode.setFigureGender(FigureMode.FigureGender.valueOf(sex));
        figureMode.setSexualOrientation(FigureMode.SexualOrientation.UNKNOWN);
        figureMode.setFigureRelationship(System.currentTimeMillis() + "");
        figureMode.setImagePathThumbnail(System.currentTimeMillis() + "");
        figureMode.setImagePpath("");
        figureMode.setFigureXlremarks("");
        figureMode.setUpdateDate(System.currentTimeMillis());
        figureMode.setFigure_usersid_shortid(Utils.getUniqueMessageForFigureidId());
        figureMode.setIsOpen("1");
        return figureMode;

    }

    @Click(R.id.rela_header)
    void setHeader() {
        Intent intent = new Intent(context, HeaderSetActivity_.class)
                .putExtra("operateType", operateType).putExtra("figureId", figureId);
        startActivityForResult(intent, SETHEADER);
    }

    @Click(R.id.rela_name)
    void setName() {
        Intent intent = new Intent(context, PersonalInfoEditActivity_.class)
                .putExtra("roleType", "").putExtra("roleId", "")
                .putExtra("operation", "setName")
                .putExtra("name", tv_name.getText().toString());
        startActivityForResult(intent, SETNAME);
    }

    @Click(R.id.rela_sex)
    void setSex() {
        Intent intent = new Intent(context, SexSelectActivity_.class)
                .putExtra("operateType", operateType).putExtra("roleId", "")
                .putExtra("sex", tv_sex.getText().toString());
        startActivityForResult(intent, SETSEX);
    }

    @Click(R.id.rela_describe)
    void setDescription() {
        Intent intent = new Intent(context, PersonalInfoEditActivity_.class)
                .putExtra("roleType", "").putExtra("roleId", "")
                .putExtra("operation", "setDescription")
                .putExtra("description", tv_describe.getText().toString());
        startActivityForResult(intent, SETDESCRIBE);
    }

    private FigureMode.FigureGender getSex(String sex) {
        if (sex.equals("男")) {
            return FigureMode.FigureGender.MALE;
        } else if (sex.equals("女")) {
            return FigureMode.FigureGender.FEMALE;
        } else if (sex.equals("不公开")) {
            return FigureMode.FigureGender.PRIVATE;
        } else {
            return FigureMode.FigureGender.UNKNOWN;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogCatLog.e(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
        if (requestCode == SETNAME && resultCode == Activity.RESULT_OK) {

            tv_name.setText(data.getStringExtra("name"));
            if (BorrowConstants.TYPE_INFO.equals(operateType)) {
                figureMode.setFigureName(data.getStringExtra("name"));
                ContactManager.getInstance().addFigureTable(figureMode);
            }
        } else if (requestCode == SETSEX && resultCode == Activity.RESULT_OK) {

            tv_sex.setText(data.getStringExtra("sex"));
            if (BorrowConstants.TYPE_INFO.equals(operateType)) {
                figureMode.setFigureGender(getSex(data.getStringExtra("sex")));
                ContactManager.getInstance().addFigureTable(figureMode);
            }
        } else if (requestCode == SETDESCRIBE && resultCode == Activity.RESULT_OK) {

            tv_describe.setText(data.getStringExtra("description"));
            if (BorrowConstants.TYPE_INFO.equals(operateType)) {
                figureMode.setFigureInfo(data.getStringExtra("description"));
                ContactManager.getInstance().addFigureTable(figureMode);
            }
        } else if (requestCode == SETHEADER) {
            if (BorrowConstants.TYPE_INFO.equals(operateType)) { //修改头像
                mAvatarChange = true;
                figureImgId = ContactManager.getInstance()
                        .getCurrentFigure(figureId).getFigureImageid();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImageUtils.showCommonImage(PersonalInfoActivity.this, iv_header,
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH, figureImgId, R.drawable.head);
                    }
                }, 150);

            } else { // 新建角色头像
                iv_header.setImageBitmap(
                        ImageUtils.decodeThumbnailsBitmap(
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH + "temp.jpg"));
            }
        }
    }
}

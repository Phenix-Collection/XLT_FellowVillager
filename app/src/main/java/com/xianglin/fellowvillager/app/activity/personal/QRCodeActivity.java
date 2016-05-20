package com.xianglin.fellowvillager.app.activity.personal;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.QRGen;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 个人二维码
 *
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_qrcode)
public class QRCodeActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.iv_person_header)
    CircleImage mPersonHeader;
    @ViewById(R.id.tv_person_name)
    TextView mPersonName;  //用户名
    @ViewById(R.id.tv_person_number)
    TextView mPersonNumber;//角色id
    @ViewById(R.id.tv_person_nickname)
    TextView mPersonNickname;//昵称
    @ViewById(R.id.qr_code)
    ImageView mQrCode;  //二维码图片
    @ViewById(R.id.tv_code_tips)
    TextView mCodeTips;  //提示信息

    @Extra
    FigureMode currentUser;

    private int DELYED = 15 * 60000;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                handler.postDelayed(this, DELYED);
                getQrCodeToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @AfterViews
    void initView() {
        mTopView.setAppTitle(R.string.my_qrcode);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();

        if (currentUser == null) return;
        ImageUtils.showCommonImage(this, mPersonHeader,
                FileUtils.IMG_CACHE_HEADIMAGE_PATH, currentUser.getFigureImageid(), R.drawable.head);
        mPersonName.setText(currentUser.getFigureName());
        currentUser.getFigureGender();
        mPersonNumber.setText("乡邻号:" + currentUser.getFigureUsersid());
        mPersonNickname.setVisibility(View.GONE);
        DataDealUtil.showGenderImg(context, mPersonName, currentUser);
        String tip = getResources().getString(R.string.qrcode_valid_time_tip);
        mCodeTips.setText(String.format(tip, DELYED / 60000));
        getQrCodeToken();
        handler.postDelayed(runnable, DELYED);
    }

    @Background
    void getQrCodeToken() {
        SyncApi.getInstance().getUserToken(currentUser.getFigureUsersid(), QRCodeActivity.this,
                new SyncApi.CallBack<String>() {
                    @Override
                    public void success(String mode) {
                        final String qrCodeToken = mode;
                        LogCatLog.e(TAG, "111111 qrCodeToken = " + qrCodeToken);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mQrCode.setImageBitmap(QRGen.creatQRGen("xl://0&" + qrCodeToken, 300));
                            }
                        });
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }
}

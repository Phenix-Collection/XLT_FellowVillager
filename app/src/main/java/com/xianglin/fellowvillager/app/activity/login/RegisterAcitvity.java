/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity.login;


import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.xianglin.cif.common.service.facade.model.FigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.XlidListAdapter;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi.CallBack;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.JpushUtil;
import com.xianglin.fellowvillager.app.widget.dialog.LoadingDialog;
import com.xianglin.fellowvillager.app.utils.SystemBarTintManager;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.vo.RandomXlidVo;
import com.xianglin.xlappcore.common.service.facade.vo.XlidVo;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 注册第一步->选择乡邻ID
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/13 17:34  XLXZ Exp $
 */
//@Fullscreen //全屏
@EActivity(R.layout.activity_rigster)
public class RegisterAcitvity extends BaseActivity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.lv_xlid)
    ListView mXLidListView;

    @ViewById(R.id.btn_sure)
    Button sureXLID;

    @ViewById(R.id.view_status_bar)
    View view;
    private final int REQUEST_CODE = 111;
    private LoadingDialog mLoadingDialog;
    private String tempXlid;//供服务端恢复原来未选择的xlid

    private XlidListAdapter mXlidListAdapter;
    private List<RandomXlidVo> mData = new ArrayList<RandomXlidVo>();
    private LoadingDialog loadingDialog;

    @AfterInject
    void init() {
        JpushUtil.setAlias(getApplicationContext(),"");
    }

    //注解完成执行
    @AfterViews
    void initView() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.app_title_bg);
        } else {
            view.setVisibility(View.GONE);
        }


        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);

        mXlidListAdapter = new XlidListAdapter(mData, this);
        mXlidListAdapter.setXIselectedWithPosition(0);
        mXLidListView.setAdapter(mXlidListAdapter);
        mXLidListView.setOnItemClickListener(RegisterAcitvity.this);

        DeviceInfoUtil.setListViewHeight(mXLidListView);

        loadingDialog.show();
        getData();
    }

    /**
     * 随机 获取三个 乡邻ID
     */

    public void preRegister() {
        btnToGrey();
        loadingDialog.show();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Background
    void getData() {
//        SyncApi.getInstance().register(figureId, "", RegisterAcitvity.this,
//                new SyncApi.CallBack<FigureDTO>() {
//                    @Override
//                    public void success(FigureDTO mode) {
//                        mFigureDTO = mode;
//                        LogCatLog.e(TAG, "111111 register mFigureDTO = " + mFigureDTO);
//                        SetUserNameActivity_.intent(RegisterAcitvity.this)
//                                .mDeviceInfo(baseDeviceInfo)
//                                .mDeviceId(deviceId)
//                                .mFigureId(figureId)
//                                .mFigureDTO(mFigureDTO).start();
//                        finish();
//                    }
//
//                    @Override
//                    public void failed(String errTip, int errCode) {
//                        tip(errTip);
//                    }
//                }
//        );
    }


    @UiThread
    void setData(XlidVo registerXLID, String msg) {
        loadingDialog.dismiss();
        if (msg == null) {
            mXLidListView.setEnabled(true);
            if (registerXLID != null && registerXLID.getXlidList() != null && registerXLID.getXlidList().size() > 0) {
                tempXlid = registerXLID.getTempXlid() + "";

                if (mData.size() == 3) {
                    mData.clear();
                    mData.addAll(registerXLID.getXlidList());
                    mXlidListAdapter.notifyDataSetChanged();
                } else {
                    mData.addAll(registerXLID.getXlidList());
                    mXlidListAdapter.setNewData(registerXLID.getXlidList());
                }
            } else if (registerXLID == null) {
                btnToGrey();
                mXlidListAdapter.setXIselectedWithPosition(-1);
                mXlidListAdapter.setNewData(null);
            }
        } else {
            btnToGrey();
            if (!b) {
                mXlidListAdapter.setXIselectedWithPosition(-1);
                mXLidListView.setAdapter(mXlidListAdapter);
                b = true;
            }
            mXLidListView.setEnabled(false);
            tip(msg);
        }
    }

    boolean b = false;

    private boolean mItemClickFlag = true, mRefreshFlag = true;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        if (!ToastUtils.isFastDoubleClick(1000) && mItemClickFlag) {

            mRefreshFlag = false;
            mDelayHandler.sendEmptyMessageDelayed(1, 1000);
            XlidListAdapter adapter = (XlidListAdapter) parent.getAdapter();

            if (adapter.getItem(position).getXLID() == 0) {
                adapter.init();
                preRegister();
            } else {
                adapter.setXIselectedWithPosition(position);
                adapter.notifyDataSetChanged();
                btnToClick();
            }
        }


    }

    @Click(R.id.btn_sure)
    void sureXLID() {
        XlidListAdapter adapter = (XlidListAdapter) mXLidListView.getAdapter();

        if (adapter.getXlidSelected() != null && adapter.getXlidSelected().getXLID() != 0) {
            //去设置用户名
//            SetUserNameActivity_.intent(this).
//                    mXlid(adapter.getXlidSelected().
//                            getXLID() + "").mTempXlid(tempXlid).
//                    startForResult(REQUEST_CODE);

        } else {
            tip(R.id.rigsteracitvity_no_select_xlid);
        }

    }

    /**
     * 刷新一组乡邻id
     */
    @Click(R.id.tv_refresh)
    void onRefreshXLID() {
        b = true;
        if (!ToastUtils.isFastDoubleClick(1000) && mRefreshFlag) {

            mItemClickFlag = false;
            mDelayHandler.sendEmptyMessageDelayed(2, 1000);
            if (!mXlidListAdapter.nextData()) {
                preRegister();
            } else {
                btnToGrey();
            }
        }
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode) {
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        finish(); //下一步成功
    }

    Handler mDelayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRefreshFlag = true;
                    break;
                case 2:
                    mItemClickFlag = true;
                    break;
                default:

                    break;
            }


        }
    };

    /**
     * 按钮置灰
     */
    void btnToGrey() {
        sureXLID.setEnabled(false);
        sureXLID.setBackgroundResource(R.drawable.btn_circle_togrey);
        sureXLID.setTextColor(getResources().getColor(R.color.text_rig_btn));
    }

    /**
     * 按钮正常
     */
    void btnToClick() {
        sureXLID.setEnabled(true);
        sureXLID.setBackgroundResource(R.drawable.btn_circle_green);
        sureXLID.setTextColor(getResources().getColor(R.color.btn_commen_selector));
    }
}

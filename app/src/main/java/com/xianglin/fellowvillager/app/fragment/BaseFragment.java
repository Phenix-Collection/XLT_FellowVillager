package com.xianglin.fellowvillager.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xianglin.fellowvillager.app.activity.BaseActivity;

/**
 * Created by zhanglisan on 2016/2/23.
 */
public class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected Activity mActivity;
    protected BaseActivity mBaseActivity;
    protected Context mContext;
    protected Resources mRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
        mBaseActivity = mActivity instanceof BaseActivity
                        ?
                        (BaseActivity)mActivity
                        :
                        null;
        if (mBaseActivity != null) {
            mRes = mBaseActivity.getResources();
        } else if (mActivity != null) {
            mRes = mActivity.getResources();
        }
    }

    /**
     * 自定义吐司
     *
     * @param msg
     */
    public void tip(final String msg) {
        mBaseActivity.tip(msg);
    }

    /**
     * 自定义吐司
     *
     * @param resId
     */
    protected void tip(final int resId) {
        mBaseActivity.tip(resId);
    }


    /**
     * 显示加载框
     */
    protected void showLoadingDialog() {
        mBaseActivity.showLoadingDialog();
    }

    /**
     * 关闭加载框
     */
    protected void hideLoadingDialog() {
        mBaseActivity.hideLoadingDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

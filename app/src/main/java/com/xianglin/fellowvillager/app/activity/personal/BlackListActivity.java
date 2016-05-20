package com.xianglin.fellowvillager.app.activity.personal;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.fragment.BlackListFragment;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单
 *
 * @author bruce yang
 * @version v 1.0.0 2016/3/17
 */
@EActivity(R.layout.activity_black_list)
public class BlackListActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.tab_user)
    RelativeLayout mTabUser;// 用户
    @ViewById(R.id.tab_group)
    RelativeLayout mTabGroup;//群
    @ViewById(R.id.txt_user)
    TextView mTxtUser;
    @ViewById(R.id.txt_group)
    TextView mTxtGroup;
    @ViewById(R.id.user_liner)
    ImageView mIvUser;
    @ViewById(R.id.group_liner)
    ImageView mIvGroup;

    @Extra
    String currentUserId;

    private FragmentManager fragmentManager;
    private Fragment userFragment, groupFragment;
    private int index = 1;

    @AfterViews
    void initView() {
        mTopView.setAppTitle(R.string.str_blacklist);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();

        mTabUser.setOnClickListener(this);
        mTabGroup.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();

        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        userFragment = new BlackListFragment();
        Bundle args = new Bundle();
        args.putInt("blacklist_type", 0);
        args.putString("current_figure", currentUserId);
        userFragment.setArguments(args);
        transaction.replace(R.id.content_layout, userFragment);
        transaction.commit();
    }


    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!newFragment.isAdded()) {
            transaction.replace(R.id.content_layout, newFragment);
            transaction.commit();
        } else {
            transaction.show(newFragment);
        }
    }

    private void clearStatus() {
        if (index == 1) {
            mTxtUser.setTextColor(Color.BLACK);
            mIvUser.setVisibility(View.GONE);
        } else if (index == 2) {
            mTxtGroup.setTextColor(Color.BLACK);
            mIvGroup.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        clearStatus();
        switch (v.getId()) {
            case R.id.tab_user:
                if (userFragment == null) {
                    userFragment = new BlackListFragment();
                    Bundle args = new Bundle();
                    args.putInt("blacklist_type", 0);
                    args.putString("current_figure", currentUserId);
                    userFragment.setArguments(args);
                }
                replaceFragment(userFragment);
                mTxtUser.setTextColor(getResources().getColor(R.color.button_green));
                mIvUser.setVisibility(View.VISIBLE);
                index = 1;
                break;
            case R.id.tab_group:
                if (groupFragment == null) {
                    groupFragment = new BlackListFragment();
                    Bundle args = new Bundle();
                    args.putInt("blacklist_type", 1);
                    args.putString("current_figure", currentUserId);
                    groupFragment.setArguments(args);
                }
                replaceFragment(groupFragment);
                mTxtGroup.setTextColor(getResources().getColor(R.color.button_green));
                mIvGroup.setVisibility(View.VISIBLE);
                index = 2;
                break;
        }
    }

}

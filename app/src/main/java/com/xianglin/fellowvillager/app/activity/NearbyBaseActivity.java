/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.fragment.NearbyListFragment_;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 附近联系人,使用fragment方便以后扩展地图展示
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/18 13:30  XLXZ Exp $
 */
@EActivity(R.layout.activity_nearby_base)
public class NearbyBaseActivity extends BaseActivity {
    @ViewById(R.id.topview)
    TopView mTopView;
    //注解完成执行
    @AfterViews
    void initView() {

        mTopView.initCommonTop(R.string.activity_nearby_title);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)//replace()
                    .commit();
        }
    }

    protected Fragment createFragment() {
        return new  NearbyListFragment_();
    }
}

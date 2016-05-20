package com.xianglin.fellowvillager.app.activity.personal;

import android.view.View;
import android.widget.LinearLayout;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.activity.personal.BlackListActivity_;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 个人设置
 *
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_setting)
public class SettingActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.ll_blacklist)
    LinearLayout mBlackList;

    @Extra
    FigureMode currentUser;

    @AfterViews
    void initViiew() {
        mTopView.setAppTitle(R.string.settings);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        mBlackList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlackListActivity_.intent(SettingActivity.this)
                        .currentUserId(currentUser.getFigureUsersid()).start();
            }
        });
    }

}

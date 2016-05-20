package com.xianglin.fellowvillager.app.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.xianglin.cif.common.service.facade.model.enums.Contact;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.fragment.MainContactFragment_;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 选择好友名片
 * Created by ex-zhangxiang on 2016/1/6.
 */
@EActivity(R.layout.activity_select_business_card)
public class SelectBusinessCard extends BaseActivity{
    @ViewById(R.id.topview)
    TopView mTopView;

    public static final String ISCARD = "iscard";
    @Extra
    String name;
    //注解完成执行
    @AfterViews
    void initView() {

        initTop();

        LogCatLog.i(TAG, "---------------------------"+name);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentBusinessCard);
        if (fragment == null) {
            fragment = createFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ISCARD, "true");
            bundle.putString(NewCardActivity.KEYNAME, name);
            fragment.setArguments(bundle);
            manager.beginTransaction()
                    .add(R.id.fragmentBusinessCard, fragment)//replace()
                    .commit();
        }
    }

    /**
     * 初始化topView
     */
    private void initTop(){
        mTopView.initCommonTop(R.string.activity_bsncard_title);
        mTopView.setRightTextViewText(R.string.cancel);
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.head_text_color);
        if (csl != null) {
            mTopView.getRightTextView().setTextColor(csl);
        };
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    protected Fragment createFragment() {
        return new MainContactFragment_();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewCardActivity.REQUSET_BUSINESS_CARD && resultCode == RESULT_OK) {
            Contact businessCardBean = (Contact) data.getSerializableExtra(NewCardActivity.KEYID);
            LogCatLog.i(TAG, "-----------keyid-----------" + businessCardBean.toString());

            Intent intent = new Intent();
            intent.putExtra(NewCardActivity.KEYID, data.getSerializableExtra(NewCardActivity.KEYID));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

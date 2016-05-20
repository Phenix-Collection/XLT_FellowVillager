package com.xianglin.fellowvillager.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 新建卡片
 * Created by ex-zhangxiang on 2016/1/7.
 */
@EActivity(R.layout.activity_new_card)
public class NewCardActivity extends BaseActivity {
    @ViewById(R.id.ll_new_card)
    LinearLayout businessCard;
    @ViewById(R.id.topview)
    TopView mTopView;

    Context mContext;

    public static final int REQUSET_BUSINESS_CARD = 1;
    public static final String KEYNAME = "key_name";
    public static final String KEYID = "key_id";
    @Extra
    String name;

    @AfterViews
    void initView() {

        mContext = this;
        initTop();

        LogCatLog.i(TAG, "----------------------------" + name);
        businessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectBusinessCard_.intent(NewCardActivity.this).name(name).startForResult(REQUSET_BUSINESS_CARD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSET_BUSINESS_CARD && resultCode == RESULT_OK) {
            Contact businessCardBean = (Contact) data.getSerializableExtra(KEYID);
            LogCatLog.i(TAG, "-----------keyid-----------" + businessCardBean.toString());

            Intent intent = new Intent();
            intent.putExtra(KEYID, data.getSerializableExtra(KEYID));
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 初始化topView
     */
    private void initTop() {
        mTopView.initCommonTop(R.string.activity_newcard_title);
        mTopView.setRightTextViewText(R.string.cancel);
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.head_text_color);
        if (csl != null) {
            mTopView.getRightTextView().setTextColor(csl);
        }
        ;
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }
}

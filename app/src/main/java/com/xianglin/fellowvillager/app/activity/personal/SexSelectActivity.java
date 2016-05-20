package com.xianglin.fellowvillager.app.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 
 * 个人头像
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_edt_sex)
public class SexSelectActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏

    @ViewById(R.id.tv_unknow)
    TextView tv_unknow;
    @ViewById(R.id.tv_man)
    TextView tv_man;
    @ViewById(R.id.tv_woman)
    TextView tv_woman;

    String operateType;

    @AfterViews
    void initViiew(){
        mTopView.setAppTitle("性别");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        operateType=getIntent().getStringExtra("operateType");
        String sex=getIntent().getStringExtra("sex");
        setDefault(sex,false);

    }

    void setDefault(String sex,boolean isfinish){
        tv_unknow.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        tv_man.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        tv_woman.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        if(sex.equals(getString(R.string.str_unopen))){
            tv_unknow.setCompoundDrawablesWithIntrinsicBounds(null,null,
                    getResources().getDrawable(R.drawable.sex_select_icon),null);
            if(isfinish)saveSex(getString(R.string.str_unopen));
        }else if(sex.equals(getString(R.string.str_man))){
            tv_man.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.sex_select_icon), null);
            if(isfinish)saveSex(getString(R.string.str_man));
        }else if(sex.equals(getString(R.string.str_woman))){
            tv_woman.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sex_select_icon), null);
            if(isfinish)saveSex(getString(R.string.str_woman));
        }else{
            if(isfinish)saveSex(getString(R.string.str_unknow));
        }
    }

    void saveSex(String sex){
        Intent intent=new Intent(context,PersonalInfoActivity_.class);
        intent.putExtra("sex",sex);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
    @Click(R.id.rela_unknow)
    void click_unknow(){
        setDefault(getString(R.string.str_unopen),true);

    }

    @Click(R.id.rela_man)
    void click_man(){
        setDefault(getString(R.string.str_man),true);


    }

    @Click(R.id.rela_woman)
    void click_woman(){
        setDefault(getString(R.string.str_woman),true);

    }



}

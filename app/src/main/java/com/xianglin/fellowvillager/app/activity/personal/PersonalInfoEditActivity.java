package com.xianglin.fellowvillager.app.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 
 * 信息编辑页
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_edt_name)
public class PersonalInfoEditActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.tv_name)
    TextView tv_name;

    @ViewById(R.id.et_name)
    EditText ed_name;

    @ViewById(R.id.tv_num)
    TextView tv_num;

    String operation;
    private int etLength=30;

    private void editName(){
        mTopView.setAppTitle("名称");
        ViewGroup.LayoutParams params=ed_name.getLayoutParams();
        params.height= DeviceInfoUtil.dip2px(50);;
        ed_name.setLayoutParams(params);
        tv_name.setText("名称");
        tv_num.setVisibility(View.GONE);
        ed_name.setText(getIntent().getStringExtra("name"));
        ed_name.setGravity(Gravity.CENTER_VERTICAL);
        ed_name.setSingleLine(true);
    }

    private void editDescription(){
        mTopView.setAppTitle("个人描述");
        tv_name.setVisibility(View.GONE);
        tv_num.setVisibility(View.VISIBLE);
        ed_name.setText(getIntent().getStringExtra("description"));
        ViewGroup.LayoutParams params=ed_name.getLayoutParams();
        params.height= DeviceInfoUtil.dip2px(120);
        ed_name.setLayoutParams(params);
        ed_name.setGravity(Gravity.LEFT | Gravity.TOP);
        ed_name.setSingleLine(false);
        tv_num.setText((etLength - ed_name.getText().length()) + "");
    }

    @AfterViews
    void initViiew(){
        operation=getIntent().getStringExtra("operation");
        if(operation.equals("setName")){
            editName();
        }else if(operation.equals("setDescription")){
            editDescription();
        }

        ed_name.setSelection(ed_name.getEditableText().length());
        ed_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etLength - s.length()>=0) {
                    tv_num.setText((etLength - s.length()) + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    mTopView.getRightTextView().setTextColor(getResources().getColor(R.color.white));
                    mTopView.getRightTextView().setEnabled(true);
                }else{
                    mTopView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
                    mTopView.getRightTextView().setEnabled(false);
                }
            }
        });

        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        mTopView.setRightTextViewText("完成");
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFinish();
            }
        });

    }

    void clickFinish(){
        Intent intent;
        if(operation.equals("setName")){
            intent=new Intent(context,PersonalInfoActivity_.class);
            intent.putExtra("name", ed_name.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            setResult( Activity.RESULT_OK,intent);
        }else if(operation.equals("setDescription")){
            intent=new Intent(context,PersonalInfoActivity_.class);
            intent.putExtra("description",ed_name.getText().toString());
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    };



}

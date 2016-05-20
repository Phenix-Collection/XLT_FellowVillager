package com.xianglin.fellowvillager.app.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 
 * 他人主页设置备注
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_edt_name)
public class UserEditRemarkActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.tv_name)
    TextView tv_name;

    @ViewById(R.id.et_name)
    EditText et_name;
    @Extra
    String titleName;
    @Extra
    String toChatId;
    @Extra
    String contactId;
    Contact contact;

    @AfterViews
    void initViiew(){
        mTopView.setAppTitle("备注信息");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        mTopView.setRightTextViewText("完成");
        contact=ContactManager.getInstance().getContact(contactId);
        if(contact==null){
            return;
        }
        mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact.setXlReMarks(et_name.getText().toString());
                ContactManager.getInstance().addContactInternal(contact);
                updateRemarkName();

            }
        });
        tv_name.setText("备注");
        et_name.setText(contact.xlReMarks);
        if(et_name.getEditableText().length()==0){
            mTopView.getRightTextView().setEnabled(false);
            mTopView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
        }else{
            et_name.setSelection(et_name.getEditableText().length());
        }
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mTopView.getRightTextView().setTextColor(getResources().getColor(R.color.white));
                    mTopView.getRightTextView().setEnabled(true);
                }/* else {
                    mTopView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
                    mTopView.getRightTextView().setEnabled(false);
                }*/
            }
        });
    }

    @Background
    void updateRemarkName() {
        SyncApi.getInstance().update(
                ContactManager.getInstance().getCurrentFigureID(),
                contact.xlUserID,
                contact.figureUsersId,
                et_name.getText().toString(),
                UserEditRemarkActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        finish();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                        finish();
                    }
                }
        );
    }


}

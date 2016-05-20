package com.xianglin.fellowvillager.app.activity;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * 
 * 资料设置
 * @author chengshengli
 * @version v 1.0.0 2016/2/24 11:52 XLXZ Exp $
 */
@EActivity(R.layout.activity_user_info_edt)
public class UserInfoEditActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.img_black)
    ImageView img_black;
    @ViewById(R.id.tv_black)
    TextView tv_black;
    @Extra
    String titleName;

    String toChatId;
    String contactId;
    Contact contact;
    @AfterViews
    void initViiew(){
        mTopView.setAppTitle("资料设置");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        toChatId=getIntent().getStringExtra("toChatId");
        contactId=getIntent().getStringExtra("contactId");
        contact=ContactManager.getInstance().getContact(contactId);
        if(contact.contactLevel== Contact.ContactLevel.BLACK){
            img_black.setImageResource(R.drawable.radio_checked_btn);
            tv_black.setText("移出黑名单");
        }else{
            img_black.setImageResource(R.drawable.radio_unchecked_btn);
            tv_black.setText("加入黑名单");
        }
    }
    @Click(R.id.rela_remark)
    void remark_click(){
        UserEditRemarkActivity_.intent(context)
                .toChatId(toChatId)
                .contactId(contactId)
                .start();
    }

    @Click(R.id.img_black)
    void black_click(){
        moveToBlack(contact);
    }

    @Background
    void moveToBlack(final Contact contact){
        String figureId=contact.figureId;
        if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())){
            figureId="";
        }
        if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
            tip("陌生人不能拉黑");
            return;
        }
        if(contact.contactLevel== Contact.ContactLevel.BLACK){
            SyncApi.getInstance().moveOutofBlacklist(figureId, contact.xlUserID,
                    contact.figureUsersId, context, new SyncApi.CallBack(){

                        @Override
                        public void success(Object mode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    contact.contactLevel = Contact.ContactLevel.NORMAL;
                                    ContactManager.getInstance().addContactInternal(contact);
                                    img_black.setImageResource(R.drawable.radio_unchecked_btn);
                                    tv_black.setText("加入黑名单");
                                }
                            });

                        }

                        @Override
                        public void failed(String errTip, int errCode) {

                        }
                    });
        }else{
            SyncApi.getInstance().moveIntoBlacklist(figureId, contact.xlUserID,
                    contact.figureUsersId, context, new SyncApi.CallBack() {
                        @Override
                        public void success(Object mode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //ContactManager.getInstance().deleteContactInternal(contact.contactId);
                                    contact.contactLevel=Contact.ContactLevel.BLACK;
                                    ArrayList<FigureMode> figureModes = contact.figureGroup;
                                    for (int i = 0; i < figureModes.size(); i++) {
                                        Contact delContact = ContactManager.getInstance().getContact(ContactDBHandler
                                                .getContactId(contact.figureUsersId, figureModes.get(i).getFigureUsersid()));
                                        ContactManager.getInstance().deleteContactInternal(delContact.contactId);
                                        img_black.setImageResource(R.drawable.radio_checked_btn);
                                        tv_black.setText("移出黑名单");
                                    }
                                }
                            });

                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip("拉黑失败:" + errTip);
                        }
                    });
        }

    }


}

package com.xianglin.fellowvillager.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fima.cardsui.objects.GoodCard;
import com.fima.cardsui.views.CardUI;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.ChatSecretSetActivity_;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.loader.CardLoader;
import com.xianglin.fellowvillager.app.loader.SQLiteCursorLoader;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.SingleThreadExecutor;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 个人（左下角“全部”）：全部名片列表 点击条目：弹出对话框确认是否发送 发送给谁----》对方（名片 ok ）
 群  （左下角“全部”）：全部名片列表 点击条目：弹出对话框确认是否发送 发送给谁----》对方（名片 ok ）

 对话管理：1、名片----》去对话 ok 2、链接----》 webview 3、历史记录----》（名片 ok ）
 群管理  ：1、名片----》去对话 ok 2、链接----》 webview 3、历史记录----》（名片 ok ）

 个人对话中  ：1、名片----》去对话 ok  2、链接----》webview
 群对话中    ：1、名片----》去对话 ok  2、链接----》webview

 此Activity是个人对话管理
 */
@EActivity(R.layout.activity_person_detail)
public class PersonDetailActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏
    @ViewById(R.id.iv_person_header)
    CircleImage person_header_iv;// 头像
    @ViewById(R.id.tv_person_name)
    TextView person_name_tv;// 用户名
    @ViewById(R.id.tv_person_number)
    TextView person_number_tv;// 用户FigureId
    @ViewById(R.id.ll_history_record)
    LinearLayout history_record_ll;// 历史记录
    @ViewById(R.id.sv_codes)
    ScrollView codes_sv;// 放卡片的
    @ViewById(R.id.cardsview)
    CardUI cardsview;// 卡片View

    @ViewById(R.id.rl_secret_set)
    RelativeLayout rl_secret_set;

    @ViewById(R.id.tv_secret_time)
    TextView tv_secret_time;
    @ViewById(R.id.arrow)
    ImageView arrow;

    @Extra
    String headerImgId;
    @Extra
    String toChatName;
    @Extra
    String toChatId;
	@Extra
    String currentFigureId;
    @Extra
    String figureId;//对方角色id

    CardLoader mCardLoader;
    List<MessageBean> mMessageBeanList;

    private static final int GOTOSPECIFICVISITINGCODEACTIVITY = 0;

    // 首先执行
    @AfterInject
    public void init(){
        mCardLoader = new CardLoader(this, toChatId, BorrowConstants.CHATTYPE_SINGLE, 0, 5, true, true);
        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(0, getIntent().getExtras(), new CardListCallbacks());
    }

    // 接着执行
    @AfterViews
    public void initView(){
        // 初始化标题栏
        mTopView.setAppTitle("详情");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();

        // 头像
        ImageUtils.showCommonImage(this, person_header_iv, FileUtils.IMG_CACHE_HEADIMAGE_PATH, headerImgId, R.drawable.head);
        // 用户名
        person_name_tv.setText(toChatName);
        // 乡邻号
        Contact contact = ContactManager.getInstance().getContact(toChatId);
        if (contact == null) {
            person_number_tv.setText("figure id： "+figureId);
            rl_secret_set.setClickable(false);
            arrow.setVisibility(View.GONE);
        } else {
            person_number_tv.setText("figure id： " + contact.figureUsersId);
            if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
                rl_secret_set.setClickable(false);
                arrow.setVisibility(View.GONE);
            }
        }
    }

    private boolean flag = true;
    private class CardListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mCardLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            SQLiteCursorLoader sqLiteCursorLoader= (SQLiteCursorLoader) loader;
            data.unregisterContentObserver(sqLiteCursorLoader.getObserver());

            CardDBHandler.MessageCursor messageCursor=new CardDBHandler.MessageCursor(data);
            mMessageBeanList = messageCursor.getMessageBeanList();
            if(mMessageBeanList == null || mMessageBeanList.size() == 0){
                history_record_ll.setVisibility(View.GONE);
                return;
            }
            GoodCard mGoodCard = null;
            for(int i=mMessageBeanList.size() - 1;i>=0;i--){
               final MessageBean bean = mMessageBeanList.get(i);
                if(bean.msgType == MessageChatAdapter.IDCARD){// 名片
                    final NameCardBean mNameCardBean = bean.idCard;
                    mGoodCard = new GoodCard(PersonDetailActivity.this, mNameCardBean, MessageChatAdapter.IDCARD, codes_sv);
                    // 如果是个人名片，点击后就是去“聊天”
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.IDCARD,mNameCardBean.getMsg_key());
                                }
                            });
                            if(mNameCardBean.getType() == BorrowConstants.CHATTYPE_SINGLE){
                                ChatMainActivity_//
                                        .intent(PersonDetailActivity.this)//
                                        //// TODO: 2016/3/9  收到名片后 是否可以选择身份去聊天
                                        .currentFigureId(bean.figureId)// 当前角色
                                        .toChatXlId(mNameCardBean.getUserId())// 附近的人 xluserid
                                        .toChatId(mNameCardBean.getFigureId())//figureUserId
                                        .titleName(mNameCardBean.getName())
                                        .headerImgId(mNameCardBean.getImgId())
                                        .toChatName(mNameCardBean.getName())
                                        .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                                        .start();
                            }else if(mNameCardBean.getType() == BorrowConstants.CHATTYPE_GROUP){
                                ChatMainActivity_.intent(PersonDetailActivity.this)
                                        .titleName(mNameCardBean.getName())
                                        .toChatId(mNameCardBean.getFigureId())
                                        .chatType(BorrowConstants.CHATTYPE_GROUP)
                                        .headerImgId(mNameCardBean.getImgId())
                                        .toChatName(mNameCardBean.getName())
                                        .currentFigureId(currentFigureId)
                                        .start();
                            }
                            animLeftToRight();
                        }
                    });
                }else if(bean.msgType == MessageChatAdapter.WEBSHOPPING){// 商品
                    final GoodsDetailBean mGoodsDetailBean = bean.goodsCard;
                    mGoodCard = new GoodCard(PersonDetailActivity.this, mGoodsDetailBean, MessageChatAdapter.WEBSHOPPING, codes_sv);
                    // 如果是链接，点击后跳到WebView
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            if(mGoodsDetailBean.getUrl() == null){
                                return;
                            }
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.WEBSHOPPING, mGoodsDetailBean.getMsg_key());
                                }
                            });
                            Intent intent = new Intent(PersonDetailActivity.this, WebviewActivity_.class);
                            intent.putExtra("url", mGoodsDetailBean.getUrl());
                            startActivity(intent);
                            animLeftToRight();
                        }
                    });
                }else if(bean.msgType == MessageChatAdapter.NEWSCARD){// 新闻
                    final NewsCard mNewsCardBean = bean.newsCard;
                    mGoodCard = new GoodCard(PersonDetailActivity.this, mNewsCardBean, MessageChatAdapter.NEWSCARD, codes_sv);
                    // 如果是链接，点击后跳到WebView
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            if(mNewsCardBean.getUrl() == null){
                                return;
                            }
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.NEWSCARD,mNewsCardBean.getMsg_key());
                                }
                            });
                            Intent intent = new Intent(PersonDetailActivity.this, WebviewActivity_.class);
                            intent.putExtra("url", mNewsCardBean.getUrl());
                            startActivity(intent);
                            animLeftToRight();
                        }
                    });
                }
                if(flag){
                    flag = false;
                    cardsview.addCard(mGoodCard);
                }
                cardsview.addCardToLastStack(mGoodCard);
            }
            cardsview.refresh();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_secret_time.setText(DataDealUtil.getSecretTime(currentFigureId,figureId));
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        if(resultCode == RESULT_OK){
            Intent intent = new Intent(PersonDetailActivity.this, ChatMainActivity_.class);
            intent.putExtra("MessageBean", (MessageBean) data.getSerializableExtra("MessageBean"));
            setResult(RESULT_OK, intent);
            finish();
            animRightToLeft();
        }
    }

    @Click(R.id.ll_history_record)
    public void onClick (View v) {
        switch (v.getId()){
            // 历史记录
            case R.id.ll_history_record :
                Intent intent = new Intent(PersonDetailActivity.this, SpecificVisitingCodeActivity_.class);
                intent.putExtra("headerImgId", headerImgId);
                intent.putExtra("toChatName", toChatName);
                intent.putExtra("toChatId", toChatId);
                startActivityForResult(intent, GOTOSPECIFICVISITINGCODEACTIVITY);
                animLeftToRight();
                break;
            default:
        }
    }

    @Click(R.id.rl_secret_set)
    void toSetSecret(){
        ChatSecretSetActivity_.intent(context).currentFigureId(currentFigureId)
                .tochatId(figureId).start();
    }


}

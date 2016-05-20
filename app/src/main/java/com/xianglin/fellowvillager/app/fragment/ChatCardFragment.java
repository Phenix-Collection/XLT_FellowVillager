package com.xianglin.fellowvillager.app.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.AllVisitingCodeActivity_;
import com.xianglin.fellowvillager.app.activity.NewCardActivity;
import com.xianglin.fellowvillager.app.activity.SelectBusinessCard_;
import com.xianglin.fellowvillager.app.adapter.HorizontalCardAdapter;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.SendMsgController;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.loader.CardLoader;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * 卡片
 */
@EFragment(R.layout.fragment_chat_card)
public class ChatCardFragment extends BaseFragment {

    @ViewById(R.id.hsv_chat)
    RecyclerView mListCardView;
    @ViewById(R.id.tv_album)
    TextView tvAlbum;
    List<MessageBean> mMessageBeanList = new ArrayList<MessageBean>();
    CardLoader mCardLoader;
    String toChatName;
    String name;
    HorizontalCardAdapter adapter;

    @AfterViews
    void init() {
        Bundle bundle = getArguments();
        name = bundle.getString(NewCardActivity.KEYNAME);
        toChatName = (String) bundle.get("toChatName");
        mCardLoader = new CardLoader(mBaseActivity, null, BorrowConstants.CHATTYPE_SINGLE, 0, 10, true, false);
        LoaderManager lm = mBaseActivity.getSupportLoaderManager();
        lm.initLoader(1, mBaseActivity.getIntent().getExtras(), new CardListCallbacks());

    }

    @AfterViews
    public void initView() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListCardView.setLayoutManager(linearLayoutManager);
    }

    private List<MessageBean> beanList;

    private class CardListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mCardLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null) {
                return;
            }
            if (isResume) {
                CardDBHandler.MessageCursor messageCursor = new CardDBHandler.MessageCursor(data);
                beanList = messageCursor.getMessageBeanList();
                if (beanList == null) {
                    beanList = new ArrayList<MessageBean>();
                }
                setDefaultItem();

                setData(beanList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

    }

    private boolean isResume = false;

    public void setResume(boolean resume) {
        isResume = resume;
    }

    /**
     * 设置添加、更多按钮
     */
    private void setDefaultItem() {
        MessageBean.Builder builder = new MessageBean.Builder();
        builder.msgType(-1);
        MessageBean addBean = new MessageBean(builder);
        addBean.xlReMarks = "add";
        beanList.add(0, addBean);
        if (beanList.size() > 1) {
            MessageBean moreBean = new MessageBean(builder);
            moreBean.xlReMarks = "more";
            beanList.add(moreBean);
        }
    }

    /**
     * 设置Adapter并显示数据
     */
    private void setData(List<MessageBean> beanList) {
        mMessageBeanList.clear();
        mMessageBeanList.addAll(beanList);
        if (adapter == null||isResume) {
            isResume = false;
            adapter = new HorizontalCardAdapter(mBaseActivity, mMessageBeanList);

            mListCardView.setAdapter(adapter);

            adapter.setOnRecyclerViewListener(new HorizontalCardAdapter.OnRecyclerViewListener() {
                @Override
                public void onItemClick(int position) {
                    OnRecyleViewClick(position);
                }

                @Override
                public boolean onItemLongClick(int position) {
                    return false;
                }
            });
        }

    }
    /**
     * 实现itemClick
     *
     * @param position
     */
    public void OnRecyleViewClick(int position) {
        if (position == 0) {
            SelectBusinessCard_.intent(mContext).name(name).startForResult(ChatMainActivity.REQUEST_CODE_ID_CARD);
            return;
        }
        final MessageBean bean = mMessageBeanList.get(position);
        if (bean == null) {
            return;
        }
        if (!TextUtils.isEmpty(bean.xlReMarks) && bean.xlReMarks.equals("more")) {
            onClickAll();
            return;
        }
        // 发送名片或者链接
        if (bean.msgType == MessageChatAdapter.IDCARD) {
            NameCardBean mNameCardBean = bean.idCard;
            bean.msgKey = String.valueOf(System.currentTimeMillis());
            bean.msgLocalKey = bean.msgKey;
            if (mBaseActivity instanceof ChatMainActivity) {

                Contact nameCard= new Contact.Builder(Contact.ITEM)
                        .xlUserName(mNameCardBean.getName())
                        .figureUsersId(mNameCardBean.getFigureId())
                        .xlUserId(mNameCardBean.getUserId())
                        .file_id( mNameCardBean.getImgId())
                        .build();

                SendMsgController.getInstance().sendChatIDCard(
                        bean,
                        nameCard,
                        false
                );
            }
        } else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
            GoodsDetailBean mGoodsDetailBean = bean.goodsCard;
            bean.msgKey = String.valueOf(System.currentTimeMillis());
            bean.msgLocalKey = bean.msgKey;
            if (mBaseActivity instanceof ChatMainActivity) {
                SendMsgController.getInstance().sendChatGoods(
                        bean,
                        mGoodsDetailBean.getGoodsId(),
                        mGoodsDetailBean.getName(),
                        mGoodsDetailBean.getImgURL(),
                        mGoodsDetailBean.getPrice(),
                        mGoodsDetailBean.getAbstraction(),
                        mGoodsDetailBean.getUrl(),
                        false
                );
            }
        } else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
            NewsCard newsCard = bean.newsCard;
            bean.msgKey = String.valueOf(System.currentTimeMillis());
            bean.msgLocalKey = bean.msgKey;
            if (mBaseActivity instanceof  ChatMainActivity) {
                SendMsgController.getInstance().sendChatNews(
                        bean,
                        newsCard.getNewsid(),
                        newsCard.getTitle(),
                        newsCard.getImgurl(),
                        newsCard.getSummary(),
                        newsCard.getUrl(),
                        false
                );
            }
        }
    }


    @Click(R.id.tv_album)
    public void onClickAll() {
        Intent intent = new Intent(mBaseActivity, AllVisitingCodeActivity_.class);
        intent.putExtra("toChatName", toChatName);
        startActivityForResult(intent, ChatMainActivity.GOTOPERSONDETAILACTIVITY);
        mBaseActivity.animBottomToTop();
    }
}

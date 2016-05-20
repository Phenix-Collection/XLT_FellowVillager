package com.xianglin.fellowvillager.app.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.Utils;

import java.util.List;

/**
 * class describtion
 * Created by LiuHaoLiang.
 *
 * @author LiuHaoliang
 * @version v 1.0.0 2016/1/13 XLXZ Exp
 */
public class HorizontalCardAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private Activity mActivity;
    private List<MessageBean> mMessageBeanList;

    public HorizontalCardAdapter(Activity activity, List<MessageBean> list) {
        this.mActivity = activity;
        this.mMessageBeanList = list;
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageBeanList.get(position).msgType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case MessageChatAdapter.IDCARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person_visiting_code, null);
                return new ViewHolderIdCard(view);
            case MessageChatAdapter.WEBSHOPPING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_visiting_code, null);
                return new ViewHolderWebShopping(view);
            case MessageChatAdapter.NEWSCARD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_visiting_code, null);
                return new ViewHolderNews(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_card_layout, null);
                return new ViewHolderAdd(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageBean bean = mMessageBeanList.get(position);
        if (bean.msgType == MessageChatAdapter.IDCARD) {
            ShowIDCard(((ViewHolderIdCard) holder), bean, position);
        } else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
            ShowShoppingCard(((ViewHolderWebShopping) holder), bean, position);
        } else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
            ShowNewsCard(((ViewHolderNews) holder), bean, position);
        } else {
            CardAddOrMore(((ViewHolderAdd) holder), bean, position);
        }
    }

    private void ShowShoppingCard(ViewHolderWebShopping holderWebShopping, MessageBean bean, int position) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holderWebShopping.llGoodVisitingCard.getLayoutParams(); // 取控件View当前的布局参数
        linearParams.setMargins(10, 0, 10, 0);
        holderWebShopping.llGoodVisitingCard.setLayoutParams(linearParams);
        ImageUtils.showUrlImage(holderWebShopping.img, bean.goodsCard.getImgURL());
        holderWebShopping.desc_tv.setText(bean.goodsCard.getAbstraction());
        double price = Utils.parseDouble(bean.goodsCard.getPrice());
        holderWebShopping.now_price_tv.setText("￥ " + Utils.formatDecimal(price / 100, 2));
        if (holderWebShopping.title_tv != null) {
            holderWebShopping.title_tv.setText("商品");
            holderWebShopping.title_tv.setBackgroundResource(R.drawable.bg_top_round_red_txt);
        }
        holderWebShopping.position = position;
    }

    private void CardAddOrMore(ViewHolderAdd holderAddOrMore, MessageBean bean, int position) {

        holderAddOrMore.position = position;
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holderAddOrMore.llAddCard.getLayoutParams(); // 取控件View当前的布局参数
        linearParams.width = 291 * Utils.dipToPixel(mActivity, 235) * 5 / 484 / 6;
        holderAddOrMore.llAddCard.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件View

        if (bean.xlReMarks.equals("add")) {
            holderAddOrMore.img.setImageResource(R.drawable.icon_add_card);
            holderAddOrMore.tvCardDes.setVisibility(View.VISIBLE);

        } else if (bean.xlReMarks.equals("more")) {
            holderAddOrMore.img.setImageResource(R.drawable.icon_photo_more);
            holderAddOrMore.tvCardDes.setVisibility(View.GONE);
        }
    }

    private void ShowIDCard(ViewHolderIdCard holderIdCard, MessageBean bean, int position) {
        if (bean.idCard.getType() == BorrowConstants.CHATTYPE_SINGLE) {
            holderIdCard.visiting_code_title_tv.setText("名片");
        } else {
            holderIdCard.visiting_code_title_tv.setText("群名片");
        }
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holderIdCard.llPersionCard.getLayoutParams(); // 取控件View当前的布局参数
        linearParams.setMargins(10, 0, 10, 0);
        holderIdCard.llPersionCard.setLayoutParams(linearParams);
        holderIdCard.position = position;
        ImageUtils.showCommonImage(mActivity, holderIdCard.head_iv, FileUtils.IMG_CACHE_HEADIMAGE_PATH, bean.idCard.getImgId(), R.drawable.head);
        holderIdCard.name_tv.setText(bean.idCard.getName());
        holderIdCard.number_tv.setText("乡邻号: " + bean.idCard.getFigureId());
    }

    private void ShowNewsCard(ViewHolderNews holderNews, MessageBean bean, int position) {
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) holderNews.mLlNewsCard.getLayoutParams(); // 取控件View当前的布局参数
        linearParams.setMargins(10, 0, 10, 0);
        linearParams.height = Utils.dipToPixel(mActivity, 161);
        holderNews.mLlNewsCard.setLayoutParams(linearParams);
        if (TextUtils.isEmpty(bean.newsCard.getImgurl())) {
            holderNews.iv_visiting_code_news_img.setVisibility(View.GONE);
        } else {
            ImageUtils.showUrlImage(holderNews.iv_visiting_code_news_img, bean.newsCard.getImgurl());
        }
        holderNews.tv_visiting_code_news_title.setText(bean.newsCard.getSummary());
        holderNews.position = position;
    }

    @Override
    public int getItemCount() {
        return mMessageBeanList.size();
    }


    /**
     * 个人名片
     */
    public class ViewHolderIdCard extends RecyclerView.ViewHolder {
        public TextView visiting_code_title_tv;
        public ImageView head_iv;
        public TextView name_tv;
        public TextView number_tv;
        public LinearLayout llPersionCard;
        public int position;

        public ViewHolderIdCard(View v) {
            super(v);
            visiting_code_title_tv = (TextView) v.findViewById(R.id.tv_person_visiting_code_type);
            head_iv = (ImageView) v.findViewById(R.id.iv_idImg);
            name_tv = (TextView) v.findViewById(R.id.tv_idName);
            number_tv = (TextView) v.findViewById(R.id.tv_idXLID);
            llPersionCard = (LinearLayout) v.findViewById(R.id.ll_person_visiting_code);
            llPersionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewListener.onItemClick(position);
                }
            });
        }
    }

    /**
     * 购物
     */
    public class ViewHolderWebShopping extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title_tv;
        public TextView desc_tv;
        public TextView now_price_tv;
        public LinearLayout llGoodVisitingCard;
        public int position;

        public ViewHolderWebShopping(View v) {
            super(v);
            img = (ImageView) v.findViewById(R.id.iv_good_visiting_code_img);
            title_tv = (TextView) v.findViewById(R.id.tv_good_visiting_code_type);
            desc_tv = (TextView) v.findViewById(R.id.tv_good_visiting_code_desc);
            now_price_tv = (TextView) v.findViewById(R.id.tv_good_visiting_code_price);
            llGoodVisitingCard = (LinearLayout) v.findViewById(R.id.ll_good_visiting_code);
            llGoodVisitingCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewListener.onItemClick(position);
                }
            });
        }
    }

    /**
     * 添加以及更多卡片item
     */
    public class ViewHolderAdd extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView tvCardDes;
        public LinearLayout llAddCard;
        public int position;

        public ViewHolderAdd(View v) {
            super(v);
            img = (ImageView) v.findViewById(R.id.img_card_);
            tvCardDes = (TextView) v.findViewById(R.id.tv_card_descuption);
            llAddCard = (LinearLayout) v.findViewById(R.id.ll_add_card);
            llAddCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewListener.onItemClick(position);
                }
            });
        }
    }

    /**
     * 新闻/无图的时候隐藏图片
     */
    public class ViewHolderNews extends RecyclerView.ViewHolder {
        // 显示时间
        public TextView tv_news_visiting_code_time;
        // 用于点击事件
        public LinearLayout mLlNewsCard;
        public ImageView iv_visiting_code_news_img;
        public TextView tv_visiting_code_news_title;
        public ImageView iv_visiting_code_news_link;
        public int position;

        public ViewHolderNews(View v) {
            super(v);
            tv_news_visiting_code_time = (TextView) v.findViewById(R.id.tv_news_visiting_code_time);
            mLlNewsCard = (LinearLayout) v.findViewById(R.id.ll_news_visiting_code);
            iv_visiting_code_news_img = (ImageView) v.findViewById(R.id.iv_news_visiting_code_img);
            tv_visiting_code_news_title = (TextView) v.findViewById(R.id.tv_news_visiting_code_desc);
            iv_visiting_code_news_link = (ImageView) v.findViewById(R.id.iv_news_visiting_code_link);
            mLlNewsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewListener.onItemClick(position);
                }
            });
        }
    }
}
package com.fima.cardsui.objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.utils.ACache;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.Utils;

public class GoodCard extends ACard{

	private int id;

	private NameCardBean mNameCardBean;

	private GoodsDetailBean mGoodsDetailBean;

	private NewsCard mNewsCardBean;
	private Activity mActivity;
	private ACache mACache;
	private Bitmap bitmap;

	public GoodCard(Activity activity, NameCardBean cardBean, int id, ScrollView sv) {
		super(sv);
		mActivity = activity;
		mNameCardBean = cardBean;
		this.id = id;
		mACache = ACache.get(activity);
	}
	public GoodCard(Activity activity, GoodsDetailBean detailBean, int id, ScrollView sv) {
		super(sv);
		mActivity = activity;
		mGoodsDetailBean = detailBean;
		this.id = id;
		mACache = ACache.get(activity);
	}
	public GoodCard(Activity activity, NewsCard newsCardBean, int id, ScrollView sv) {
		super(sv);
		mActivity = activity;
		mNewsCardBean = newsCardBean;
		this.id = id;
		mACache = ACache.get(activity);
	}

	public GoodCard(String title) {
		super(title);
	}
	
	public GoodCard(String title, int image) {
		super(title, image);
	}

	@Override
	protected void applyTo(final View convertView) {
		View view = null;
		if(id == MessageChatAdapter.IDCARD){// 名片
			if(mNameCardBean == null){
				return;
			}

			TextView tv_person_visiting_code_type = (TextView) convertView.findViewById(R.id.tv_person_visiting_code_type);
			ImageView idImg = (ImageView) convertView.findViewById(R.id.iv_idImg);
			TextView idName = (TextView) convertView.findViewById(R.id.tv_idName);
			TextView idXLID = (TextView) convertView.findViewById(R.id.tv_idXLID);

//			if(mNameCardBean.getType() == BorrowConstants.CHATTYPE_SINGLE){
//				tv_person_visiting_code_type.setText("个人名片");
//			}else if(mNameCardBean.getType() == BorrowConstants.CHATTYPE_GROUP){
//				tv_person_visiting_code_type.setText("群名片");
//			}
			ImageUtils.showCommonImage(mActivity, idImg, FileUtils.IMG_CACHE_HEADIMAGE_PATH, mNameCardBean.getImgId(), R.drawable.head);
			idName.setText(mNameCardBean.getName());
			idXLID.setText("乡邻号: " + mNameCardBean.getFigureId());

		}else if(id == MessageChatAdapter.WEBSHOPPING){// 宜农商品
			if(mGoodsDetailBean == null){
				return;
			}

			// 电商
			TextView tv_good_visiting_code_type = (TextView) convertView.findViewById(R.id.tv_good_visiting_code_type);
			// 图片
			final ImageView iv_good_visiting_code_img = (ImageView) convertView.findViewById(R.id.iv_good_visiting_code_img);
			// 描述信息
			TextView tv_good_visiting_code_desc = (TextView) convertView.findViewById(R.id.tv_good_visiting_code_desc);
			// 价格
			TextView tv_good_visiting_code_price = (TextView) convertView.findViewById(R.id.tv_good_visiting_code_price);
//			ImageLoader.getInstance().displayImage(mGoodsDetailBean.getImgURL(), iv_visiting_code_img);
//			ImageUtils.showUrlImage(iv_visiting_code_img, mGoodsDetailBean.getImgURL());

			bitmap = mACache.getAsBitmap(mGoodsDetailBean.getImgURL());
			if(bitmap != null){
				iv_good_visiting_code_img.setImageBitmap(bitmap);
			}else{
				try {
					ImageUtils.showUrlImage(iv_good_visiting_code_img, mGoodsDetailBean.getImgURL());
					ImageLoader.getInstance().loadImage(mGoodsDetailBean.getImgURL(), new ImageLoadingListener() {
						@Override
						public void onLoadingStarted (String imageUri, View view) {
							
						}
						
						@Override
						public void onLoadingFailed (String imageUri, View view, FailReason failReason) {
							
						}
						
						@Override
						public void onLoadingComplete (String imageUri, View view, Bitmap loadedImage) {
							if(loadedImage != null){
//								iv_good_visiting_code_img.setImageBitmap(loadedImage);
								mACache.put(imageUri, loadedImage);
							}
						}

						@Override
						public void onLoadingCancelled (String imageUri, View view) {

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			tv_good_visiting_code_desc.setText(mGoodsDetailBean.getAbstraction());
			tv_good_visiting_code_price.setText("￥ " + Utils.formatDecimal(Double.parseDouble(mGoodsDetailBean.getPrice()) / 100, 2));
		}else if(id == MessageChatAdapter.NEWSCARD){
			if(mNewsCardBean == null){
				return;
			}
			if(!TextUtils.isEmpty(mNewsCardBean.getImgurl())){// 有图片新闻
				TextView tv_news_visiting_code_type = (TextView) convertView.findViewById(R.id.tv_news_visiting_code_type);
				final ImageView iv_news_visiting_code_img = (ImageView) convertView.findViewById(R.id.iv_news_visiting_code_img);
				TextView tv_news_visiting_code_desc = (TextView) convertView.findViewById(R.id.tv_news_visiting_code_desc);
//				ImageView iv_news_visiting_code_link = (ImageView) convertView.findViewById(R.id.iv_news_visiting_code_link);
//				ImageLoader.getInstance().displayImage(mNewsCardBean.getImgurl(), iv_visiting_code_news_img);

				bitmap = mACache.getAsBitmap(mNewsCardBean.getImgurl());
				if(bitmap != null){
					iv_news_visiting_code_img.setImageBitmap(bitmap);
				}else{
					try {
						ImageUtils.showUrlImage(iv_news_visiting_code_img, mNewsCardBean.getImgurl());
						ImageLoader.getInstance().loadImage(mNewsCardBean.getImgurl(), new ImageLoadingListener() {
							@Override
							public void onLoadingStarted (String imageUri, View view) {

							}

							@Override
							public void onLoadingFailed (String imageUri, View view, FailReason failReason) {

							}

							@Override
							public void onLoadingComplete (String imageUri, View view, Bitmap loadedImage) {
								if (loadedImage != null) {
//									iv_news_visiting_code_img.setImageBitmap(loadedImage);
									mACache.put(imageUri, loadedImage);
								}
							}

							@Override
							public void onLoadingCancelled (String imageUri, View view) {

							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				tv_news_visiting_code_desc.setText(mNewsCardBean.getSummary());
			}else{// 无图片新闻
				TextView tv_news_visiting_code_no_image_type = (TextView) convertView.findViewById(R.id.tv_news_visiting_code_no_image_type);
				TextView tv_news_visiting_code_no_image_desc = (TextView) convertView.findViewById(R.id.tv_news_visiting_code_no_image_desc);
//				ImageView iv_news_visiting_code_no_image_link = (ImageView) convertView.findViewById(R.id.iv_news_visiting_code_no_image_link);
				tv_news_visiting_code_no_image_desc.setText(mNewsCardBean.getSummary());
			}
		}
	}

	protected int getCardLayoutId() {
		if(id == MessageChatAdapter.IDCARD){
			return R.layout.item_person_visiting_code;
		}else if(id == MessageChatAdapter.WEBSHOPPING){
			return R.layout.item_good_visiting_code;
		}else if(id == MessageChatAdapter.NEWSCARD){
			if(mNewsCardBean == null){
				return -1;
			}
			if(!TextUtils.isEmpty(mNewsCardBean.getImgurl())){
				return R.layout.item_news_visiting_code;
			}else{
				return R.layout.item_news_visiting_code_no_image;
			}
		}else{
			return  -1;
		}
	}

	public NameCardBean getmNameCardBean () {
		return mNameCardBean;
	}
	public GoodsDetailBean getmGoodsDetailBean () {
		return mGoodsDetailBean;
	}
	public NewsCard getmNewsCardBean () {
		return mNewsCardBean;
	}

}

package com.xianglin.fellowvillager.app.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.PersonDetailActivity_;
import com.xianglin.fellowvillager.app.activity.WebviewActivity_;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.SingleThreadExecutor;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.dialog.SendCardDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.xianglin.fellowvillager.app.widget.dialog.SendCardDialog;

public class VisitingCodeAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<MessageBean> mMessageBeanList;
	private String toName;
	private Calendar calendar;
	private Map<String, Boolean> timeMap;
	private Map<Integer, TextView> memoryMap;
	private String currentYear;
	private String currentMonth;
	private static boolean isDirectOpen = true;

	public VisitingCodeAdapter () {
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public VisitingCodeAdapter (Activity activity, List<MessageBean> list, String toName, boolean isDirectOpen) {
		this.mActivity = activity;
		this.mMessageBeanList = list;
		this.toName = toName;
		this.isDirectOpen = isDirectOpen;

		calendar = Calendar.getInstance();
		timeMap = new HashMap<String, Boolean>();
		memoryMap = new HashMap<Integer, TextView>();

		currentYear = String.valueOf(calendar.get(Calendar.YEAR));
		currentMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		if (currentMonth.length() == 1) {
			currentMonth = "0" + currentMonth;
		}
	}

	@Override
	public int getCount () {
		return mMessageBeanList != null ? mMessageBeanList.size() : 0;
	}

	@Override
	public Object getItem (int position) {
		return null;
	}

	@Override
	public long getItemId (int position) {
		return 0;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup viewGroup) {
		MessageBean bean = mMessageBeanList.get(position);
		if (bean == null) {
			return LayoutInflater.from(mActivity).inflate(R.layout.item_person_visiting_code, viewGroup, false);
		}
		ViewHolderIdCard holder_idacrd = null;
		ViewHolderWebShopping holder_webshopping = null;
		ViewHolderNews holder_news = null;
		ViewHolderNewsNoImage holder_news_no_image = null;
		if (convertView == null) {
			if (bean.msgType == MessageChatAdapter.IDCARD) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_person_visiting_code, viewGroup, false);
				holder_idacrd = new ViewHolderIdCard(mActivity, convertView, bean, toName);
				convertView.setTag(holder_idacrd);
			} else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_good_visiting_code, viewGroup, false);
				holder_webshopping = new ViewHolderWebShopping(mActivity, convertView, bean, toName);
				convertView.setTag(holder_webshopping);
			} else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
				if (bean.newsCard != null) {
					if (!TextUtils.isEmpty(bean.newsCard.getImgurl())) {
						convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_news_visiting_code, viewGroup, false);
						holder_news = new ViewHolderNews(mActivity, convertView, bean, toName);
						convertView.setTag(holder_news);
					} else {
						convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_news_visiting_code_no_image, viewGroup, false);
						holder_news_no_image = new ViewHolderNewsNoImage(mActivity, convertView, bean, toName);
						convertView.setTag(holder_news_no_image);
					}
				}
			}
		} else {
			if (bean.msgType == MessageChatAdapter.IDCARD) {
				if (convertView.getTag() instanceof ViewHolderIdCard) {
					holder_idacrd = (ViewHolderIdCard) convertView.getTag();
				} else {
					convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_person_visiting_code, viewGroup, false);
					holder_idacrd = new ViewHolderIdCard(mActivity, convertView, bean, toName);
					convertView.setTag(holder_idacrd);
				}
			} else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
				if (convertView.getTag() instanceof ViewHolderWebShopping) {
					holder_webshopping = (ViewHolderWebShopping) convertView.getTag();
				} else {
					convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_good_visiting_code, viewGroup, false);
					holder_webshopping = new ViewHolderWebShopping(mActivity, convertView, bean, toName);
					convertView.setTag(holder_webshopping);
				}
			} else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
				if (bean.newsCard != null) {
					if (!TextUtils.isEmpty(bean.newsCard.getImgurl())) {
						if (convertView.getTag() instanceof ViewHolderNews) {
							holder_news = (ViewHolderNews) convertView.getTag();
						} else {
							convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_news_visiting_code, viewGroup, false);
							holder_news = new ViewHolderNews(mActivity, convertView, bean, toName);
							convertView.setTag(holder_news);
						}
					} else {
						if (convertView.getTag() instanceof ViewHolderNewsNoImage) {
							holder_news_no_image = (ViewHolderNewsNoImage) convertView.getTag();
						} else {
							convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_news_visiting_code_no_image, viewGroup, false);
							holder_news_no_image = new ViewHolderNewsNoImage(mActivity, convertView, bean, toName);
							convertView.setTag(holder_news_no_image);
						}
					}
				}
			}
		}

		// 填充数据
		if (bean.msgType == MessageChatAdapter.IDCARD) {// 名片

			showTime(position, holder_idacrd.tv_person_visiting_code_time);

			//			if(bean.idCard.getType() == BorrowConstants.CHATTYPE_SINGLE){
			//				holder_idacrd.tv_person_visiting_code_type.setText("个人名片");
			//			}else if(bean.idCard.getType() == BorrowConstants.CHATTYPE_GROUP){
			//				holder_idacrd.tv_person_visiting_code_type.setText("群名片");
			//			}
			ImageUtils.showCommonImage(mActivity, holder_idacrd.head_iv, FileUtils.IMG_CACHE_HEADIMAGE_PATH, bean.idCard.getImgId(), R.drawable.head);
			//			ImageUtils.showUrlImage(holder_idacrd.head_iv, bean.idCard.getImgId());
			holder_idacrd.name_tv.setText(bean.idCard.getName());
			holder_idacrd.number_tv.setText("乡邻号: " + bean.idCard.getFigureId());
		} else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {// 宜农商品

			showTime(position, holder_webshopping.tv_good_visiting_code_time);

			//			ImageLoader.getInstance().displayImage(bean.goodsCard.getImgURL(), holder_webshopping.img);
			ImageUtils.showUrlImage(holder_webshopping.iv_good_visiting_code_img, bean.goodsCard.getImgURL());
			holder_webshopping.tv_good_visiting_code_desc.setText(bean.goodsCard.getAbstraction());
			holder_webshopping.tv_good_visiting_code_price.setText("￥ " + Utils.formatDecimal(Double.parseDouble(bean.goodsCard.getPrice()) / 100, 2));
		} else if (bean.msgType == MessageChatAdapter.NEWSCARD) {

			if (bean.newsCard != null) {
				if (!TextUtils.isEmpty(bean.newsCard.getImgurl())) {// 有图片新闻

					showTime(position, holder_news.tv_news_visiting_code_time);

					//					ImageLoader.getInstance().displayImage(bean.newsCard.getImgurl(), holder_news.iv_visiting_code_news_img);
					ImageUtils.showUrlImage(holder_news.iv_news_visiting_code_img, bean.newsCard.getImgurl());
					holder_news.tv_news_visiting_code_desc.setText(bean.newsCard.getSummary());
				} else {// 无图片新闻

					showTime(position, holder_news_no_image.tv_news_visiting_code_no_image_time);

					holder_news_no_image.tv_news_visiting_code_no_image_desc.setText(bean.newsCard.getSummary());
				}
			}
		}

		if (position == 0) {
			if (bean.msgType == MessageChatAdapter.IDCARD) {
				holder_idacrd.tv_person_visiting_code_time.setVisibility(View.GONE);
			} else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
				holder_webshopping.tv_good_visiting_code_time.setVisibility(View.GONE);
			} else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
				if (!TextUtils.isEmpty(bean.newsCard.getImgurl())) {
					holder_news.tv_news_visiting_code_time.setVisibility(View.GONE);
				} else {
					holder_news_no_image.tv_news_visiting_code_no_image_time.setVisibility(View.GONE);
				}
			}
		}

		return convertView;
	}

	/**
	 当前卡片的时间跟上一张卡片的时间进行对比。
	 如：当前卡片的时间为2015年02月22日，上一张卡片的时间为2015年01月10日，那么在这两张卡片之间根据条件要显示“本月”或“2015年01月”或“2015年02月”；
	 前后两张卡片的时间年月相同，则不显示。
	 @param position
	 @param tv
	 */
	private void showTime (int position, TextView tv) {
		if(position == 0){
			return;
		}
		MessageBean bean1 = null;
		MessageBean bean2 = null;
		String time1 = null;
		String time2 = null;
		String year1 = "";
		String year2 = "";
		String month1 = "";
		String month2 = "";
		bean1 = mMessageBeanList.get(position - 1);
		bean2 = mMessageBeanList.get(position);
		if(bean1 == null || bean2 == null){
			return;
		}
		time1 = bean1.msgDate;
		time2 = bean2.msgDate;
		if(TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2)){
			return;
		}
		year1 = time1.split("-")[0];
		month1 = time1.split("-")[1];
		year2 = time2.split("-")[0];
		month2 = time2.split("-")[1];

		if(year1.equals(year2) && month1.equals(month2)){
			// 本来“tv”就是隐藏的，所以直接返回就行了
			return;
		}

		tv.setVisibility(View.VISIBLE);
		if (currentYear.equals(year2) && currentMonth.equals(month2)) {
			tv.setText("本月");
		}else{
			tv.setText(year2 + "年" + month2 + "月");
		}
	}

	public static class ViewHolderIdCard {
		// 显示时间
		public TextView tv_person_visiting_code_time;
		// 用于点击事件
		public LinearLayout ll_person_visiting_code;
		public TextView tv_person_visiting_code_type;
		public ImageView head_iv;
		public TextView name_tv;
		public TextView number_tv;

		public ViewHolderIdCard (final Activity activity, View v, final MessageBean bean, final String toName) {
			tv_person_visiting_code_time = (TextView) v.findViewById(R.id.tv_person_visiting_code_time);
			ll_person_visiting_code = (LinearLayout) v.findViewById(R.id.ll_person_visiting_code);
			tv_person_visiting_code_type = (TextView) v.findViewById(R.id.tv_person_visiting_code_type);
			head_iv = (ImageView) v.findViewById(R.id.iv_idImg);
			name_tv = (TextView) v.findViewById(R.id.tv_idName);
			number_tv = (TextView) v.findViewById(R.id.tv_idXLID);
			ll_person_visiting_code.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick (View v) {
					if (isDirectOpen) {
						directOpen(activity, bean);
					} else {
						popupDialog(activity, bean, toName);
					}
				}
			});
		}
	}

	public static class ViewHolderWebShopping {
		// 显示时间
		public TextView tv_good_visiting_code_time;
		// 用于点击事件
		public LinearLayout ll_good_visiting_code;
		public TextView tv_good_visiting_code_type;
		public ImageView iv_good_visiting_code_img;
		public TextView tv_good_visiting_code_desc;
		public TextView tv_good_visiting_code_price;

		public ViewHolderWebShopping (final Activity activity, View v, final MessageBean bean, final String toName) {
			tv_good_visiting_code_time = (TextView) v.findViewById(R.id.tv_good_visiting_code_time);
			ll_good_visiting_code = (LinearLayout) v.findViewById(R.id.ll_good_visiting_code);
			iv_good_visiting_code_img = (ImageView) v.findViewById(R.id.iv_good_visiting_code_img);
			tv_good_visiting_code_type = (TextView) v.findViewById(R.id.tv_good_visiting_code_type);
			tv_good_visiting_code_desc = (TextView) v.findViewById(R.id.tv_good_visiting_code_desc);
			tv_good_visiting_code_price = (TextView) v.findViewById(R.id.tv_good_visiting_code_price);
			ll_good_visiting_code.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick (View v) {
					if (isDirectOpen) {
						directOpen(activity, bean);
					} else {
						popupDialog(activity, bean, toName);
					}
				}
			});
		}

	}

	public static class ViewHolderNews {
		// 显示时间
		public TextView tv_news_visiting_code_time;
		// 用于点击事件
		public LinearLayout ll_news_visiting_code;
		public TextView tv_news_visiting_code_type;
		public ImageView iv_news_visiting_code_img;
		public TextView tv_news_visiting_code_desc;
		public ImageView iv_news_visiting_code_link;

		public ViewHolderNews (final Activity activity, View v, final MessageBean bean, final String toName) {
			if (bean.newsCard == null) {
				return;
			}
			if (!TextUtils.isEmpty(bean.newsCard.getImgurl())) {
				tv_news_visiting_code_time = (TextView) v.findViewById(R.id.tv_news_visiting_code_time);
				ll_news_visiting_code = (LinearLayout) v.findViewById(R.id.ll_news_visiting_code);
				tv_news_visiting_code_type = (TextView) v.findViewById(R.id.tv_news_visiting_code_type);
				iv_news_visiting_code_img = (ImageView) v.findViewById(R.id.iv_news_visiting_code_img);
				tv_news_visiting_code_desc = (TextView) v.findViewById(R.id.tv_news_visiting_code_desc);
				iv_news_visiting_code_link = (ImageView) v.findViewById(R.id.iv_news_visiting_code_link);
				ll_news_visiting_code.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick (View v) {
						if (isDirectOpen) {
							directOpen(activity, bean);
						} else {
							popupDialog(activity, bean, toName);
						}
					}
				});
			}
		}
	}

	public static class ViewHolderNewsNoImage {
		// 显示时间
		public TextView tv_news_visiting_code_no_image_time;
		// 用于点击事件
		public LinearLayout ll_news_visiting_code_no_image;
		public TextView tv_news_visiting_code_no_image_type;
		public TextView tv_news_visiting_code_no_image_desc;
		public ImageView iv_news_visiting_code_no_image_link;

		public ViewHolderNewsNoImage (final Activity activity, View v, final MessageBean bean, final String toName) {
			if (bean.newsCard == null) {
				return;
			}
			if (TextUtils.isEmpty(bean.newsCard.getImgurl())) {
				tv_news_visiting_code_no_image_time = (TextView) v.findViewById(R.id.tv_news_visiting_code_no_image_time);
				ll_news_visiting_code_no_image = (LinearLayout) v.findViewById(R.id.ll_news_visiting_code_no_image);
				tv_news_visiting_code_no_image_type = (TextView) v.findViewById(R.id.tv_news_visiting_code_no_image_type);
				tv_news_visiting_code_no_image_desc = (TextView) v.findViewById(R.id.tv_news_visiting_code_no_image_desc);
				iv_news_visiting_code_no_image_link = (ImageView) v.findViewById(R.id.iv_news_visiting_code_no_image_link);
				ll_news_visiting_code_no_image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick (View v) {
						if (isDirectOpen) {
							directOpen(activity, bean);
						} else {
							popupDialog(activity, bean, toName);
						}
					}
				});
			}
		}
	}

	private static void popupDialog (final Activity activity, final MessageBean bean, String toName) {
		// 弹出框确认是否发送
		new SendCardDialog.Builder(activity).setMessage(toName)
				.setBackButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick (DialogInterface dialog, int which) {

						dialog.cancel();

					}
				}).setConfirmButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick (DialogInterface dialog, int which) {

				String currentTime = String.valueOf(System.currentTimeMillis());
				Intent intent = new Intent(activity, PersonDetailActivity_.class);
				bean.msgKey = currentTime;
				bean.msgLocalKey = currentTime;
				intent.putExtra("MessageBean", bean);

				dialog.cancel();
				if (activity instanceof BaseActivity) {
					activity.setResult(Activity.RESULT_OK, intent);
					((BaseActivity) activity).closeActivity();
				}

			}
		}).create().show();
	}

	private static void directOpen (final Activity activity, final MessageBean bean) {
		if (bean == null) {
			return;
		}
		if (bean.msgType == MessageChatAdapter.IDCARD) {// 名片
			final NameCardBean mNameCardBean = bean.idCard;
			if (mNameCardBean == null) {
				return;
			}
			// 如果是个人名片，点击后就是去“聊天”
			SingleThreadExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run () {
					new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.IDCARD, mNameCardBean.getMsg_key());
				}
			});
			if (mNameCardBean.getType() == BorrowConstants.CHATTYPE_SINGLE) {

				ChatMainActivity_//
						.intent(activity)//
						//// TODO: 2016/3/9  收到名片后 是否可以选择身份去聊天
						.currentFigureId(bean.figureId)// 当前角色
						.toChatXlId(mNameCardBean.getUserId())// 附近的人 xluserid
						.toChatId(mNameCardBean.getFigureId())//figureUserId
						.titleName(mNameCardBean.getName())
						.headerImgId(mNameCardBean.getImgId())
						.toChatName(mNameCardBean.getName())
						.chatType(BorrowConstants.CHATTYPE_SINGLE)//
						.start();

			} else if (mNameCardBean.getType() == BorrowConstants.CHATTYPE_GROUP) {
				ChatMainActivity_.intent(activity)
						.titleName(mNameCardBean.getName())
						.toChatId(mNameCardBean.getFigureId())
						.chatType(BorrowConstants.CHATTYPE_GROUP)
						.headerImgId(mNameCardBean.getImgId())
						.toChatName(mNameCardBean.getName())
						.start();
			}
		} else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {// 商品
			final GoodsDetailBean mGoodsDetailBean = bean.goodsCard;
			if (mGoodsDetailBean.getUrl() == null) {
				return;
			}
			SingleThreadExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run () {
					new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.WEBSHOPPING, mGoodsDetailBean.getMsg_key());
				}
			});
			Intent intent = new Intent(activity, WebviewActivity_.class);
			intent.putExtra("url", mGoodsDetailBean.getUrl());
			activity.startActivity(intent);
		} else if (bean.msgType == MessageChatAdapter.NEWSCARD) {// 新闻
			final NewsCard mNewsCardBean = bean.newsCard;
			if (mNewsCardBean.getUrl() == null) {
				return;
			}
			SingleThreadExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run () {
					new CardDBHandler().saveCardLastOpenTime(MessageChatAdapter.NEWSCARD, mNewsCardBean.getMsg_key());
				}
			});
			Intent intent = new Intent(activity, WebviewActivity_.class);
			intent.putExtra("url", mNewsCardBean.getUrl());
			activity.startActivity(intent);
		}
		if (activity instanceof BaseActivity) {
			((BaseActivity) activity).animLeftToRight();
		}

	}

}

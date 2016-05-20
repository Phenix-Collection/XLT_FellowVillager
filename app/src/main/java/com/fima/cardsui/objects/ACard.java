package com.fima.cardsui.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.fima.cardsui.adapter.StackAdapter;
import com.fima.cardsui.listener.SwipeDismissTouchListener;
import com.fima.cardsui.util.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.xianglin.fellowvillager.app.R;

import java.util.ArrayList;

public abstract class ACard extends AbstractCard {

	// 放用户布局的容器
	protected View mCardLayout;
	private OnClickListener mOnClickListener;

	private static final float _12F = 0f;
	private static final float _45F = 60f;
//	private static final float _45F = 70f;
	private StackAdapter mStackAdapter;
	private Context mContext;
	private String title, stackTitleColor;
	private ArrayList<ACard> mACardsAList;
	private static final String NINE_OLD_TRANSLATION_Y = "translationY";
	private int mPosition;
	private ScrollView mScrollView;

	public ACard (ScrollView sv) {
		mACardsAList = new ArrayList<ACard>();
		mScrollView = sv;
	}

	public ACard () {
		mACardsAList = new ArrayList<ACard>();
	}

	public ACard (String title) {
		mACardsAList = new ArrayList<ACard>();
		this.title = title;
	}

	public ACard (String title, String desc) {
		mACardsAList = new ArrayList<ACard>();
		this.title = title;
		this.desc = desc;
	}

	public ACard (String title, int image) {
		mACardsAList = new ArrayList<ACard>();
		this.title = title;
		this.image = image;
	}

	public ACard (String title, String desc, int image) {
		mACardsAList = new ArrayList<ACard>();
		this.title = title;
		this.desc = desc;
		this.image = image;
	}

	public ACard (String titlePlay, String description, String color, String titleColor, Boolean hasOverflow, Boolean
			isClickable) {
		mACardsAList = new ArrayList<ACard>();
		this.titlePlay = titlePlay;
		this.description = description;
		this.color = color;
		this.titleColor = titleColor;
		this.hasOverflow = hasOverflow;
		this.isClickable = isClickable;
	}

	public ACard (String titlePlay, String description, int imageRes, String titleColor, Boolean hasOverflow, Boolean
			isClickable) {
		mACardsAList = new ArrayList<ACard>();
		this.titlePlay = titlePlay;
		this.description = description;
		this.titleColor = titleColor;
		this.hasOverflow = hasOverflow;
		this.isClickable = isClickable;
		this.imageRes = imageRes;
	}

	/****************************************************************************************************************************************************************************/
	// 在StackAdapter中使用
	// 每调用一次这个方法都要对整个界面上的元素扫描一遍
	// 每次更新一下界面（增加或者删除一张卡片）都需要对每个元素重新绑定事件
	public View getView (Context context, View convertView, boolean swipable) {
		mContext = context;

		if (convertView != null) {
			if (convertView.getId() == R.id.stackRoot) {
				if (convert(convertView)) {
					return convertView;
				}
			}
		}

		final View view = LayoutInflater.from(context).inflate(R.layout.item_stack, null);
		assert view != null;
		final RelativeLayout container = (RelativeLayout) view.findViewById(R.id.stackRoot);

		final int cardsArraySize = mACardsAList.size();
		final int lastCardPosition = cardsArraySize - 1;

		ACard card;
		View cardView;
		for (int i = 0; i < cardsArraySize; i++) {
			card = mACardsAList.get(i);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					Utils.convertDpToPixelInt(context, 170));
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			int topPx = 0;

			// handle the view
			cardView = card.getView(context);

			// handle the listener
			if (i == lastCardPosition) {// 能看到全部内容的那个View
				cardView.setOnClickListener(card.getClickListener());
			} else {
				cardView.setOnClickListener(getClickListener(this, container, i));
			}

			float dp = 0f;
			if (i > 0) {
				dp = (_45F * i) - _12F;
				topPx = Utils.convertDpToPixelInt(context, dp);
				lp.setMargins(0, topPx, 0, 0);
			}

			cardView.setLayoutParams(lp);

			// 不要向左或者向右滑动取消
			if (swipable) {
				cardView.setOnTouchListener(new SwipeDismissTouchListener(cardView, card, new com.fima.cardsui
						.listener.SwipeDismissTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss (View view, Object token) {
						ACard c = (ACard) token;
					}
				}));
			}

			container.addView(cardView);
		}

		return view;
	}

	/**************************************************************************************************************************************************/

	@Override
	public View getView (Context context, boolean swipable) {
		mContext = context;
		return getView(context, false);
	}

	// 用户的布局
	@Override
	public View getView (Context context) {
		mContext = context;
		// 放用户布局的容器
		View view = LayoutInflater.from(context).inflate(R.layout.item_card, null);
		mCardLayout = view;
		try {
			// 添加用户的布局(View中没有addView()方法)
			((FrameLayout) view.findViewById(R.id.cardContent)).addView(getCardContent(context));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		LinearLayout.LayoutParams lp = new LinearLayout
				.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int bottom = Utils.convertDpToPixelInt(context, 12);
		lp.setMargins(0, 0, 0, bottom);
		//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		// Utils.convertDpToPixelInt(context, 170));
		//        lp.setMargins(0, 0, 0, 0);
		view.setLayoutParams(lp);
		return view;
	}

	public OnClickListener getClickListener () {
		return mOnClickListener;
	}

	public void setOnClickListener (OnClickListener listener) {
		mOnClickListener = listener;
	}

	/*****************************************************************************************************************/

	protected abstract void applyTo (View convertView);

	protected abstract int getCardLayoutId ();

	public View getCardContent (Context context) {
		View view = LayoutInflater.from(context).inflate(getCardLayoutId(), null);
		applyTo(view);
		return view;
	}

	public boolean convert (View convertCardView) {
		View view = convertCardView.findViewById(getCardLayoutId());
		if (view == null) {
			return false;
		}
		applyTo(view);
		return true;
	}

	/*****************************************************************************************************************/
	// index取值[0, 3]

	/**
	 点击的事件是滑动到底部

	 @param mACard
	 @param container 放所有卡片的容器
	 @param index 取值[0, 3]

	 @return
	 */
	private OnClickListener getClickListener (final ACard mACard, final RelativeLayout container, final int index) {
		return new OnClickListener() {
			@Override
			public void onClick (View v) {
				// init views array
				View[] views = new View[container.getChildCount()];

				for (int i = 0; i < views.length; i++) {
					views[i] = container.getChildAt(i);
				}

				int last = views.length - 1;

				if (index != last) {
					if (index == 0) {
						onClickFirstCard(mACard, container, index, views);
						mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					} else if (index < last) {
						onClickOtherCard(mACard, container, index, views, last);
						mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}

			}

			public void onClickFirstCard (final ACard mACard, final RelativeLayout frameLayout, final int index,
			                              View[] views) {

				// run through all the cards
				for (int i = 0; i < views.length; i++) {
					ObjectAnimator anim = null;

					if (i == 0) {
						// the first goes all the way down
						float downFactor = 0;
						if (views.length > 2) {
							downFactor = convertDpToPixel((_45F) * (views.length - 1) - 1);
						} else {
							downFactor = convertDpToPixel(_45F);
						}

						anim = ObjectAnimator.ofFloat(views[i], NINE_OLD_TRANSLATION_Y, 0, downFactor);
						anim.addListener(getAnimationListener(mACard, frameLayout, index, views[index]));

					} else if (i == 1) {
						// the second goes up just a bit

						float upFactor = convertDpToPixel(-17f);
						anim = ObjectAnimator.ofFloat(views[i], NINE_OLD_TRANSLATION_Y, 0, upFactor);

					} else {
						// the rest go up by one card
						float upFactor = convertDpToPixel(-1 * _45F);
						anim = ObjectAnimator.ofFloat(views[i], NINE_OLD_TRANSLATION_Y, 0, upFactor);
					}

					if (anim != null) {
						anim.start();
					}

				}
			}

			public void onClickOtherCard (final ACard mACard, final RelativeLayout frameLayout, final int index,
			                              View[] views, int last) {
				// if clicked card is in middle
				for (int i = index; i <= last; i++) {
					// run through the cards from the clicked position
					// and on until the end
					ObjectAnimator anim = null;

					if (i == index) {
						// the selected card goes all the way down
						float downFactor = convertDpToPixel(_45F * (last - i) + _12F);
						anim = ObjectAnimator.ofFloat(views[i], NINE_OLD_TRANSLATION_Y, 0, downFactor);
						anim.addListener(getAnimationListener(mACard, frameLayout, index, views[index]));
					} else {
						// the rest go up by one
						float upFactor = convertDpToPixel(_45F * -1);
						anim = ObjectAnimator.ofFloat(views[i], NINE_OLD_TRANSLATION_Y, 0, upFactor);
					}

					if (anim != null) {
						anim.start();
					}
				}
			}

		};
	}

	private AnimatorListener getAnimationListener (final ACard mACard, final RelativeLayout frameLayout, final int
			index, final View clickedCard) {
		return new AnimatorListener() {
			@Override
			public void onAnimationStart (Animator animation) {
				if (index == 0) {
					View newFirstCard = frameLayout.getChildAt(1);
					handleFirstCard(newFirstCard);
				} else {
					//                    clickedCard.setBackgroundColor(clickedCard.getContext().getResources()
					// .getColor(android.R.color.white));
					//                    clickedCard.setBackgroundResource(R.drawable.card_background);
				}
				frameLayout.removeView(clickedCard);
				frameLayout.addView(clickedCard);
			}

			private void handleFirstCard (View newFirstCard) {
				newFirstCard.setBackgroundColor(newFirstCard.getContext().getResources().getColor(android.R.color.white));
				//                newFirstCard.setBackgroundResource(R.drawable.card_background);// R.drawable
				// .card_background
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
						.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				int top = 0;
				int bottom = 0;

				top = 2 * Utils.convertDpToPixelInt(mContext, 8) + Utils.convertDpToPixelInt(mContext, 1);
				bottom = Utils.convertDpToPixelInt(mContext, 12);

				lp.setMargins(0, top, 0, bottom);
				newFirstCard.setLayoutParams(lp);
//				newFirstCard.setPadding(0, Utils.convertDpToPixelInt(mContext, 8), 0, 0);
			}

			@Override
			public void onAnimationRepeat (Animator animation) {

			}

			@Override
			public void onAnimationEnd (Animator animation) {
				ACard card = mACard.remove(index);
				mACard.add(card);

				mStackAdapter.setItems(mACard, mACard.getPosition());

				// refresh();
				mStackAdapter.notifyDataSetChanged();
			}

			@Override
			public void onAnimationCancel (Animator animation) {

			}
		};
	}

	public void setPosition (int position) {
		mPosition = position;
	}

	public int getPosition () {
		return mPosition;
	}

	public void add (ACard newCard) {
		mACardsAList.add(newCard);
	}

	public ACard remove (int index) {
		return mACardsAList.remove(index);
	}

	public void setAdapter (StackAdapter stackAdapter) {
		mStackAdapter = stackAdapter;
	}

	protected float convertDpToPixel (float dp) {
		return Utils.convertDpToPixel(mContext, dp);
	}

}
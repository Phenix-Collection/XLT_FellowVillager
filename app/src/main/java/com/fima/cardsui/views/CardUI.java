package com.fima.cardsui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fima.cardsui.adapter.StackAdapter;
import com.fima.cardsui.objects.ACard;
import com.xianglin.fellowvillager.app.R;

import java.util.ArrayList;

/**
 * 在xml文件中使用，得到这个对象后，就可以调用addCard()、addCardToLastStack()、refresh()等方法了
 */
public class CardUI extends FrameLayout {
	private Context					mContext;
	private ArrayList<ACard>		mACardsAList;
	private View					mPlaceholderView;
	private QuickReturnListView		mQuickReturnListView;
	private int						mQuickReturnHeight;
	private int						mCachedVerticalScrollRange;
	private boolean					mSwipeable			= false;
	// 栈适配器
	private StackAdapter			mAdapter;

	public CardUI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	public CardUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	public CardUI(Context context) {
		super(context);
		initData(context);
	}

	/*************************************************************************************************************/

	private void initData(Context context) {
		mContext = context;
		mACardsAList = new ArrayList<ACard>();
		LayoutInflater inflater = LayoutInflater.from(context);
		// cards_view是需要的
		inflater.inflate(R.layout.cards_view, this);
		mQuickReturnListView = (QuickReturnListView) findViewById(R.id.listView);
	}

	public void refresh() {
		if (mAdapter != null) {
			mAdapter.setSwipeable(mSwipeable);
			mAdapter.setItems(mACardsAList);
			return;
		}
		mAdapter = new StackAdapter(mContext, mACardsAList, mSwipeable);
		if (mQuickReturnListView != null) {
			mQuickReturnListView.setAdapter(mAdapter);
		}
	}

	public void addCard(ACard card) {
		addCard(card, false);
	}

	public void addCard(ACard card, boolean refresh) {
		mACardsAList.add(card);
		if (refresh) {
			refresh();
		}
	}

	public void addCardToLastStack(ACard card) {
		addCardToLastStack(card, false);
	}

	public void addCardToLastStack(ACard card, boolean refresh) {
		if (mACardsAList.isEmpty()) {
			addCard(card, refresh);
			return;
		}
		int lastItemPos = mACardsAList.size() - 1;
		ACard mACard = (ACard) mACardsAList.get(lastItemPos);
		mACard.add(card);
		mACardsAList.set(lastItemPos, mACard);
		if (refresh) {
			refresh();
		}
	}

	public void addStack(ACard mACard) {
		addStack(mACard, false);
	}

	public void addStack(ACard mACard, boolean refresh) {
		mACardsAList.add(mACard);
		if (refresh) {
			refresh();
		}
	}
	
	public void setSwipeable(boolean b) {
		mSwipeable = b;
	}

}

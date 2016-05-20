package com.fima.cardsui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fima.cardsui.objects.ACard;

import java.util.ArrayList;

public class StackAdapter extends BaseAdapter {

	private Context					mContext;
	private ArrayList<ACard>	mACardsAList;
	private boolean					mSwipeable;

	public StackAdapter(Context context, ArrayList<ACard> stacks, boolean swipable) {
		mContext = context;
		mACardsAList = stacks;
		mSwipeable = swipable;
	}

	@Override
	public int getCount() {
		return mACardsAList.size();
	}

	@Override
	public ACard getItem(int position) {
		return (ACard) mACardsAList.get(position);
	}
	
	public void setItems(ArrayList<ACard> aCard) {
		mACardsAList = aCard;
		// 刷新
		notifyDataSetChanged();
	}
	
	public void setItems(ACard aCard, int position) {
		// 把哪个对象放到哪个位置
		mACardsAList.set(position, aCard);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ACard mACard = getItem(position);
		mACard.setAdapter(this);
		mACard.setPosition(position);
		convertView = mACard.getView(mContext, convertView, mSwipeable);
		return convertView;
	}

	public void setSwipeable(boolean b) {
		mSwipeable = b;
	}

}
package com.xianglin.fellowvillager.app.chat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.Utils;

public class SideBar extends View {
	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	private int m_nItemHeight = Utils.dipToPixel(getContext(), 15);
	Paint paint;
	public SideBar(Context context) {
		super(context);
//		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init();
	}

	public void init(char [] l) {
//		l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
//				'W', 'X', 'Y', 'Z' };

		this.l = l;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		init();
	}

	public void setListView(ListView _list) {
		list = _list;
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + l[idx]);
			if (sectionIndexter == null) {
				ListAdapter listAdpater =  list.getAdapter();

				if (listAdpater instanceof HeaderViewListAdapter) {
					listAdpater = ((HeaderViewListAdapter) list.getAdapter()).getWrappedAdapter();;
				}
				if (listAdpater instanceof SectionIndexer){
					sectionIndexter = (SectionIndexer) listAdpater;

				}

			}
			   //if(sectionIndexter==null)return true;
				int position = sectionIndexter.getPositionForSection(l[idx]);
				if (position == -1) {
					return true;
				}
				list.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width=getMeasuredWidth();
		int height;
		if(l==null)height=0;
		else height =m_nItemHeight*l.length;

		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		} else
		{
			int desired = (int) (getPaddingTop() + height + getPaddingBottom());
			height = desired;
		}
		setMeasuredDimension(width, height);
	}

	protected void onDraw(Canvas canvas) {
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.gray));
		paint.setTextSize(Utils.dipToPixel(getContext(), 12));
		// paint.setTextSize(20);
		// paint.setColor(0xff595c61);
		Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		paint.setTypeface(font);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; l!=null&&i < l.length; i++) {
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
					+ (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}

	public void invalidate(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		}).start();
	}
}

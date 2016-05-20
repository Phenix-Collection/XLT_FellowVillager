package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.Utils;

public class BladeView extends View {
	private OnItemClickListener mOnItemClickListener;
//	String[] b = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
//			"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
//			"Y", "Z" };
    Character[]  b;
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	private PopupWindow mPopupWindow;
	private TextView mPopupText;
	private Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what){
				case 1:
					requestLayout();
					break;
			}
			super.dispatchMessage(msg);
		}
	};
	private int m_nItemHeight = Utils.dipToPixel(getContext(),15);
	public BladeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setAlpha(Character[]   alphaList){
		this.b=alphaList;
	}

	public BladeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BladeView(Context context) {
		super(context);
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		if (showBkg) {
//			canvas.drawColor(Color.parseColor("#00000000"));
//		}
//        if(b==null||b.length==0)
//			return;
//		int height = getHeight();
//		int width = getWidth();
//		int singleHeight = height / b.length;
//		for (int i = 0; i < b.length; i++) {
//			paint.setColor(Color.BLACK);
//			paint.setTextSize(Utils.dipToPixel(getContext(),12));
//			paint.setTypeface(Typeface.DEFAULT_BOLD);
//			paint.setFakeBoldText(true);
//			paint.setAntiAlias(true);
//			if (i == choose) {
//				paint.setColor(Color.parseColor("#3399ff"));
//			}
//			float xPos = width / 2 - paint.measureText(String.valueOf(b[i])) / 2;
//			float yPos = singleHeight * i + singleHeight;
//			canvas.drawText(String.valueOf(b[i]), xPos, yPos, paint);
//			paint.reset();
//		}
//
//	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width=getMeasuredWidth();
		int height;
		if(b==null)height=0;
		else height =m_nItemHeight*b.length;

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
		paint.setColor(getResources().getColor(R.color.app_text_color));
		paint.setTextSize(Utils.dipToPixel(getContext(), 11));
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setFakeBoldText(true);
		paint.setAntiAlias(true);
		// paint.setTextSize(20);
		// paint.setColor(0xff595c61);
		//Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
		//paint.setTypeface(font);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; b!=null&&i < b.length; i++) {
			canvas.drawText(String.valueOf(b[i]), widthCenter, m_nItemHeight
					+ (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		//final int c = (int) (y / getHeight() * b.length);

		int c =(int) y / m_nItemHeight;
		if (c >= b.length) {
			c = b.length - 1;
		} else if (c < 0) {
			c = 0;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c) {
				if (c >=0 && c < b.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			dismissPopup();
			invalidate();
			break;
		}
		return true;
	}

	private void showPopup(int item) {
		if (mPopupWindow == null) {
			handler.removeCallbacks(dismissRunnable);
			mPopupText = new TextView(getContext());
			mPopupText.setBackgroundColor(getResources().getColor(R.color.app_title_bg));
			mPopupText.setTextColor(Color.WHITE);
			mPopupText.setTextSize(30);
			mPopupText.setGravity(Gravity.CENTER);
			mPopupWindow = new PopupWindow(mPopupText, DeviceInfoUtil.dip2px(50), DeviceInfoUtil.dip2px(50));
		}

//		String text = "";
//		if (item == 0) {
//			text = "#";
//		} else {
//			text = Character.toString((char) ('A' + item - 1));
//		}
		mPopupText.setText(b[item] + "");
		if (mPopupWindow.isShowing()) {
			mPopupWindow.update();
		} else {
			mPopupWindow.showAtLocation(getRootView(),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}

	private void dismissPopup() {
		handler.postDelayed(dismissRunnable, 800);
	}

	Runnable dismissRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
			}
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void invalidateAlpha(){
		handler.sendEmptyMessage(1);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void performItemClicked(int item) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(String.valueOf(b[item]));
			showPopup(item);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(String s);
	}

}

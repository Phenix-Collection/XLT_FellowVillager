package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.Utils;

public class PinyinSideBar extends View {
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
//	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
//			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
//			"W", "X", "Y", "Z", "#" };
	char[] b;
	private int m_nItemHeight = Utils.dipToPixel(getContext(),15);
	private int choose = -1;// 选中
	private Paint paint = new Paint();

	private TextView mTextDialog;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}


	public PinyinSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PinyinSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PinyinSideBar(Context context) {
		super(context);
	}

	public void setAlpha(char[] alphaList){
		this.b=alphaList;
	}

	/**
	 * 重写这个方法
	 */
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		// 获取焦点改变背景颜色.
//		int height = getHeight();// 获取对应高度
//		int width = getWidth(); // 获取对应宽度
//		if(b==null)return;
//		int singleHeight = height / b.length;// 获取每一个字母的高度
//
//		for (int i = 0; i < b.length; i++) {
//			paint.setColor(getResources().getColor(R.color.gray));
//			// paint.setColor(Color.WHITE);
//			paint.setTypeface(Typeface.DEFAULT_BOLD);
//			paint.setAntiAlias(true);
//			paint.setTextSize(Utils.dipToPixel(getContext(), 15));
//			// 选中的状态
//			if (i == choose) {
//				paint.setColor(Color.parseColor("#3399ff"));
//				paint.setFakeBoldText(true);
//			}
//			// x坐标等于中间-字符串宽度的一半.
//			float xPos = width / 2 - paint.measureText(b[i]) / 2;
//			float yPos = singleHeight * i + singleHeight;
//			canvas.drawText(b[i], xPos, yPos, paint);
//			paint.reset();// 重置画笔
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
		final float y = event.getY();// 点击y坐标
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		if(b==null)return false;
		final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
			case MotionEvent.ACTION_UP:
				setBackgroundDrawable(new ColorDrawable(0x00000000));
				choose = -1;//
				invalidate();
				if (mTextDialog != null) {
					mTextDialog.setVisibility(View.INVISIBLE);
				}
				break;

			default:
//				setBackgroundResource(R.drawable.sidebar_background);
				if (oldChoose != c) {
					if (c >= 0 && c < b.length) {
						if (listener != null) {
							listener.onTouchingLetterChanged(b[c]+"");
						}
						if (mTextDialog != null) {
							mTextDialog.setText(b[c]+"");
							mTextDialog.setVisibility(View.VISIBLE);
						}

						choose = c;
						invalidate();
					}
				}

				break;
		}
		return true;
	}

	/**
	 * 向外公开的方法
	 *
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 *
	 * @author coder
	 *
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
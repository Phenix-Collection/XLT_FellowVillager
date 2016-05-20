package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.xianglin.fellowvillager.app.R;
/**
 * Created by yangjibin on 16/3/29.
 */
public class CircleProgressView extends ProgressBar {

    private static final int DEFAULT_REACHED_COLOR = 0XFFf0f0f0;
    private static final int DEFAULT_UNREACHED_COLOR = 0xFFf46442;
    private static final int DEFAULT_WIDTH_REACHED_PROGRESS_BAR = 3;
    private static final int DEFAULT_WIDTH_UNREACHED_PROGRESS_BAR = 2;

    protected Paint mPaint = new Paint();

    /**
     * height of reached progress bar
     */
    protected int mReachedProgressBarHeight = dp2px(DEFAULT_WIDTH_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     */
    protected int mReachedBarColor = DEFAULT_REACHED_COLOR;
    /**
     * color of unreached bar
     */
    protected int mUnReachedBarColor = DEFAULT_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_WIDTH_UNREACHED_PROGRESS_BAR);

    private int mRadius = dp2px(30);
    private int mMaxPaintWidth;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        obtainStyledAttributes(attrs);

        mPaint.setColor(mReachedBarColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        mMaxPaintWidth = Math.max(mReachedProgressBarHeight,
                mUnReachedProgressBarHeight);
        int expect = mRadius * 2 + mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        int realWidth = Math.min(width, height);
        mRadius = (realWidth - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;

        setMeasuredDimension(realWidth, realWidth);
    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleProgressView);

        mRadius = (int) attributes.getDimension(R.styleable.CircleProgressView_circleRadius, mRadius);
        mReachedBarColor = attributes
                .getColor(R.styleable.CircleProgressView_reachedColor, DEFAULT_REACHED_COLOR);
        mUnReachedBarColor = attributes
                .getColor(R.styleable.CircleProgressView_unreachedColor, DEFAULT_UNREACHED_COLOR);
        mReachedProgressBarHeight = (int) attributes
                .getDimension(R.styleable.CircleProgressView_reachedWidth, mReachedProgressBarHeight);
        mUnReachedProgressBarHeight = (int) attributes
                .getDimension(R.styleable.CircleProgressView_unreachedWidth, mUnReachedProgressBarHeight);
        attributes.recycle();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop()
                + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        // draw unreaded bar
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        // draw reached bar
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mReachedProgressBarHeight);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90,
                sweepAngle, false, mPaint);

        canvas.restore();
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

}

package com.xianglin.fellowvillager.app.widget.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;

/**
 * 滑动tab
 *
 * @author bruce yang
 * @version v 1.0.0 2016/3/17
 */
public class SlidingTabView extends LinearLayout {

    private final int DEFAULT_INDICATOR_COLOR = 0xffffffff;//色卡默认颜色
    private final int DEFAULT_TEXT_SIZE = 18; //title默认字体大小
    private final int DEFAULT_TEXT_PADDING = 16;  //默认padding
    private final int DEFAULT_INDICATOR_HEIGHT = 3;  //indicator默认高度

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mIndicatorColor = DEFAULT_INDICATOR_COLOR; //页卡的颜色
    private Paint mIndicatorPaint;//页卡画笔

    private TabItemName mItemName;  //获取 tab 每个 item 的信息
    private int mSelectedPosition;  //当前选中的页面位置
    private float mSelectionOffset;  //页面的偏移量

    private int mIndicatorHeight = DEFAULT_INDICATOR_HEIGHT;  //滑动指示器的高度

    public SlidingTabView(Context context) {
        this(context, null);
    }

    public SlidingTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取TypedArray和自定义属性的个数
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.SlidingTabView);
        int N = typedArray.getIndexCount();

        for (int i = 0; i < N; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingTabView_indicatorColor:
                    mIndicatorColor = typedArray.getColor(attr, DEFAULT_INDICATOR_COLOR);//获取页卡颜色值
                    break;
                case R.styleable.SlidingTabView_indicatorHeight:
                    mIndicatorHeight = (int) typedArray.getDimension(attr,
                            DEFAULT_INDICATOR_HEIGHT * getResources().getDisplayMetrics().density);//获取页卡的高度

                    break;
            }
        }
        /*释放TypedArray*/
        typedArray.recycle();
        initView();
    }

    private void initView() {
        setWillNotDraw(false);
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getChildCount() == 0) {
            return;
        }
        final int height = getHeight();
        /*当前页面的View tab*/
        View selectView = getChildAt(mSelectedPosition);
        /*计算开始绘制的位置*/
        int left = selectView.getLeft();
        int right = selectView.getRight();

        if (mSelectionOffset > 0) {
            View nextView = getChildAt(mSelectedPosition + 1);
            //如果有偏移量，重新计算开始绘制的位置
            left = (int) (mSelectionOffset * nextView.getLeft() + (1.0f - mSelectionOffset) * left);
            //如果有偏移量，重新计算结束绘制的位置
            right = (int) (mSelectionOffset * nextView.getRight() + (1.0f - mSelectionOffset) * right);
        }
        //绘制滑动的页卡
        canvas.drawRect(left + 100, height - mIndicatorHeight, right - 100, height, mIndicatorPaint);

    }

    /**
     * 设置viewPager，初始化SlidingTab，在这个方法中为SlidingLayout设置内容
     *
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        removeAllViews();  //先移除所以已经填充的内容

        if (viewPager == null) {
            throw new RuntimeException("ViewPager不能为空");
        }
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new InternalViewPagerChange());
        populateTabLayout();
    }

    public void setViewPagerOnChangeListener(ViewPager.OnPageChangeListener pagerOnChangeListener) {
        mListener = pagerOnChangeListener;
    }

    /**
     * 填充layout，设置其内容
     */
    private void populateTabLayout() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabOnClickListener = new TabOnClickListener();
        mItemName = (TabItemName) adapter;
        for (int i = 0; i < adapter.getCount(); i++) {
            TextView textView = createDefaultTabView(getContext());
            textView.setOnClickListener(tabOnClickListener);
            textView.setText(mItemName.getTabName(i));
            addView(textView);
        }
    }

    /**
     * 创建默认的TabItem
     *
     * @param context
     * @return
     */
    private TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
        textView.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        textView.setLayoutParams(layoutParams);
        int padding = (int) (DEFAULT_TEXT_PADDING * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        textView.setAllCaps(true);
        return textView;
    }

    private class TabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < getChildCount(); i++) {
                if (v == getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }

    /**
     * @param position
     * @param positionOffset
     */
    private void viewPagerChange(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    private class InternalViewPagerChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            /*
            * 规律：
            * 当positionOffset为0时，position就是当前view的位置
            * 当positionOffset不为0时，position为左边页面的位置
            *                         position + 1为右边页面的位置
            * */

            viewPagerChange(position, positionOffset);
            if (mListener != null) {
                mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {

            if (mListener != null) {
                mListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (mListener != null) {
                mListener.onPageScrollStateChanged(state);
            }
        }
    }

    /**
     * 回调获取 item name 的接口
     */
    public interface TabItemName {
        /**
         * 获取 tab name
         *
         * @param position
         * @return
         */
        String getTabName(int position);
    }
}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.chat.utils.AnimationUtil;
import com.xianglin.fellowvillager.app.widget.TopView;

import java.util.List;

/**
 * @author Aizaz AZ
 */
public class BasePhotoPreviewActivity extends BaseActivity implements OnPageChangeListener, OnClickListener {

    private ViewPager mViewPager;
    //	private RelativeLayout layoutTop;
//	private ImageButton btnBack;
//	private TextView tvPercent;
    protected List<PhotoModel> photos;
    //	private Button btn_send;
    protected int current;


    TopView topView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photopreview);
//		layoutTop = (RelativeLayout) findViewById(R.id.layout_top_app);
//		btnBack = (ImageButton) findViewById(R.id.btn_back_app);
//		tvPercent = (TextView) findViewById(R.id.tv_percent_app);
//		btn_send= (Button) findViewById(R.id.btn_send);
        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        topView = (TopView) findViewById(R.id.topview);
//		btnBack.setOnClickListener(this);
//		btn_send.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);

        //overridePendingTransition(R.anim.activity_alpha_action_in, 0); // 渐入效果

        topView.initCommonTop(R.string.activity_photo_title);

    }

    /**
     * 绑定数据，更新界面
     */
    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photos == null) {
                return 0;
            } else {
                return photos.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoPreview photoPreview = new PhotoPreview(getApplicationContext());
            ((ViewPager) container).addView(photoPreview);
            photoPreview.loadImage(photos.get(position));
            photoPreview.setOnClickListener(photoItemClickListener);
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };
    protected boolean isUp;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
        updatePercent();
    }

    protected void updatePercent() {
        //tvPercent.setText((current + 1) + "/" + photos.size());
    }

    /**
     * 图片点击事件回调
     */
    private OnClickListener photoItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isUp) {
//				new ActivityAnimUtil(getApplicationContext(), R.anim.translate_up)
//						.setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
                isUp = true;
            } else {
//				new ActivityAnimUtil(getApplicationContext(), R.anim.translate_down_current)
//						.setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
                isUp = false;
            }
        }
    };
}

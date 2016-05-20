package com.xianglin.fellowvillager.app.widget.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xianglin.fellowvillager.app.R;


public class RefreshImageButton extends LinearLayout {
	private Animation animation;
	private Context mContext;
	private ImageView imageView;

	private boolean isRotate = false;
	
	public void setImageResource(int resId){
		imageView.setImageResource(resId);
	}
	public RefreshImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;

		imageView = new ImageView(mContext);
		imageView.setImageResource(R.drawable.webview_refresh);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		this.addView(imageView, lp);

		animation = AnimationUtils.loadAnimation(mContext,R.anim.image_rotate);
		LinearInterpolator lin = new LinearInterpolator();  
		animation.setInterpolator(lin);  
	}

	// 开始动画
	public void rotate() {
		if (animation != null) {
			imageView.startAnimation(animation);
			isRotate = true;
		}

	}

	// 停止动画
	public void stopRotate() {
		if (animation != null) {
			imageView.clearAnimation();
			isRotate = false;
		}
	}
	
	public boolean isRotate() {
		return isRotate;
	}
}

/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;

public class PhotoPreview extends LinearLayout implements OnClickListener {

	private ProgressBar pbLoading;
	//private GestureImageView ivContent;
	private OnClickListener l;
    private SimpleDraweeView ivContent;
	public PhotoPreview(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_photopreview, this, true);

		pbLoading = (ProgressBar) findViewById(R.id.pb_loading_vpp);
		//ivContent = (GestureImageView) findViewById(R.id.iv_content_vpp);
		ivContent = (SimpleDraweeView) findViewById(R.id.iv_content_vpp);
		//ivContent.setOnClickListener(this);
	}

	public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
		this(context);
	}

	public PhotoPreview(Context context, AttributeSet attrs) {
		this(context);
	}

	public void loadImage(PhotoModel photoModel) {
//		loadImage("file://" + photoModel.getOriginalPath());

		Uri uri = Uri.parse("file://" + photoModel.getOriginalPath());
		ImageRequest request = ImageRequestBuilder
				.newBuilderWithSource(uri)
				.setResizeOptions(
						new ResizeOptions(
								DeviceInfoUtil.dip2px(360),
								DeviceInfoUtil.dip2px(480)
						)
				)
				.build();
		DraweeController controller = Fresco.newDraweeControllerBuilder()
				.setImageRequest(request)
				.setOldController(ivContent.getController())
				.build();

		ivContent.setController(controller);

	}

	private void loadImage(String path) {
		ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				//ivContent.setImageBitmap(loadedImage);
				ivContent.setImageBitmap(loadedImage);
				
				pbLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				//ivContent.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_loadfailed));
				pbLoading.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.l = l;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_content_vpp && l != null){
			//l.onClick(ivContent);
		}
	};

}

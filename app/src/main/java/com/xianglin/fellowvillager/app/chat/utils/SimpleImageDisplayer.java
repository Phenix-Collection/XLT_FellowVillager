package com.xianglin.fellowvillager.app.chat.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class SimpleImageDisplayer implements BitmapDisplayer {

	private int targetWidth;

	public SimpleImageDisplayer(int targetWidth) {
		this.targetWidth = targetWidth;
	}

	// @Override
	// public Bitmap display(Bitmap bitmap, ImageView imageView,
	// LoadedFrom loadedFrom) {

	// if (bitmap != null) {
	// bitmap = ImageUtils.resizeImageByWidth(bitmap, targetWidth);
	// }
	// imageView.setImageBitmap(bitmap);
	// return bitmap;
	// }

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware,
			LoadedFrom loadedFrom) {

		if (bitmap != null) {
			bitmap = Util.resizeImageByWidth(bitmap, targetWidth);
		}
		imageAware.setImageBitmap(bitmap);
	}

}

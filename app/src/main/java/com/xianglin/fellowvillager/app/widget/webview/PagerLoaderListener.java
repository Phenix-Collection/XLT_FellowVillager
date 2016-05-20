package com.xianglin.fellowvillager.app.widget.webview;

import android.graphics.Bitmap;
import android.webkit.WebView;

public interface PagerLoaderListener {
	void onPageStarted(WebView view, String url, Bitmap favicon);
	
	public void onPageFinished(WebView view, String url);
}

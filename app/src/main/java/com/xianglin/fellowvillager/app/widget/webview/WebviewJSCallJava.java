package com.xianglin.fellowvillager.app.widget.webview;


import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

public class WebviewJSCallJava {

	private Context context;
	private BBWebCore webView;
	private Handler handler = new Handler();

	public WebviewJSCallJava(Context context,BBWebCore webView) {
		this.context = context;
		this.webView = webView;
	}

	@JavascriptInterface
	public void savingTakingData(String callBack) {

		}


	@JavascriptInterface
	public void nativeCallJavaScript(final String callbackName) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				webView.callJavascript("App.nativeCallbacks." + callbackName,
						null);
			}
		});
	}


}

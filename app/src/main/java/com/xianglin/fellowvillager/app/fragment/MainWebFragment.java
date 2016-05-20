package com.xianglin.fellowvillager.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.CaptureActivity;
import com.xianglin.fellowvillager.app.activity.MainActivity;
import com.xianglin.fellowvillager.app.activity.WebviewActivity_;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.widget.webview.BBViewClient;
import com.xianglin.fellowvillager.app.widget.webview.BBWebCore;
import com.xianglin.fellowvillager.app.widget.webview.BBWebCoreClient;
import com.xianglin.fellowvillager.app.widget.webview.HostJsScope;
import com.xianglin.fellowvillager.app.widget.webview.WebviewJSCallJava;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


/**
 */
@EFragment(R.layout.fragment_main_webview)
public class MainWebFragment extends BaseFragment implements View.OnClickListener {

    @ViewById(R.id.progress_bar)
    ProgressBar mProgressBar;
    @ViewById(R.id.webview)
    static
    BBWebCore webView;
    private static final String BASE_URL = "https://mai.xianglin.cn/index.php/wap/";
    @SuppressLint("AddJavascriptInterface")
    @AfterViews
    void init() {

        webView.getSettings().setUserAgentString("One Account Android;Mozilla");
        webView.setNetworkAvailable(true);
        webView.addJavascriptInterface(new WebviewJSCallJava(mContext, webView), "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (mBaseActivity.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                webView.setWebContentsDebuggingEnabled(true);
            }
        }

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebChromeClient(new CustomChromeClient("HostApp", HostJsScope.class));
        webView.setWebViewClient(new BBWebCoreClient(mContext) {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String c = CookieManager.getInstance().getCookie(url);
                CookieSyncManager.getInstance().sync();

                //通知webview底部bar页面加载完成
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!BASE_URL.equals(url)) {
                    WebviewActivity_.intent(MainWebFragment.this).extra("url",url).start();
                    return true;
                }else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
//        CookieSyncManager.createInstance(mContext);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
//        CookieSyncManager.getInstance().sync();
        webView.loadUrl(BASE_URL);

    }

    public static void setWebViewUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result").trim();
            if (!TextUtils.isEmpty(scanResult.trim())) {
                if (scanResult.startsWith("http")) {
                    webView.loadUrl(scanResult);
                    ToastUtils.showCenterToast(scanResult, mContext);
                } else {
                    ToastUtils.showCenterToast(scanResult, mContext);
                }
            } else {
                ToastUtils.showCenterToast("扫描失败，请重试！", mContext);
            }
        }
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(mBaseActivity, CaptureActivity.class));
        if (mBaseActivity instanceof MainActivity) {
            ((MainActivity) mContext).setTopViewTitle("");
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

    }

    public class CustomChromeClient extends BBViewClient {

        public CustomChromeClient(String injectedName, Class injectedCls) {
            super(injectedName, injectedCls);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            ToastUtils.showCenterToast(title, mContext);
        }


    }


}

package com.xianglin.fellowvillager.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.webview.BBViewClient;
import com.xianglin.fellowvillager.app.widget.webview.BBWebCore;
import com.xianglin.fellowvillager.app.widget.webview.BBWebCoreClient;
import com.xianglin.fellowvillager.app.widget.webview.WebviewJSCallJava;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_webview_layout)
public class WebviewActivity extends BaseActivity {
    private List<String> mListUrl = new ArrayList<String>();
    @ViewById(R.id.progress_bar)
    ProgressBar mProgressBar;
    @ViewById(R.id.webview)
    BBWebCore webView;
    @ViewById(R.id.topview)
    TopView mTopView;
    @ViewById(R.id.ll_back_layout)
    LinearLayout backLayout;
    private String baseUrl = "https://mai.xianglin.cn/index.php/wap/";
    private boolean isBack = false;

    @AfterViews
    void init() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mTopView.setLeftImageResource(R.drawable.icon_close);
        baseUrl = this.getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(baseUrl)) {
            mTopView.setAppTitle("乡邻购");
            baseUrl = "https://mai.xianglin.cn/index.php/wap/";
        }
        mListUrl.add(baseUrl);

        webView.getSettings().setUserAgentString("One Account Android;Mozilla");
        webView.setNetworkAvailable(true);

        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.addJavascriptInterface(new WebviewJSCallJava(this, webView), "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
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
        webView.setWebChromeClient(new CustomChromeClient(this));
        webView.setWebViewClient(new BBWebCoreClient(this) {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mTopView.setAppTitle(view.getTitle());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mTopView.setAppTitle(view.getTitle());
                super.onPageFinished(view, url);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
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
        webView.loadUrl(baseUrl);
    }

    @Click(R.id.ll_back_layout)
    void onBackUrlOrFinish() {
        finish();
    }


    public class CustomChromeClient extends BBViewClient {

        public CustomChromeClient(Context context) {
            super(context);
        }

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
            mTopView.setAppTitle(title);
        }


    }

    @Override
    public void onBackPressed() {
        if (webView == null) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}

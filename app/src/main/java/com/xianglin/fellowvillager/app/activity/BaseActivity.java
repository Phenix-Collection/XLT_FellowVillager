/**
 * 乡邻小站
 * Copyright (c) 2011-2015 xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.receiver.HomeReceiver;
import com.xianglin.fellowvillager.app.utils.ACache;
import com.xianglin.fellowvillager.app.utils.ActivityManagerTool;
import com.xianglin.fellowvillager.app.utils.StyleManager;
import com.xianglin.fellowvillager.app.utils.ThreadPool;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.widget.dialog.LoadingDialog;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.concurrent.ExecutorService;

public class BaseActivity extends FragmentActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected Context context;
    protected LoadingDialog loadingDialog;
    private ActivityManager mActivityManager;
    private ACache mACache;// 数据缓存、用于Activity之间传值时使用
    private HomeReceiver receiver = new HomeReceiver();
    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //	 setContentView(R.layout.title_bar);  //by pengyang 继承BaseActivity需要把这句注释掉,
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityManagerTool.getActivityManager().add(this);

        if (loadingDialog == null) {
            synchronized (this) {
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(context);
                }
            }
        }

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        }

        if (mACache == null) {
            mACache = ACache.get(context);
        }

        StyleManager styleManager = StyleManager.getInstance(this);
        styleManager.setStatusBarStyle(this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogCatLog.d(TAG, "开始处理");
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        //RepeatSendMessageManager.startRepeatSendMessage();// true -> false -> start
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogCatLog.d(TAG, "停止处理");
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
        hideLoadingDialog();
        //RepeatSendMessageManager.stopRepeatSendMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        ActivityManagerTool.getActivityManager().removeActivity(this);
    }

    /**
     * @Description 不固定线程池
     * @author <a href="http://t.cn/RvIApP5">ceychen@foxmail.com</a>
     * @date 2014-8-19 15:42:06
     */
    public static ExecutorService cachedPool() {
        return ThreadPool.getCachedThreadPool();
    }

    /**
     * @Description 单线程池
     * @author <a href="http://t.cn/RvIApP5">ceychen@foxmail.com</a>
     * @date 2014-8-19 15:47:27
     */
    public static ExecutorService singlePool() {
        return ThreadPool.getSingleThreadPool();
    }

    /**
     * 当前Activity退出时的动画
     */
    public void animRightToLeft() {
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_to);
    }

    /**
     * 当前Activity进入时的动画
     */
    public void animLeftToRight() {
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void animBottomToTop() {
        overridePendingTransition(R.anim.roll_up_in, R.anim.roll);
    }

    public void animTopToBottom() {
        overridePendingTransition(R.anim.roll, R.anim.roll_down_out);
    }

    /**
     * 自定义吐司
     *
     * @param msg
     */
    public void tip(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastForLong(
                        context,
                        msg
                );
            }
        });

    }

    /**
     * 自定义吐司
     *
     * @param resId
     */
    public void tip(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toastForLong(
                        context,
                        getString(resId)
                );
            }
        });
    }


    /**
     * 显示加载框
     */
    public void showLoadingDialog() {
        if (loadingDialog == null || loadingDialog.isShowing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    /**
     * 关闭加载框
     */
    public void hideLoadingDialog() {
        if (loadingDialog == null || !loadingDialog.isShowing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loadingDialog!=null){
                    loadingDialog.dismiss();
                    loadingDialog = null;
                }

            }
        });
    }

    /**
     * 打开下一个activity
     */
    public void startNextActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
        animLeftToRight();
    }

    /**
     * 为子类设定一个公共的结束Activity的方法
     */
    public void closeActivity() {
        System.gc();
        this.finish();
        animRightToLeft();
    }

    /**
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            XLApplication.isHome = true;
        }
        // 继续执行父类的其他点击事件
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }


}
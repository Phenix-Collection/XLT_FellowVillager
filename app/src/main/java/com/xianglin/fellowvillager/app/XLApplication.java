/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app;

import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.constants.ENVController;
import com.xianglin.fellowvillager.app.constants.ReleaseSwitch;
import com.xianglin.fellowvillager.app.exception.CustomCrashHandler;
import com.xianglin.fellowvillager.app.longlink.LongLinkUtils;
import com.xianglin.fellowvillager.app.longlink.handler.RepeatSendMessageHandler;
import com.xianglin.fellowvillager.app.longlink.listener.MessageListenerManager;
import com.xianglin.fellowvillager.app.longlink.longlink.LongLinkServiceManager;
import com.xianglin.fellowvillager.app.longlink.longlink.servicelistener.LongLinkServiceConnectListener;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.db.DBUtil;
import com.xianglin.mobile.common.filenetwork.model.AddressManager;
import com.xianglin.mobile.common.info.AppInfo;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.mobile.common.logging.PerformanceLog;
import com.xianglin.mobile.common.utils.CacheSet;
import com.xianglin.mobile.framework.XiangLinApplication;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

//import com.xianglin.station.db.InitDao;

/**
 * 应用上下文 需在要manifest文件中注册
 *
 * @author songdiyuan
 * @version $Id: XLApplication.java, v 1.0.0 2015-8-7 下午5:59:15 xl Exp $
 */
public class XLApplication extends XiangLinApplication {
    private static final String TAG = "XLApplication";
    /**
     * SO库加载
     * webp so库加载
     *
     */
    static {
        System.loadLibrary("webp");
        LogCatLog.d(TAG, "webp load");
    }

    /* 服务绑定相关变量 */
    public static boolean isConnect;// 是否链接
    public static String toChatId = "";// 当前聊天的id
    public static boolean isHome;//是否在点击home在后台
    public static LongLinkServiceManager longLinkServiceManager;
    public static MessageListenerManager messageListenerManager;
    public static RepeatSendMessageHandler repeatSendMessageHandler;

    /**
     * @see com.xianglin.mobile.framework.XiangLinApplication#onCreate()
     */
    @Override
    public synchronized void onCreate() {
        super.onCreate();
        initAssistData();
    }


    /**
     * 初始化辅助工具
     */
    private void initAssistData() {
        /* 初始化app基本信息 */
        AppInfo.createInstance(context);
        /* 初始化device基本信息 */
        DeviceInfo.createInstance(context);
        /* 初始化日志打印基本信息 */
        LogCatLog.init();
        /* 初始化性能日志记录器 */
        PerformanceLog.createInstance();
		/* 初始化文件目录 */
        FileUtils.createInstance(context);
        /* 腾讯bugly初始化*/
        CrashReport.initCrashReport(context, "900016489", false);

		/* 初始化全局异常捕获信息 */
        CustomCrashHandler customCrashHandler = CustomCrashHandler.getInstance();
        customCrashHandler.init(context);
		/* 初始化接口环境 */
        String xlEnv = ReleaseSwitch.XL_ENV_DEBUG_VALUE;
		/* 初始化数据库版本 */
        int xlDBVer = ReleaseSwitch.DB_DEBUG_VER;

        String appKey = AppInfo.getInstance().getMetaValue(this, "UM_APP_KEY","");
        String channelName = ReleaseSwitch.UM_CHANNEL_NAME;

        // 非调试模式(自动化打包)下从manifest文件里面读取meta值
        if (!AppInfo.getInstance().isDebuggable()) {
            xlEnv = AppInfo.getInstance().getMetaValue(context, "XL_ENV", xlEnv);

            channelName = AppInfo.getInstance().getMetaValue(this, "UM_CHANNEL_NAME", ReleaseSwitch.UM_CHANNEL_NAME);
            try {
                xlDBVer = Integer.parseInt(AppInfo.getInstance().getMetaValue(this, "XL_DB_VER", String.valueOf(ReleaseSwitch.DB_DEBUG_VER)));
            } catch (Exception e) {
                e.printStackTrace();
                xlDBVer = ReleaseSwitch.DB_DEBUG_VER;
            }
        }

        PersonSharePreference.setXlEnv(xlEnv);

        /*初始化接口环境地址*/
        ENVController.initEnv(context, xlEnv);



        /*为配合APPInfo init函数 渠道参数获取*/
        CacheSet.getInstance(context).putString("channels", channelName);
		
		/* 初始化图片数据加载 */
        initImageLoad();
		
		/* 初始化数据库 */
        initDataBase(xlDBVer);
		
		/* 初始化友盟数据统计*/
        initUMeng(appKey, channelName);

		/* 初始化长连接服务*/
        initLongLinkService();


		/*初始化消息监听管理*/
        initMessageListenerManager();

        /* 获取设备信息,用来提交到服务器生成设备id*/
        initDeviceInfo();

        /*初始化极光推送*/
        initJpush();
        
        setFileAddress(xlEnv,xlDBVer);

//        initFileServer();

        /*fresco库初始化*/
        Fresco.initialize(context);


    }

    /**
     * 初始化极光推送
     */
    private void initJpush() {
        JPushInterface.init(context);
        JPushInterface.setLatestNotificationNumber(context, 1);

    }

    /**
     * 初始化友盟数据统计
     *
     * @param appKey
     * @param channelName
     */
    private void initUMeng(String appKey, String channelName) {
        OnlineConfigAgent.getInstance().updateOnlineConfig(context, appKey, channelName);
        //日志加密传输
        AnalyticsConfig.enableEncrypt(true);
    }

    /**
     * 初始化图片数据加载
     */
    private void initImageLoad() {
        File cacheDir = FileUtils.createInstance(this).ImgCachePath;
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_picture_loading)
                .showImageOnFail(R.drawable.ic_picture_loadfailed)
                .cacheInMemory(true).cacheOnDisk(true)
                .resetViewBeforeLoading(true).considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        int cacheMaxSize = maxMemory /8;
/*
        if(deviceModel.contains("HM")||deviceModel.contains("GT-I9508V")){
            cacheMaxSize=maxMemory /50;
        } ;
*/

       //// TODO: 2016/3/22  sd不可用时,初始化错误,导致app无法启动 
        ImageLoaderConfiguration config1 = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(720, 1280)
                // default = device screen dimensions
                .diskCacheExtraOptions(720, 1280, null)
                .threadPoolSize(3)
                // default Thread.NORM_PRIORITY - 1
                .threadPriority(Thread.NORM_PRIORITY)
                // default FIFO
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(cacheMaxSize))
                //.memoryCacheSize(2 * 1024 * 1024)
                //.memoryCacheSizePercentage(13)
                // default
                .diskCache(new UnlimitedDiskCache(cacheDir))
                // default
                .diskCacheSize(50 * 1024 * 1024)
                // default
                .imageDownloader(new BaseImageDownloader(this))
                // default
                .imageDecoder(new BaseImageDecoder(false))
                // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                // default
                .defaultDisplayImageOptions(imageOptions).build();


        ImageLoader.getInstance().init(config1);
    }

    /**
     * 初始化数据库
     */
    private void initDataBase(int xlDBVer) {
        new DBUtil.Builder()

                .mName(BorrowConstants.INNER_DBNAME)
                .mVersion(xlDBVer).build()
                .init();
    }


    /**
     * 初始化长链接服务
     */
    private static void initLongLinkService() {
        longLinkServiceManager = LongLinkServiceManager.getInstance(context);
        //bind服务
        longLinkServiceManager.bindService(new LongLinkServiceConnectListener() {
            @Override
            public void connectSuccess(ILongLinkService mService, boolean mIsServiceBound) {

                //长连接 初始化

                new LongLinkUtils().initLongLink(longLinkServiceManager, mIsServiceBound, context);
                initMessage();
                isConnect = true;
            }

            @Override
            public void connectFailure(boolean mIsServiceBound) {
                isConnect = false;

                LogCatLog.d(TAG, "服务连接失败 ！重连服务机制 后面 再弄");
            }
        });
    }

    public static LongLinkServiceManager getLongLinkService() {
        if (longLinkServiceManager == null) {
            initLongLinkService();
        }
        return longLinkServiceManager;
    }



    /**
     * 初始化消息监听管理
     */
    private static void initMessageListenerManager() {

        messageListenerManager = MessageListenerManager.getInstance();
    }

    /**
     * 获取监听对象
     */
    public static MessageListenerManager getMessageListenerManager() {

        if (messageListenerManager == null) {

            initMessageListenerManager();

        }
        return messageListenerManager;
    }



    public void setFileAddress(String xlEnv,int xlDBVer) {
        if (AddressManager.setAddress(ENVController.FILE_HOST,
                                      ENVController.FILE_PORT,
                                      xlEnv,
                                      xlDBVer,
                                      BorrowConstants.INNER_DBNAME)) {
            LogCatLog.d(TAG, "设置文件地址成功");
        } else {
            LogCatLog.d(TAG, "设置文件地址失败");
        }

    }

    public static synchronized RepeatSendMessageHandler initMessage(){
        if (repeatSendMessageHandler == null) {
            repeatSendMessageHandler = new RepeatSendMessageHandler(context);
        }
        return repeatSendMessageHandler;
    }


    /**
     * 获取设备信息,用来提交到服务器生成设备id
     */
    private void initDeviceInfo() {
        //imei  Imsi
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        // 当android SerialId为空时用来代替它  同时保存cpu wifi bt 信息
        String xl_deviceid = DeviceInfoUtil.initXLAndroidID(this);
        PersonSharePreference.setAndroidSerialId(xl_deviceid);

        PersonSharePreference.setAndroidImei(deviceInfo.getImei());
        PersonSharePreference.setAndroidImsi(deviceInfo.getImsi());

    }


}

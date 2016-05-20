# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 指定代码的压缩级别
-optimizationpasses 5
# 不使用大小写混合
-dontusemixedcaseclassnames
# 混淆第三方jar
-dontskipnonpubliclibraryclasses
# 混淆时不做预校验
-dontpreverify
 # 混淆时记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保持哪些类不被混淆：四大组件，应用类，配置类等等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.app.Fragment

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

 # 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}



# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# 这个主要是在layout中写的onclick方法android:onclick="onClick"，不进行混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
 }

#保持注解
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepattributes Signature


###====================华丽丽的分割线=================================###

#极光push
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-ignorewarnings
-dontwarn com.xianglin.mobile.common.**
-dontwarn io.netty.**
-dontwarn com.umeng.analytics.**
-dontwarn org.apache.shiro.**
-dontwarn org.androidannotations.**
-dontwarn demo.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.aps.**
-dontwarn com.google.gson.**
-dontwarn com.sun.codemodel.**
-dontwarn u.aly.**


-keep class android.support.v4.**{*;}
-keep class com.xianglin.xlappcore.common.service.facade.**{*;}
-keep class com.xianglin.fellowvillager.app.rpc.service.**{*;}
-keep class com.xianglin.fellowvillager.app.model.**{*;}

-keep class com.xianglin.mobile.common.rpc.**{*;}
-keep class com.xianglin.mobile.common.transport.**{*;}
-keep class com.xianglin.mobile.common.filenetwork.model.**{*;}

-keep class com.xianglin.fellowvillager.app.chat.utils.SmileUtils{*;}

#cif
-keep class com.xianglin.cif.common.service.facade.**{*;}
#appserv
-keep class com.xianglin.appserv.common.service.facade.**{*;}




##AMap_V2.0 Location
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#腾讯bugly
-keep public class com.tencent.bugly.**{*;}


##======================umeng防混淆=================###
-keepclassmembers class * {
        public <init>(org.json.JSONObject);
}

-keep public class [com.xianglin.fellowvillager.app].R$*{
        public static final int *;
}

-keep class com.umeng.onlineconfig.OnlineConfigAgent {
        public <fields>;
        public <methods>;

}

-keep class com.umeng.onlineconfig.OnlineConfigLog {
        public <fields>;
        public <methods>;
}

-keep interface com.umeng.onlineconfig.UmengOnlineConfigureListener {
        public <methods>;
}

##======================fresco混淆==========================###
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**


##=========================eventbus==========================##
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


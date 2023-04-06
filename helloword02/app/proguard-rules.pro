# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
########################################### 本项目混淆 ####################################
-ignorewarnings
-dontwarn android.support.**
# ------不混淆实体类-----------

# ------不混淆泛型和反射----
-keepattributes Signature
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
# ------不混淆枚举----
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# -----不混淆序列化-------
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
# ------不混淆泛型和反射----
-keepattributes Signature
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
# ------不混淆集成自view的自定义控件----
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    void init*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# ------不混淆的第三方库------
-dontwarn com.fce.**
-keep class com.fce.** { *;}
# org
-dontwarn org.**
-keep class org.** { *;}
# android
-dontwarn android.**
-keep class android.** { *;}
-dontwarn com.android.**
-keep class com.android.** { *;}
# google
-dontwarn android.**
-keep class android.** { *;}
# androidx
-dontwarn androidx.**
-keep class androidx.** { *;}
# jdk
-dontwarn java.**
-keep class java.** { *;}
-dontwarn javax.**
-keep class javax.** { *;}
-dontwarn org.**
-keep class org.** { *;}
-dontwarn sun.misc.**
-keep class sun.misc.** { *;}
# squareup
-dontwarn com.squareup.**
-keep class com.squareup.** { *;}
# dinuscxj
-dontwarn com.dinuscxj.**
-keep class com.dinuscxj.** { *;}
# okhttp
-dontwarn okio.**
-keep class okio.** { *;}
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
# gson
-dontwarn ccom.google.gson.**
-keep class com.google.gson.** { *;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava2 RxAndroid
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }
# jetbrains
-dontwarn org.jetbrains.**
-keep class org.jetbrains.** { *; }
# junit
-dontwarn junit.**
-keep class junit.** { *; }
# apache
-dontwarn org.apache.**
-keep class apache.** { *; }
# 不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
########################################### 本项目混淆 ####################################
########################################### arouter ####################################
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class com.inovance.palmhouse.base.bridge.constant.** {
    *;
}
# 如果使用了 @Autowired 注入，需添加下面规则，保护实现
-keepclasseswithmembers class * {
    @com.alibaba.android.arouter.facade.annotation.Autowired <fields>;
}
########################################### arouter ####################################
########################################### persistentcookiejar ####################################
-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
########################################### persistentcookiejar ####################################
########################################### agentweb ####################################
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**
#Java 注入类不要混淆 ， 例如 sample 里面的 AndroidInterface 类 ， 需要 Keep 。
-keepclassmembers class com.just.agentweb.sample.common.AndroidInterface{ *; }
########################################### agentweb ####################################
########################################### glide ####################################
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
########################################### glide ####################################
########################################### videoplayer ####################################
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * extends com.inovance.palmhouse.base.widget.video.BaseVideoView { *; }
########################################### videoplayer ####################################
###########################################  BaseRecyclerViewAdapterHelper ####################################
-keep class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder {
 <init>(...);
}
###########################################  BaseRecyclerViewAdapterHelper ####################################
########################################### OkHttp3 ####################################
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
########################################### OkHttp3 ####################################
########################################### sharesdk ####################################
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class com.mob.**{*;}
-keep class com.bytedance.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.** {*;}
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
########################################### sharesdk ####################################
########################################### 腾讯兔小巢 ####################################
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
########################################### 腾讯兔小巢 ####################################
########################################### 百度埋码 ####################################
-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }
########################################### 百度埋码 ####################################
########################################### XXPermissions ####################################
-dontwarn com.hjq.permissions.**
########################################### XXPermissions ####################################
########################################### 百度埋码 ####################################
-keep class com.baidu.mobstat.** { *; }
-keep class com.baidu.bottom.** { *; }
########################################### 百度埋码 ####################################
########################################### monitor ####################################
-keep class com.lygttpod.monitor.** { *; }
########################################### monitor ####################################
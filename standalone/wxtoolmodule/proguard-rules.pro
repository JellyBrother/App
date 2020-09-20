# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bean/Developer/IDE/Android_SDK/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For _more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# Add any project specific keep options here:
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
# }
#-applymapping mapping.txt
#-printmapping proguard.map
#-----------------混淆配置设定------------------------------------------------------------------------
-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-optimizationpasses 7            # 指定代码的压缩级别
-dontusemixedcaseclassnames      # 是否使用大小写混合
-dontpreverify                   # 混淆时是否做预校验
-verbose                         # 混淆时是否记录日志
-keepattributes *Annotation*,InnerClasses,*EnclosingMethod*  # 保持注解
-ignorewarning                   # 忽略警告
-dontoptimize                    # 优化不优化输入的类文件
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    #优化
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#-----------------不需要混淆系统组件等-------------------------------------------------------------------
-dontwarn android.support.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.support.**   #如果有引用v4或者v7包，需添加
-keep class com.classtc.test.entity.**{*;}        #过滤掉自己编写的实体类
-keep class * extends java.lang.annotation.Annotation
-keep class com..android..commonui.business.reply.**{*;}


#----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在------------------------------------
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

  #保持枚举 enum 类不被混淆
-keepclassmembers enum * {
   public static **[] values();
   public static ** valueOf(java.lang.String);}

-keepclassmembers class * {
   public void *ButtonClicked(android.view.View);}

#不混淆资源类
-keepclassmembers class **.R$* {
   public static <fields>;}

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
   native <methods>;}

################ 保护EventBus的注解 ##################
-keepclassmembers class ** {@org.greenrobot.eventbus.Subscribe <methods>;}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);}

################ layout 中写的onclick方法android:onclick="onClick"，不进行混淆 ##################
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);}

################ webview  ##################
-keepattributes *JavascriptInterface*
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {public *;}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);}
-keepclassmembers class cn.xx.xx.Activity$AppAndroid {public *;}

################ retrofit ##################
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**

################ gson ##################
-dontwarn rx.*
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#-----------------不需要混淆第三方类库------------------------------------------------------------------
-keep class com..android..commonui.detail.helper.UriCallBackHelper{*;}
-keep class com.google.android.exoplayer.**  { *; }   # 对exoplayer.jar的混淆处理
-keep class merge.tv.danmaku.ijk.media.player.**  { *; }   # welive的混淆处理
-keep class com..works.welive.**  { *; }   # welive的混淆处理
-keep class com..android.it.dynamic.**  { *; }   # dynamic的混淆处理
-keep class com..android..commonui.dynamic.**  { *; }
-keep class com.ucloud.uvod.**  { *; }   # welive的混淆处理
-keep class com..android..commonui.detail.nativedetail.**  { *; }
-keep class com..android..core.entity.**{*;}
-keep class com..android..core.acache.**{*;}
-keep class com..android..core.dbhelper.**{*;}
-keep class com..android..commondata.bean.**{*;}
-keep class com..android..commonui.detail.webdetail.**  { *; }
-keep class com..android..commonui.detail.webdetail.ui.**$XHR{*;}
-keep class com..android..commonui.detail.webdetail.ui.**$JavaCallJs{*;}
-keep class com..android..commonui.detail.webdetail.ui.**$JavacallJs{*;}
-keep class com..android..commonui.detail.webdetail.ui.**$JsCallJava{*;}
-keep class com..android..commonui.detail.webdetail.androidjs.**{*;}
-keep class com..android..commonui.detail.bean.**{*;}
-keep class com..android..commonui.morelists.bean.**{*;}
-keep class com..android..business.base.BaseBean{*;}
-keep class com..android..business.cc3team.talent.bean.**{*;}
-keep class com..android..business.cc3team.me.bean.**{*;}
-keep class com..android..business.detail.bean.**{*;}
-keep class com..android..business.team.home.bean.**{*;}
-keep class com..android..business.team.list.bean.**{*;}
-keep class com..android..business.team.list.h5.**{*;}
-keep class com..android..business.team.post.bean.**{*;}
-keep class com..android..business.team.search.bean.**{*;}
-keep class com..android..business.team.search.ui.SearchResultFragment$*{*;}
-keep class com..android..business.whitepaper.bean.**{*;}
-keep class com..works..**{*;}

-keep class Constant {*;}
-keep class com.alipay.euler.andfix.**{*;}
#-keep class com..zelda.plugin.**{*;}
-keep class com..works..**{*;}

-keep class * implements com..we.base.IBundleCache,com..we.base.IBundleService {*;}

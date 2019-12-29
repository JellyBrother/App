############公共配置，建议各模块都保留########
#common content, don't modify.
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-printmapping	 mapping.txt
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Fragment
-keep public class * extends android.view.View
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.database.sqlite.SQLiteOpenHelper
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends com.android.mms.smart.dot.DotItem
-keep public class com.android.mms.smart.dot.TedDotting
-ignorewarning
-keepattributes SourceFile,LineNumberTable
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#安全SDK相关jni不能混淆
-keep class android.app.**{*; }
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
    *;
}

#第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，打包时就会报错。
-dontwarn android.support.**
-keep class android.support.** { *;}
-dontwarn android.os.**

#########模块自定义配置###############
# custom content


#########独立apk自定义配置###############
#-libraryjars libs/tedsdk.jar
-keepattributes Signature,*Annotation*
-keep public class com.ted.android.common.update.http.** {
    public protected *;
}
-keep class * extends **.RequestParams{*;}
-keep class * extends **.DefaultParamsBuilder{*;}
-keep class com.android.mms.easytransfer.MmsBackupRestoreDataChunk { *; }





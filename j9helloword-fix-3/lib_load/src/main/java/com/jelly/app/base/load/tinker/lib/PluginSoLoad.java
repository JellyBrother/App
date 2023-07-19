package com.jelly.app.base.load.tinker.lib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.jelly.app.base.load.PluginLoader;
import com.jelly.app.base.load.utils.FilePath;
import com.jelly.app.base.load.utils.PluginCombinePathList;
import com.jelly.app.base.load.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.PathClassLoader;

/**
 * 优先加载最合适的so
 * https://www.cnblogs.com/janehlp/p/7473240.html
 * https://blog.csdn.net/qq_25138543/article/details/128066131
 * https://my.oschina.net/jjyuangu/blog/1930843
 * 查看：adb shell "getprop |grep cpu"
 * 查看支持的最高的abi类型：adb shell getprop ro.product.cpu.abi
 * 获取当前Android手机支持的所有CPU ABI类型：adb shell getprop ro.product.cpu.abilist
 */
public class PluginSoLoad {

    public static void loadSo(Context context) throws Throwable {
        // armeabi，armeabi-v7a，x86，mips，arm64-v8a，mips64，x86_64
        String abi = "arm64-v8a";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.CPU_ABI;
        } else {
            abi = Build.SUPPORTED_ABIS[0];
        }
        ClassLoader classLoader = context.getClassLoader();
        File[] files = FilePath.getPluginUnZipLibDir().listFiles();
//        File[] files = FilePath.getOatDir().listFiles();
        File abiFile = null;
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (TextUtils.equals(file.getName(), abi)) {
                abiFile = file;
                continue;
            }
            TinkerLoadLibrary.installNativeLibraryPath(classLoader, file);
        }
        if (abiFile != null) {
            TinkerLoadLibrary.installNativeLibraryPath(classLoader, abiFile);
        }
    }

    public static void loadSo2(Context context) throws Throwable {
        ClassLoader classLoader = context.getClassLoader();
        TinkerLoadLibrary.installNativeLibraryPath(classLoader, new File(PluginLoader.apkPath));
    }

    public static void loadSo3(Context context) throws Throwable {
        ClassLoader classLoader = context.getClassLoader();
        PackageManager pm = context.getPackageManager();
        PackageInfo mPackageInfo = pm.getPackageArchiveInfo(PluginLoader.apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
        if (mPackageInfo == null || mPackageInfo.applicationInfo == null) {
            return;
        }
        ApplicationInfo applicationInfo = mPackageInfo.applicationInfo;
        ClassLoader selfClassLoader = context.getClassLoader();
        PathClassLoader apkClassLoader = new PathClassLoader(applicationInfo.sourceDir, applicationInfo.nativeLibraryDir, selfClassLoader);
        Field apkPathListField = ReflectUtils.reflect(apkClassLoader.getClass()).getField(PluginCombinePathList.FIELD_PATH_LIST);
        Object apkPathListValue = apkPathListField.get(apkClassLoader);
        List<File> files = (List<File>) ReflectUtils.reflect(apkPathListValue).field(PluginCombinePathList.FIELD_NATIVE_LIBRARY_DIRECTORIES).get();
        for (File file : files) {
            TinkerLoadLibrary.installNativeLibraryPath(classLoader, file);
        }
    }

    public static void loadSo4(Context context) throws Throwable {
        NativeLibraryHelperCompat.copyNativeBinaries(new File(PluginLoader.apkPath), FilePath.getPluginUnZipLibDir2());
        File pluginUnZipLibDir2 = FilePath.getPluginUnZipLibDir2();
        File[] files = pluginUnZipLibDir2.listFiles();
//        loadSo(context);
        int a = 0;
    }
}

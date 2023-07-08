package com.jelly.app.base.load.tinker.lib;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.jelly.app.base.load.utils.FilePath;

import java.io.File;

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
}

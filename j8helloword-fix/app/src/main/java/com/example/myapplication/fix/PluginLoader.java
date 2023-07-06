package com.example.myapplication.fix;

import android.app.Application;
import android.content.Context;

import com.example.myapplication.fix.tinker.ShareTinkerLog;
import com.example.myapplication.fix.tinker.dex.SystemClassLoaderAdder;
import com.example.myapplication.fix.tinker.lib.TinkerLoadLibrary;
import com.example.myapplication.fix.tinker.res.TinkerResourcePatcher;
import com.example.myapplication.fix.utils.FilePath;
import com.example.myapplication.fix.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class PluginLoader {
    private static final String TAG = "PluginLoader";
    public static String apkPath = "";
    public static Context app;

    public static void attachBaseContext(Context context) {
        ShareTinkerLog.e(TAG, "attachBaseContext start:" + context);
        PluginLoader.app = context;
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            PluginLoader.app = applicationContext;
        }
//        String fileName = "2036-release.apk";
        String fileName = "app-debug.apk";
        apkPath = FilePath.getPluginPath() + File.separator + fileName;
        // 复制apk
        FileUtils.copyAssetsFile(context, fileName, apkPath);
        // 解压
        unzipDir();
        // 加载so
        installSo();
        // 加载dex
        installDex();
        // 加载资源
        loadResource();
        ShareTinkerLog.e(TAG, "attachBaseContext end:" + context);
    }

    private static void unzipDir() {
        try {
            ShareTinkerLog.e(TAG, "attachBaseContext unzipDir start:");
            if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
                FileUtils.unzipFile(apkPath, FilePath.getPluginUnZipPath());
            }
            ShareTinkerLog.e(TAG, "attachBaseContext unzipDir end:");
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContext unzipDir Throwable:", t);
        }
    }

    private static void installSo() {
        try {
            ShareTinkerLog.e(TAG, "attachBaseContext installSo start:");
            ClassLoader classLoader = app.getClassLoader();
            File[] files = FilePath.getPluginUnZipLibDir().listFiles();
            for (File f : files) {
                if (f == null) {
                    continue;
                }
                TinkerLoadLibrary.installNativeLibraryPath(classLoader, f);
            }
            ShareTinkerLog.e(TAG, "attachBaseContext installSo end:");
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContext installSo Throwable:", t);
        }
    }

    private static void installDex() {
        try {
            ShareTinkerLog.e(TAG, "attachBaseContext installDex start:");
            ArrayList<File> list = new ArrayList<>();
            list.add(new File(apkPath));
            ClassLoader classLoader = app.getClassLoader();
            SystemClassLoaderAdder.installDexes((Application) app, classLoader, FilePath.getOatDir(), list, true, true);
            SystemClassLoaderAdder.installDexes((Application) app, classLoader, FilePath.getOatDir(), list, false, true);
            ShareTinkerLog.e(TAG, "attachBaseContext installDex end:");
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContext installDex Throwable:", t);
        }
    }

    private static void loadResource() {
        try {
            ShareTinkerLog.e(TAG, "attachBaseContext loadResource start:");
            TinkerResourcePatcher.isResourceCanPatch(app);
            TinkerResourcePatcher.monkeyPatchExistingResources(app, apkPath, false);
            ShareTinkerLog.e(TAG, "attachBaseContext loadResource end:");
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContext loadResource Throwable:", t);
        }
    }
}

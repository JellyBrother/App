package com.jelly.app.base.load;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Keep;

import com.jelly.app.base.load.tinker.ShareTinkerLog;
import com.jelly.app.base.load.tinker.dex.SystemClassLoaderAdder;
import com.jelly.app.base.load.tinker.lib.PluginSoLoad;
import com.jelly.app.base.load.tinker.res.TinkerResourcePatcher;
import com.jelly.app.base.load.utils.FilePath;
import com.jelly.app.base.load.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class PluginLoader {
    private static final String TAG = PluginLoader.class.getSimpleName() + "PluLoader";
    public static String apkPath = "";
    public static String fileName = "";
    public static Context app;

    @Keep
    public static void attachBaseContext(Context context) {
        attachBaseContextBefore(context);
        attachBaseContextAfter(context);
    }

    public static void attachBaseContextBefore(Context context) {
        ShareTinkerLog.e(TAG, "attachBaseContextBefore start:" + context);
        PluginLoader.app = context;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                PluginLoader.app = applicationContext;
            }
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            HiddenApiBypass.startBypass();
//        }
        try {
            String[] plugins = context.getAssets().list(FilePath.PATH_PLUGIN);
            fileName = plugins[0];
            apkPath = FilePath.getPluginPath() + File.separator + plugins[0];
            // 复制apk
            FileUtils.copyAssetsFile(context, FilePath.PATH_PLUGIN + File.separator + plugins[0], apkPath);
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContextBefore getAssets list Throwable:", t);
        }
        // 解压
        unzipDir();
        ShareTinkerLog.e(TAG, "attachBaseContextBefore end:");
    }

    public static void attachBaseContextAfter(Context context) {
        ShareTinkerLog.e(TAG, "attachBaseContextAfter start:" + context);
        PluginLoader.app = context;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                PluginLoader.app = applicationContext;
            }
        }
        // 加载dex
        installDex();
        // 加载so
        installSo();
        // 加载资源
        loadResource();
        ShareTinkerLog.e(TAG, "attachBaseContextAfter end:");
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
//            PluginSoLoad.loadSo(app);
//            PluginSoLoad.loadSo2(app);
//            PluginSoLoad.loadSo3(app);
            PluginSoLoad.loadSo4(app);
            ShareTinkerLog.e(TAG, "attachBaseContext installSo end:");
        } catch (Throwable t) {
            ShareTinkerLog.e(TAG, "attachBaseContext installSo Throwable:", t);
        }
    }

    private static void installDex() {
        try {
            ShareTinkerLog.e(TAG, "attachBaseContext installDex start:");
            ArrayList<File> list = new ArrayList<>();
//            list.add(new File(apkPath));

            File[] files = FilePath.getPluginUnzipDir().listFiles();
            for (File file : files) {
                if (file == null) {
                    continue;
                }
                if (file.getName().endsWith(".dex")) {
                    list.add(file);
                }
            }

            ClassLoader classLoader = app.getClassLoader();
            SystemClassLoaderAdder.installDexes((Application) app, classLoader, FilePath.getOatDir(), list, false, true);
            File oatDir = FilePath.getOatDir();
            SystemClassLoaderAdder.installDexes((Application) app, classLoader, FilePath.getOatDir(), list, true, true);
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

package com.jelly.app.base.load;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Keep;

import com.jelly.app.base.load.tinker.res.TinkerResourcePatcher;
import com.jelly.app.base.load.utils.FilePath;
import com.jelly.app.base.load.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginLoader {
    public static String apkPath = "";
    public static Context app;

    static {
        System.loadLibrary("loadPlugin");
    }

    @Keep
    public static void attachBaseContext(Context context) {
        PluginLoader.app = context;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                PluginLoader.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(FilePath.PATH_PLUGIN);
            apkPath = FilePath.getPluginPath() + File.separator + plugins[0];
            // 复制apk
            FileUtils.copyAssetsFile(context, FilePath.PATH_PLUGIN + File.separator + plugins[0], apkPath);
            // 解压
            if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
                FileUtils.unzipFile(apkPath, FilePath.getPluginUnZipPath());
            }
            // apk集合
            ArrayList<File> apkFiles = new ArrayList<>();
            apkFiles.add(new File(apkPath));
            // 加载so
            load((Application) app, getLibFiles(), apkFiles, FilePath.getOatDir(), apkPath);


            // 加载资源
            loadResource();
        } catch (Throwable t) {
            Log.e("", "init", t);
        }
    }

    public static ArrayList<File> getLibFiles() {
        // armeabi，armeabi-v7a，x86，mips，arm64-v8a，mips64，x86_64
        String abi = "arm64-v8a";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.CPU_ABI;
        } else {
            abi = Build.SUPPORTED_ABIS[0];
        }
        File[] files = FilePath.getPluginUnZipLibDir().listFiles();
        File abiFile = null;
        ArrayList<File> libFiles = new ArrayList<>();
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (TextUtils.equals(file.getName(), abi)) {
                abiFile = file;
                continue;
            }
            libFiles.add(file);
        }
        if (abiFile != null) {
            libFiles.add(0, abiFile);
        }
        return libFiles;
    }

    private static void loadResource() {
        try {
            TinkerResourcePatcher.isResourceCanPatch(app);
            TinkerResourcePatcher.monkeyPatchExistingResources(app, apkPath, false);
        } catch (Throwable t) {
        }
    }

    public static native void load(Application context, List<File> libFiles, List<File> apkFiles, File oatDir, String apkPath);
}

package com.jelly.app.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.jelly.app.base.load.Start;

import java.io.File;
import java.util.ArrayList;

/**
 * 从C:\work\code\github\App\j14helloword-jni\lib_load_jni\build\intermediates\library_and_local_jars_jni\release\jni下找到so跟apk一起压缩
 */
public class Start2 {
    public static String assetsName = "";
    public static Context app;
    public static String pluginPath = "";

    public static void init(Context context, String password, String assetsN) {
        Start2.app = context;
        Start2.assetsName = assetsN;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                Start2.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(Start2.assetsName);
            if (plugins.length < 1) {
                return;
            }
            // 没有解压，就执行文件操作
            init2(context, password, plugins);
        } catch (Throwable t) {
            Log.e("Start", "init", t);
        }
    }

    public static void init2(Context context, String password, String[] plugins) {
        if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
            // 先删除
            boolean deleteFile = FileUtils.delete(FilePath.getRootLoadDir());
            // 复制分卷
            String subsectionName = "";
            for (String fileName : plugins) {
                if (fileName.endsWith(".zip")) {
                    subsectionName = fileName;
                }
                String subsectionPath = FilePath.getPluginSubsectionPath() + File.separator + fileName;
                FileUtils.copyAssetsFile(context, Start2.assetsName + File.separator + fileName, subsectionPath);
            }
            // 解压、解密分卷
            String subsectionPath = FilePath.getPluginSubsectionPath() + File.separator + subsectionName;
            FileUtils.unzipFileByPassword(subsectionPath, FilePath.getPluginPath(), password);
            String[] list = FilePath.getPluginDir().list();
            String apkName = "";
            String rarName = "";
            for (String fileName : list) {
                if (fileName.endsWith(".apk")) {
                    apkName = fileName;
                }
                if (fileName.endsWith(".aar")) {
                    rarName = fileName;
                }
            }
            pluginPath = FilePath.getPluginPath() + File.separator + apkName;
            // 解压rar
            String rarPath = FilePath.getPluginPath() + File.separator + rarName;
            FileUtils.unzipFile(rarPath, FilePath.getPluginUnAarPath());
            // 解压apk文件
            FileUtils.unzipFile(pluginPath, FilePath.getPluginUnZipPath());
        } else {
            String[] list = FilePath.getPluginDir().list();
            String name = "";
            for (String fileName : list) {
                if (fileName.endsWith(".apk")) {
                    name = fileName;
                    break;
                }
            }
            pluginPath = FilePath.getPluginPath() + File.separator + name;
        }
        // dex集合
        ArrayList<File> dexFiles = new ArrayList<>();
        File[] files = FilePath.getPluginUnzipDir().listFiles();
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (file.getName().endsWith(".dex") || file.getName().endsWith(".jar")) {
                dexFiles.add(file);
            }
        }
        // 开始加载
        Start.init((Application) app, getLibFiles(), dexFiles, FilePath.getOatDir(), pluginPath, getSoPath());
    }

    public static ArrayList<File> getLibFiles() {
        String abi = getAbi();
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

    public static String getSoPath() {
        String abi = getAbi();
        File[] files = FilePath.getPluginUnAarJniAbiDir(abi).listFiles();
        return FilePath.getPluginUnAarJniAbiPath(abi) + File.separator + files[0].getName();
    }

    public static String getAbi() {
        // armeabi，armeabi-v7a，x86，mips，arm64-v8a，mips64，x86_64
        String abi;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.CPU_ABI;
        } else {
            abi = Build.SUPPORTED_ABIS[0];
        }
        if (TextUtils.isEmpty(abi)) {
            abi = "arm64-v8a";
        }
        return abi;
    }
}

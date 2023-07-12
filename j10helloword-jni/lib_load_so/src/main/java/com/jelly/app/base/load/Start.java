package com.jelly.app.base.load;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Keep;

import com.jelly.app.base.load.utils.FilePath;
import com.jelly.app.base.load.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Start {
    public static String pluginPath = "";
    public static Context app;

    static {
        System.loadLibrary("loadPlugin");
    }

    @Keep
    public static void attachBaseContext(Context context) {
        Start.app = context;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                Start.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(FilePath.PATH_PLUGIN);
            if (plugins.length < 1) {
                return;
            }
            pluginPath = FilePath.getPluginPath() + File.separator + plugins[0];
            // 复制apk
            FileUtils.copyAssetsFile(context, FilePath.PATH_PLUGIN + File.separator + plugins[0], pluginPath);
            // 解压
            if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
                FileUtils.unzipFile(pluginPath, FilePath.getPluginUnZipPath());
            }
            // apk集合
            ArrayList<File> pluginFiles = new ArrayList<>();
            File pluginFile = new File(pluginPath);
            pluginFiles.add(pluginFile);
            // 加载so
            load((Application) app, getLibFiles(), pluginFiles, FilePath.getOatDir(), pluginFile, pluginPath);
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

    public static native void load(Application context, List<File> libFiles, List<File> pluginFiles,
                                   File oatDir, File pluginFile, String pluginPath);

    @Keep
    public static <T> List<T> getNewList(List<T> origList, List<T> nowList, List<T> otherList) {
        if (origList == null) {
            origList = new ArrayList<>(2);
        }
        final Iterator<T> iterator = origList.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            for (T t : nowList) {
                if (t.equals(next)) {
                    iterator.remove();
                    break;
                }
            }
        }
        origList.addAll(0, nowList);
        if (otherList == null) {
            otherList = new ArrayList<>(2);
        }
        final List<T> newList = new ArrayList<>(origList.size() + otherList.size() + 1);
        newList.addAll(origList);
        newList.addAll(otherList);
        return newList;
    }

    @Keep
    public static String getLibPath(Object nativeLibraryDirectories) {
        List<File> oldNativeLibraryDirectories = null;
        if (nativeLibraryDirectories instanceof List) {
            oldNativeLibraryDirectories = (List<File>) nativeLibraryDirectories;
        } else {
            Arrays.asList((File[]) nativeLibraryDirectories);
        }
        StringBuilder libraryPathBuilder = new StringBuilder();
        boolean isFirstItem = true;
        for (File libDir : oldNativeLibraryDirectories) {
            if (libDir == null) {
                continue;
            }
            if (isFirstItem) {
                isFirstItem = false;
            } else {
                libraryPathBuilder.append(File.pathSeparator);
            }
            libraryPathBuilder.append(libDir.getAbsolutePath());
        }
        return libraryPathBuilder.toString();
    }
}

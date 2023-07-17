package com.yer4xhG1xe;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Keep;

import com.yer4xhG1xe.utils.FilePath;
import com.yer4xhG1xe.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Start {
    public static String pluginPath = "";
    public static String assetsName = "";
    public static Context app;

    static {
        System.loadLibrary("load");
    }

    @Keep
    public static void init(Context context, String password, String assetsN) {
        Start.app = context;
        Start.assetsName = assetsN;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                Start.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(Start.assetsName);
            if (plugins.length < 1) {
                return;
            }
            // 没有解压，就执行文件操作
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
                    FileUtils.copyAssetsFile(context, Start.assetsName + File.separator + fileName, subsectionPath);
                }
                // 解压、解密分卷
                String subsectionPath = FilePath.getPluginSubsectionPath() + File.separator + subsectionName;
                FileUtils.unzipFileByPassword(subsectionPath, FilePath.getPluginPath(), password);
                String[] list = FilePath.getPluginDir().list();
                pluginPath = FilePath.getPluginPath() + File.separator + list[0];
                // 解压文件
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
            // apk集合
            ArrayList<File> pluginFiles = new ArrayList<>();
            File pluginFile = new File(pluginPath);
            pluginFiles.add(pluginFile);
            // 开始加载
            load((Application) app, getLibFiles(), pluginFiles, FilePath.getOatDir(), pluginFile, pluginPath);
        } catch (Throwable t) {
            Log.e("Start", "init", t);
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
    public static String getPath(Object nativeLibraryDirectories) {
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

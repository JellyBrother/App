package com.jelly.app.base.load.droid;

import android.annotation.TargetApi;
import android.os.Build;

import com.jelly.app.base.load.PluginLoader;
import com.jelly.app.base.load.utils.ReflectUtils;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NativeLibraryHelperCompat2 {

    private static final String TAG = NativeLibraryHelperCompat2.class.getSimpleName();

    private static final Class nativeLibraryHelperClass() throws ClassNotFoundException {
        return Class.forName("com.android.internal.content.NativeLibraryHelper");
    }

    private static final Class handleClass() throws ClassNotFoundException {
        return Class.forName("com.android.internal.content.NativeLibraryHelper$Handle");
    }

    public static final int copyNativeBinaries(File apkFile, File sharedLibraryDir) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return copyNativeBinariesAfterL(apkFile, sharedLibraryDir);
        } else {
            return copyNativeBinariesBeforeL(apkFile, sharedLibraryDir);
        }

    }

    private static int copyNativeBinariesBeforeL(File apkFile, File sharedLibraryDir) {
        try {
            Object[] args = new Object[2];
            args[0] = apkFile;
            args[1] = sharedLibraryDir;
            return ReflectUtils.reflect(nativeLibraryHelperClass()).method("copyNativeBinariesIfNeededLI", args).get();
//            return (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "copyNativeBinariesIfNeededLI", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static int copyNativeBinariesAfterL(File apkFile, File sharedLibraryDir) {
        try {
            Object handleInstance = ReflectUtils.reflect(handleClass()).method("create", apkFile).get();
//            Object handleInstance = MethodUtils.invokeStaticMethod(handleClass(), "create", apkFile);
            if (handleInstance == null) {
                return -1;
            }

            String abi = null;

            //在64位处理器中，如果导入的so库未包含64位的，比如只导入了armeabi，此时就会找不到该abi。
            //应该在32位abi中再次寻找。
            if (isVM64()) {
                if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
                    Set<String> abis = getAbisFromApk(apkFile.getAbsolutePath());
                    if (abis == null || abis.isEmpty()) {
                        return 0;
                    }
                    int abiIndex = ReflectUtils.reflect(nativeLibraryHelperClass()).method("findSupportedAbi", handleInstance, Build.SUPPORTED_64_BIT_ABIS).get();
//                    int abiIndex = (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "findSupportedAbi", handleInstance, Build.SUPPORTED_64_BIT_ABIS);
                    if (abiIndex >= 0) {
                        abi = Build.SUPPORTED_64_BIT_ABIS[abiIndex];
                    }
                }
            } //else {
            //如果abi为空，再次查找。
            if (abi == null) {
                if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                    Set<String> abis = getAbisFromApk(apkFile.getAbsolutePath());
                    if (abis == null || abis.isEmpty()) {
                        return 0;
                    }
                    int abiIndex = ReflectUtils.reflect(nativeLibraryHelperClass()).method("findSupportedAbi", handleInstance, Build.SUPPORTED_32_BIT_ABIS).get();
//                    int abiIndex = (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "findSupportedAbi", handleInstance, Build.SUPPORTED_32_BIT_ABIS);
                    if (abiIndex >= 0) {
                        abi = Build.SUPPORTED_32_BIT_ABIS[abiIndex];
                    }
                }
            }

            if (abi == null) {
                return -1;
            }

            Object[] args = new Object[3];
            args[0] = handleInstance;
            args[1] = sharedLibraryDir;
            args[2] = abi;
            return ReflectUtils.reflect(nativeLibraryHelperClass()).method("copyNativeBinaries", args).get();
//            return (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "copyNativeBinaries", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isVM64() {
        Set<String> supportedAbis = getAbisFromApk(getHostApk());
        if (Build.SUPPORTED_64_BIT_ABIS.length == 0) {
            return false;
        }

        if (supportedAbis == null || supportedAbis.isEmpty()) {
            return true;
        }

        for (String supportedAbi : supportedAbis) {
            if ("arm64-v8a".endsWith(supportedAbi) || "x86_64".equals(supportedAbi) || "mips64".equals(supportedAbi)) {
                return true;
            }
        }

        return false;
    }

    private static Set<String> getAbisFromApk(String apk) {
        try {
            ZipFile apkFile = new ZipFile(apk);
            Enumeration<? extends ZipEntry> entries = apkFile.entries();
            Set<String> supportedAbis = new HashSet<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.contains("../")) {
                    continue;
                }
                if (name.startsWith("lib/") && !entry.isDirectory() && name.endsWith(".so")) {
                    String supportedAbi = name.substring(name.indexOf("/") + 1, name.lastIndexOf("/"));
                    supportedAbis.add(supportedAbi);
                }
            }
            return supportedAbis;
        } catch (Exception e) {
        }
        return null;
    }

    private static String getHostApk() {
        return PluginLoader.app.getApplicationInfo().sourceDir;
    }
}
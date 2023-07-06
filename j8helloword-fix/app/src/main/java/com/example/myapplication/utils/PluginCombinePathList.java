package com.example.myapplication.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import dalvik.system.PathClassLoader;

/**
 * https://blog.csdn.net/u011200604/article/details/60143582
 * https://www.jianshu.com/p/d73a36876af4
 * https://github.com/Tencent/tinker
 * https://github.com/Tencent/Shadow
 * https://github.com/DroidPluginTeam/DroidPlugin
 * <p>
 * virtual---DelegateApplication64Bit--attachBaseContext
 * tinker--NewClassLoaderInjector--createNewClassLoader
 * tinker--TinkerLoadLibrary--install
 * tinker--TinkerManager --loadPatch
 * tinker--TinkerResourceLoader--加载资源
 * tinker--TinkerLoader--dex res so 加载都在这里
 * tinker--boolean loadTinkerJars = TinkerDexLoader.loadTinkerJars(app, patchVersionDirectory, oatDex, resultIntent, isSystemOTA, isProtectedApp);
 * tinker--boolean loadArkHotFixJars = TinkerArkHotLoader.loadTinkerArkHot(app, patchVersionDirectory, resultIntent);
 * tinker--boolean loadTinkerResources = TinkerResourceLoader.loadTinkerResources(app, patchVersionDirectory, resultIntent);
 * tinker--TinkerLoadLibrary--so加载
 * droidplugin--PluginHelper--
 * droidplugin--NativeLibraryHelperCompat--so加载
 */
public class PluginCombinePathList {
    private static final String TAG = "PluginCombinePathList";

    // dalvik.system.BaseDexClassLoader - private final DexPathList pathList;
    private static final String FIELD_PATH_LIST = "pathList";

    // dalvik.system.DexPathList - private Element[] dexElements;
    private static final String CLASS_ELEMENT = "dalvik.system.DexPathList$Element";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";

    // dalvik.system.DexPathList - NativeLibraryElement[] nativeLibraryPathElements;
    private static final String CLASS_NATIVE_LIBRARY_ELEMENTS = "dalvik.system.DexPathList$NativeLibraryElement";
    private static final String FIELD_NATIVE_LIBRARY_PATH_ELEMENTS = "nativeLibraryPathElements";

    // dalvik.system.DexPathList - private final List<File> nativeLibraryDirectories;
    private static final String CLASS_FILE = "java.io.File";
    private static final String FIELD_NATIVE_LIBRARY_DIRECTORIES = "nativeLibraryDirectories";

    // dalvik.system.DexPathList - private IOException[] dexElementsSuppressedExceptions;
    private static final String CLASS_IO_EXCEPTION = "java.io.IOException";
    private static final String FIELD_DEX_ELEMENTS_SUPPRESSED_EXCEPTIONS = "dexElementsSuppressedExceptions";

    public static void combinePathList(Context context, String apkPath) {
        try {
            Log.e(TAG, "combinePathList start");
            PackageManager pm = context.getPackageManager();
            PackageInfo mPackageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA);
            if (mPackageInfo == null || mPackageInfo.applicationInfo == null) {
                return;
            }
            ApplicationInfo applicationInfo = mPackageInfo.applicationInfo;
            ClassLoader selfClassLoader = context.getClassLoader();


            PathClassLoader apkClassLoader = new PathClassLoader(applicationInfo.sourceDir, applicationInfo.nativeLibraryDir, selfClassLoader);
            // BaseDexClassLoader - private final DexPathList pathList;
            Field selfPathListField = ReflectUtils.reflect(selfClassLoader.getClass()).getField(FIELD_PATH_LIST);
            Object selfPathListValue = selfPathListField.get(selfClassLoader);
            Field apkPathListField = ReflectUtils.reflect(apkClassLoader.getClass()).getField(FIELD_PATH_LIST);
            Object apkPathListValue = apkPathListField.get(apkClassLoader);

            Object[] nativeLibraryPathElements = combineArray(selfPathListValue, apkPathListValue, FIELD_NATIVE_LIBRARY_PATH_ELEMENTS, CLASS_NATIVE_LIBRARY_ELEMENTS);
            setFieldObject(selfPathListValue, FIELD_NATIVE_LIBRARY_PATH_ELEMENTS, nativeLibraryPathElements);
            List<Object> nativeLibraryDirectories = combineList(selfPathListValue, apkPathListValue, FIELD_NATIVE_LIBRARY_DIRECTORIES);
            setFieldObject(selfPathListValue, FIELD_NATIVE_LIBRARY_DIRECTORIES, nativeLibraryDirectories);
            Object[] dexElements = combineArray(selfPathListValue, apkPathListValue, FIELD_DEX_ELEMENTS, CLASS_ELEMENT);
            setFieldObject(selfPathListValue, FIELD_DEX_ELEMENTS, dexElements);
            Object[] dexElementsSuppressedExceptions = combineArray(selfPathListValue, apkPathListValue, FIELD_DEX_ELEMENTS_SUPPRESSED_EXCEPTIONS, CLASS_IO_EXCEPTION);
            setFieldObject(selfPathListValue, FIELD_DEX_ELEMENTS_SUPPRESSED_EXCEPTIONS, dexElementsSuppressedExceptions);
            Log.e(TAG, "combinePathList end");
        } catch (Throwable t) {
            Log.e(TAG, "combinePathList Throwable:", t);
        }
    }

    /**
     * 合并数组
     */
    private static Object[] combineArray(Object selfPathListObject, Object apkPathListObject, String field, String className) {
        try {
            Object[] selfArray = (Object[]) ReflectUtils.reflect(selfPathListObject).field(field).get();
            Object[] apkArray = (Object[]) ReflectUtils.reflect(apkPathListObject).field(field).get();
            if (selfArray == null && apkArray == null) {
                return null;
            }
            int apkArrayLength = 0;
            Class<?> componentType = null;
            if (apkArray != null) {
                componentType = apkArray.getClass().getComponentType();
                apkArrayLength = apkArray.length;
            }
            int selfArrayLength = 0;
            if (selfArray != null) {
                selfArrayLength = selfArray.length;
            }
            if (componentType == null) {
                componentType = Class.forName(className);
            }
            Object[] copyArray = (Object[]) Array.newInstance(componentType, apkArrayLength + selfArrayLength);
            if (apkArray != null) {
                System.arraycopy(apkArray, 0, copyArray, 0, apkArrayLength);
            }
            if (selfArray != null) {
                System.arraycopy(selfArray, 0, copyArray, apkArrayLength, selfArrayLength);
            }
//            if (selfArray != null) {
//                System.arraycopy(selfArray, 0, copyArray, 0, selfArrayLength);
//            }
//            if (apkArray != null) {
//                System.arraycopy(apkArray, 0, copyArray, selfArrayLength, apkArrayLength);
//            }
            return copyArray;
        } catch (Throwable t) {
            Log.e(TAG, "combineArray Throwable:", t);
        }
        return null;
    }

    /**
     * 合并集合
     */
    private static List<Object> combineList(
            Object selfPathListObject,
            Object apkPathListObject,
            String field
    ) {
        try {
            List<Object> selfList = (List<Object>) ReflectUtils.reflect(selfPathListObject).field(field).get();
            List<Object> apkList = (List<Object>) ReflectUtils.reflect(apkPathListObject).field(field).get();
//            File[] files = FilePath.getPluginUnZipLibDir().listFiles();
//            List<Object> apkList = new ArrayList<>();
//            for (File f : files) {
//                if (f == null) {
//                    continue;
//                }
//                apkList.add(f);
//            }
            if (apkList == null) {
                return selfList;
            }
            if (selfList == null) {
                return apkList;
            }
            apkList.addAll(selfList);
            return apkList;
        } catch (Throwable t) {
            Log.e(TAG, "combineList Throwable:", t);
        }
        return null;
    }

    /**
     * 反射赋值
     */
    private static void setFieldObject(
            Object pathListObject,
            String field,
            Object fieldValue
    ) {
        try {
            ReflectUtils.reflect(pathListObject).field(field, fieldValue);
        } catch (Throwable t) {
            Log.e(TAG, "setFieldObject Throwable:", t);
        }
    }
}

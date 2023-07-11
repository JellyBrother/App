#include <jni.h>
#include <string>
#include <sys/system_properties.h>

// 日志打印
#include <android/log.h>

#define LOG_TAG "loader"
#define LOGE(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

void installNativeLibraryPathElements(JNIEnv *env, jobject context, jobject files);

void installDexElements(JNIEnv *env, jobject context, jobject files, jobject oatDir);

void installClassLoader(JNIEnv *env, jobject context, jobject oatDir, jstring apkPath);

jobject newObj(JNIEnv *env, const char *className, const char *methodName, const char *methodSig);

jobject getObjField(JNIEnv *env, jobject obj, const char *fieldName, const char *fieldSig);

jobject getObjMethod(JNIEnv *env, jobject obj, const char *methodName, const char *methodSig);

void setObjField(JNIEnv *env, jobject obj, const char *fieldName,
                 const char *fieldSig, jobject value);

// 安卓sdk版本
static int sdkVerison = 0;

// 处理so
extern "C" JNIEXPORT void JNICALL
Java_com_jelly_app_base_load_PluginLoader_load(
        JNIEnv *env,
        jclass clazz,
        jobject context,
        jobject libFiles,
        jobject apkFiles,
        jobject oatDir,
        jstring apkPath
) {
    // 1. 获取 SDK 版本号 , 存储于 C 字符串 sdk_verison_str 中
    char sdk[128] = "0";
    // 获取版本号方法
    __system_property_get("ro.build.version.sdk", sdk);
    //将版本号转为 int 值
    sdkVerison = atoi(sdk);
    // 处理so
    installNativeLibraryPathElements(env, context, libFiles);
    // 处理dex
    installDexElements(env, context, apkFiles, oatDir);
    // 处理ClassLoader
    installClassLoader(env, context, oatDir, apkPath);

}

void installNativeLibraryPathElements(JNIEnv *env, jobject context, jobject files) {
    jobject classLoader = getObjMethod(env, context, "getClassLoader",
                                       "()Ljava/lang/ClassLoader;");
    // 获取pathList
    jobject pathList = getObjField(env, classLoader, "pathList",
                                   "Ldalvik/system/DexPathList;");
    // 获取pathList的nativeLibraryDirectories变量
    jobject nativeLibraryDirectories = getObjField(env, pathList, "nativeLibraryDirectories",
                                                   "Ljava/util/List;");
    // 获取pathList的systemNativeLibraryDirectories变量
    jobject systemNativeLibraryDirectories = getObjField(env, pathList,
                                                         "systemNativeLibraryDirectories",
                                                         "Ljava/util/List;");
    // 拼接集合，调用java类方便一点
    jclass loadUtilClz = env->FindClass("com/jelly/app/base/load/utils/LoadUtil");
    jmethodID getNewListMethodID = env->GetStaticMethodID(loadUtilClz, "getNewList",
                                                          "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)Ljava/util/List;");
    jobject getNewList = env->CallStaticObjectMethod(loadUtilClz, getNewListMethodID,
                                                     nativeLibraryDirectories,
                                                     files, systemNativeLibraryDirectories);
    // 调用pathList的makePathElements方法构建nativeLibraryPathElements
    jclass dexPathListClz = env->GetObjectClass(pathList);
    jobject makePathElements;
    jfieldID nativeLibraryPathElementsFieldID;
    if (sdkVerison >= 25) {
        jmethodID makePathElementsMethodID = env->GetStaticMethodID(dexPathListClz,
                                                                    "makePathElements",
                                                                    "(Ljava/util/List;)[Ldalvik/system/DexPathList$NativeLibraryElement;");
        makePathElements = env->CallStaticObjectMethod(dexPathListClz,
                                                       makePathElementsMethodID,
                                                       getNewList);
        nativeLibraryPathElementsFieldID = env->GetFieldID(dexPathListClz,
                                                           "nativeLibraryPathElements",
                                                           "[Ldalvik/system/DexPathList$NativeLibraryElement;");
        jboolean throwable = env->ExceptionCheck();
        if (throwable) {
            // 清除异常信息
            env->ExceptionClear();
            LOGE("installV25So throwable");

            jmethodID makePathElementsMethodIDV23 = env->GetStaticMethodID(dexPathListClz,
                                                                           "makePathElements",
                                                                           "(Ljava/util/List;Ljava/io/File;Ljava/util/List;)[Ldalvik/system/DexPathList$Element;");
            jobject suppressedExceptions = newObj(env, "java/util/ArrayList", "<init>", "()V");
            makePathElements = env->CallStaticObjectMethod(dexPathListClz,
                                                           makePathElementsMethodIDV23,
                                                           getNewList, nullptr,
                                                           suppressedExceptions);
            nativeLibraryPathElementsFieldID = env->GetFieldID(dexPathListClz,
                                                               "nativeLibraryPathElements",
                                                               "[Ldalvik/system/DexPathList$Element;");
        }
    } else {
        jmethodID makePathElementsMethodIDV23 = env->GetStaticMethodID(dexPathListClz,
                                                                       "makePathElements",
                                                                       "(Ljava/util/List;Ljava/io/File;Ljava/util/List;)[Ldalvik/system/DexPathList$Element;");
        jobject suppressedExceptions = newObj(env, "java/util/ArrayList", "<init>", "()V");
        makePathElements = env->CallStaticObjectMethod(dexPathListClz,
                                                       makePathElementsMethodIDV23,
                                                       getNewList, nullptr,
                                                       suppressedExceptions);
        nativeLibraryPathElementsFieldID = env->GetFieldID(dexPathListClz,
                                                           "nativeLibraryPathElements",
                                                           "[Ldalvik/system/DexPathList$Element;");
    }
    // 给pathList的nativeLibraryPathElements变量赋值
    env->SetObjectField(pathList,
                        nativeLibraryPathElementsFieldID,
                        makePathElements);
}

void installDexElements(JNIEnv *env, jobject context, jobject files, jobject oatDir) {
    jobject classLoader = getObjMethod(env, context, "getClassLoader",
                                       "()Ljava/lang/ClassLoader;");
    // 获取pathList
    jobject pathList = getObjField(env, classLoader, "pathList",
                                   "Ldalvik/system/DexPathList;");
    // 调用pathList的makePathElements方法构建dexElements
    jclass dexPathListClz = env->GetObjectClass(pathList);
    jmethodID makePathElementsMethodID;
    if (sdkVerison >= 23) {
        makePathElementsMethodID = env->GetStaticMethodID(dexPathListClz,
                                                          "makePathElements",
                                                          "(Ljava/util/List;Ljava/io/File;Ljava/util/List;)[Ldalvik/system/DexPathList$Element;");
        jboolean throwable = env->ExceptionCheck();
        if (throwable) {
            // 清除异常信息
            env->ExceptionClear();
            makePathElementsMethodID = env->GetStaticMethodID(dexPathListClz,
                                                              "makePathElements",
                                                              "(Ljava/util/ArrayList;Ljava/io/File;Ljava/util/ArrayList;)[Ldalvik/system/DexPathList$Element;");
        }
    } else {
        makePathElementsMethodID = env->GetStaticMethodID(dexPathListClz,
                                                          "makePathElements",
                                                          "(Ljava/util/ArrayList;Ljava/io/File;Ljava/util/ArrayList;)[Ldalvik/system/DexPathList$Element;");
    }
    jobject suppressedExceptions = newObj(env, "java/util/ArrayList", "<init>", "()V");
    jobjectArray extraDexElements = static_cast<jobjectArray>(env->CallStaticObjectMethod(
            dexPathListClz,
            makePathElementsMethodID,
            files, oatDir,
            suppressedExceptions));
    // 获取pathList的dexElements变量
    jobjectArray originalDexElements = static_cast<jobjectArray>(getObjField(env, pathList,
                                                                             "dexElements",
                                                                             "[Ldalvik/system/DexPathList$Element;"));
    // 创建新数组，将两个dexElements合并
    int extraDexElementsLength = env->GetArrayLength(extraDexElements);
    int originalDexElementsLength = env->GetArrayLength(originalDexElements);
    jint totalLength = extraDexElementsLength + originalDexElementsLength;
    jobject element = env->GetObjectArrayElement(originalDexElements, 0);
    jclass elementClz = env->GetObjectClass(element);
    jobjectArray combinedElements = env->NewObjectArray(totalLength, elementClz, nullptr);
    for (int i = 0; i < extraDexElementsLength; i++) {
        jobject obj = env->GetObjectArrayElement(extraDexElements, i);
        env->SetObjectArrayElement(combinedElements, i, obj);
    }
    for (int i = 0; i < originalDexElementsLength; i++) {
        jobject obj = env->GetObjectArrayElement(originalDexElements, i);
        env->SetObjectArrayElement(combinedElements, extraDexElementsLength + i, obj);
    }
    // 给pathList的dexElements变量赋值新数组
    setObjField(env, pathList, "dexElements", "[Ldalvik/system/DexPathList$Element;",
                combinedElements);
}

void installClassLoader(JNIEnv *env, jobject context, jobject oatDir, jstring apkPath) {
    jobject baseClassLoader = getObjMethod(env, context, "getClassLoader",
                                           "()Ljava/lang/ClassLoader;");
    // 获取pathList
    jobject pathList = getObjField(env, baseClassLoader, "pathList",
                                   "Ldalvik/system/DexPathList;");
    // 获取pathList的nativeLibraryDirectories变量
    jobject nativeLibraryDirectories = getObjField(env, pathList, "nativeLibraryDirectories",
                                                   "Ljava/util/List;");
    // 拼接路径，调用java类方便一点
    jclass loadUtilClz = env->FindClass("com/jelly/app/base/load/utils/LoadUtil");
    jmethodID getNewArrayMethodID = env->GetStaticMethodID(loadUtilClz, "getLibPath",
                                                           "(Ljava/lang/Object;)Ljava/lang/String;");
    jobject libPath = env->CallStaticObjectMethod(loadUtilClz, getNewArrayMethodID,
                                                  nativeLibraryDirectories);
    // 创建ClassLoader
    jobject classLoader;
    if (sdkVerison < 27) {
        jclass tinkerClassLoaderClz = env->FindClass(
                "com/jelly/app/base/load/tinker/dex/TinkerClassLoader");
        jmethodID tinkerClassLoaderMethodID = env->GetMethodID(tinkerClassLoaderClz,
                                                               "<init>",
                                                               "(Ljava/lang/String;Ljava/io/File;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
        classLoader = env->NewObject(tinkerClassLoaderClz,
                                     tinkerClassLoaderMethodID, apkPath, oatDir,
                                     libPath, baseClassLoader);
    } else {
        jclass delegateLastClassLoaderClz = env->FindClass("dalvik/system/DelegateLastClassLoader");
        jmethodID delegateLastClassLoaderMethodID = env->GetMethodID(delegateLastClassLoaderClz,
                                                                     "<init>",
                                                                     "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
        if (sdkVerison >= 31) {
            classLoader = env->NewObject(delegateLastClassLoaderClz,
                                         delegateLastClassLoaderMethodID, apkPath,
                                         libPath, baseClassLoader);
        } else {
            jclass classLoaderClz = env->FindClass("java/lang/ClassLoader");
            jmethodID classLoaderMethodID = env->GetStaticMethodID(classLoaderClz,
                                                                   "getSystemClassLoader",
                                                                   "()Ljava/lang/ClassLoader;");
            jobject javaClassLoader = env->CallStaticObjectMethod(classLoaderClz,
                                                                  classLoaderMethodID);
            classLoader = env->NewObject(delegateLastClassLoaderClz,
                                         delegateLastClassLoaderMethodID, apkPath,
                                         libPath, javaClassLoader);
            setObjField(env, classLoader, "parent", "Ljava/lang/ClassLoader;", baseClassLoader);
        }
    }
    // pathList注入classLoader
    if (sdkVerison < 26) {
        setObjField(env, pathList, "definingContext", "Ljava/lang/ClassLoader;",
                    classLoader);
    }
    // 主线程注入classLoader
    jclass threadClz = env->FindClass("java/lang/Thread");
    jmethodID currentThreadMethodID = env->GetStaticMethodID(threadClz,
                                                             "currentThread",
                                                             "()Ljava/lang/Thread;");
    jobject threadObj = env->CallStaticObjectMethod(threadClz,
                                                    currentThreadMethodID);
    jmethodID setContextClassLoaderMethodID = env->GetMethodID(threadClz,
                                                               "setContextClassLoader",
                                                               "(Ljava/lang/ClassLoader;)V");
    env->CallVoidMethod(threadObj, setContextClassLoaderMethodID, classLoader);
    // 上下文注入classLoader
    jobject baseContext = getObjField(env, context, "mBase",
                                      "Landroid/content/Context;");
    setObjField(env, baseContext, "mClassLoader", "Ljava/lang/ClassLoader;", classLoader);
    // mBase的mPackageInfo注入classLoader
    jobject mPackageInfo = getObjField(env, baseContext, "mPackageInfo",
                                       "Landroid/app/LoadedApk;");
    setObjField(env, mPackageInfo, "mClassLoader", "Ljava/lang/ClassLoader;", classLoader);
    // mBase的Resources注入classLoader
    jclass contextClz = env->GetObjectClass(context);
    jmethodID getResourcesMethodID = env->GetMethodID(contextClz, "getResources", "()Landroid/content/res/Resources;");
    jobject resources = env->CallObjectMethod(context, getResourcesMethodID);
    setObjField(env, resources, "mClassLoader", "Ljava/lang/ClassLoader;", classLoader);
    // Resources的mDrawableInflater注入classLoader
    jobject drawableInflater = getObjField(env, resources, "mDrawableInflater",
                                           "Landroid/graphics/drawable/DrawableInflater;");
    setObjField(env, drawableInflater, "mClassLoader", "Ljava/lang/ClassLoader;", classLoader);
}

jobject newObj(JNIEnv *env, const char *className, const char *methodName, const char *methodSig) {
    jclass objClz = env->FindClass(className);
    jmethodID methodID = env->GetMethodID(objClz, methodName, methodSig);
    jobject newObj = env->NewObject(objClz, methodID);
    return newObj;
}

jobject getObjMethod(JNIEnv *env, jobject obj, const char *methodName, const char *methodSig) {
    jclass objClz = env->GetObjectClass(obj);
    jmethodID objMethodID = env->GetMethodID(objClz, methodName, methodSig);
    jobject objField = env->CallObjectMethod(obj, objMethodID);
    return objField;
}

jobject getObjField(JNIEnv *env, jobject obj, const char *fieldName, const char *fieldSig) {
    jclass objClz = env->GetObjectClass(obj);
    jfieldID objFieldID = env->GetFieldID(objClz, fieldName, fieldSig);
    jobject objField = env->GetObjectField(obj, objFieldID);
    return objField;
}

void setObjField(JNIEnv *env, jobject obj, const char *fieldName,
                 const char *fieldSig, jobject value) {
    if (obj == nullptr) {
        return;
    }
    jclass objClz = env->GetObjectClass(obj);
    jfieldID objFieldID = env->GetFieldID(objClz,
                                          fieldName,
                                          fieldSig);
    env->SetObjectField(obj,
                        objFieldID,
                        value);
}


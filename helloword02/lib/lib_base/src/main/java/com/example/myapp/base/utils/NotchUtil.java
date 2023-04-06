package com.example.myapp.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.WindowInsets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class NotchUtil {

    /**
     * 判断是否是8.0系统之后的异形屏(刘海屏,水滴屏,挖孔屏等)
     * 必须在Activity的onAttachedToWindow()方法中才会生效,其他地方windowInsets为 null
     * onCreate->onStart->onResume->onAttachedToWindow
     */
    public static boolean IsNotchScreen(Context context, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO 各种品牌
            return isAndroidPNotchScreen(windowInsets) || hasNotchInScreenAtVivo(context) || hasNotchInScreenAtOppo(context)
                    || hasNotchInScreenAtHuawei(context) || hasNotchInScreenAtMI();
        }
        return false;
    }


    /**
     * Android P 异形屏判断
     *
     * @param windowInsets 必须使用控件的setOnApplyWindowInsetsListener()接口回调方法中的才或者在Activity的
     *                     onAttachedToWindow()方法中调用getWindow().getDecorView().getRootWindowInsets();
     *                     其他地方获取会直接为空
     */
    @SuppressLint("NewApi")
    public static boolean isAndroidPNotchScreen(WindowInsets windowInsets) {
        boolean isNotchScreen = false;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P && null != windowInsets) {
            DisplayCutout cutout = windowInsets.getDisplayCutout();
            if (cutout != null) {
                List<Rect> rects = cutout.getBoundingRects();
                if (rects != null && rects.size() > 0) {
                    isNotchScreen = true;
                }
            }
        }
        return isNotchScreen;
    }

    /**
     * 判断是否华为异形屏
     */
    public static boolean hasNotchInScreenAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.e("hasNotchInScreenAtHuawei()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("hasNotchInScreenAtHuawei()-> NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("hasNotchInScreenAtHuawei()-> Exception");
        } finally {
            LogUtils.e("hasNotchInScreenAtHuawei()-> ClassNotFoundException");
            return ret;
        }
    }

    /**
     * 获取华为O版本异形屏宽高
     */
    public static int[] getNotchSizeForHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            LogUtils.e("HUAWEI-getNotchSize()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("HUAWEI-getNotchSize()-> NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("HUAWEI-getNotchSize()-> Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 判断oppo 机型是否异形屏
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static String getNotchOppoProperty() {
        String value = "";
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method hideMethod = cls.getMethod("get", String.class);
            Object object = cls.newInstance();
            value = (String) hideMethod.invoke(object, "ro.oppo.screen.heteromorphism");
        } catch (ClassNotFoundException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        } catch (NoSuchMethodException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        } catch (InstantiationException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        } catch (IllegalAccessException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        } catch (IllegalArgumentException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        } catch (InvocationTargetException e) {
            LogUtils.e("getNotchOppoProperty()->get error() ", e.getMessage());
        }
        return value;
    }

    /**
     * 判断vivo机型是否异形屏
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    public static boolean hasNotchInScreenAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("android.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e) {
            LogUtils.e("hasNotchInScreenAtVivo()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            LogUtils.e("hasNotchInScreenAtVivo()-> NoSuchMethodException");
        } catch (Exception e) {
            LogUtils.e("hasNotchInScreenAtVivo()-> Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 判断小米机型是否异形屏
     */
    private static Method getBooleanMethod = null;

    public static boolean hasNotchInScreenAtMI() {
        try {
            if (getBooleanMethod == null) {
                getBooleanMethod = Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, boolean.class);
            }
            //Log.i(TAG,"getBoolean:"+getBooleanMethod.invoke(null, key, def));
            return (Boolean) getBooleanMethod.invoke(null, "ro.miui.notch", false);
        } catch (Exception e) {
            return false;
        }
    }
}
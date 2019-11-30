package com.jelly.baselibrary.common;

import android.app.Application;

/**
 * Author：
 * Date：2019.11.21 10:46
 * Description：常量类
 */
public class BaseCommon {

    public static final class Base {
        // 日志打印
        public static boolean isLogOn = true;
        // Application
        public static Application application;
        // 跳转点击限制时间间隔
        public static final int INTERVAL_TIME_START_ACTIVITY = 1000;
        // 搜索点击限制时间间隔
        public static final int INTERVAL_TIME_SEARCH = 500;
    }
}

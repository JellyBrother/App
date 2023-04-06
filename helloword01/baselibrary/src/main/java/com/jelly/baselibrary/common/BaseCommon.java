package com.jelly.baselibrary.common;

import android.app.Application;

/**
 * Description：常量类
 */
public class BaseCommon {

    public static final class Base {
        // 跳转点击限制时间间隔
        public static final int INTERVAL_TIME_START_ACTIVITY = 1000;
        // 搜索点击限制时间间隔
        public static final int INTERVAL_TIME_SEARCH = 500;
        // Application
        public static Application application;
        // 日志打印
        public static boolean enableLog = false;
        // Chat组件
        public static boolean enableChat = false;
        // OtherTool组件
        public static boolean enableOtherTool = false;
        // WxTool组件
        public static boolean enableWxTool = false;
        // WechatBusinessTool组件
        public static boolean enableWechatBusinessTool = false;
        // WechatBusinessTool组件
        public static boolean isDebug = false;
    }
}

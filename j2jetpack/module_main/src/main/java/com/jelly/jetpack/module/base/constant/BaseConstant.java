package com.jelly.jetpack.module.base.constant;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

public class BaseConstant {

    public static void init(Context context, boolean isDebug) {
        Base.sApp = (Application) context.getApplicationContext();
        Base.isDebug = isDebug;
        Base.initNetwork();
    }

    public static final class Base {
        public static boolean isDebug = true;
        // 埋点策略：0，默认只百度埋点；1，只java后台埋点；2、百度和java后台埋点；
        public static String statisticsStrategy = "0";
        // Application
        public static Application sApp;
        public static Uri splashUri;
        // 打包时间
        public static String sBuildTime;

        public static void initNetwork() {
        }

        public static void clean() {
            splashUri = null;
        }
    }

    public static final class Log {
        public static final String PAGE_LIFE = "pageLife";
    }
}

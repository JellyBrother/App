package com.example.myapp.base.constant;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class BaseConstant {

    public static void init(Context context, String environment, String statisticsStrategy, String buildTime, boolean isDebug) {
        BaseConstant.Base.sApp = (Application) context.getApplicationContext();
        BaseConstant.Base.statisticsStrategy = statisticsStrategy;
        BaseConstant.Base.sBuildTime = buildTime;
        BaseConstant.Base.isDebug = isDebug || !TextUtils.equals(environment, BaseConstant.Environment.PRO);
        android.util.Log.d("BaseConstant", "init environment:" + environment + ",statisticsStrategy:" + statisticsStrategy
                + ",isDebug:" + BaseConstant.Base.isDebug);
        BaseConstant.Environment.initEnvironment(environment);
        BaseConstant.Config.initPalmHouseBaseUrl();
        BaseConstant.Base.initNetwork();
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

    public static final class HttpConfig {
        // 网络请求读写时间
        public static final int TIMEOUT_READ = 15;
        public static final int TIMEOUT_CONNECTION = 15;
        // 通用请求头：版本名称
        public static final String HEAD_VERSION = "pVer";
        // 通用请求头：设备id
        public static final String HEAD_DEV_ID = "pDevId";
        // 通用请求头：设备名称
        public static final String HEAD_DEV_NAME = "pDevName";
        // 通用请求头：系统版本
        public static final String HEAD_SYS_VER = "pSysVer";
        // 通用请求头：前端类型iOS：1、安卓：2、H5：3
        public static final String HEAD_PLATFORM = "platform";
        public static final String HEAD_PLATFORM_IOS = "1";
        public static final String HEAD_PLATFORM_ANDROID = "2";
        public static final String HEAD_PLATFORM_H5 = "3";
        // 通用请求头：构建打包时间
        public static final String HEAD_BUILD_TIME = "pBuildTime";
        // 通用请求头：本地生成的随机id，用于备用设备id
        public static final String HEAD_ID_CARD = "pIdCard";
        // 通用请求头：时间戳
        public static final String HEAD_TIME = "pTime";
        // 通用请求头：渠道包
        public static final String HEAD_CHANNEL = "pChannel";
        public static final String TOKEN_BEARER = "bearer ";
        public static final String TOKEN_BASIC = "Basic ";
        // 后台分割符 逗号
        public static final String CUT_COMMA = ",";
        // 后台分割符 冒号
        public static final String CUT_COLON = "::";
        // 下载文件分割符
        public static final String CUT_COMMA_FILE = ".";
    }

    public static final class Path {
        public static final String APP_ROOT = "myapp";
        public static final String IMAGE = "images";
        public static final String CACHE = "/data/data/com.example.myapp/cache";
        public static final String EXTERNAL_CACHE = "/storage/emulated/0/Android/data/com.example.myapp/cache";
        public static final String FILE = "/data/user/0/com.example.myapp/files";
        public static final String DEFAULT_USER_ID_MD5 = "palm_home_uid";
        public static final String SHARE = "share";
        public static final String TEMP = "temp";
        public static final String SHARE_TEMP_JPEG = "ShareTemp.jpeg";
        public static final String HTTP_CACHE = "http_cache";
        public static final String DOWLOAD = "dowload";
        public static final String LOG = "log";
        public static final String VIDEO_CACHE = "video_cache";
    }

    public static final class Config {
        /**
         * http://10.44.219.175/api/
         * aa-test.bb.com    【待解析】  120.77.154.230      内/外网解析，部署在阿里云
         * aa.bb.com          【待解析】  IP 待分配    部署在阿里云       内/外网解析
         * aam.bb.com         【待解析  IP 待分配     部署在阿里云     内/外网解析
         */
//        public static String URL_PALMHOUSE = "";//http://10.44.219.98:3000/mock/39/  http://10.44.219.175/api/
        public static String URL_JA_PALMHOUSE = "http://aa-dev.bb.com/"; //"http://10.45.161.13:8888/";
        public static String URL_PREVIEW_MEANS = "https://gray-idesign.bb.com/kkfileview8012/";
        public static String URL_H5 = "http://aa-dev.bb.com/";

        public static void initPalmHouseBaseUrl() {
            if (Environment.environ == Env.SIT) {
                URL_JA_PALMHOUSE = "http://aa-dev.bb.com/";
                URL_PREVIEW_MEANS = "https://gray-idesign.bb.com/kkfileview8012/";
                URL_H5 = "http://aa-dev.bb.com/";
                return;
            }
            if (Environment.environ == Env.UAT) {
                URL_JA_PALMHOUSE = "https://aa-test.bb.com/";
                URL_PREVIEW_MEANS = "https://aa-test.bb.com/kkfile/";
                URL_H5 = "https://aa-test.bb.com/";
                return;
            }
            if (Environment.environ == Env.PRO) {
                URL_JA_PALMHOUSE = "https://aa.bb.com/";
                URL_PREVIEW_MEANS = "https://aa.bb.com/kkfile/";
                URL_H5 = "https://aa.bb.com/";
                return;
            }
        }
    }

    public static final class Environment {
        public static final String SIT = "sit";
        public static final String UAT = "uat";
        public static final String PRO = "pro";

        public static Env environ = Env.SIT;

        public static void initEnvironment(String env) {
            if (TextUtils.equals(env, SIT)) {
                environ = Env.SIT;
                return;
            }
            if (TextUtils.equals(env, UAT)) {
                environ = Env.UAT;
                return;
            }
            if (TextUtils.equals(env, PRO)) {
                environ = Env.PRO;
                return;
            }
        }
    }

    public static final class Alpha {
        public static final float ALPHA_40 = 0.4f;
        public static final float ALPHA_100 = 1.0f;
    }

    public static final class StatisticsStrategy {
        public static final String ONLY_BAIDU = "0";
        public static final String ONLY_JAVA = "1";
        public static final String BAIDU_JAVA = "2";
    }
}

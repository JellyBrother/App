package com.jelly.myapp.base.constant

import android.app.Application
import android.content.Context

object BaseConstant {
    fun init(context: Context, isDebug: Boolean) {
        Base.isInit = true
        Base.sApp = context.applicationContext as Application
        Base.isDebug = isDebug
    }

    object Base {
        var isInit = false

        var isDebug = true

        // Application
        var sApp: Application? = null
    }

    object Log {
        const val PAGE_LIFE = "pageLife"
    }

    object HttpConfig {
        // 网络请求读写时间
        const val TIMEOUT_READ = 15
        const val TIMEOUT_CONNECTION = 15

        // 通用请求头：版本名称
        const val HEAD_VERSION = "pVer"

        // 通用请求头：设备id
        const val HEAD_DEV_ID = "pDevId"

        // 通用请求头：设备名称
        const val HEAD_DEV_NAME = "pDevName"

        // 通用请求头：系统版本
        const val HEAD_SYS_VER = "pSysVer"

        // 通用请求头：前端类型iOS：1、安卓：2、H5：3
        const val HEAD_PLATFORM = "platform"
        const val HEAD_PLATFORM_IOS = "1"
        const val HEAD_PLATFORM_ANDROID = "2"
        const val HEAD_PLATFORM_H5 = "3"

        // 通用请求头：构建打包时间
        const val HEAD_BUILD_TIME = "pBuildTime"

        // 通用请求头：本地生成的随机id，用于备用设备id
        const val HEAD_ID_CARD = "pIdCard"

        // 通用请求头：时间戳
        const val HEAD_TIME = "pTime"

        // 通用请求头：渠道包
        const val HEAD_CHANNEL = "pChannel"
        const val TOKEN_BEARER = "bearer "
        const val TOKEN_BASIC = "Basic "

        // 后台分割符 逗号
        const val CUT_COMMA = ","

        // 后台分割符 冒号
        const val CUT_COLON = "::"

        // 下载文件分割符
        const val CUT_COMMA_FILE = "."
    }

    object Path {
        const val APP_ROOT = "myapp"
        const val IMAGE = "images"
        const val CACHE = "/data/data/com.example.myapp/cache"
        const val EXTERNAL_CACHE = "/storage/emulated/0/Android/data/com.example.myapp/cache"
        const val FILE = "/data/user/0/com.example.myapp/files"
        const val DEFAULT_USER_ID_MD5 = "palm_home_uid"
        const val SHARE = "share"
        const val TEMP = "temp"
        const val SHARE_TEMP_JPEG = "ShareTemp.jpeg"
        const val HTTP_CACHE = "http_cache"
        const val DOWLOAD = "dowload"
        const val LOG = "log"
        const val VIDEO_CACHE = "video_cache"
    }

    object Alpha {
        const val ALPHA_40 = 0.4f
        const val ALPHA_100 = 1.0f
    }

    object StatisticsStrategy {
        const val ONLY_BAIDU = "0"
        const val ONLY_JAVA = "1"
        const val BAIDU_JAVA = "2"
    }
}
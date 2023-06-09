package com.jelly.myapp.base.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.blankj.utilcode.util.*
import com.jelly.myapp.base.BuildConfig
import com.jelly.myapp.base.constant.BaseConstant
import com.jelly.myapp.base.utils.CachePath
import java.text.SimpleDateFormat
import java.util.*

open class BaseApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.e(BaseConstant.Log.PAGE_LIFE, "attachBaseContext base:$base")
    }

    override fun onCreate() {
        super.onCreate()
        if (!BaseConstant.Base.isInit) {
            initConfig()
            initOtherByMain()
            initByThread()
        }
        LogUtils.e(BaseConstant.Log.PAGE_LIFE, "onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LogUtils.e(BaseConstant.Log.PAGE_LIFE, "onConfigurationChanged")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        LogUtils.e(BaseConstant.Log.PAGE_LIFE, "onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        LogUtils.e(BaseConstant.Log.PAGE_LIFE, "onTrimMemory level:$level")
        eventDuration()
    }

    override fun onTerminate() {
        super.onTerminate()
        LogUtils.e(BaseConstant.Log.PAGE_LIFE, "onTerminate")
    }

    private fun initConfig() {
        //基础配置
        BaseConstant.init(
            this,
            BuildConfig.DEBUG
        )
        Utils.init(this)
        initLog()
    }

    private fun initOtherByMain() {
        //奔溃捕获上报
        initCrash()
        //之前ARouter初始化耗时500毫秒左右
//        initARouter()
    }

    private fun initByThread() {
        ThreadUtils.getCachedPool().execute(Runnable {
            //分享sdk初始化耗时150毫秒左右
            //                ShareUtil.getInstance().submitPolicy();
        })
    }

    private fun initLog() {
        val config: LogUtils.Config = LogUtils.getConfig()
            .setLogSwitch(BaseConstant.Base.isDebug) // 设置 log 总开关，包括输出到控制台和文件，默认开
            .setConsoleSwitch(BaseConstant.Base.isDebug) // 设置是否输出到控制台开关，默认开
            .setGlobalTag(null) // 设置 log 全局标签，默认为空
            // 当全局标签不为空时，我们输出的 log 全部为该 tag，
            // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
            .setLogHeadSwitch(false) // 设置 log 头信息开关，默认为开
            .setLog2FileSwitch(BaseConstant.Base.isDebug) // 打印 log 时是否存到文件的开关，默认关
            .setDir(CachePath.getLogCacheDir()) // 当自定义路径为空时，写入应用的/cache/log/目录中
            .setFilePrefix("") // 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
            .setFileExtension(".log") // 设置日志文件后缀
            .setBorderSwitch(false) // 输出日志是否带边框开关，默认开
            .setSingleTagSwitch(true) // 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
            .setConsoleFilter(LogUtils.V) // log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
            .setFileFilter(LogUtils.V) // log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
            .setStackDeep(1) // log 栈深度，默认为 1
            .setStackOffset(0) // 设置栈偏移，比如二次封装的话就需要设置，默认为 0
            .setSaveDays(3) // 设置日志可保留天数，默认为 -1 表示无限时长
            // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
            .addFormatter(object : LogUtils.IFormatter<java.util.ArrayList<*>?>() {

                override fun format(arrayList: java.util.ArrayList<*>?): String {
                    return "LogUtils Formatter ArrayList { $arrayList }"
                }
            })
            .addFileExtraHead("ExtraKey", "ExtraValue")
        LogUtils.i(config.toString())
    }

    private fun initCrash() {
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            val topActivity: Activity = ActivityUtils.getTopActivity()
            var activityName = ""
            if (topActivity != null) {
                activityName = topActivity.javaClass.name
            }
            val activitySize: Int = ActivityUtils.getActivityList().size
            val time = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
            val errorMsg =
                ("crash time:" + time + ",activityName：" + activityName + ",activitySize：" + activitySize
                        + ",Thread name：" + t.name + ",msg:" + Log.getStackTraceString(e))
            LogUtils.e(errorMsg)
            //                Statistics.eventCrash(errorMsg);
            if (activitySize > 0) {
                AppUtils.relaunchApp()
            }
        }
    }

    private fun eventDuration() {
        // 统计在线时长
    }
}
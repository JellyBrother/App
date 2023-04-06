package com.example.myapp;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.myapp.base.bridge.utils.PolicyUtil;
import com.example.myapp.base.cache.CachePath;
import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.ActivityUtils;
import com.example.myapp.base.utils.AppUtils;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.ThreadUtils;
import com.example.myapp.base.utils.Utils;
import com.hjq.permissions.XXPermissions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JellyProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        initConfig();
        initOtherByMain();
        initByThread();
        return true;
    }

    private void initConfig() {
        //基础配置
        BaseConstant.init(getContext(), BuildConfig.ENVIRONMENT, BuildConfig.STATISTICS_STRATEGY, BuildConfig.BUILD_TIME, BuildConfig.DEBUG);
        Utils.init();
        initLog();
    }

    private void initOtherByMain() {
        //奔溃捕获上报
        initCrash();
        //之前ARouter初始化耗时500毫秒左右
        initARouter();
        //隐私协议
        PolicyUtil.INSTANCE.submitPolicy();
        //权限
        XXPermissions.setCheckMode(false);
    }

    private void initByThread() {
        ThreadUtils.getCachedPool().execute(new Runnable() {
            @Override
            public void run() {
                //分享sdk初始化耗时150毫秒左右
//                ShareUtil.getInstance().submitPolicy();
            }
        });
    }

    private void initLog() {
        LogUtils.Config config = LogUtils.getConfig()
                .setLogSwitch(BaseConstant.Base.isDebug)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BaseConstant.Base.isDebug)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(false)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(BaseConstant.Base.isDebug)// 打印 log 时是否存到文件的开关，默认关
                .setDir(CachePath.getLogCacheDir())// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
                .setFileExtension(".log")// 设置日志文件后缀
                .setBorderSwitch(false)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFormatter(new LogUtils.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList arrayList) {
                        return "LogUtils Formatter ArrayList { " + arrayList.toString() + " }";
                    }
                })
                .addFileExtraHead("ExtraKey", "ExtraValue");
        LogUtils.i(config.toString());
    }

    private void initCrash() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                Activity topActivity = ActivityUtils.getTopActivity();
                String activityName = "";
                if (topActivity != null) {
                    activityName = topActivity.getClass().getName();
                }
                int activitySize = ActivityUtils.getActivityList().size();
                String time = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(new Date());
                String errorMsg = "crash time:" + time + ",activityName：" + activityName + ",activitySize：" + activitySize
                        + ",Thread name：" + t.getName() + ",msg:" + LogUtils.formatObject(e);
                LogUtils.e(errorMsg);
//                Statistics.eventCrash(errorMsg);
                if (activitySize > 0) {
                    AppUtils.relaunchApp();
                }
            }
        });
    }

    private void initARouter() {
        if (BaseConstant.Base.isDebug) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.printStackTrace(); // 打印日志的时候打印线程堆栈
            com.download.library.Runtime.getInstance().setDebug(true); // 开启下载的日志
        } else {
            com.download.library.Runtime.getInstance().setDebug(false);
        }
        long startTime = System.currentTimeMillis();
        ARouter.init(BaseConstant.Base.sApp); // 尽可能早，推荐在Application中初始化
        LogUtils.d("Arouter init time cost: " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }
}

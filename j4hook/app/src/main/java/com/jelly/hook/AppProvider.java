package com.jelly.hook;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jelly.hook.module.base.constant.BaseConstant;

public class AppProvider extends FileProvider {

    @Override
    public boolean onCreate() {
        initConfig();
        initOtherByMain();
        return true;
    }

    private void initConfig() {
        //基础配置
        BaseConstant.init(getContext(), BuildConfig.DEBUG);
    }

    private void initOtherByMain() {
        //奔溃捕获上报
        initCrash();
        //之前ARouter初始化耗时500毫秒左右
        initARouter();
    }

    private void initCrash() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                Log.e(BaseConstant.Log.PAGE_LIFE, "initCrash Thread name: " + t.getName() + ",Throwable:" + Log.getStackTraceString(e));
            }
        });
    }

    private void initARouter() {
        if (BaseConstant.Base.isDebug) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.printStackTrace(); // 打印日志的时候打印线程堆栈
        } else {
        }
        long startTime = System.currentTimeMillis();
        ARouter.init(BaseConstant.Base.sApp); // 尽可能早，推荐在Application中初始化
        Log.d(BaseConstant.Log.PAGE_LIFE, "Arouter init time cost: " + (System.currentTimeMillis() - startTime) + " milliseconds");
    }
}

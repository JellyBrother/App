package com.jelly.jetpack;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jelly.jetpack.module.base.constant.BaseConstant;

public class App extends Application {
    protected String TAG = "BaseViewModel";
    protected long onCreateTime;

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        TAG = getClass().getSimpleName();
        onCreateTime = System.currentTimeMillis();
        log("onCreate");
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        log("onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        log("onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        log("onTrimMemory level" + level);
        super.onTrimMemory(level);
        event();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("onConfigurationChanged");
    }

    protected void log(String msg) {
        long intervalTime = System.currentTimeMillis() - onCreateTime;
        Log.d(BaseConstant.Log.PAGE_LIFE, TAG + " " + msg + ",Interval time:" + intervalTime);
    }

    private void event() {
        // 统计在线时长
    }
}

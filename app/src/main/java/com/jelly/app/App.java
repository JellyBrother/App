package com.jelly.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.jelly.baselibrary.common.BaseCommon;
import com.squareup.leakcanary.LeakCanary;

/**
 * Author：
 * Date：2019.11.28 10:39
 * Description：应用的主入口
 */
public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        BaseCommon.Base.application = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}

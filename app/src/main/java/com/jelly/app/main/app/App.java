package com.jelly.app.main.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.jelly.app.main.common.AppCommon;
import com.jelly.baselibrary.common.BaseCommon;

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        BaseCommon.Base.application = this;
        AppCommon.init();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}

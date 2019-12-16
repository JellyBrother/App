package com.jelly.wxtoolmodule.main.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.wxtoolmodule.main.common.AppCommon;

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        BaseCommon.Base.application = this;
        AppCommon.init();
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}

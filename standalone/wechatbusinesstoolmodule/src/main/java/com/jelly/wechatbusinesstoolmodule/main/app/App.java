package com.jelly.wechatbusinesstoolmodule.main.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.wechatbusinesstoolmodule.main.common.AppCommon;

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

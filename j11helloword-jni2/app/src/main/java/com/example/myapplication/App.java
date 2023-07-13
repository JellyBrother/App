package com.example.myapplication;

import android.app.Application;
import android.content.Context;

import com.example.myapplication.utils.Utils;
import com.jelly.app.base.load.Start;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
//        PluginLoader.attachBaseContextBefore(base);
        super.attachBaseContext(base);
//        PluginLoader.attachBaseContextAfter(this);
    }

    @Override
    public void onCreate() {
        Utils.init(this);
        Start.attachBaseContext(this, "mx123+++");
        super.onCreate();
    }
}

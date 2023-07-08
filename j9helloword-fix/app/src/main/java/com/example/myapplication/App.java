package com.example.myapplication;

import android.app.Application;
import android.content.Context;

import com.jelly.app.base.load.PluginLoader;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
//        PluginLoader.attachBaseContextBefore(base);
        super.attachBaseContext(base);
//        PluginLoader.attachBaseContextAfter(this);
    }

    @Override
    public void onCreate() {
        PluginLoader.attachBaseContext(this);
        super.onCreate();
    }
}

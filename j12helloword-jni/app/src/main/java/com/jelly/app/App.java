package com.jelly.app;

import android.app.Application;
import android.content.Context;

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
//        Start.attachBaseContext(this, "mx123+++");
        super.onCreate();
    }
}

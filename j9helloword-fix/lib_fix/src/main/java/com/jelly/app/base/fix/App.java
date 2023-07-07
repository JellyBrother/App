package com.jelly.app.base.fix;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        PluginLoader.attachBaseContextBefore(base);
        super.attachBaseContext(base);
        PluginLoader.attachBaseContextAfter(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

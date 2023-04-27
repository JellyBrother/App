package com.jelly.myapp2.base.ui;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jelly.myapp2.base.constant.Constant;

public class BaseService extends Service {

    @Override
    public void onCreate() {
        Log.e(Constant.Log.TAG, "service Service1Service onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(Constant.Log.TAG, "service Service1Service onBind");
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(Constant.Log.TAG, "service Service1Service onStart startId:" + startId);
        super.onStart(intent, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(Constant.Log.TAG, "service Service1Service onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(Constant.Log.TAG, "service Service1Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.e(Constant.Log.TAG, "service Service1Service unbindService");
        super.unbindService(conn);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Constant.Log.TAG, "service Service1Service onStartCommand flags:" + flags + ",startId:" + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(Constant.Log.TAG, "service Service1Service onConfigurationChanged");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(Constant.Log.TAG, "service Service1Service onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        Log.e(Constant.Log.TAG, "service Service1Service onTrimMemory level:" + level);
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        Log.e(Constant.Log.TAG, "service Service1Service onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        Log.e(Constant.Log.TAG, "service Service1Service onDestroy");
        super.onDestroy();
    }
}

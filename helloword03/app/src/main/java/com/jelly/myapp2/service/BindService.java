package com.jelly.myapp2.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jelly.myapp2.aidl.BindServiceImpl;
import com.jelly.myapp2.base.constant.Constant;
import com.jelly.myapp2.base.ui.BaseService;

public class BindService extends BaseService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(Constant.Log.TAG, "service BindService onBind");
        return new BindServiceImpl();
    }
}

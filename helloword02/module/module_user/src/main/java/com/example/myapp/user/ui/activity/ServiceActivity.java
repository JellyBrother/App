package com.example.myapp.user.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.myapp.base.bridge.constant.ARouterConstant;
import com.example.myapp.base.ui.activity.BaseActivity;
import com.example.myapp.base.widget.helper.ToastHelper;
import com.example.myapp.user.databinding.UserAtcMainBinding;
import com.example.myapp.user.ui.connection.UserServiceConnection;

/**
 * **1进程内调用service
 * startService和bindService都不会报错
 * **2跨进程调用service，service在清单文件注册要加上android:exported="true"
 * ***2.1bindService不会报错
 * ***2.2startService会报错：
 * ****2.2.1 IllegalStateException: Not allowed to start service Intent
 * 这个时候要判断安卓版本Build.VERSION.SDK_INT >= Build.VERSION_CODES.O时使用startForegroundService，小于才能使用startService
 * ****2.2.2 判断完成后还会报错RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground(): ServiceRecord
 * 这个时候要onStartCommand方法中调用startForeground加上Notification
 * **3service的生命周期
 * ***3.1调用startService
 * onCreate--onStartCommand--onStart
 * 进程死了调用onTrimMemory--onTaskRemoved--onCreate--onDestroy
 * ***3.2调用bindService
 * Service的onCreate--Service的onBind--ServiceConnection的onServiceConnected
 * 进程死了调用onTrimMemory--onUnbind--onDestroy
 */
@Route(path = ARouterConstant.User.USER_SERVICE_ACTIVITY)
public class ServiceActivity extends BaseActivity {
    private UserAtcMainBinding mBinding;
    private UserServiceConnection serviceConnection;

    @Override
    protected View getLayoutView() {
        mBinding = UserAtcMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBinding.tvwTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastHelper.INSTANCE.showShort("tvwTest1 onClick");

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.example.myapp2", "com.jelly.myapp2.service.StartService");
                intent.setComponent(componentName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
            }
        });
        mBinding.tvwTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastHelper.INSTANCE.showShort("tvwTest2 onClick");

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.example.myapp2", "com.jelly.myapp2.service.BindService");
                intent.setComponent(componentName);
                serviceConnection = new UserServiceConnection();
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}

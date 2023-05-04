package com.example.myapp.user.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.myapp.base.bridge.constant.ARouterConstant;
import com.example.myapp.base.ui.activity.BaseVmActivity;
import com.example.myapp.base.widget.helper.ToastHelper;
import com.example.myapp.user.databinding.UserAtcMainBinding;
import com.example.myapp.user.ui.viewmodel.ServiceActivityVm;

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
public class ServiceActivity extends BaseVmActivity<ServiceActivityVm> {
    private UserAtcMainBinding mBinding;
    private Intent startServiceIntent;

    @Override
    protected Class<ServiceActivityVm> getDefaultViewModel() {
        return ServiceActivityVm.class;
    }

    @Override
    protected View getLayoutView() {
        mBinding = UserAtcMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initObserver() {
        super.initObserver();
        getViewModel().getServiceConnectionStateLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null) {
                    return;
                }
                if (integer == 1) {
                    mBinding.tvwDesc.setText("服务连接成功");
                }
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBinding.tvwTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastHelper.INSTANCE.showShort("tvwTest1 onClick");

                startServiceIntent = new Intent();
                ComponentName componentName = new ComponentName("com.example.myapp2", "com.jelly.myapp2.service.StartService");
                startServiceIntent.setComponent(componentName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(startServiceIntent);
                } else {
                    startService(startServiceIntent);
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
                bindService(intent, getViewModel().getServiceConnection(), Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (startServiceIntent != null) {
            stopService(startServiceIntent);
        }
        if (getViewModel().getServiceConnection() != null) {
            unbindService(getViewModel().getServiceConnection());
        }
        super.onDestroy();
    }

    private void testCase() {
        ///////////////////////////////////////////////////// startService /////////////////////////////////////////////////////////
//        Intent intent = new Intent(this, StartService.class);
//        startService(intent); // 开启服务
//        stopService(intent); // 停止服务
        ///////////////////////////////////////////////////// startService /////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////// bindService /////////////////////////////////////////////////////////
//        Intent intent = new Intent(this, BindService.class);
//        ServiceConnection connection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//            }
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//            }
//        };
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//        unbindService(connection);
        ///////////////////////////////////////////////////// bindService /////////////////////////////////////////////////////////


    }
}

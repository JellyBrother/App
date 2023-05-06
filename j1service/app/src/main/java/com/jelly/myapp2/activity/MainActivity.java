package com.jelly.myapp2.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.jelly.myapp2.IBindService;
import com.jelly.myapp2.base.ui.BaseActivity;
import com.jelly.myapp2.databinding.ActivityMainBinding;
import com.jelly.myapp2.utils.ToastUtil;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private UserServiceConnection serviceConnection;

    @Override
    protected View getLayoutView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.tvwTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort("tvwTest1 onClick");

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.example.myapp2", "com.jelly.myapp2.service.StartService");
                intent.setComponent(componentName);
                startService(intent);
            }
        });
        binding.tvwTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort("tvwTest2 onClick");

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

    private class UserServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("service", "activity MainServiceConnection onServiceConnected");
            try {
                IBindService anInterface = IBindService.Stub.asInterface(service);
                String basicTypes = anInterface.getTextByInput(50);
                Log.e("service", "activity MainServiceConnection onServiceConnected basicTypes:" + basicTypes);
            } catch (RemoteException e) {
                Log.e("service", "activity MainServiceConnection onServiceConnected RemoteException:" + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("service", "activity MainServiceConnection onServiceDisconnected");
        }
    }
}
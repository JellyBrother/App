package com.example.myapp.user.ui.viewmodel;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.myapp.base.viewmodel.BaseModel;
import com.example.myapp.base.viewmodel.BaseViewModel;
import com.jelly.myapp2.IBindService;

public class ServiceActivityVm extends BaseViewModel<BaseModel> {
    private ServiceConnection serviceConnection;
    private IBindService iBindService;
    private MutableLiveData<Integer> mServiceConnectionStateLiveData;

    public MutableLiveData<Integer> getServiceConnectionStateLiveData() {
        if (mServiceConnectionStateLiveData == null) {
            mServiceConnectionStateLiveData = new MutableLiveData<>();
        }
        return mServiceConnectionStateLiveData;
    }

    public ServiceConnection getServiceConnection() {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.e("service", "ServiceActivityVm getServiceConnection onServiceConnected");
                    iBindService = IBindService.Stub.asInterface(service);
                    getServiceConnectionStateLiveData().postValue(1);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.e("service", "ServiceActivityVm getServiceConnection onServiceDisconnected");
                    getServiceConnectionStateLiveData().postValue(2);
                }
            };
        }
        return serviceConnection;
    }


}

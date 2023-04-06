package com.example.myapp.base.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.NetworkUtils;
import com.example.myapp.base.utils.ReflectUtils;

/**
 * 基类
 */
public class BaseViewModel<M extends BaseModel> extends ViewModel {
    public static final int STATUS_NET_CONNECTED = 1;
    public static final int STATUS_NET_DISCONNECTED = 2;
    protected String TAG = "BaseViewModel";
    private int mNetStatus = 0;
    private M model;

    public BaseViewModel() {
        try {
            TAG = getClass().getSimpleName();
            iniData();
        } catch (Throwable e) {
            LogUtils.e(TAG, "BaseViewModel Throwable:", e);
        }
    }

    public void onDestroy() {
        LogUtils.d(BaseConstant.Log.PAGE_LIFE, TAG + "onDestroy");
        if (model != null) {
            model.onDestroy();
            model = null;
        }
    }

    @Override
    protected void onCleared() {
        LogUtils.d(BaseConstant.Log.PAGE_LIFE, TAG + "onCleared");
        if (model != null) {
            model.onCleared();
            model = null;
        }
        super.onCleared();
    }

    protected M getModel() {
        if (model != null) {
            return model;
        }
        try {
            Class<?> vmClass = ReflectUtils.getGenericSuperclass(getClass());
            model = (M) vmClass.newInstance();
        } catch (Exception e) {
            LogUtils.e(TAG, "getModel Throwable:", e);
        }
        return model;
    }

    protected void iniData() {
        if (NetworkUtils.isConnected()) {
            setNetworkStatus(STATUS_NET_CONNECTED);
        } else {
            setNetworkStatus(STATUS_NET_DISCONNECTED);
        }
    }

    private void setNetworkStatus(int netStatus) {
        if (mNetStatus != netStatus) {
            onNetworkStatusChanged(netStatus);
        }
        mNetStatus = netStatus;
    }

    protected void onNetworkStatusChanged(int netStatus) {

    }
}
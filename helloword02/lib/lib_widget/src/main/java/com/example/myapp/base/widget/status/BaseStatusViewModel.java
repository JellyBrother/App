package com.example.myapp.base.widget.status;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.example.myapp.base.net.utils.ApiResponse;
import com.example.myapp.base.utils.NetworkUtils;
import com.example.myapp.base.viewmodel.BaseModel;
import com.example.myapp.base.viewmodel.BaseViewModel;
import com.example.myapp.base.widget.R;
import com.example.myapp.base.widget.helper.ToastHelper;

/**
 * 加载数据基类
 */
public class BaseStatusViewModel<M extends BaseModel> extends BaseViewModel<M> {
    // 状态
    private MutableLiveData<StatusType> mStatusTypeLiveData;

    public MutableLiveData<StatusType> getStatusTypeLiveData() {
        if (mStatusTypeLiveData == null) {
            mStatusTypeLiveData = new MutableLiveData<>();
        }
        return mStatusTypeLiveData;
    }

    /**
     * 开始网络请求有网络-开始转圈请求接口
     */
    public void startRequest() {
    }

    public void onResumeRequest() {
    }

    public void onStartRequestFail() {
        if (NetworkUtils.isConnected()) {
            getStatusTypeLiveData().postValue(StatusType.STATUS_ERROR);
        } else {
            getStatusTypeLiveData().postValue(StatusType.STATUS_NO_NET);
        }
    }

    public void onStartRequestEmpty() {
        getStatusTypeLiveData().postValue(StatusType.STATUS_EMPTY);
    }

    public static void toastFail(ApiResponse response, CharSequence localToast) {
        if (response == null) {
            if (NetworkUtils.isConnected()) {
//                ToastHelper.INSTANCE.showShort(R.string.base_toast_service_error);
            } else {
//                ToastHelper.INSTANCE.showShort(R.string.base_toast_no_net);
            }
            return;
        }
        String message = response.getMessage();
        if (!TextUtils.isEmpty(message)) {
            ToastHelper.INSTANCE.showShort(message);
            return;
        }
        if (!TextUtils.isEmpty(localToast)) {
            ToastHelper.INSTANCE.showShort(localToast);
        }
    }
}
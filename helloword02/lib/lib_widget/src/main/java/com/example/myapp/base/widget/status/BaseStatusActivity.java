package com.example.myapp.base.widget.status;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.example.myapp.base.ui.activity.BaseVmActivity;
import com.example.myapp.base.utils.NetworkUtils;
import com.example.myapp.base.widget.interfaces.IRetryListener;

public abstract class BaseStatusActivity<VM extends BaseStatusViewModel> extends BaseVmActivity<VM> {
    protected StatusView mStatusView;

    @Override
    protected void initListener() {
        super.initListener();
        getViewModel().getStatusTypeLiveData().observe(this, new Observer<StatusType>() {
            @Override
            public void onChanged(StatusType statusType) {
                if (mStatusView == null) {
                    return;
                }
                mStatusView.setStatus(statusType);
            }
        });
        setRetryListener();
    }

    /**
     * 重试
     */
    protected void setRetryListener() {
        if (mStatusView != null) {
            mStatusView.setRetryListener(new IRetryListener() {
                @Override
                public void onRetry(View v) {
                    startRequest();
                }
            });
        }
    }

    /**
     * 开始网络请求-方法之前可以先转圈
     */
    protected void startRequest() {
        if (NetworkUtils.isConnected()) {
            getViewModel().startRequest();
            return;
        }
        startRequestNoNet();
    }

    /**
     * 开始网络请求无网络处理
     */
    protected void startRequestNoNet() {

    }
}
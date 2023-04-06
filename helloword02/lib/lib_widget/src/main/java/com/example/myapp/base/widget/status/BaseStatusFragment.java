package com.example.myapp.base.widget.status;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.example.myapp.base.ui.fragment.BaseVmFragment;
import com.example.myapp.base.utils.NetworkUtils;
import com.example.myapp.base.widget.interfaces.IRetryListener;

public abstract class BaseStatusFragment<VM extends BaseStatusViewModel> extends BaseVmFragment<VM> {
    protected StatusView mStatusView;

    public BaseStatusFragment() {
    }

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
    protected void setRetryListener(){
        if (mStatusView!=null){
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
    public void startRequest() {
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

    protected void onResumeRequest() {
        if (NetworkUtils.isConnected()) {
            getViewModel().onResumeRequest();
            return;
        }
    }
}
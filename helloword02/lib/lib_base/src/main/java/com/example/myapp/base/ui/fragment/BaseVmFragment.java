package com.example.myapp.base.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.ReflectUtils;
import com.example.myapp.base.viewmodel.BaseViewModel;

/**
 * 基类
 */
public abstract class BaseVmFragment<VM extends BaseViewModel> extends BaseFragment {
    private VM mViewModel;

    public BaseVmFragment() {
    }

    @Override
    protected void initIntent(Bundle bundle) {
        super.initIntent(bundle);
        getViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getViewModel().onDestroy();
    }

    protected Class<VM> getDefaultViewModel() {
        return null;
    }

    public VM getViewModel() {
        if (mViewModel == null) {
            Class<VM> vmClass = getDefaultViewModel();
            if (vmClass == null) {
                try {
                    vmClass = (Class<VM>) ReflectUtils.getGenericSuperclass(getClass());
                } catch (Throwable t) {
                    LogUtils.d("getViewModel Throwable：" + t);
                }
            }
//            ViewModelProvider.AndroidViewModelFactory instance = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
//            mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) instance).get(vmClass);
            mViewModel = new ViewModelProvider(this).get(vmClass);
        }
        return mViewModel;
    }
}
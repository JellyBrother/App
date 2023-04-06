package com.example.myapp.base.ui.activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.ReflectUtils;
import com.example.myapp.base.viewmodel.BaseViewModel;

/**
 * 基类
 */
public abstract class BaseVmActivity<VM extends BaseViewModel> extends BaseActivity {
    private VM mViewModel;

    @Override
    protected void initIntent(Bundle bundle) {
        super.initIntent(bundle);
        getViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModel().onDestroy();
    }

    /**
     * 当界面使用AndroidEntryPoint注解的时候会变成Hilt_WebViewActivity，导致反射不到ViewModel，因此需要子类复写，给出ViewModel.class
     */
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
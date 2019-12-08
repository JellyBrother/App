package com.jelly.baselibrary.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author：
 * Date：2019.11.20 14:21
 * Description：Activity的基类
 */
public abstract class BaseLifecycleFragment<T extends BaseViewModel> extends BaseFragment {
    // 当前Activity持有的ViewModel
    protected T mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (mViewModel == null) {
            mViewModel = initViewModel();
        }
        mViewModel.initData(getArguments());
        observerData();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.destroyData();
    }

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：初始化ViewModel
     *
     * @return ViewModel
     */
    protected abstract T initViewModel();

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：数据操作
     */
    protected abstract void observerData();
}

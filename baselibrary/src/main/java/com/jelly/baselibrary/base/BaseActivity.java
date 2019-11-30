package com.jelly.baselibrary.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jelly.baselibrary.utils.LogUtil;

/**
 * Author：
 * Date：2019.11.20 14:21
 * Description：Activity的基类
 */
public abstract class BaseActivity<T extends BaseViewModel> extends FragmentActivity {
    private static final String TAG = "BaseActivity";
    // 当前Activity持有的ViewModel
    protected T mViewModel;
    // 上下文
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TAG = this.getClass().getSimpleName();
        LogUtil.getInstance().d(TAG, "onCreate");
        mActivity = this;
        if (mViewModel == null) {
            mViewModel = initViewModel();
        }
        initView(savedInstanceState);
        mViewModel.initData(getIntent().getExtras());
        observerData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.getInstance().d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.getInstance().d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().d(TAG, "onDestroy");
        mViewModel.destroyData();
    }

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：初始化控件
     *
     * @param savedInstanceState Bundle
     */
    protected abstract void initView(Bundle savedInstanceState);

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

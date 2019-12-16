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
public abstract class BaseActivity extends FragmentActivity {
    private static String TAG = "BaseActivity";
    // 上下文
    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        LogUtil.getInstance().d(TAG, "onCreate");
        mActivity = this;
        initView(savedInstanceState);
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
    }

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：初始化控件
     *
     * @param savedInstanceState Bundle
     */
    protected abstract void initView(Bundle savedInstanceState);
}
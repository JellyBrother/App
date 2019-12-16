package com.jelly.baselibrary.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelly.baselibrary.utils.LogUtil;

/**
 * Author：
 * Date：2019.11.20 14:21
 * Description：Activity的基类
 */
public abstract class BaseFragment extends Fragment {
    private static String TAG = "BaseFragment";
    // 上下文
    protected Activity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        LogUtil.getInstance().d(TAG, "onCreateView");
        mActivity = getActivity();
        View rootView = getRootView(inflater, container, savedInstanceState);
        initView(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.getInstance().d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.getInstance().d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().d(TAG, "onDestroy");
    }

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：初始化控件
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return 根布局
     */
    protected abstract View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * Author：
     * Date：2019.11.20 14:15
     * Description：初始化控件
     *
     * @param rootView           View
     * @param savedInstanceState Bundle
     */
    protected abstract void initView(View rootView, Bundle savedInstanceState);
}

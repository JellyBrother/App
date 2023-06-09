package com.example.myapp.base.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.LogUtils;

/**
 * 基类
 */
public class BaseFragment extends Fragment {
    protected String TAG = "BaseViewModel";
    protected long onCreateTime;
    protected long onResumeTime;
    protected boolean isOnPause = true;
    protected View mRootView;

    public BaseFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        TAG = getClass().getSimpleName();
        onCreateTime = System.currentTimeMillis();
        log("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        try {
            if (savedInstanceState == null) {
                savedInstanceState = getArguments();
            }
            initIntent(savedInstanceState);
        } catch (Throwable e) {
            LogUtils.e(TAG, "onCreate Throwable:", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log("onCreateView");
        mRootView = getLayoutView();
        if (mRootView != null) {
            return mRootView;
        }
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        try {
            initObserver();
            initView();
            initListener();
            setViewSize(getResources().getConfiguration());
            initData();
        } catch (Throwable e) {
            LogUtils.e(TAG, "onActivityCreated Throwable:", e);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于onResume()方法
            LogUtils.d(TAG, "当前可见Fragment:" + this.getClass().getSimpleName());
        } else {
            // 相当于onPause()方法
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            // 相当于onPause()方法
        } else {
            // 相当于onResume()方法
            LogUtils.d(TAG, "当前可见Fragment:" + this.getClass().getSimpleName());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    public void onResume() {
        isOnPause = false;
        super.onResume();
        onResumeTime = System.currentTimeMillis();
        log("onResume");
    }

    @Override
    public void onPause() {
        isOnPause = true;
        super.onPause();
        log("onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        log("onStop");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        log("onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        log("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        log("onDetach");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("onConfigurationChanged");
        setViewSize(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        log("onLowMemory");
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        log("onPictureInPictureModeChanged isInPictureInPictureMode:" + isInPictureInPictureMode);
    }

    protected void log(String msg) {
        long intervalTime = System.currentTimeMillis() - onCreateTime;
        LogUtils.d(BaseConstant.Log.PAGE_LIFE, TAG + " " + msg + ",Interval time:" + intervalTime);
    }

    protected View getLayoutView() {
        return null;
    }

    protected void initIntent(Bundle bundle) {
    }

    protected void initObserver() {
    }

    protected void initView() {
    }

    protected void initListener() {
    }

    protected void setViewSize(Configuration newConfig) {
    }

    protected void initData() {
        log("iniData");
    }
}
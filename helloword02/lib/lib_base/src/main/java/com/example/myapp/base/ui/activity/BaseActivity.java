package com.example.myapp.base.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.LogUtils;

/**
 * 基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = "BaseActivity";
    private final String BUNDLE_KEY_ISONSAVEINSTANCE = "Bundle_key_IsOnSaveInstance";
    protected long mOnCreateTime;
    protected long onResumeTime;
    protected boolean mIsOnSaveInstance = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            TAG = getClass().getSimpleName();
            mOnCreateTime = System.currentTimeMillis();
            log("onCreate");
            initIntent(savedInstanceState);
            initContentView();
            initObserver();
        } catch (Throwable t) {
            LogUtils.e(TAG, "onCreate t:", t);
        }
    }

    protected void initObserver() {
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        try {
            log("onContentChanged");
            initView();
            initListener();
            setViewSize(getResources().getConfiguration());
            initData();
        } catch (Throwable t) {
            LogUtils.e(TAG, "onContentChanged t:", t);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log("onRestoreInstanceState");
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
        mIsOnSaveInstance = false;
        onResumeTime = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        log("onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("onStop");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        log("onSaveInstanceState");
        outState.putBoolean(BUNDLE_KEY_ISONSAVEINSTANCE, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("onConfigurationChanged");
        setViewSize(newConfig);
    }

    protected void log(String msg) {
        long intervalTime = System.currentTimeMillis() - mOnCreateTime;
        LogUtils.d(BaseConstant.Log.PAGE_LIFE, TAG + " " + msg + ",Interval time:" + intervalTime);
    }

    protected void initIntent(Bundle bundle) {
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        if (bundle != null) {
            mIsOnSaveInstance = bundle.getBoolean(BUNDLE_KEY_ISONSAVEINSTANCE, false);
        }
    }

    protected abstract View getLayoutView();

    protected void initContentView() {
        View layoutView = getLayoutView();
        if (layoutView != null) {
            setContentView(layoutView);
        }
    }

    protected void initView() {
    }

    protected void initListener() {
    }

    protected void setViewSize(Configuration configuration) {
    }

    protected void initData() {
        log("iniData");
    }
}
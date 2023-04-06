package com.example.myapp.base.widget.base;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;

import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

public class BaseLinearLayout extends LinearLayout {
    protected String TAG = "BaseLinearLayout";
    protected ComponentActivity activity;

    public BaseLinearLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BaseLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setViewSize();
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            activity = (ComponentActivity) Utils.getActivity(context);
            TAG = getClass().getSimpleName();
            initView(attrs);
            setViewSize();
            initData();
            initListener();
        } catch (Throwable e) {
            LogUtils.e(TAG, "init Throwable:", e);
        }
    }

    protected void initView(AttributeSet attrs) {
    }

    protected void setViewSize() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }
}

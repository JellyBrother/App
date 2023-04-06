package com.example.myapp.base.widget.video;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

public class BaseVideoView extends RelativeLayout {
    public static String TAG = "BaseVideoView";
    protected ComponentActivity mActivity;
    protected VideoView mVideoView;
    protected VideoOption videoOption;

    public BaseVideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d(TAG, "onConfigurationChanged Configuration:" + newConfig);
        setViewSize(newConfig);
    }

    private void init(Context context) {
        // 初始化
        try {
            TAG = getClass().getSimpleName();
            LogUtils.d(TAG, "init");
            mActivity = (ComponentActivity) Utils.getActivity(getContext());
            initView(context);
            setViewSize(getResources().getConfiguration());
            initListener();
            initData();
        } catch (Throwable t) {
            LogUtils.e(TAG, "init Throwable:" + t);
        }
    }

    protected void initView(Context context) {
        LogUtils.d(TAG, "initView");
    }

    protected void setViewSize(Configuration configuration) {
        LogUtils.d(TAG, "setViewSize");
    }

    protected void initListener() {
        LogUtils.d(TAG, "initListener");
        mActivity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                switch (event) {
                    case ON_RESUME:
                        onResume();
                        break;
                    case ON_PAUSE:
                        onPause();
                        break;
                    case ON_DESTROY:
                        onDestroy();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    protected void initData() {
        LogUtils.d(TAG, "initData");
    }

    protected void onPause() {
        LogUtils.d(TAG, "onPause");
        mVideoView.suspend();
    }

    protected void onResume() {
        LogUtils.d(TAG, "onResume");
        mVideoView.resume();
    }

    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        mVideoView.stopPlayback();
        removeAllViews();
    }

    public void setData(VideoOption videoOption) {
        LogUtils.d("setData VideoOption:" + videoOption);
        this.videoOption = videoOption;
    }
}

package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.example.myapp.base.bridge.jump.MainJumpUtil;
import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.ui.activity.BaseActivity;
import com.example.myapp.base.utils.ActivityUtils;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.databinding.AppActSplashBinding;

public class SplashActivity extends BaseActivity {
    private AppActSplashBinding mBinding;

    @Override
    protected View getLayoutView() {
        mBinding = AppActSplashBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtils.finishOtherActivities(SplashActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSplashUri();
                jump();
            }
        }, 1000);
    }

    private void getSplashUri() {
        Intent intent = getIntent();
        if (intent == null) {
            LogUtils.d(TAG, "getSplashUri intent == null");
            return;
        }
        String action = intent.getAction();
        LogUtils.d(TAG, "getSplashUri action:" + action);
        if (!TextUtils.equals(action, Intent.ACTION_VIEW)) {
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        BaseConstant.Base.splashUri = uri;
        LogUtils.d(TAG, "getSplashUri splashUri:" + BaseConstant.Base.splashUri);
    }

    private void jump() {
        MainJumpUtil.jumpMainActivity();
        finish();
    }
}
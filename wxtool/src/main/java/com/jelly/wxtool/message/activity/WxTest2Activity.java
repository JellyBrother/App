package com.jelly.wxtool.message.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.R;
import com.jelly.wxtool.main.utils.JumpUtil;
import com.jelly.wxtool.message.service.FloatingButtonService;
import com.jelly.wxtool.message.viewmodel.WxToolMainViewModel;

public class WxTest2Activity extends BaseActivity<WxToolMainViewModel>{
    private static final String TAG = "WxToolMainActivity";
    private static final int SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS_MUST = 1;
    private TextView mTvJumpWx;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.wxtool_act_main);
        initViews();
        initListeners();
    }

    @Override
    protected WxToolMainViewModel initViewModel() {
        return new WxToolMainViewModel();
    }

    @Override
    protected void observerData() {

    }

    private void initViews() {
        mTvJumpWx = findViewById(R.id.tv_init);

    }

    private void initListeners() {
        mTvJumpWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FloatingButtonService.isStarted) {
                    return;
                }
                if (!Settings.canDrawOverlays(mActivity)) {
                    ToastUtil.makeText("当前无权限，请授权");
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
                } else {
                    startService(new Intent(mActivity, FloatingButtonService.class));
                    JumpUtil.startWeixinActivity(mActivity);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION) {
            LogUtil.getInstance().d(TAG, "onActivityResult");
            if (!Settings.canDrawOverlays(mActivity.getApplication())) {
                ToastUtil.makeText("授权失败");
            } else {
                ToastUtil.makeText("授权成功");
                startService(new Intent(mActivity, FloatingButtonService.class));
                JumpUtil.startWeixinActivity(mActivity);
            }
        }
    }
}

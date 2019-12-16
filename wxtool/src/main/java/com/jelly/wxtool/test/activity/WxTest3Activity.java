package com.jelly.wxtool.test.activity;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.R;
import com.jelly.wxtool.test.service.AddFriendService3;
import com.jelly.wxtool.test.service.FloatingButtonService;
import com.jelly.wxtool.test.viewmodel.WxToolTestActViewModel;

public class WxTest3Activity extends BaseLifecycleActivity<WxToolTestActViewModel> {
    private static final String TAG = "WxToolMainActivity";
    private static final int SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS_MUST = 1;
    private TextView mTvJumpWx;
    private TextView mTvAddFriend;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.wxtool_act_main_test);
        initViews();
        initListeners();
    }

    @Override
    protected WxToolTestActViewModel initViewModel() {
        return new WxToolTestActViewModel();
    }

    @Override
    protected void observerData() {

    }

    private void initViews() {
        mTvJumpWx = findViewById(R.id.tv_init);
        mTvAddFriend = findViewById(R.id.tv_add_friend);
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
                    startWeiXin();
                }
            }
        });
        mTvAddFriend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Toast.makeText(mActivity, "7.0及以上才有手势", Toast.LENGTH_SHORT).show();
                    return;
                }
                Path path = new Path();
                path.moveTo(10, 800);
                path.lineTo(400, 800);
                final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, 500);
                AddFriendService3.mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(),
                        new AccessibilityService.GestureResultCallback() {
                            @Override
                            public void onCompleted(GestureDescription gestureDescription) {
                                super.onCompleted(gestureDescription);
                                Toast.makeText(mActivity, "手势返成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(GestureDescription gestureDescription) {
                                super.onCancelled(gestureDescription);
                                Toast.makeText(mActivity, "手势失败", Toast.LENGTH_SHORT).show();
                            }
                        }, null);
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
                startWeiXin();
            }
        }
    }

    private void startWeiXin() {
        // 这里是启动悬浮窗
//        startService(new Intent(mActivity, FloatingButtonService.class));
        // 启动辅助功能
        if (!AddFriendService3.isStart()) {
            try {
                startService(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//                JumpUtil.startWeixinActivity(mActivity);
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        }
    }
}

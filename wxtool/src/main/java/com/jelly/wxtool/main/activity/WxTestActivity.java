package com.jelly.wxtool.main.activity;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.wxtool.R;
import com.jelly.wxtool.main.WxToolMainViewModel;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class WxTestActivity extends BaseActivity<WxToolMainViewModel> implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "WxToolMainActivity";
    private static final int SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS_MUST = 1;
    private TextView mTvJumpWx;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.wxtool_act_main);
        initViews();
        initListeners();
//        requestPermissions();
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
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION);

//                WindowUtil.showPopupWindow(mActivity);

//                try {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setComponent(cmp);
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    ToastUtil.makeText("检查到您手机没有安装微信，请安装后使用该功能");
//                }
            }
        });
    }

    /**
     * 权限申请描述：
     * EasyPermissions.hasPermissions判断权限列表是否全部申请
     * 没有就会申请没有同意的权限EasyPermissions.requestPermissions
     * 所有权限申请完毕后，
     * 执行onPermissionsGranted返回同意列表
     * 执行onPermissionsDenied返回拒绝列表
     */
    private void requestPermissions() {
        String[] perms = {Manifest.permission.SYSTEM_ALERT_WINDOW};
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
            // 已经申请过权限
            LogUtil.getInstance().d(TAG, "requestPermissions hasPermissions");
        } else {
            // 没有申请过权限，现在去申请
            EasyPermissions.requestPermissions(mActivity, "需要必要的权限", REQUEST_PERMISSIONS_MUST, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 把执行结果的操作给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            LogUtil.getInstance().d(TAG, "onPermissionsGranted");
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // 请求失败
        if (perms.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_ACTION_MANAGE_OVERLAY_PERMISSION){
            LogUtil.getInstance().d(TAG, "onActivityResult");
        }
    }
}

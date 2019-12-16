package com.jelly.wxtool.search.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.main.utils.WxToolJumpUtil;
import com.jelly.wxtool.search.utils.AccessibilityUtil;
import com.jelly.wxtool.search.viewmodel.AddBySearchActViewModel;
import com.jelly.wxtool.search.service.AddBySearchService;

public class AddBySearchAct extends BaseLifecycleActivity<AddBySearchActViewModel> {
    private static final String TAG = "WxToolMainAct";
    private static final int REQUEST_ALERT_PERMISSIONS = 1;
    private static final int REQUEST_ACCESSIBILITY_PERMISSIONS = 2;

    @Override
    protected void initView(Bundle savedInstanceState) {
        requestAlertPermissions();
    }

    @Override
    protected AddBySearchActViewModel initViewModel() {
        return new AddBySearchActViewModel();
    }

    @Override
    protected void observerData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.getInstance().d(TAG, "onActivityResult requestCode:" + requestCode);
        if (requestCode == REQUEST_ALERT_PERMISSIONS) {
            if (!Settings.canDrawOverlays(mActivity.getApplication())) {
                LogUtil.getInstance().d(TAG, "onActivityResult requestAlertPermissions fail");
                ToastUtil.makeText("悬浮窗授权失败");
                finish();
            } else {
                LogUtil.getInstance().d(TAG, "onActivityResult requestAlertPermissions success");
                ToastUtil.makeText("悬浮窗授权成功");
                requestAccessibilityPermissions();
            }
        }
        if (requestCode == REQUEST_ACCESSIBILITY_PERMISSIONS) {
            requestAccessibilityPermissions();
        }
    }

    // 请求悬浮窗
    private void requestAlertPermissions() {
        LogUtil.getInstance().d(TAG, "requestAlertPermissions");
        if (AddBySearchService.isServiceStarted) {
            startAccessibilityService();
            return;
        }
        if (Settings.canDrawOverlays(mActivity)) {
            LogUtil.getInstance().d(TAG, "requestAlertPermissions requestAccessibilityPermissions");
            requestAccessibilityPermissions();
        } else {
            ToastUtil.makeText("当前无权限，请授权");
            LogUtil.getInstance().d(TAG, "requestAlertPermissions startActivityForResult");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_ALERT_PERMISSIONS);
        }
    }

    // 请求辅助功能
    private void requestAccessibilityPermissions() {
        if (AccessibilityUtil.checkAccessibilityEnabled(AddBySearchService.class)) {
            startAccessibilityService();
            return;
        }
        new AlertDialog.Builder(mActivity)
                .setTitle("请求悬浮窗权限")
                .setMessage("微信辅助功能需要授权悬浮窗和辅助功能权限，是否授权？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions startActivityForResult1");
                            startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQUEST_ACCESSIBILITY_PERMISSIONS);
                        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
                            LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions startActivityForResult1 exception:" + e.toString());
                            try {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions startActivityForResult2");
                                startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSIONS);
                            } catch (Throwable e2) {
                                LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions startActivityForResult2 exception:" + e.toString());
                            }
                        }
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void startAccessibilityService() {
        LogUtil.getInstance().d(TAG, "startAccessibilityService");
        AccessibilityUtil.wakeUpScreen(mActivity);
        WxToolJumpUtil.startWeixinActivity(mActivity);
    }
}

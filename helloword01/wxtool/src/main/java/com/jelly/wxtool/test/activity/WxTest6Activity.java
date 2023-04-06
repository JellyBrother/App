package com.jelly.wxtool.test.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.main.utils.WxToolJumpUtil;
import com.jelly.wxtool.test.service.AddFriendService;
import com.jelly.wxtool.test.utils.AccessibilityUtil;
import com.jelly.wxtool.test.viewmodel.WxToolTestActViewModel;

public class WxTest6Activity extends BaseLifecycleActivity<WxToolTestActViewModel> {
    private static final String TAG = "WxToolMainActivity";
    private static final int REQUEST_ALERT_PERMISSIONS = 1;
    private static final int REQUEST_ACCESSIBILITY_PERMISSIONS = 2;

    @Override
    protected void initView(Bundle savedInstanceState) {
        LogUtil.getInstance().d(TAG, "initView");
        initViews();
        initListeners();
        requestAlertPermissions();
    }

    @Override
    protected WxToolTestActViewModel initViewModel() {
        return new WxToolTestActViewModel();
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

    private void initViews() {
    }

    private void initListeners() {
    }

    // 请求悬浮窗
    private void requestAlertPermissions() {
        LogUtil.getInstance().d(TAG, "requestAlertPermissions");
        if (AddFriendService.isServiceStarted) {
            startAccessibilityService();
            return;
        }
        if (!Settings.canDrawOverlays(mActivity)) {
            ToastUtil.makeText("当前无权限，请授权");
            LogUtil.getInstance().d(TAG, "requestAlertPermissions startActivityForResult");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_ALERT_PERMISSIONS);
        } else {
            LogUtil.getInstance().d(TAG, "requestAlertPermissions requestAccessibilityPermissions");
            requestAccessibilityPermissions();
        }
    }

    // 请求辅助功能
    private void requestAccessibilityPermissions() {
        if (AddFriendService.isServiceStarted) {
            startAccessibilityService();
            return;
        }
        try {
            int enable = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            if (enable == 1) {
                String services = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (!TextUtils.isEmpty(services)) {
                    TextUtils.SimpleStringSplitter split = new TextUtils.SimpleStringSplitter(':');
                    split.setString(services);
                    while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                        if (split.next().equalsIgnoreCase(getPackageName() + "/" + "AccessibilityService")) {
                            // 有辅助功能权限
                            LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions startAccessibilityService");
                            startAccessibilityService();
                        }
                    }
                }
            }
        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            LogUtil.getInstance().d(TAG, "requestAccessibilityPermissions exception:" + e.toString());
        }
        new AlertDialog.Builder(mActivity)
                .setTitle("checkSetting标题")
                .setMessage("checkSetting内容")
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

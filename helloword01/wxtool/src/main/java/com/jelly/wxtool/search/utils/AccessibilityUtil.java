package com.jelly.wxtool.search.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.PublicUtil;

import java.util.List;

/**
 * 辅助功能/无障碍相关工具
 */
public class AccessibilityUtil {
    private static final String TAG = "AccessibilityUtil";

    /**
     * Check当前辅助服务是否启用
     *
     * @return 是否启用
     */
    public static boolean checkAccessibilityEnabled(Class service) {
        AccessibilityManager mAccessibilityManager = (AccessibilityManager) BaseCommon.Base.application.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(service.getName())) {
                return true;
            }
        }
        return isSettingOpen(service, BaseCommon.Base.application);
    }

    /**
     * 检查系统设置：是否开启辅助服务
     *
     * @param service 辅助服务
     */
    public static boolean isSettingOpen(Class service, Context cxt) {
        try {
            int enable = Settings.Secure.getInt(cxt.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            if (enable != 1)
                return false;
            String services = Settings.Secure.getString(cxt.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (!TextUtils.isEmpty(services)) {
                TextUtils.SimpleStringSplitter split = new TextUtils.SimpleStringSplitter(':');
                split.setString(services);
                while (split.hasNext()) { // 遍历所有已开启的辅助服务名
                    if (split.next().equalsIgnoreCase(cxt.getPackageName() + "/" + service.getName()))
                        return true;
                }
            }
        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            Log.e(TAG, "isSettingOpen: " + e.getMessage());
        }
        return false;
    }

    /**
     * 唤醒点亮和解锁屏幕(60s)
     */
    public static void wakeUpScreen(Context context) {
        try {
            //唤醒点亮屏幕
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isScreenOn()) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "wakeUpScreen");
                wl.acquire(60000); // 60s后释放锁
            }

            //解锁屏幕
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null && km.inKeyguardRestrictedInputMode()) {
                KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
                kl.disableKeyguard();
            }
        } catch (Throwable e) {
            Log.e(TAG, "wakeUpScreen: " + e.getMessage());
        }
    }

    /**
     * 跳转到系统设置：开启辅助服务
     */
    public static void jumpToSetting(final Context cxt) {
        try {
            cxt.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Throwable e) {//若出现异常，则说明该手机设置被厂商篡改了,需要适配
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cxt.startActivity(intent);
            } catch (Throwable e2) {
                Log.e(TAG, "jumpToSetting: " + e2.getMessage());
            }
        }
    }

    public static void recycleData(Object... params) {
        if (params == null) {
            return;
        }
        int length = params.length;
        if (length == 0) {
            return;
        }
        for (int i = 0; i < length; i++) {
            Object object = params[i];
            if (object instanceof AccessibilityNodeInfo) {
                ((AccessibilityNodeInfo) object).recycle();
            }
            if (object instanceof List) {
                ((List) object).clear();
                object = null;
            }
        }
    }
}
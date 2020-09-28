package com.jelly.app.main.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.jelly.baselibrary.utils.LogUtil;

public class AppJumpUtil {
    private static final String TAG = "AppJumpUtil";

    public static void startChatMainActivity(Activity mActivity) {
        try {
            Intent intent = new Intent("com.jelly.chat.main.activity.ChatMainActivity");
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.getInstance().e(TAG, "打开ChatMainActivity页面失败");
        }
    }

    public static void startOtherToolMainActivity(Activity mActivity) {
        try {
            Intent intent = new Intent("com.jelly.othertool.main.activity.OtherToolMainActivity");
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.getInstance().e(TAG, "打开OtherToolMainActivity页面失败");
        }
    }

    public static void startWxToolMainActivity(Activity mActivity) {
        try {
            Intent intent = new Intent("com.jelly.wxtool.main.activity.WxToolMainAct");
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.getInstance().e(TAG, "打开WxToolMainActivity页面失败");
        }
    }

    public static void startWechatBusinessToolMainAct(Activity mActivity) {
        try {
            Intent intent = new Intent("com.jelly.wechatbusinesstool.main.activity.WechatBusinessToolMainAct");
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtil.getInstance().e(TAG, "打开WechatBusinessToolMainAct页面失败");
        }
    }
}
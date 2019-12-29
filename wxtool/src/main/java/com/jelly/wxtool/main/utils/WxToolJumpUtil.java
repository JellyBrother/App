package com.jelly.wxtool.main.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.search.activity.AddBySearchAct;
import com.jelly.wxtool.test.activity.WxToolTestAct;

public class WxToolJumpUtil {
    private static final String TAG = "WxToolJumpUtil";

    public static void startWxTestActivity(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
        LogUtil.getInstance().i(TAG,"startWxTestActivity");
        Intent intent = new Intent(mActivity, WxToolTestAct.class);
        mActivity.startActivity(intent);
    }

    public static void startAddBySearchAct(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
        LogUtil.getInstance().i(TAG,"startAddBySearchAct");
        Intent intent = new Intent(mActivity, AddBySearchAct.class);
        mActivity.startActivity(intent);
    }

    public static void startWeixinActivity(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
        LogUtil.getInstance().i(TAG,"startWeixinActivity");
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.makeText("检查到您手机没有安装微信，请安装后使用该功能");
        }
    }
}
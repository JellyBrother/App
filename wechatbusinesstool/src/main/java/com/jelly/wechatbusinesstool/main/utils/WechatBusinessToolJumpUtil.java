package com.jelly.wechatbusinesstool.main.utils;

import android.app.Activity;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;

public class WechatBusinessToolJumpUtil {
    private static final String TAG = "OtherToolJumpUtil";

    public static void startSearchListAct(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
        LogUtil.getInstance().d(TAG, "startSearchListAct");

    }
}
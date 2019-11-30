package com.jelly.chatlibrary.main.utils;
import android.app.Activity;

import com.jelly.baselibrary.utils.PublicUtil;

public class JumpUtil {
    private static final String TAG = "JumpUtil";

    public static void startMessageMainActivity(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
    }
}
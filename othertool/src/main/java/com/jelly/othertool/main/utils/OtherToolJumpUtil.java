package com.jelly.othertool.main.utils;

import android.app.Activity;
import android.content.Intent;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.othertool.search.activity.SearchListAct;

public class OtherToolJumpUtil {
    private static final String TAG = "OtherToolJumpUtil";

    public static void startSearchListAct(Activity mActivity) {
        if (PublicUtil.isFastClik()) {
            return;
        }
        LogUtil.getInstance().d(TAG, "startSearchListAct");
        Intent intent = new Intent(mActivity, SearchListAct.class);
        mActivity.startActivity(intent);
    }
}
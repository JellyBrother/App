package com.jelly.baselibrary.utils;

import android.widget.Toast;

import com.jelly.baselibrary.common.BaseCommon;

public class ToastUtil {

    public static void makeText(String text) {
        Toast.makeText(BaseCommon.Base.application, text, Toast.LENGTH_LONG).show();
    }

    public static void makeText(int text) {
        Toast.makeText(BaseCommon.Base.application, text, Toast.LENGTH_LONG).show();
    }
}
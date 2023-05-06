package com.jelly.jetpack.module.base.utils;

import android.widget.Toast;

import com.jelly.jetpack.module.base.constant.BaseConstant;

public class ToastUtil {

    public static void showShort(String text) {
        Toast.makeText(BaseConstant.Base.sApp, text, Toast.LENGTH_SHORT).show();
    }
}

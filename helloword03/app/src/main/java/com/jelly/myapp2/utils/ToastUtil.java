package com.jelly.myapp2.utils;

import android.widget.Toast;

import com.jelly.myapp2.base.constant.Constant;

public class ToastUtil {

    public static void showShort(String text) {
        Toast.makeText(Constant.App.app, text, Toast.LENGTH_SHORT).show();
    }
}

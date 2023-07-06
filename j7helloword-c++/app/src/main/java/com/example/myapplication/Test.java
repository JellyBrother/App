package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

public class Test {
    public static String getString(int num) {
        return "aa" + num;
    }

    public static Drawable getDrawable(Context context) {
        try {
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.jelly_img);
            int color = resources.getColor(R.color.jelly_blue);
            View inflate = LayoutInflater.from(context).inflate(R.layout.activity_main2, null);
            return drawable;
        }catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
}

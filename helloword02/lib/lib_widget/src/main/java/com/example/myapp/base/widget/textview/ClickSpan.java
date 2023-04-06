package com.example.myapp.base.widget.textview;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

import androidx.annotation.NonNull;

/**
 * 不带下划线的点击span
 */
public abstract class ClickSpan extends ClickableSpan {

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(ds.linkColor);
    }
}

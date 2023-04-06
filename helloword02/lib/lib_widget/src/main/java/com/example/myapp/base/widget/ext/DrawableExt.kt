package com.example.myapp.base.widget.ext

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.myapp.base.utils.Utils

fun Drawable.setTintColorInt(@ColorInt tintColor: Int): Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
//    wrapDrawable.setBounds(
//        0,
//        0,
//        wrapDrawable.intrinsicWidth,
//        wrapDrawable.intrinsicHeight
//    )
    DrawableCompat.setTint(
        wrapDrawable,
        tintColor
    )
    return wrapDrawable
}

fun Drawable.setTintColor(@ColorRes tintColor: Int): Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
    wrapDrawable.setBounds(
        0,
        0,
        wrapDrawable.intrinsicWidth,
        wrapDrawable.intrinsicHeight
    )
    DrawableCompat.setTint(
        wrapDrawable,
        ContextCompat.getColor(Utils.getApp(), tintColor)
    )
    return wrapDrawable
}
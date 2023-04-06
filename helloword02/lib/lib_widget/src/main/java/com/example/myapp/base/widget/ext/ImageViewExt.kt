package com.example.myapp.base.widget.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.myapp.base.utils.Utils

fun ImageView.setTintDrawable(@DrawableRes drawableId: Int, @ColorRes tintColor: Int) {
    ContextCompat.getDrawable(Utils.getApp(), drawableId)?.setTintColor(tintColor)?.let {
        this.setImageDrawable(it)
    }
}

fun ImageView.setTintDrawable(drawable: Drawable?, @ColorRes tintColor: Int) {
    drawable?.setTintColor(tintColor)?.let {
        this.setImageDrawable(it)
    }
}
package com.example.myapp.base.widget.ext

import android.text.Html
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.myapp.base.utils.StringUtils
import com.example.myapp.base.utils.Utils

fun TextView.setHtml(@StringRes stringResId: Int) {
    text = Html.fromHtml(StringUtils.getString(stringResId))
}

fun TextView.drawableEnd(@DrawableRes endDrawableId: Int?) =
    setCompoundDrawablesWithIntrinsicBounds(
        null,
        null,
        if (endDrawableId != null) ContextCompat.getDrawable(
            Utils.getApp(),
            endDrawableId
        ) else null,
        null
    )

fun TextView.drawableStart(@DrawableRes startDrawableId: Int?) =
    setCompoundDrawablesWithIntrinsicBounds(
        if (startDrawableId != null) ContextCompat.getDrawable(
            Utils.getApp(),
            startDrawableId
        ) else null,
        null,
        null,
        null
    )

fun TextView.drawableTop(@DrawableRes topDrawableId: Int?) =
    setCompoundDrawablesWithIntrinsicBounds(
        null,
        if (topDrawableId != null) ContextCompat.getDrawable(
            Utils.getApp(),
            topDrawableId
        ) else null,
        null,
        null
    )
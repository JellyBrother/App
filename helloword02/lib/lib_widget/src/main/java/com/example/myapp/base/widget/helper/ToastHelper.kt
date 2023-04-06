package com.example.myapp.base.widget.helper

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.myapp.base.widget.ext.drawableTop
import com.example.myapp.base.utils.StringUtils
import com.example.myapp.base.utils.StringUtils.getString
import com.example.myapp.base.utils.ViewUtils
import com.example.myapp.base.widget.R
import com.example.myapp.base.widget.toast.ToastUtils

object ToastHelper {
    private fun makeCommonToast(): ToastUtils {
        return ToastUtils
            .make()
            .setGravity(Gravity.CENTER, -1, -1)
//            .setBgResource(R.drawable.base_toast_bg)
            .setTextColor(Color.WHITE)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param text The text.
     */
    fun showShort(text: CharSequence?) {
        makeCommonToast().setDurationIsLong(false).show(text)
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     */
    fun showShort(@StringRes resId: Int) {
        makeCommonToast().setDurationIsLong(false).show(getString(resId))
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    fun showShort(@StringRes resId: Int, vararg args: Any?) {
        makeCommonToast().setDurationIsLong(false).show(getString(resId, *args))
    }

    /**
     * Show the toast for a short period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    fun showShort(format: String?, vararg args: Any?) {
        makeCommonToast().setDurationIsLong(false).show(StringUtils.format(format, *args))
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param text The text.
     */
    fun showLong(text: CharSequence?) {
        makeCommonToast().setDurationIsLong(true).show(text)
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     */
    fun showLong(@StringRes resId: Int) {
        makeCommonToast().setDurationIsLong(true).show(getString(resId))
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param resId The resource id for text.
     * @param args  The args.
     */
    fun showLong(@StringRes resId: Int, vararg args: Any?) {
        makeCommonToast().setDurationIsLong(true).show(getString(resId, *args))
    }

    /**
     * Show the toast for a long period of time.
     *
     * @param format The format.
     * @param args   The args.
     */
    fun showLong(format: String?, vararg args: Any?) {
        makeCommonToast().setDurationIsLong(true).show(StringUtils.format(format, *args))
    }
}
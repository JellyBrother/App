package com.example.myapp.base.widget.immersion

import android.app.Activity
import androidx.annotation.ColorInt
import com.gyf.immersionbar.ImmersionBar
import com.example.myapp.base.utils.ColorUtils

/**
 * Activity的沉浸式纯色状态栏实现
 * @param barColor 状态栏和导航栏颜色
 */
class ActivityColorBar(@ColorInt private val barColor: Int, val activity: Activity) :
    BaseBar {
    override fun initImmersionBar() {
        ImmersionBar.with(activity)
            .statusBarColorInt(barColor)
            .navigationBarColorInt(barColor)
            .statusBarDarkFont(ColorUtils.isLightColor(barColor)) // 状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(ColorUtils.isLightColor(barColor)) // 导航栏图标是深色，不写默认为亮色
            .fitsSystemWindows(true) // //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色
            .init()
    }
}
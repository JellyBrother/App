package com.example.myapp.base.widget.immersion

import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import com.gyf.immersionbar.ImmersionBar
import com.example.myapp.base.utils.ColorUtils

/**
 * DialogFragment的沉浸式纯色状态栏实现
 * @param barColor 状态栏和导航栏颜色
 */
class DialogFragmentColorBar(@ColorInt private val barColor: Int, val fragment: DialogFragment) :
    BaseBar {
    override fun initImmersionBar() {
        ImmersionBar.with(fragment)
            .statusBarColorInt(barColor)
            .navigationBarColorInt(barColor)
            .statusBarDarkFont(ColorUtils.isLightColor(barColor)) // 状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(ColorUtils.isLightColor(barColor)) // 导航栏图标是深色，不写默认为亮色
            .fitsSystemWindows(true) // //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色
            .init()
    }
}
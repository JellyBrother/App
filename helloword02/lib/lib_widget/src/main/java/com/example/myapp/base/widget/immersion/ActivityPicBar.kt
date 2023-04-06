package com.example.myapp.base.widget.immersion

import android.app.Activity
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import com.gyf.immersionbar.ImmersionBar
import com.example.myapp.base.utils.ColorUtils

/**
 * Activity的沉浸式图片状态栏实现
 * @param viewId 解决状态栏和布局重叠问题
 * @param navigationBarColor 导航栏颜色
 */
class ActivityPicBar(
    @IdRes val viewId: Int,
    private val isStatusDarkFont: Boolean,
    @ColorInt val navigationBarColor: Int,
    val activity: Activity
) :
    BaseBar {
    override fun initImmersionBar() {
        ImmersionBar.with(activity)
            .transparentStatusBar()
            .titleBar(viewId)
            .navigationBarColorInt(navigationBarColor)
            .statusBarDarkFont(isStatusDarkFont) // 状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(ColorUtils.isLightColor(navigationBarColor)) // 导航栏图标是深色，不写默认为亮色
            .init()
    }
}
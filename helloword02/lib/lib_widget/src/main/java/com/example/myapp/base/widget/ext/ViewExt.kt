package com.example.myapp.base.widget.ext

import android.app.Activity
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.example.myapp.base.utils.ClickUtils

fun View.setVisible(isShow: Boolean) {
    visibility = if (isShow) View.VISIBLE else View.GONE
}

fun View.setWidth(width: Int) {
    val lp = layoutParams
    lp.width = width
    layoutParams = lp
}

fun View.setHeight(height: Int) {
    val lp = layoutParams
    lp.height = height
    layoutParams = lp
}

fun View.singleClick(listener: View.OnClickListener) {
    ClickUtils.applySingleDebouncing(this, listener)
}

fun View.wrapView(@LayoutRes layoutId: Int, initBlock: ((v: View) -> Unit)? = null): View {
    val viewGroup = (parent as? ViewGroup)
    val index = viewGroup?.indexOfChild(this) ?: -1

    viewGroup?.removeView(this)

    val child = this
    val childLayoutParams = layoutParams

    val viewWrap = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
    val frameLayout = FrameLayout(context)
    viewGroup?.addView(frameLayout, index, childLayoutParams)
    frameLayout.apply {
        addView(
            child,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
        )
        addView(
            viewWrap,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
        )
    }

    initBlock?.invoke(viewWrap)

    return viewWrap
}

val View.activity: Activity?
    get() {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        return null
    }

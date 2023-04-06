package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.children
import com.example.myapp.base.utils.LogUtils
import com.example.myapp.base.utils.ScreenUtils
import kotlin.math.abs

class DisallowTabChangeRelativeLayout : RelativeLayout {
    private var mInitialTouchX = 0f
    private var mInitialTouchY = 0f
    private val allowViewList by lazy { mutableListOf<View>() }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findAllowView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        allowViewList.clear()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialTouchX = ev.x
                mInitialTouchY = ev.y
                parent.requestDisallowInterceptTouchEvent(
                    !isTouchInAllowViews(
                        ev.x.toInt(),
                        ev.y.toInt()
                    )
                )
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouchInAllowViews(ev.x.toInt(), ev.y.toInt())) {
                    val dx = ev.x - mInitialTouchX
                    val dy = ev.y - mInitialTouchY
                    if (abs(dx) > abs(dy)) {
                        LogUtils.dTag("select-nestScroll-rootTab", "切换Tab：是")
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    } else {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun findAllowView(viewGroup: ViewGroup) {
        viewGroup.children.forEach {
            if (it.contentDescription == "allow") {
                allowViewList.add(it)
            } else {
                if (it is ViewGroup) {
                    findAllowView(it)
                }
            }
        }
        LogUtils.w(allowViewList.map { "${it::class.java.simpleName}${it.id}" })
    }

    private fun isTouchInAllowViews(x: Int, y: Int): Boolean {
        return allowViewList.any {
            ScreenUtils.isTouchPointInView(it, x, y)
        }
    }
}
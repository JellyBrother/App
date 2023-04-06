package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.base.utils.LogUtils
import kotlin.math.abs

class NestedHorizontalRecyclerView : RecyclerView {
    private var mInitialTouchX = 0f
    private var mInitialTouchY = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mInitialTouchX = ev.x
                mInitialTouchY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - mInitialTouchX
                val dy = ev.y - mInitialTouchY
                if (abs(dx) > abs(dy)) {
                    LogUtils.i("是否水平左边已经到底${!canScrollHorizontally(-1)}::${dx}")
                    LogUtils.i("是否水平右边已经到底${!canScrollHorizontally(1)}::${dx}")
                    // 水平左边已经到底还再继续往左滑动
                    if (!canScrollHorizontally(-1) && dx > 0) {
                        parent.requestDisallowInterceptTouchEvent(false)
                        return super.dispatchTouchEvent(ev)
                    }
                    // 水平右边已经到底还再继续往右滑动
                    if (!canScrollHorizontally(1) && dx < 0) {
                        parent.requestDisallowInterceptTouchEvent(false)
                        return super.dispatchTouchEvent(ev)
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
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
}
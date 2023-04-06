package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.base.utils.LogUtils
import kotlin.math.abs

class NestedVerticalRecyclerView : RecyclerView {
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
                if (abs(dy) > abs(dx)) {
                    // 垂直往上已经到底还再继续往上滑动
                    if (!canScrollVertically(-1) && dy > 0) {
                        LogUtils.dTag("select-nestScroll-childRecycler", "滑动产品列表：否")
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                    // 垂直往下已经到底还再继续往下滑动
                    if (!canScrollVertically(1) && dy < 0) {
                        LogUtils.dTag("select-nestScroll-childRecycler", "滑动产品列表：否")
                        parent.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                    LogUtils.dTag("select-nestScroll-childRecycler", "滑动产品列表：是")
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
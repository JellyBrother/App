package com.example.myapp.base.widget.table

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class TableRowScrollView : HorizontalScrollView {
    private var listener: ScrollChangedListener? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //记录当前触摸的HListViewScrollView
        listener?.setCurrentTouchView(this)
        return super.onTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        //当当前的HListViewScrollView被触摸时，滑动其它
        if (null != listener && null != listener!!.getCurrentTouchView() && listener!!.getCurrentTouchView() === this) {
            listener!!.onUIScrollChanged(l, t, oldl, oldt)
        } else {
            super.onScrollChanged(l, t, oldl, oldt)
        }
    }

    fun setScrollChangedListener(listener: ScrollChangedListener?) {
        this.listener = listener
    }

    interface ScrollChangedListener {
        fun setCurrentTouchView(currentTouchView: TableRowScrollView?)
        fun getCurrentTouchView(): TableRowScrollView?
        fun onUIScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }
}
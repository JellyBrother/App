package com.example.myapp.base.widget.table

import com.example.myapp.base.utils.LogUtils

class ScrollHelper : TableRowScrollView.ScrollChangedListener {
    //存放所有的可横向滚动的行
    private var mRowScrollViews: MutableList<TableRowScrollView> = ArrayList()
    private var currentTouchView: TableRowScrollView? = null
    private var mOnUIScrollChanged: OnUIScrollChanged? = null
    //表格行上次横向滑动的ScrollX
    private var rowLastScrollX: Int = 0

    fun addRowScrollView(hScrollView: TableRowScrollView) {
        hScrollView.setScrollChangedListener(this)
        mRowScrollViews.add(hScrollView)
    }

    fun rowScrollAlign(hScrollView: TableRowScrollView) {
        if (mRowScrollViews.isNotEmpty()) {
            //这是给第一次满屏，或者快速下滑等情况时，新创建的会再创建一个convertView的时候，把这个新进入的convertView里的HListViewScrollView移到对应的位置
            if (rowLastScrollX != 0) {
                hScrollView.post { //在主线程中去移动到对应的位置
                    hScrollView.scrollTo(rowLastScrollX, 0)
                }
            }
        }
        LogUtils.w("参数对比表格行列表${mRowScrollViews.map { it.scrollX }.joinToString(separator = "::")}")
    }

    override fun setCurrentTouchView(currentTouchView: TableRowScrollView?) {
        this.currentTouchView = currentTouchView
    }

    override fun getCurrentTouchView(): TableRowScrollView? {
        return currentTouchView
    }

    override fun onUIScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        if (mOnUIScrollChanged != null) {
            val hListViewScrollView: TableRowScrollView = firstRowScrollView
            val maxScrollX: Int =
                hListViewScrollView.getChildAt(0).measuredWidth - hListViewScrollView.measuredWidth
            mOnUIScrollChanged!!.onUIScrollChanged(
                l,
                oldl,
                maxScrollX,
                hListViewScrollView.scrollX
            )
        }
        for (scrollView in mRowScrollViews) {
            //防止重复滑动
            if (currentTouchView !== scrollView) {
                scrollView.smoothScrollTo(l, t)
                rowLastScrollX = l
            }
        }
    }

    /**
     * 立即滚动
     */
    fun allRowScrollTo(l: Int, t: Int) {
        for (scrollView in mRowScrollViews) {
            scrollView.scrollTo(l, t)
        }
    }

    /**
     * 平缓滚动
     */
    fun allRowSmoothScrollTo(l: Int, t: Int) {
        for (scrollView in mRowScrollViews) {
            scrollView.smoothScrollTo(l, t)
        }
    }

    fun scrollCurrentRow() {
        if (currentTouchView != null) {
            currentTouchView!!.post { //重新刷新对齐
                allRowScrollTo(currentTouchView!!.scrollX, 0)
            }
        }
    }

    val firstRowScrollView: TableRowScrollView
        get() = mRowScrollViews[0]

    fun setOnUIScrollChanged(onUIScrollChanged: OnUIScrollChanged) {
        mOnUIScrollChanged = onUIScrollChanged
    }

    fun recycle() {
        mRowScrollViews.clear()
    }

    interface OnUIScrollChanged {
        fun onUIScrollChanged(l: Int, oldl: Int, maxScrollX: Int, getScrollX: Int)
    }
}
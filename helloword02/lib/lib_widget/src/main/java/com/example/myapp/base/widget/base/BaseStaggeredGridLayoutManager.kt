package com.example.myapp.base.widget.base

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapp.base.utils.LogUtils

/**
 * 修复瀑布流bug
 * java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
 * java.util.ArrayList.get(ArrayList.java:437)
 * androidx.recyclerview.widget.StaggeredGridLayoutManager$Span.calculateCachedStart(Unknown Source:3)
 * androidx.recyclerview.widget.StaggeredGridLayoutManager$Span.getStartLine(Unknown Source:7)
 * https://blog.csdn.net/lvi_166/article/details/112315853?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-112315853-blog-77448164.pc_relevant_recovery_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-112315853-blog-77448164.pc_relevant_recovery_v2&utm_relevant_index=2
 */
class BaseStaggeredGridLayoutManager : StaggeredGridLayoutManager {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (t: Throwable) {
            LogUtils.e("onLayoutChildren", t)
        }
    }

    override fun onScrollStateChanged(state: Int) {
        try {
            super.onScrollStateChanged(state)
        } catch (t: Throwable) {
            LogUtils.e("onScrollStateChanged", t)
        }
    }
}
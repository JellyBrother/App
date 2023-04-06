package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.base.utils.LogUtils

class DividerItemDecoration : RecyclerView.ItemDecoration {
    private var mDivider: Drawable? = null
    private var mOrientation: Int = LinearLayoutManager.VERTICAL
    private val mBounds = Rect()
    private var lastIsShow = true

    constructor(
        context: Context,
        orientation: Int = LinearLayoutManager.VERTICAL,
    ) {
        val a = context.obtainStyledAttributes(ATTRS)
        this.mDivider = a.getDrawable(0)
        if (this.mDivider == null) {
            LogUtils.w(
                "DividerItem",
                "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()"
            )
        }
        a.recycle()
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        this.mOrientation = orientation
    }

    fun setLastIsShow(lastIsShow: Boolean) {
        this.lastIsShow = lastIsShow
    }

    fun setDrawable(drawable: Drawable?) {
        this.mDivider = drawable
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.layoutManager != null && mDivider != null) {
            if (mOrientation == 1) {
                drawVertical(c, parent)
            } else {
                drawHorizontal(c, parent)
            }
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left,
                parent.paddingTop,
                right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        //当最后一个子项的时候去除下划线
        val lastChildIndex = getLastChildIndex(parent)
        mDivider?.let {
            for (i in 0 until lastChildIndex) {
                val child = parent.getChildAt(i)
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.bottom + Math.round(child.translationY)
                val top = (bottom - it.intrinsicHeight)
                it.setBounds(left, top, right, bottom)
                LogUtils.dTag(
                    this::class.simpleName,
                    "index：$i +left：$left +top：$top +right：$right +bottom：$bottom"
                )
                it.draw(canvas)
            }
        }

        canvas.restore()
    }

    private fun getLastChildIndex(parent: RecyclerView): Int {
        return if (lastIsShow) parent.childCount else parent.childCount - 1
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft,
                top,
                parent.width - parent.paddingRight,
                bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }

        val lastChildIndex = getLastChildIndex(parent)
        mDivider?.let {
            for (i in 0 until lastChildIndex) {
                val child = parent.getChildAt(i)
                parent.layoutManager?.getDecoratedBoundsWithMargins(child, this.mBounds)
                val right = this.mBounds.right + Math.round(child.translationX)
                val left = right - it.intrinsicWidth
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
        }

        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0)
        } else {
            if (this.mOrientation == 1) {
                //纵向RecyclerView
                parent.adapter?.let {
                    outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
                }
            } else {
                //横向RecyclerView
                parent.adapter?.let {
                    outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
                }
            }
        }
    }

    fun getDividerHeight(): Int? {
        return mDivider?.intrinsicHeight
    }

    companion object {
        val ATTRS = intArrayOf(16843284)
    }
}
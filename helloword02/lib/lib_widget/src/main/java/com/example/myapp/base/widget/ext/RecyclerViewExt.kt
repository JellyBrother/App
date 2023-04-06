package com.example.myapp.base.widget.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 为View包裹ViewGroup
 */
fun <T : BaseQuickAdapter<out Any, out BaseViewHolder>> RecyclerView.commonSetAdapter(
    adapter: T,
    @LayoutRes emptyViewId: Int,
    emptyText: String? = null
) {
    this.adapter = adapter
    this.post {
        val emptyView =
            LayoutInflater.from(context).inflate(emptyViewId, this, false)
        // 只有emptyView是ViewGroup且emptyText不为空才会更改文本
        if (emptyView is ViewGroup && !emptyText.isNullOrEmpty()) {
            for (i in 0 until emptyView.childCount) {
                val childView = emptyView.getChildAt(i)
                if (childView is TextView) {
                    childView.text = emptyText
                }
            }
        }
        adapter.setEmptyView(emptyView)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.setSpaceOnClickListener(onClickListener: View.OnClickListener) {
    val gestureDetector = GestureDetector(context, object : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onClickListener.onClick(this@setSpaceOnClickListener)
            return false
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {

        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

    })
    setOnTouchListener { v, event ->
        //发现只有点击了空白处，v是自身recyclerView
        if (v is RecyclerView) {
            gestureDetector.onTouchEvent(event)
        }
        false
    }
}

fun <T : ViewGroup> View.wrapViewGroup(block: (Context) -> T): T {
    val viewGroup = (parent as? ViewGroup)
    val index = viewGroup?.indexOfChild(this) ?: -1

    viewGroup?.removeView(this)

    val child = this
    val childLayoutParams = layoutParams

    return block(context).apply {
        viewGroup?.addView(this, index, childLayoutParams)
        addView(
            child,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}
package com.example.myapp.base.utils

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {
    /**
     * 根据ItemView个数动态设置RecyclerView高度
     * @param maxItemCount: RecyclerView高度等于itemCount个ItemView的高度
     * @param itemPosition: 用来作为标准高度的ItemView的position
     */
    fun setRecyclerHeightByItemCount(
        rv: RecyclerView,
        itemPosition: Int,
        maxItemCount: Int,
        decorationHeight: Int = 0
    ) {
        rv.adapter?.let { adapter ->
            if (adapter.itemCount > 0) {
                val holder = adapter.createViewHolder(
                    rv, adapter
                        .getItemViewType(itemPosition)
                )
                adapter.onBindViewHolder(holder, itemPosition)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(
                        rv.width,
                        View.MeasureSpec.EXACTLY
                    ),
                    View.MeasureSpec.makeMeasureSpec(
                        0,
                        View.MeasureSpec.UNSPECIFIED
                    )
                )
                holder.itemView.layout(
                    0, 0, holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()

                val measuredHeight = holder.itemView.measuredHeight
                val lp = rv.layoutParams
                lp.width = -1
                lp.height =
                    measuredHeight * maxItemCount + getItemMarginVertical(holder.itemView.layoutParams) * maxItemCount + decorationHeight * maxItemCount
                rv.layoutParams = lp
            }
        }
    }

    private fun getItemMarginVertical(lp: ViewGroup.LayoutParams): Int {
        var verticalMargin = 0
        (lp as? ViewGroup.MarginLayoutParams)?.let {
            verticalMargin += it.topMargin
            verticalMargin += it.bottomMargin
        }
        return verticalMargin
    }
}
package com.example.myapp.base.widget.table

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.myapp.base.widget.R
import kotlin.math.max

class ContentCellAdapter(
    private val tableAdapter: TableAdapter,
    private val scrollHelper: ScrollHelper,
    private val widthMap: HashMap<String, Int>
) :
    BaseQuickAdapter<Any, TableViewHolder>(R.layout.base_table_recycler_item) {

    override fun onItemViewHolderCreated(viewHolder: TableViewHolder, viewType: Int) {
        viewHolder.llFirstColumn = viewHolder.getView(R.id.ll_first_column)
        viewHolder.llRowTitle = viewHolder.getView(R.id.ll_row_title)
        viewHolder.llDataGroup = viewHolder.getView(R.id.ll_data_group)
        viewHolder.thsDataGroup = viewHolder.getView(R.id.ths_data_group)
        viewHolder.cellViews = arrayOfNulls(tableAdapter.contentColumn)

        scrollHelper.addRowScrollView(viewHolder.thsDataGroup)
        viewHolder.llFirstColumn.setOnClickListener(viewHolder.rowClickListener)
        viewHolder.llDataGroup.setOnClickListener(viewHolder.rowClickListener)
    }

    override fun convert(holder: TableViewHolder, item: Any) {
        //更新每行的views的数据
        updateRowViews(holder, getItemPosition(item))
        //更熟的views数据后重新测量高度，取那一行的最大高度作为整行的高度
        val maxHeight = getMaxHeight(holder.cellViews)
        //重新更新view到表格上
        updateUI(holder, maxHeight)
        scrollHelper.rowScrollAlign(holder.thsDataGroup)

        //为了尽可能少的影响ScrollView的触摸事件，所以点击事件这里取个巧，直接设置在这两个Linearlayout上
        holder.rowClickListener.setData(tableAdapter, getItemPosition(item), holder.itemView)
    }

    private fun updateRowViews(holder: TableViewHolder, row: Int) {
        for (i in 0 until tableAdapter.contentColumn) {
            if (!tableAdapter.firstColumnIsMove && i == 0) {
                val titleView = tableAdapter.getTableRowTitleView(row, holder.titleView)
                holder.llRowTitle.removeAllViews()
                holder.llRowTitle.addView(titleView)
                val view = tableAdapter.getTableCellView(
                    row, 0,
                    holder.cellViews[0], holder.llFirstColumn
                )
                holder.cellViews[0] = view
            } else {
                val view = tableAdapter.getTableCellView(
                    row, i,
                    holder.cellViews[i], holder.llDataGroup
                )
                holder.cellViews[i] = view
            }
        }
    }

    private fun getMaxHeight(views: Array<View?>): Int {
        var maxHeight = 0
        for (i in 0 until tableAdapter.contentColumn) {
            //测量模式：宽度以标题行各列的宽度为准，高度为自适应
            val w: Int =
                View.MeasureSpec.makeMeasureSpec(widthMap["$i"] ?: 0, View.MeasureSpec.EXACTLY)
            val h: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            views[i]!!.measure(w, h)
            maxHeight = max(maxHeight, views[i]!!.measuredHeight)
        }
        return maxHeight
    }

    private fun updateUI(holder: TableViewHolder, maxHeight: Int) {
        //其实这里可以优化一下，不用remove掉全部又加一次，以后再优化一下。。。。
        holder.llFirstColumn.removeAllViews()
        holder.llDataGroup.removeAllViews()
        for (i in 0 until tableAdapter.contentColumn) {
            if (!tableAdapter.firstColumnIsMove && i == 0) {
                holder.cellViews[0]?.let {
                    holder.llFirstColumn.addView(it, widthMap["0"] ?: 0, maxHeight)
                }
            } else {
                holder.cellViews[i]?.let {
                    holder.llDataGroup.addView(it, widthMap["$i"] ?: 0, maxHeight)
                }
            }
        }
    }
}
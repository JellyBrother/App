package com.example.myapp.base.widget.table

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface TableAdapter {
    // 第一列是否可移动
    val firstColumnIsMove: Boolean

    // 表格内容的行数，不包括标题行
    val contentRows: Int

    // 列数
    val contentColumn: Int

    // 标题的view，这里从0开始，这里要注意，一定要有view返回去，不能为null，每一行
    // 各列的宽度就等于标题行的列的宽度，且边框的话，自己在这里和下文的表格单元格view里面设置
    fun getTitleView(columnPosition: Int, parent: ViewGroup?): View

    // 表格正文的view，行和列都从0开始，宽度的话在载入的时候，默认会是以标题行各列的宽度，高度的话自适应
    fun getTableCellView(
        contentRow: Int,
        contentColumn: Int,
        view: View?,
        parent: ViewGroup?
    ): View?

    // 每一行的标题
    fun getTableRowTitleView(contentRow: Int, view: View?): View

    fun getFooterView(view: RecyclerView): View?

    fun getItem(contentRow: Int): Any

    // 每一行被点击的时候的回调
    fun onClickContentRowItem(row: Int, convertView: View?)
}
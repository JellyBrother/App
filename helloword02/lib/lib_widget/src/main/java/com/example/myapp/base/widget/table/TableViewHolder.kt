package com.example.myapp.base.widget.table

import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.myapp.base.widget.table.RowClickListener
import com.example.myapp.base.widget.table.TableRowScrollView

class TableViewHolder(view: View) : BaseViewHolder(view) {
    val titleView: View? = null
    lateinit var llFirstColumn: LinearLayout
    lateinit var llRowTitle: LinearLayout
    lateinit var llDataGroup: LinearLayout
    lateinit var thsDataGroup: TableRowScrollView
    lateinit var cellViews: Array<View?>
    val rowClickListener: RowClickListener = RowClickListener()
}

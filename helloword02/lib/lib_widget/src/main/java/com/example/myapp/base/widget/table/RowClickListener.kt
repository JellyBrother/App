package com.example.myapp.base.widget.table

import android.view.View

class RowClickListener : View.OnClickListener {
    private var tableAdapter: TableAdapter? = null
    private var row = 0
    private var convertView: View? = null

    fun setData(tableAdapter: TableAdapter?, row: Int, convertView: View?) {
        this.tableAdapter = tableAdapter
        this.row = row
        this.convertView = convertView
    }

    override fun onClick(v: View) {
        tableAdapter?.onClickContentRowItem(row, convertView)
    }
}
package com.example.myapp.base.widget.table

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.base.widget.R

open class TableView : LinearLayout {
    private val layoutInflater by lazy {
        LayoutInflater.from(context)
    }

    // 是否显示标题行
    private var showTitle = false

    // 表格标题
    private var tableTitleLayout: LinearLayout

    // 表格正文内容+表格行标题
    private val tableFrameLayout: FrameLayout by lazy {
        FrameLayout(context)
    }

    // 表格正文内容
    private var tableRecycler: RecyclerView

    // 表格行标题
    private val tableRowTitleLayout: LinearLayout by lazy {
        LinearLayout(context)
    }

    // 存放标题行中的每一列的宽度，所有的行里的每一列都是基于标题行的每一列的宽度，都跟标题行的每一列的宽度相等
    private val widthMap = HashMap<String, Int>()

    private val scrollHelper: ScrollHelper by lazy {
        ScrollHelper()
    }

    // 头部高度
    var headerHeight: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        //默认显示标题行
        showTitle = true
        orientation = VERTICAL
        tableTitleLayout =
            layoutInflater.inflate(R.layout.base_table_title, this, false) as LinearLayout

        tableRecycler = RecyclerView(context)
        tableRecycler.layoutManager = SmoothScrollLayoutManager(context)
    }

    //设置adapter
    fun setAdapter(tableAdapter: TableAdapter) {
        //清除各原有数据
        recycle()
        //载入标题行
        initTitles(tableAdapter)
        //载入表格正文
        initContentList(tableAdapter)
        if (!showTitle) {
            //假如设置了不显示标题行，在这里隐藏掉
            getChildAt(0).visibility = View.GONE
        }
    }

    fun recycle() {
        tableFrameLayout.removeAllViews()
        removeAllViews()
        widthMap.clear()
        scrollHelper.recycle()
        postDelayed({
            scrollHelper.getCurrentTouchView()?.scrollTo(0, 0)
            scrollHelper.allRowScrollTo(0, 0)
        }, 50)
    }

    private fun initTitles(tableAdapter: TableAdapter) {
        val llFirstColumn = tableTitleLayout.findViewById<LinearLayout>(R.id.ll_first_column)
        var i = 0
        if (tableAdapter.firstColumnIsMove) {
            //假如设置是可移动的，则把不可移动的部分llFirstColumn隐藏掉，把所有数据都加在HListViewScrollView中
            llFirstColumn.visibility = GONE
        } else {
            //假如是设置了第一列不可移动的，则把第一列的数据加到llFirstColumn中，其余的都加到HListViewScrollView中
            llFirstColumn.removeAllViews()
            val view: View = tableAdapter.getTitleView(0, llFirstColumn)
            //测量view的高度，都采用自适应的模式测量
            view.measure(0, 0)
            val firstCellWidth = view.tag as? Int ?: view.measuredWidth
            llFirstColumn.addView(view, firstCellWidth, LayoutParams.WRAP_CONTENT)
            //存起来以便设置表格正文的时候进行宽度设置
            widthMap["0"] = firstCellWidth
            //之后的titleView就都放在CHListViewScrollView中，因为0 title已经设置了，所以从1开始
            i = 1
        }

        val thsDataGroup = tableTitleLayout.findViewById<TableRowScrollView>(R.id.ths_data_group)
        //把CHListViewScrollView加入管理
        scrollHelper.addRowScrollView(thsDataGroup)

        val llDataGroup = tableTitleLayout.findViewById<LinearLayout>(R.id.ll_data_group)

        llDataGroup.removeAllViews()
        while (i < tableAdapter.contentColumn) {
            val view: View = tableAdapter.getTitleView(i, llDataGroup)
            view.measure(0, 0)
            val otherCellWidth = view.tag as? Int ?: view.measuredWidth
            llDataGroup.addView(view, otherCellWidth, LayoutParams.WRAP_CONTENT)
            widthMap[i.toString() + ""] = otherCellWidth
            i++
        }
        llDataGroup.measure(0, 0)
        headerHeight = llDataGroup.measuredHeight
        addView(tableTitleLayout)
    }

    private fun initContentList(tableAdapter: TableAdapter) {
        val adapter = ContentCellAdapter(tableAdapter, scrollHelper, widthMap)
        tableAdapter.getFooterView(tableRecycler)?.let {
            adapter.addFooterView(it)
        }
        tableRecycler.adapter = adapter
        adapter.setList(getTableList(tableAdapter))

        tableFrameLayout.addView(
            tableRecycler,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        tableFrameLayout.addView(
            tableRowTitleLayout,
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )

        addView(tableFrameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private fun getTableList(tableAdapter: TableAdapter): MutableList<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until tableAdapter.contentRows) {
            list.add(i)
        }
        return list
    }


    fun setSelection(position: Int) {
//        var titleHeight = titleHeight
//        (tableRecycler.findViewHolderForAdapterPosition(position) as? TableViewHolder)?.let { holder ->
//            if (holder.llRowTitle.childCount > 0 && holder.llRowTitle.getChildAt(0).visibility == View.VISIBLE
//            ) {
//                titleHeight = 0
//            }
//        }
        val smoothScroller = TopSmoothScroller(context)
        smoothScroller.targetPosition = position
        tableRecycler.layoutManager?.startSmoothScroll(smoothScroller)
        scrollHelper.scrollCurrentRow()
    }

    //设置是否显示标题
    fun setShowTitle(showTitle: Boolean) {
        this.showTitle = showTitle
    }

    fun getTitleLayout(): LinearLayout {
        return tableRowTitleLayout
    }

    fun addTitleLayout(view: View?) {
        tableRowTitleLayout.removeAllViews()
        tableRowTitleLayout.addView(
            view,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    fun setCurrentTouchView(currentTouchView: TableRowScrollView?) {
        scrollHelper.setCurrentTouchView(currentTouchView)
    }

    fun getFirstHListViewScrollView(): TableRowScrollView {
        return scrollHelper.firstRowScrollView
    }

    fun getContentRecyclerView(): RecyclerView {
        return tableRecycler
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        postDelayed({
            scrollHelper.getCurrentTouchView()?.scrollTo(0, 0)
            scrollHelper.allRowScrollTo(0, 0)
        }, 50)
    }
}
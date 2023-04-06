package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import com.example.myapp.base.widget.R
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class PageRefreshLayout : SmartRefreshLayout {
    private var canRefresh = true
    private var canLoadMore = false
    private var pageNum = 1
    private var pageSize = DEFAULT_PAGE_SIZE
    private var mOnRequestBlock: ((pageNum: Int, pageSize: Int) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    init {
        ClassicsFooter.REFRESH_FOOTER_PULLING = "点击或上拉加载更多"
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载"
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新..."
        ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载..."
        ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成"
        ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败"
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了"
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageRefreshLayout)

        canRefresh = typedArray.getBoolean(R.styleable.PageRefreshLayout_canRefresh, true)
        canLoadMore =
            typedArray.getBoolean(R.styleable.PageRefreshLayout_canLoadMore, false)
        pageSize = typedArray.getInteger(R.styleable.PageRefreshLayout_pageSize, DEFAULT_PAGE_SIZE)

        initView()

        typedArray.recycle()
    }

    private fun initView() {
        setEnableRefresh(canRefresh)
        setEnableLoadMore(canLoadMore)
        if (canRefresh) {
            setRefreshHeader(
                ClassicsHeader(context)
            )
            setOnRefreshListener {
                pageNum = 1
                mOnRequestBlock?.invoke(pageNum, pageSize)
            }
            setDisableContentWhenRefresh(false) //是否在刷新的时候禁止列表的操作
        }
        if (canLoadMore) {
            setRefreshFooter(
                ClassicsFooter(context)
                    .setTextSizeTitle(TypedValue.COMPLEX_UNIT_SP, 12f)
                    .setFinishDuration(500) //设置刷新完成显示的停留时间
            )
            setOnLoadMoreListener {
                pageNum++
                mOnRequestBlock?.invoke(pageNum, pageSize)
            }
            setDragRate(0.5f) //显示下拉高度/手指真实下拉高度=阻尼效果
            setReboundDuration(300) //回弹动画时长（毫秒）
            setEnableOverScrollBounce(true) //是否启用越界回弹
            setEnableFooterFollowWhenNoMoreData(true) //是否在全部加载结束之后Footer跟随内容
            setEnableScrollContentWhenRefreshed(true) //是否在刷新完成时滚动列表显示新的内容
            setDisableContentWhenLoading(false) //是否在加载的时候禁止列表的操作
        }
    }

    fun startRefreshWithoutAnimation() {
        resetPageNum()
        mOnRequestBlock?.invoke(pageNum, pageSize)
    }

    fun isPullRefresh(): Boolean {
        return pageNum == 1
    }

    fun loadMoreFail() {
        pageNum--
    }

    fun resetPageNum() {
        pageNum = 1
    }

    fun setOnRequestBlock(onRequestBlock: (pageNum: Int, pageSize: Int) -> Unit) {
        this.mOnRequestBlock = onRequestBlock
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 15
    }
}
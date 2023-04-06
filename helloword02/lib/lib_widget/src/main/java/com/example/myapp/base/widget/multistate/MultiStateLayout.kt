package com.example.myapp.base.widget.multistate

import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes

class MultiStateLayout internal constructor() {

    companion object {
        internal var instance = MultiStateLayout()

        @JvmStatic
        fun init() = MultiStateLayout().apply { instance = this }
    }

    private val stateViewList = arrayListOf<StateInfo>()

    internal val emptyInfo: StateInfo
        get() = getStateView(LayoutStateType.STATE_EMPTY)

    internal val loadingInfo: StateInfo
        get() = getStateView(LayoutStateType.STATE_LOADING)

    internal val errorInfo: StateInfo
        get() = getStateView(LayoutStateType.STATE_ERROR)

    internal val noNetworkInfo: StateInfo
        get() = getStateView(LayoutStateType.STATE_NO_NETWORK)

    /**
     * 设置加载中视图
     * @param layoutId:布局Id
     * @param hintTextId:用于显示提示文字的控件Id
     * @param hintText:提示文字
     */
    @JvmOverloads
    fun setLoadingView(
        @LayoutRes layoutId: Int,
        @IdRes hintTextId: Int = NULL_RESOURCE_ID,
        hintText: String? = null
    ) = addStateViewToList(LayoutStateType.STATE_LOADING, layoutId, hintTextId, hintText)

    /**
     * 设置空数据视图
     * @param layoutId:布局Id
     * @param hintTextId:用于显示提示文字的控件Id
     * @param hintText:提示文字
     * @param clickViewIds:需要设置点击事件的控件Id
     */
    @JvmOverloads
    fun setEmptyView(
        @LayoutRes layoutId: Int,
        @IdRes hintTextId: Int = NULL_RESOURCE_ID,
        hintText: String? = null,
        @IdRes vararg clickViewIds: Int
    ) = addStateViewToList(
        LayoutStateType.STATE_EMPTY,
        layoutId,
        hintTextId,
        hintText,
        *clickViewIds
    )

    /**
     * 设置错误视图
     * @param layoutId:布局Id
     * @param hintTextId:用于显示提示文字的控件Id
     * @param hintText:提示文字
     * @param clickViewIds:需要设置点击事件的控件Id
     */
    @JvmOverloads
    fun setErrorView(
        @LayoutRes layoutId: Int,
        @IdRes hintTextId: Int = NULL_RESOURCE_ID,
        hintText: String? = null,
        @IdRes vararg clickViewIds: Int
    ) = addStateViewToList(
        LayoutStateType.STATE_ERROR,
        layoutId,
        hintTextId,
        hintText,
        *clickViewIds
    )

    /**
     * 设置网络断开视图
     * @param layoutId:布局Id
     * @param hintTextId:用于显示提示文字的控件Id
     * @param hintText:提示文字
     * @param clickViewIds:需要设置点击事件的控件Id
     */
    @JvmOverloads
    fun setNoNetworkView(
        @LayoutRes layoutId: Int,
        @IdRes hintTextId: Int = NULL_RESOURCE_ID,
        hintText: String? = null,
        @IdRes vararg clickViewIds: Int
    ) = addStateViewToList(
        LayoutStateType.STATE_NO_NETWORK,
        layoutId,
        hintTextId,
        hintText,
        *clickViewIds
    )

    /**
     * 添加自定义视图
     * @param state:视图状态值，取值时
     * @param layoutId:布局Id
     * @param clickViewIds:需要设置点击事件的控件Id
     */
    fun addStateView(
        @IntRange(from = 5L) state: Int,
        @LayoutRes layoutId: Int,
        @IdRes vararg clickViewIds: Int
    ) =
        addStateViewToList(state, layoutId, clickViewIds = *clickViewIds)

    internal fun getStateView(status: Int) = stateViewList.firstOrNull { it.state == status }
        ?: StateInfo(status)

    private fun addStateViewToList(
        @LayoutStateType status: Int,
        @LayoutRes layoutId: Int,
        @IdRes hintId: Int = NULL_RESOURCE_ID,
        hintText: String? = null,
        @IdRes vararg clickViewIds: Int
    ) =
        this.apply {
            val index = stateViewList.indexOfFirst { it.state == status }
            val hasView = index != -1
            if (hasView) {
                stateViewList[index] = StateInfo(
                    status,
                    layoutId,
                    hintId,
                    hintText,
                    clickViewIds.toList()
                )
            } else {
                stateViewList.add(
                    StateInfo(
                        status,
                        layoutId,
                        hintId,
                        hintText,
                        clickViewIds.toList()
                    )
                )
            }
        }
}
package com.example.myapp.base.widget.controller.viewmodel

import com.example.myapp.base.net.data.JaPageReq
import com.example.myapp.base.net.data.PageInfo
import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.data.remote.doFailure
import com.example.myapp.base.net.data.remote.doSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * 列表基类
 */
open class ListViewModel : LoadingViewModel() {
    // 下拉刷新状态
    private val _listStatus: Channel<Int> = Channel()
    val listStatus = _listStatus.receiveAsFlow()

    fun <T> Flow<T>.onListLoadingStart() =
        onStart {
            setLoading(true)
            setListStatus(STATUS_START_REFRESH)
        }

    fun <T> Flow<T>.onListLoadingCompletion() =
        onCompletion {
            setLoading(false)
            setListStatus(STATUS_FINISH_REFRESH)
        }

    suspend fun setListStatus(status: Int) = _listStatus.send(status)

    suspend inline fun <reified T> ApiResult<PageInfo<T>>.doPageSuccess(successCallback: (list: PageInfo<T>) -> Unit) {
        this.doSuccess { pageInfo ->
            if (isNoMore(pageInfo)) {
                // 没有更多数据
                setListStatus(STATUS_NO_MORE)
            } else {
                if (pageInfo.isFirstPage) {
                    // 下拉刷新结束
                    setListStatus(STATUS_FINISH_REFRESH)
                } else {
                    // 上拉加载结束
                    setListStatus(STATUS_FINISH_LOAD_MORE)
                }
            }
            successCallback.invoke(pageInfo)
        }
    }

    suspend inline fun <reified T> ApiResult<PageInfo<T>>.doPageFail() {
        this.doFailure { throwable ->
            setListStatus(STATUS_FINISH_FAIL)
            setFail(throwable)
        }
    }

    suspend inline fun <reified T> ApiResult<T>.doListSuccess(successCallback: (list: T) -> Unit) {
        this.doSuccess { list ->
            setListStatus(STATUS_FINISH_REFRESH)
            successCallback.invoke(list)
        }
    }

    suspend inline fun <reified T> ApiResult<T>.doListFail() {
        this.doFailure { throwable ->
            setListStatus(STATUS_FINISH_FAIL)
            setFail(throwable)
        }
    }

    inline fun <reified T : JaPageReq, reified R> Flow<T>.pageListTransform(crossinline requestBlock: suspend (t: T) -> Flow<ApiResult<PageInfo<R>>>): Flow<PageInfo<R>> {
        return this
            .flatMapLatest {
                requestBlock.invoke(it)
                    .onStart {
                        if (it.isRefresh()) {
                            setListStatus(STATUS_START_REFRESH)
                        } else {
                            setListStatus(STATUS_START_LOAD_MORE)
                        }
                    }
            }
            .transform { result ->
                result.doPageFail()
                result.doPageSuccess { emit(it) }
            }
    }

    inline fun <reified T, reified R> Flow<T>.listTransform(crossinline requestBlock: suspend (t: T) -> Flow<ApiResult<R>>): Flow<R> {
        return this
            .flatMapLatest {
                requestBlock.invoke(it)
                    .onStart {
                        setLoading(true)
                        setListStatus(STATUS_START_REFRESH)
                    }
                    .onCompletion {
                        setLoading(false)
                    }
            }
            .transform { result ->
                result.doListFail()
                result.doListSuccess { emit(it) }
            }
    }

    companion object {
        // 开始下拉刷新
        const val STATUS_START_REFRESH = 10

        // 开始上拉加载
        const val STATUS_START_LOAD_MORE = 11

        // 没有更多数据
        const val STATUS_NO_MORE = 12

        // 下拉刷新结束
        const val STATUS_FINISH_REFRESH = 13

        // 上拉加载结束
        const val STATUS_FINISH_LOAD_MORE = 14

        // 上拉加载结束
        const val STATUS_FINISH_FAIL = 15

        fun isNoMore(pageInfo: PageInfo<*>): Boolean {
            return !pageInfo.hasNextPage
        }
    }
}
package com.example.myapp.base.widget.controller.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.data.remote.doFailure
import com.example.myapp.base.widget.status.StatusType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * 加载数据基类
 */
open class StatusViewModel : ViewModel() {
    /**
     * 一次性事件 且 一对一的订阅关系
     * 例如：弹Toast、导航Fragment等
     */
    private val _status: Channel<StatusType> = Channel()
    val mStatus = _status.receiveAsFlow()

    val _fail: Channel<Throwable?> = Channel()
    val mFail = _fail.receiveAsFlow()

    fun <T> Flow<T>.onLoadingStart(action: StartAction<T>? = null) =
        action?.let {
            onStart(it)
        } ?: onStart {
            _status.send(StatusType.STATUS_LOADING)
        }

    fun <T> Flow<T>.onLoadingCompletion(action: CompletionAction<T>? = null) =
        action?.let {
            onCompletion(it)
        } ?: onCompletion {
            _status.send(StatusType.STATUS_GONE)
        }

    fun <T> Flow<T>.loadingCatch(action: CatchAction<T>? = null) =
        action?.let {
            catch(it)
        } ?: catch {
            _status.send(StatusType.STATUS_GONE)
        }

    suspend inline fun <reified T> ApiResult<T>.onFailure() {
        this.doFailure { throwable ->
            _fail.send(throwable)
        }
    }
}
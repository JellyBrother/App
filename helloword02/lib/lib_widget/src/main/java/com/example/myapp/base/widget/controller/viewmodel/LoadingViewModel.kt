package com.example.myapp.base.widget.controller.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.base.net.data.error.NoNetException
import com.example.myapp.base.net.data.remote.ApiResult
import com.example.myapp.base.net.data.remote.doFailure
import com.example.myapp.base.widget.multistate.LayoutStateType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

typealias StartAction<T> = suspend FlowCollector<T>.() -> Unit
typealias CompletionAction<T> = suspend FlowCollector<T>.(cause: Throwable?) -> Unit
typealias CatchAction<T> = suspend FlowCollector<T>.(cause: Throwable) -> Unit

open class LoadingViewModel : ViewModel() {
    /**
     * 一次性事件 且 一对一的订阅关系
     * 例如：弹Toast、导航Fragment等
     */
    private val _loading: Channel<Boolean?> = Channel()
    val mLoading = _loading.receiveAsFlow().filterNotNull()

    private val _fail: Channel<Throwable?> = Channel()
    val mFail = _fail.receiveAsFlow().filterNotNull()

    private val _layoutState: Channel<Int?> = Channel()
    val mLayoutState = _layoutState.receiveAsFlow().filterNotNull()

    fun <T> Flow<T>.onLoadingStart(action: StartAction<T>? = null) =
        action?.let {
            onStart(it)
        } ?: onStart {
            setLoading(true)
        }

    fun <T> Flow<T>.onLoadingCompletion(action: CompletionAction<T>? = null) =
        action?.let {
            onCompletion(it)
        } ?: onCompletion {
            setLoading(false)
        }

    fun <T> Flow<T>.loadingCatch(action: CatchAction<T>? = null) =
        action?.let {
            catch(it)
        } ?: catch {
            setLoading(false)
        }

    suspend inline fun <reified T> ApiResult<T>.onFailure(showFail: Boolean = true) {
        this.doFailure { throwable ->
            if (throwable is NoNetException) {
                setLayoutState(LayoutStateType.STATE_NO_NETWORK)
            } else {
                setLayoutState(LayoutStateType.STATE_ERROR)
            }
            if (showFail) setFail(throwable)
        }
    }

    suspend fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _loading.send(isLoading)
    }

    suspend fun setFail(throwable: Throwable?) = viewModelScope.launch {
        _fail.send(throwable)
    }

    suspend fun setLayoutState(@LayoutStateType stateType: Int) = viewModelScope.launch {
        _layoutState.send(stateType)
    }
}
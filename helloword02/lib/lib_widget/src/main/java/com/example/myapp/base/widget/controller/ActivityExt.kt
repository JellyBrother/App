package com.example.myapp.base.widget.controller

import android.app.Activity
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapp.base.ui.activity.BaseActivity
import com.example.myapp.base.ui.fragment.BaseFragment
import com.example.myapp.base.widget.controller.viewmodel.LoadingViewModel
import com.example.myapp.base.widget.helper.ToastHelper
import com.example.myapp.base.widget.multistate.FrameStateLayout
import com.example.myapp.base.widget.multistate.LayoutStateType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun LoadingViewModel.observerLoading(activity: BaseActivity) {
    mLoading.collectIn(activity) {
        if (it) {
        } else {
        }
    }
    mFail.collectIn(activity) {
        ToastHelper.showShort(it.message ?: "网络异常，请稍后重试")
    }
}

fun LoadingViewModel.observerLoading(fragment: BaseFragment) {
    val activity = fragment.activity as? BaseActivity ?: return
    mLoading.collectIn(fragment.viewLifecycleOwner) {
        if (it) {
        } else {
        }
    }
    mFail.collectIn(fragment.viewLifecycleOwner) {
        ToastHelper.showShort(it.message ?: "网络异常，请稍后重试")
    }
}

fun LoadingViewModel.observerLoading(
    fragment: BaseFragment,
    errorAction: ((throwable: Throwable?) -> Unit)? = null
) {
    mLoading.collectIn(fragment.viewLifecycleOwner) {
        if (it) {
        } else {
        }
    }
    mFail.collectIn(fragment.viewLifecycleOwner) {
        errorAction?.invoke(it)
        ToastHelper.showShort(it.message ?: "网络异常，请稍后重试")
    }
}

fun LoadingViewModel.observerLayoutState(
    stateLayout: FrameStateLayout,
    lifecycleOwner: LifecycleOwner,
) {
    mLayoutState.collectIn(lifecycleOwner) {
        when (it) {
            LayoutStateType.STATE_LOADING -> stateLayout.showLoading()
            LayoutStateType.STATE_CONTENT -> stateLayout.showContent()
            LayoutStateType.STATE_EMPTY -> stateLayout.showEmpty()
            LayoutStateType.STATE_ERROR -> stateLayout.showError()
            LayoutStateType.STATE_NO_NETWORK -> stateLayout.showNoNetwork()
            else -> {

            }
        }
    }
}

fun <T> Flow<T>.flowWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED
): Flow<T> = callbackFlow {
    lifecycle.repeatOnLifecycle(minActiveState) {
        this@flowWithLifecycle.collect {
            send(it)
        }
    }
    close()
}

fun <T> Flow<T>.collectIn(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    action: (T) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
}

fun Activity.getRootView(): ViewGroup? {
    return window.findViewById(android.R.id.content)
}

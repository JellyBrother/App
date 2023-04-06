package com.example.myapp.base.widget.controller

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.myapp.base.ui.activity.BaseActivity
import com.example.myapp.base.ui.fragment.BaseFragment
import com.example.myapp.base.widget.controller.viewmodel.ListViewModel
import com.example.myapp.base.widget.controller.viewmodel.StatusViewModel
import com.example.myapp.base.widget.helper.ToastHelper
import com.example.myapp.base.widget.recyclerview.PageRefreshLayout
import com.example.myapp.base.widget.status.StatusView
import com.scwang.smart.refresh.layout.api.RefreshLayout

inline fun <reified vm : StatusViewModel> BaseFragment.observerStatus(
    statusView: StatusView,
    viewModel: vm
) {
    lifecycleScope.launchWhenStarted {
        viewModel.mStatus.collect { statusType ->
            statusView.status = statusType
        }
        viewModel.mFail.collect {
            ToastHelper.showShort(it?.message ?: "网络异常，请稍后重试")
        }
    }
}

fun ListViewModel.observerListStatus(
    mSmartRefreshLayout: RefreshLayout,
    fragment: BaseFragment
) {
    listStatus.collectIn(fragment) { status ->
        when (status) {
            ListViewModel.STATUS_NO_MORE -> {
                mSmartRefreshLayout.finishLoadMore()
                mSmartRefreshLayout.finishRefresh()
                mSmartRefreshLayout.setEnableLoadMore(false)
            }
            ListViewModel.STATUS_FINISH_REFRESH, ListViewModel.STATUS_FINISH_LOAD_MORE, ListViewModel.STATUS_FINISH_FAIL -> {
                mSmartRefreshLayout.finishRefresh()
                mSmartRefreshLayout.finishLoadMore()
            }
        }
    }
}

fun ListViewModel.observerListStatus(
    mSmartRefreshLayout: RefreshLayout,
    activity: BaseActivity
) {
    listStatus.collectIn(activity) { status ->
        when (status) {
            ListViewModel.STATUS_NO_MORE -> {
                mSmartRefreshLayout.finishLoadMore()
                mSmartRefreshLayout.finishRefresh()
                mSmartRefreshLayout.setEnableLoadMore(false)
            }
            ListViewModel.STATUS_FINISH_REFRESH, ListViewModel.STATUS_FINISH_LOAD_MORE, ListViewModel.STATUS_FINISH_FAIL -> {
                mSmartRefreshLayout.finishRefresh()
                mSmartRefreshLayout.finishLoadMore()
                mSmartRefreshLayout.setEnableLoadMore(true)
            }
        }
    }
}

fun ListViewModel.observerPageListStatus(
    refreshLayout: PageRefreshLayout,
    lifecycleOwner: LifecycleOwner,
) {
    listStatus.collectIn(lifecycleOwner) { status ->
        when (status) {
            ListViewModel.STATUS_START_REFRESH -> {}
            ListViewModel.STATUS_START_LOAD_MORE -> {}
            ListViewModel.STATUS_FINISH_FAIL -> {
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()
                if (!refreshLayout.isPullRefresh()) {
                    refreshLayout.loadMoreFail()
                }
            }
            ListViewModel.STATUS_FINISH_REFRESH -> {
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()
                refreshLayout.setEnableLoadMore(true)
            }
            ListViewModel.STATUS_FINISH_LOAD_MORE -> {
                refreshLayout.finishLoadMore()
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            ListViewModel.STATUS_NO_MORE -> {
                refreshLayout.finishLoadMore()
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(false)
            }
        }
    }
}

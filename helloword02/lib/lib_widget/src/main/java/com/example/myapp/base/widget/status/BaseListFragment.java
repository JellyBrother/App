package com.example.myapp.base.widget.status;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.example.myapp.base.utils.NetworkUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

public abstract class BaseListFragment<VM extends BaseListViewModel> extends BaseStatusFragment<VM> {
    protected SmartRefreshLayout mSmartRefreshLayout;
    protected boolean needLoadMore = false;

    public BaseListFragment() {
    }

    @Override
    protected void initListener() {
        super.initListener();
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    getViewModel().getSmartRefreshLayoutLiveData().setValue(BaseListViewModel.STATUS_REFRESH);
                    startRefresh();
                }
            });
        }
        getViewModel().getSmartRefreshLayoutLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (mSmartRefreshLayout == null) {
                    return;
                }
                if (status == BaseListViewModel.STATUS_FINISH_REFRESH) {
                    mSmartRefreshLayout.finishRefresh();
                    mSmartRefreshLayout.finishLoadMore();
                    return;
                }
                if (status == BaseListViewModel.STATUS_FINISH_LOADMORE) {
                    mSmartRefreshLayout.finishLoadMore();
                    mSmartRefreshLayout.finishRefresh();
                    return;
                }
                if (status == BaseListViewModel.STATUS_NOMORE) {
                    mSmartRefreshLayout.finishLoadMore();
                    mSmartRefreshLayout.finishRefresh();
                    mSmartRefreshLayout.setEnableLoadMore(false);
                    return;
                }
            }
        });
    }

    /**
     * 设置上拉加载监听
     */
    protected void setOnLoadMoreListener() {
        if (mSmartRefreshLayout != null) {
            needLoadMore = true;
            mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    getViewModel().getSmartRefreshLayoutLiveData().setValue(BaseListViewModel.STATUS_LOADMORE);
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 开始下拉刷新
     */
    protected void startRefresh() {
        mSmartRefreshLayout.setEnableLoadMore(needLoadMore);
        getViewModel().setPageIndex(1);
        if (NetworkUtils.isConnected()) {
            getViewModel().startRequest();
            return;
        }
        startRequestNoNet();
    }

    /**
     * 开始上拉加载
     */
    protected void startLoadMore() {
        if (NetworkUtils.isConnected()) {
            startLoadMoreWithNet();
            return;
        }
        getViewModel().getSmartRefreshLayoutLiveData().setValue(BaseListViewModel.STATUS_FINISH_REFRESH);
    }

    protected void startLoadMoreWithNet() {
    }

    @Override
    public void startRequest() {
        getViewModel().setPageIndex(1);
        super.startRequest();
    }

    @Override
    protected void startRequestNoNet() {
        super.startRequestNoNet();
        getViewModel().getSmartRefreshLayoutLiveData().setValue(BaseListViewModel.STATUS_FINISH_REFRESH);
    }
}
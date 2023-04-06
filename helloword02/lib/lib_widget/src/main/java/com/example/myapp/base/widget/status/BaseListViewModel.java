package com.example.myapp.base.widget.status;

import androidx.lifecycle.MutableLiveData;

import com.example.myapp.base.utils.ListUtil;
import com.example.myapp.base.viewmodel.BaseModel;

import java.util.List;

/**
 * 列表基类
 */
public class BaseListViewModel<M extends BaseModel> extends BaseStatusViewModel<M> {
    // 开始下拉刷新
    public static final int STATUS_REFRESH = 10;
    // 开始上拉加载
    public static final int STATUS_LOADMORE = 11;
    // 没有更多数据
    public static final int STATUS_NOMORE = 12;
    // 下拉刷新结束
    public static final int STATUS_FINISH_REFRESH = 13;
    // 上拉加载结束
    public static final int STATUS_FINISH_LOADMORE = 14;
    protected int pageIndex = 1;
    protected int pageSize = 20;
    // 下拉刷新状态
    private MutableLiveData<Integer> mSmartRefreshLayoutLiveData;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public MutableLiveData<Integer> getSmartRefreshLayoutLiveData() {
        if (mSmartRefreshLayoutLiveData == null) {
            mSmartRefreshLayoutLiveData = new MutableLiveData<>();
        }
        return mSmartRefreshLayoutLiveData;
    }

    @Override
    public void startRequest() {
        super.startRequest();
    }

    public void setFinishLoadmoreStatus(List list) {
        if (!ListUtil.isEmpty(list) && list.size() < pageSize) {
            getSmartRefreshLayoutLiveData().postValue(STATUS_NOMORE);
        } else {
            getSmartRefreshLayoutLiveData().postValue(STATUS_FINISH_LOADMORE);
        }
    }

    public void onRefreshFail() {
        onStartRequestFail();
        getSmartRefreshLayoutLiveData().postValue(STATUS_FINISH_REFRESH);
    }

    public void onRefreshEmpty() {
        onStartRequestEmpty();
        getSmartRefreshLayoutLiveData().postValue(STATUS_FINISH_REFRESH);
    }

    public void onRefreshSuccess() {
        getStatusTypeLiveData().postValue(StatusType.STATUS_GONE);
        getSmartRefreshLayoutLiveData().postValue(STATUS_FINISH_REFRESH);
    }

    public void onLoadMoreFail() {
        pageIndex--;
        getSmartRefreshLayoutLiveData().postValue(STATUS_FINISH_LOADMORE);
    }

    public boolean isNoMore(List list) {
        return ListUtil.isEmpty(list) || list.size() < pageSize;
    }

    public void onLoadNoMore() {
        pageIndex--;
        getSmartRefreshLayoutLiveData().postValue(STATUS_NOMORE);
    }
}
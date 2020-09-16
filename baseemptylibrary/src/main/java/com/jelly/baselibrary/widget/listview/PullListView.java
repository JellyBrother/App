package com.jelly.baselibrary.widget.listview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.ListView;

import com.jelly.baselibrary.utils.LogUtil;

/**
 * Description：带上拉加载和下拉刷新的ListView
 */
public class PullListView extends ListView {
    private static final String TAG = "ChatbotPullListView";
    // 上拉加载:查看更多显示-刚开始不显示
    public static final int LOAD_MORE_STATE_INIT = 0;
    // 上拉加载:查看更多显示-更多搜索结果
    public static final int LOAD_MORE_STATE_MORE = 1;
    // 上拉加载:查看更多显示-没有更多搜索结果
    public static final int LOAD_MORE_STATE_NO_MORE = 2;
    // 上拉加载:查看更多显示-数据加载中...
    public static final int LOAD_MORE_STATE_LOADING = 3;
    // 下拉刷新:初始化
    public static final int REFRESH_STATE_INIT = 4;
    // 下拉刷新:数据加载中...
    public static final int REFRESH_STATE_LOADING = 5;
    // 下拉刷新:没有搜索结果
    public static final int REFRESH_STATE_NO_DATA = 6;
    // 下拉刷新:有数据加载完成
    public static final int REFRESH_STATE_COMPLETE = 7;
    // 上下文
    private Context mContext;
    // Resources
    private Resources mResources;
    // 滑动判断
    private int mTouchSlop;
    // 当前上拉加载状态
    private int mPullUpToLoadMoreState;
    // 当前上拉加载状态
    private int mPullDownToRefreshState;
    // 垂直方向滑动距离
    private float mInitialMotionY;
    // 滑动上拉加载监听
    private OnPullUpToLoadMoreListener mOnPullUpToLoadMoreListener;
    // 滑动下拉刷新监听
    private OnPullDownToRefreshListener mOnPullDownToRefreshListener;

    public interface OnPullUpToLoadMoreListener {
        public void onPullUpToLoadMore();
    }

    public interface OnPullDownToRefreshListener {
        public void onPullDownToRefresh();
    }

    public PullListView(Context context) {
        super(context);
        init(context);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mResources = context.getResources();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float yDiff = event.getY() - mInitialMotionY;
                if (mPullDownToRefreshState == REFRESH_STATE_COMPLETE && mOnPullDownToRefreshListener != null && yDiff > mTouchSlop && isFirstItemVisible()) {
                    // 需要下拉刷新、滑动到顶部、有监听
                    LogUtil.getInstance().d(TAG, "onPullDownToRefresh");
                    mOnPullDownToRefreshListener.onPullDownToRefresh();
                }
                if (mPullUpToLoadMoreState == LOAD_MORE_STATE_MORE && mOnPullUpToLoadMoreListener != null && yDiff < -mTouchSlop && isLastItemVisible()) {
                    // 需要上拉加载、滑动到最后、有监听
                    LogUtil.getInstance().d(TAG, "onPullUpToLoadMore");
                    mOnPullUpToLoadMoreListener.onPullUpToLoadMore();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isLastItemVisible() {
        // 判断listView中最后一个item是否完全显示出来
        Adapter adapter = getAdapter();
        if (null == adapter || adapter.isEmpty()) {
            return true;
        }
        int lastItemPosition = adapter.getCount() - 1;
        int lastVisiblePosition = getLastVisiblePosition();
        if (lastVisiblePosition >= lastItemPosition - 1) {
            int childIndex = lastVisiblePosition - getFirstVisiblePosition();
            int childCount = getChildCount();
            int index = Math.min(childIndex, childCount - 1);
            View lastVisibleChild = getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= getBottom();
            }
        }
        return false;
    }

    private boolean isFirstItemVisible() {
        // 判断listView中第一个item是否完全显示出来
        View firstChild = getChildAt(0);
        if (firstChild == null) {
            return true;
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        if (firstVisiblePosition == 0 && firstChild.getTop() <= getTop()) {
            return true;
        }
        return false;
    }

    /**
     * Description：设置上拉加载监听
     *
     * @param onPullUpToLoadMoreListener 上拉加载监听
     */
    public void setOnPullUpToLoadMoreListener(OnPullUpToLoadMoreListener onPullUpToLoadMoreListener) {
        this.mOnPullUpToLoadMoreListener = onPullUpToLoadMoreListener;
    }

    /**
     * Description：设置下拉刷新监听
     *
     * @param onPullDownToRefreshListener 下拉刷新监听
     */
    public void setOnPullDownToRefreshListener(OnPullDownToRefreshListener onPullDownToRefreshListener) {
        this.mOnPullDownToRefreshListener = onPullDownToRefreshListener;
    }

    /**
     * Description：设置当前上拉加载的滑动状态
     *
     * @param state 滑动状态
     */
    public void setPullUpToLoadMoreState(int state) {
        mPullUpToLoadMoreState = state;
    }

    /**
     * Description：设置当前下拉刷新的滑动状态
     *
     * @param state 滑动状态
     */
    public void setPullDownToRefreshState(int state) {
        this.mPullDownToRefreshState = state;
    }
}

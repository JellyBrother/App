package com.jelly.baselibrary.widget.listview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jelly.baselibrary.R;
import com.jelly.baselibrary.utils.FontScaleBigUtil;

/**
 * Author：liuguodong 72071152
 * Date：2019.11.23 11:33
 * Description：上拉加载的FooterView
 */
public class PullListFooterView extends LinearLayout {
    private static final String TAG = "PullListFooterView";
    // 上下文
    private Context mContext;
    // FontScale
    private float mFontScale;
    // Resources
    private Resources mResources;
    // 包容器，因为view的根布局是GONE不掉的
    private LinearLayout mLlContainer;
    // 加载提示图片
//    private ImageView mIvFooter;
    // 加载转圈控件
    private ProgressBar mPbFooter;
    // 加载提示文本
    private TextView mTvFooter;
    // 滑动上拉加载监听
    private PullListView.OnPullUpToLoadMoreListener mOnPullUpToRefreshListener;
    // 当前状态
    private int mState;

    public PullListFooterView(Context context) {
        super(context);
        init(context);
    }

    public PullListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullListFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mResources = context.getResources();
        mFontScale = FontScaleBigUtil.getInstance().getFontScale();
        if (mFontScale != 0) {
        }
        setFooterViewAndState(PullListView.LOAD_MORE_STATE_INIT);
        // 加载更多
        mTvFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPullUpToRefreshListener == null) {
                    return;
                }
                if (mState != PullListView.LOAD_MORE_STATE_MORE) {
                    return;
                }
                // 有更多，可以点击加载
                mOnPullUpToRefreshListener.onPullUpToLoadMore();
            }
        });
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.28 17:35
     * Description：设置上拉加载监听
     *
     * @param onPullUpToRefreshListener 上拉加载监听
     */
    public void setOnPullUpToRefreshListener(PullListView.OnPullUpToLoadMoreListener onPullUpToRefreshListener) {
        this.mOnPullUpToRefreshListener = onPullUpToRefreshListener;
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.28 17:58
     * Description：设置当前滑动状态
     *
     * @param state 滑动状态
     */
    public void setFooterViewAndState(int state) {
        mState = state;
        switch (state) {
            case PullListView.LOAD_MORE_STATE_INIT:// 初始化搜索
                mLlContainer.setVisibility(GONE);
                break;
            case PullListView.LOAD_MORE_STATE_MORE:// 更多搜索结果
                mLlContainer.setVisibility(VISIBLE);
                mPbFooter.setVisibility(GONE);
                break;
            case PullListView.LOAD_MORE_STATE_LOADING:// 数据加载中...
                mLlContainer.setVisibility(VISIBLE);
                mPbFooter.setVisibility(VISIBLE);
                break;
            default:
            case PullListView.LOAD_MORE_STATE_NO_MORE:// 没有更多搜索结果
                mLlContainer.setVisibility(VISIBLE);
                mPbFooter.setVisibility(GONE);
                break;
        }
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.28 18:02
     * Description：获取footerview的转圈控件
     *
     * @return footerview的转圈控件
     */
    public ProgressBar getPbFooter() {
        return mPbFooter;
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.28 18:02
     * Description：获取footerview文本控件
     *
     * @return footerview文本控件
     */
    public TextView getTvFooter() {
        return mTvFooter;
    }
}

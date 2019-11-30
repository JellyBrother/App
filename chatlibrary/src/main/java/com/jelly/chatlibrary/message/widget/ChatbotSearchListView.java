package com.jelly.chatlibrary.message.widget;//package com.jelly.chat.message.widget;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewConfiguration;
//import android.widget.Adapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.android.mms.R;
//import com.android.mms.log.MmsLog;
//import com.android.mms.rcs.maap.chatbot.constant.ChatbotCommon;
//import com.android.mms.util.FontScaleBig;
//import com.vivo.common.animation.LKListView;
//
///**
// * Author：liuguodong 72071152
// * Date：2019.11.23 11:33
// * Description：小程序搜索列表的ListView
// */
//public class ChatbotSearchListView extends ListView {
//    private static final String TAG = "ChatbotSearchListView";
//    // 上下文
//    private Context mContext;
//    // FontScale
//    private float mFontScale;
//    // Resources
//    private Resources mResources;
//    // FooterView
//    private View mFooterView;
//    // 加载提示图片
//    private ImageView mIvMore;
//    // 加载提示文本
//    private TextView mTvMore;
//    // 滑动判断
//    private int mTouchSlop;
//    // 当前状态
//    private int mState;
//    // 垂直方向滑动距离
//    private float mInitialMotionY;
//    // 滑动监听
//    private OnRefreshListener mOnRefreshListener;
//
//    public ChatbotSearchListView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public ChatbotSearchListView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    public ChatbotSearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context);
//    }
//
//    private void init(Context context) {
//        mContext = context;
//        mResources = context.getResources();
//        mFontScale = FontScaleBig.getInstance().getFontScale();
//        mFooterView = inflate(context, R.layout.chatbot_view_search_footer, null);
//        mIvMore = mFooterView.findViewById(R.id.iv_more);
//        mTvMore = mFooterView.findViewById(R.id.tv_more);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
//        if (mFontScale != 0) {
//            mTvMore.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontScale * getResources().getDimension(R.dimen.aui_txt_size_4));
//        }
//        addFooterView(mFooterView);
//        setState(ChatbotCommon.SearchList.LOAD_MORE_STATE_INIT);
//        // 加载更多
//        mTvMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnRefreshListener == null) {
//                    return;
//                }
//                if (mState != ChatbotCommon.SearchList.LOAD_MORE_STATE_MORE) {
//                    return;
//                }
//                // 有更多，可以点击加载
//                mOnRefreshListener.onPullUpToRefresh();
//            }
//        });
//    }
//
//    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
//        this.mOnRefreshListener = mOnRefreshListener;
//    }
//
//    public interface OnRefreshListener {
//        public void onPullUpToRefresh();
//    }
//
//    public void setState(int state) {
//        mState = state;
//        switch (state) {
//            case ChatbotCommon.SearchList.LOAD_MORE_STATE_INIT:// 初始化搜索
//                mFooterView.setVisibility(GONE);
//                break;
//            case ChatbotCommon.SearchList.LOAD_MORE_STATE_MORE:// 更多搜索结果
//                mFooterView.setVisibility(VISIBLE);
//                mIvMore.setVisibility(GONE);
//                mTvMore.setText(mResources.getString(R.string.maap_chatbot_search_more));
//                mTvMore.setTextColor(mResources.getColor(R.color.links_color));
//                break;
//            case ChatbotCommon.SearchList.LOAD_MORE_STATE_LOADING:// 数据加载中...
//                mFooterView.setVisibility(VISIBLE);
//                mIvMore.setVisibility(VISIBLE);
//                mTvMore.setText(mResources.getString(R.string.maap_chatbot_main_loading));
//                mTvMore.setTextColor(mResources.getColor(R.color.maap_chatbot_main_item_text_desc_color));
//                break;
//            default:
//            case ChatbotCommon.SearchList.LOAD_MORE_STATE_NO_MORE:// 没有更多搜索结果
//                mFooterView.setVisibility(VISIBLE);
//                mIvMore.setVisibility(GONE);
//                mTvMore.setText(mResources.getString(R.string.maap_chatbot_search_no_more));
//                mTvMore.setTextColor(mResources.getColor(R.color.maap_chatbot_main_item_text_desc_color));
//                break;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        MmsLog.d(TAG, "onTouchEvent action:" + action);
//        if (mState == ChatbotCommon.SearchList.LOAD_MORE_STATE_MORE) {// 有数据
//            try {
//                if (isLastItemVisible(this)) {// 最后一个
//                    if (action == MotionEvent.ACTION_DOWN) {
//                        mInitialMotionY = event.getY();
//                    }
//                    if (action == MotionEvent.ACTION_MOVE) {
//                        int yDiff = (int) Math.abs(event.getY() - mInitialMotionY);
//                        if (yDiff > mTouchSlop && mOnRefreshListener != null) {
//                            mOnRefreshListener.onPullUpToRefresh();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                MmsLog.d(TAG, "onTouchEvent Exception:" + e.toString());
//            }
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private boolean isLastItemVisible(ListView listView) {
//        // 判断最后listView中最后一个item是否完全显示出来
//        final Adapter adapter = listView.getAdapter();
//        if (null == adapter || adapter.isEmpty()) {
//            return true;
//        }
//        final int lastItemPosition = adapter.getCount() - 1;
//        final int lastVisiblePosition = listView.getLastVisiblePosition();
//        if (lastVisiblePosition >= lastItemPosition - 1) {
//            final int childIndex = lastVisiblePosition - listView.getFirstVisiblePosition();
//            final int childCount = listView.getChildCount();
//            final int index = Math.min(childIndex, childCount - 1);
//            final View lastVisibleChild = listView.getChildAt(index);
//            if (lastVisibleChild != null) {
//                return lastVisibleChild.getBottom() <= listView.getBottom();
//            }
//        }
//        return false;
//    }
//}

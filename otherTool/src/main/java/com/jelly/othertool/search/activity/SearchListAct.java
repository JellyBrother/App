package com.jelly.othertool.search.activity;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.baselibrary.widget.listview.PullListFooterView;
import com.jelly.baselibrary.widget.listview.PullListView;
import com.jelly.othertool.R;
import com.jelly.othertool.main.common.OtherToolCommon;
import com.jelly.othertool.search.adapter.SearchListAdapter;
import com.jelly.othertool.search.entity.SearchListEntity;
import com.jelly.othertool.search.fragment.SearchListFra;
import com.jelly.othertool.search.viewmodel.SearchListActViewModel;
import com.jelly.othertool.search.widget.SearchItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchListAct extends BaseLifecycleActivity<SearchListActViewModel> {
    private static final String TAG = "SearchListAct";
    // handler标记界面开始搜索
    public static final int WHAT_SEARCH = 2;
    // 主页面展示的Fragment
    private SearchListFra mSearchListFra;
    // 空提示
    private LinearLayout mEmptyView;
    // 搜索输入框
    private EditText mEtSearch;
    // 搜索列表
    private PullListView mSearchListView;
    // listview的底部上拉加载控件
    private PullListFooterView mSearchListFooterView;
    // 列表适配器
    private SearchListAdapter mChatbotSearchListAdapter;
    // 所有搜索结果集合
    private List<SearchListEntity> mAllList = new ArrayList<>();
    // 输入框点击搜索的网络搜索结果大于最多展示条数的集合
    private List<SearchListEntity> mInputSearchMoreList = new ArrayList<>();
    // 搜索文本
    private String mSearchText = "";
    // 是否是搜索默认false不是搜索
    private boolean mIsInSearchMode = false;
    // Handler
    private ChatbotMainHandler mHandler;
    // FragmentManager
    private FragmentManager mFragmentManager;
    // 是否搜索完成,用来同步本地和网络搜索展示的，true是本地和网络搜索都完成了
    private boolean isInputSearchFinish = false;
    // 取消搜索按钮
    private TextView mTvSearchCancel;
    private InputMethodManager mInputMethodManager;
    private PullListFooterView mSearchListHeaderView;

    private static class ChatbotMainHandler extends Handler {
        // 弱引用activity
        private WeakReference<BaseActivity> mActivity;

        public ChatbotMainHandler(BaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchListAct activity = (SearchListAct) mActivity.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (msg.what == WHAT_SEARCH) {
                activity.mAllList.clear();
                activity.mInputSearchMoreList.clear();
                activity.isInputSearchFinish = false;
                activity.mViewModel.initAllSearch(activity.mSearchText);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "conv-frag", mSearchListFra);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsInSearchMode) {
            mInputMethodManager.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected SearchListActViewModel initViewModel() {
        return new SearchListActViewModel();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.othertool_act_search_list);
        initViews();
        initFragment(savedInstanceState);
        initListener();
    }

    @Override
    protected void observerData() {
        mViewModel.mInputDbSearchList.observe(new Observer<List<SearchListEntity>>() {
            @Override
            public void onChanged(@Nullable List<SearchListEntity> chatbotSearchListEntities) {
                mSearchListHeaderView.setFooterViewAndState(PullListView.LOAD_MORE_STATE_INIT);
                mSearchListView.setPullDownToRefreshState(PullListView.REFRESH_STATE_COMPLETE);
                setInputSearchData(chatbotSearchListEntities);
            }
        });
        mViewModel.mInputNetSearchList.observe(new Observer<List<SearchListEntity>>() {
            @Override
            public void onChanged(@Nullable List<SearchListEntity> list) {
                mSearchListHeaderView.setFooterViewAndState(PullListView.LOAD_MORE_STATE_INIT);
                mSearchListView.setPullDownToRefreshState(PullListView.REFRESH_STATE_COMPLETE);
                // 更多搜索结果或者没有更多搜索结果提示
                if (PublicUtil.isEmptyList(list)) {
                    setInputSearchData(list);
                    setTvMoreData(PullListView.LOAD_MORE_STATE_INIT);
                } else {
                    // 对返回的网络查询数据进行处理，只添加三条，多余的在查看更多的时候展示
                    if (list.size() < OtherToolCommon.SearchList.SHOW_MAX_ITEMS) {
                        setInputSearchData(list);
                        setTvMoreData(PullListView.LOAD_MORE_STATE_NO_MORE);
                    } else {
                        setInputSearchData(list.subList(0, OtherToolCommon.SearchList.SHOW_MAX_ITEMS));
                        setTvMoreData(PullListView.LOAD_MORE_STATE_MORE);
                        mInputSearchMoreList = list.subList(OtherToolCommon.SearchList.SHOW_MAX_ITEMS, list.size());
                    }
                }
            }
        });
        mViewModel.mLoadMoreNetSearchList.observe(new Observer<List<SearchListEntity>>() {
            @Override
            public void onChanged(@Nullable List<SearchListEntity> list) {
                setLoadMoreNetSearchData(list);
            }
        });
    }

    private void initViews() {
        mInputMethodManager = (InputMethodManager) mActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mTvSearchCancel = findViewById(R.id.tv_search_cancel);
        mEtSearch = findViewById(R.id.et_search);
        mEmptyView = findViewById(R.id.rl_search_empty);
        mSearchListView = findViewById(R.id.lv_search_list_view);
        mHandler = new ChatbotMainHandler(this);
        mChatbotSearchListAdapter = new SearchListAdapter(mActivity);
        mSearchListView.setAdapter(mChatbotSearchListAdapter);
        mSearchListHeaderView = new PullListFooterView(mActivity);
        mSearchListFooterView = new PullListFooterView(mActivity);
        mSearchListView.addHeaderView(mSearchListHeaderView);
        mSearchListView.addFooterView(mSearchListFooterView);
        initEmptyAndListViewState(false, false);
    }

    private void initFragment(Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            Fragment fragment = mFragmentManager.getFragment(savedInstanceState, "conv-frag");
            if (fragment != null) {
                mSearchListFra = (SearchListFra) fragment;
            }
        }
        if (mSearchListFra == null) {
            mSearchListFra = new SearchListFra();
        }
        mFragmentManager.beginTransaction().add(R.id.fl_main_fragment_container, mSearchListFra).commit();
    }

    private void initListener() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 搜索内容变化的时候调用
                startSearch(s + "".trim());
            }
        });
        mEtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    startSearch();
                } else {
                    cancelSearch();
                }
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 监听键盘点击搜索按钮
                    startSearch(mSearchText);
                    return true;
                }
                return false;
            }
        });
        //搜索框取消按钮按下
        mTvSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSearch.clearFocus();
            }
        });
        mSearchListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mInputMethodManager.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
                }
                return false;
            }
        });
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof SearchItem) {
                    SearchListEntity entity = ((SearchItem) view).getData();
                    if (entity == null) {
                        return;
                    }
                    LogUtil.getInstance().d(TAG, "mLvSearchListView OnItemClick");
                    ToastUtil.makeText("条目点击");
                }
            }
        });
        PullListView.OnPullUpToLoadMoreListener onPullUpToRefreshListener = new PullListView.OnPullUpToLoadMoreListener() {
            @Override
            public void onPullUpToLoadMore() {
                if (PublicUtil.isNetWorkDisconnect(mActivity)) {
                    return;
                }
                setTvMoreData(PullListView.LOAD_MORE_STATE_LOADING);
                // 有更多，可以点击加载
                mViewModel.loadMoreNetSearch();
            }
        };
        // 上拉加载更多
        mSearchListView.setOnPullUpToLoadMoreListener(onPullUpToRefreshListener);
        mSearchListFooterView.setOnPullUpToRefreshListener(onPullUpToRefreshListener);

        mSearchListHeaderView.setFooterViewAndState(PullListView.LOAD_MORE_STATE_INIT);
        mSearchListView.setPullDownToRefreshState(PullListView.REFRESH_STATE_COMPLETE);
        PullListView.OnPullDownToRefreshListener onPullDownToRefreshListener = new PullListView.OnPullDownToRefreshListener() {
            @Override
            public void onPullDownToRefresh() {
                mSearchListHeaderView.setFooterViewAndState(PullListView.LOAD_MORE_STATE_LOADING);
                mSearchListView.setPullDownToRefreshState(PullListView.REFRESH_STATE_LOADING);
                startSearch(mSearchText);
            }
        };
        //下拉刷新
        mSearchListView.setOnPullDownToRefreshListener(onPullDownToRefreshListener);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtil.getInstance().v(TAG, "onKeyUp:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mIsInSearchMode) {
                    cancelSearch();
                    return true;
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initEmptyAndListViewState(boolean isEmptyVisible, boolean isListViewVisible) {
        mEmptyView.setVisibility(isEmptyVisible ? View.VISIBLE : View.GONE);
        if (isListViewVisible) {
            mSearchListView.setVisibility(View.VISIBLE);
        } else {
            mSearchListView.setVisibility(View.GONE);
        }
    }

    private void startSearch(String text) {
        LogUtil.getInstance().d(TAG, "startSearch:" + text);
        mSearchText = PublicUtil.getString(text);
        mHandler.removeCallbacksAndMessages(null);
        if (TextUtils.isEmpty(mSearchText)) {
            initEmptyAndListViewState(false, false);
            return;
        }
        if (PublicUtil.isNetWorkDisconnect(mActivity)) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(WHAT_SEARCH, OtherToolCommon.SearchList.INTERVAL_TIME);
    }

    private void setInputSearchData(List<SearchListEntity> list) {
        LogUtil.getInstance().d(TAG, "setInputData");
        // 对数据库查询和网络请求回来的数据进行同步
        if (!PublicUtil.isEmptyList(list)) {
            mAllList.addAll(list);
            mChatbotSearchListAdapter.setInputSearchData(mAllList, mSearchText);
        }
        // 数据显示逻辑处理
        if (PublicUtil.isEmptyList(mAllList)) {
            // 说明本地和网络都没有数据
            if (isInputSearchFinish) {
                initEmptyAndListViewState(true, false);
            }
        } else {
            initEmptyAndListViewState(false, true);
        }
        if (!isInputSearchFinish) {
            isInputSearchFinish = true;
        }
    }

    private void setLoadMoreNetSearchData(List<SearchListEntity> list) {
        LogUtil.getInstance().d(TAG, "setLoadMoreNetSearchData");
        if (PublicUtil.isEmptyList(list)) {
            setTvMoreData(PullListView.LOAD_MORE_STATE_NO_MORE);
        } else {
            if (list.size() < OtherToolCommon.SearchList.SEARCH_ITEMS) {
                setTvMoreData(PullListView.LOAD_MORE_STATE_NO_MORE);
            } else {
                setTvMoreData(PullListView.LOAD_MORE_STATE_MORE);
            }
            list.addAll(0, mInputSearchMoreList);
            mChatbotSearchListAdapter.setLoadMoreNetSearchData(list);
        }
    }

    private void setTvMoreData(int state) {
        mSearchListView.setPullUpToLoadMoreState(state);
        mSearchListFooterView.setFooterViewAndState(state);
    }

    private void startSearch() {
        mEtSearch.setText("");
        mIsInSearchMode = true;
        mFragmentManager.beginTransaction().hide(mSearchListFra).commit();
        mEtSearch.requestFocus();
        mInputMethodManager.showSoftInput(mEtSearch, 0);
    }

    private void cancelSearch() {
        mIsInSearchMode = false;
        mEtSearch.setText("");
        mEtSearch.clearFocus();
        mInputMethodManager.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
        mFragmentManager.beginTransaction().show(mSearchListFra).commit();
    }
}

package com.jelly.chatlibrary.message.activity;//package com.jelly.chat.message.activity;
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.arch.lifecycle.Observer;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.mms.R;
//import com.android.mms.log.MmsLog;
//import com.android.mms.rcs.maap.chatbot.adapter.ChatbotSearchListAdapter;
//import com.android.mms.rcs.maap.chatbot.base.BaseActivity;
//import com.android.mms.rcs.maap.chatbot.bean.ChatbotSearchListEntity;
//import com.android.mms.rcs.maap.chatbot.constant.ChatbotCommon;
//import com.android.mms.rcs.maap.chatbot.fragment.ChatbotMainFragment;
//import com.android.mms.rcs.maap.chatbot.manager.ChatbotManager;
//import com.android.mms.rcs.maap.chatbot.viewmodel.ChatbotMainViewModel;
//import com.android.mms.rcs.maap.chatbot.widget.ChatbotSearchItem;
//import com.android.mms.rcs.maap.chatbot.widget.ChatbotSearchListView;
//import com.android.mms.rcs.model.DeepLinkData;
//import com.android.mms.rcs.util.ChatbotUtils;
//import com.android.mms.ui.ComposeMessageActivity;
//import com.android.mms.ui.ConversationList;
//import com.android.mms.ui.MessageUtils;
//import com.android.mms.util.FontScaleBig;
//import com.vivo.common.BbkTitleView;
//import com.vivo.common.animation.FakeView;
//import com.vivo.common.animation.SearchControl;
//import com.vivo.common.animation.SearchView;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Author：liuguodong 72071152
// * Date：2019.11.22 10:48
// * Description：小程序主页
// */
//public class ChatbotMainActivity extends BaseActivity<ChatbotMainViewModel> {
//    private static final String TAG = "ChatbotMainActivity";
//    // handler标记界面开始搜索
//    public static final int WHAT_SEARCH = 2;
//    // 跳转扫一扫
//    private static final int REQUEST_CODE_SCAN_QRCODE = 1;
//    // 是否支持扫一扫
//    private static boolean mSmsScanSup = false;
//    // 主页面展示的Fragment
//    private ChatbotMainFragment mChatbotMainFragment;
//    // 页面父布局
//    private RelativeLayout mRlContent;
//    // 顶部标题搜索的父布局
//    private RelativeLayout mRlTop;
//    // 标题
//    private BbkTitleView mBtTitle;
//    // 搜索
//    private SearchView mSvSearch;
//    // 搜索输入框
//    private EditText mEditTextSearch;
//    // 搜索列表
//    private ChatbotSearchListView mLvSearchListView;
//    // 空提示
//    private LinearLayout mEmptyView;
//    // 扫一扫
//    private FrameLayout mScanView;
//    // 空提示文本
//    private TextView mTvEmptyView;
//    // 搜索控制器
//    private SearchControl mSearchControl;
//    // 搜索文本
//    private String mSearchText = "";
//    // 是否是搜索默认false不是搜索
//    private boolean mIsInSearchMode = false;
//    // 列表适配器
//    private ChatbotSearchListAdapter mChatbotSearchListAdapter;
//    // 同步锁
//    private Object mObject = new Object();
//    // 所有搜索结果集合
//    private List<ChatbotSearchListEntity> mAllList = new ArrayList<>();
//    // 输入框点击搜索的网络搜索结果大于最多展示条数的集合
//    private List<ChatbotSearchListEntity> mInputSearchMoreList = new ArrayList<>();
//    // Handler
//    private ChatbotMainHandler mHandler;
//    // FragmentManager
//    private FragmentManager mFragmentManager;
//
//    private static class ChatbotMainHandler extends Handler {
//        // 弱引用activity
//        private WeakReference<Activity> mActivity;
//
//        public ChatbotMainHandler(Activity activity) {
//            mActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            ChatbotMainActivity activity = (ChatbotMainActivity) mActivity.get();
//            if (activity == null || activity.isFinishing()) {
//                return;
//            }
//            if (msg.what == WHAT_SEARCH) {
//                activity.initSearchView();
//                activity.mAllList.clear();
//                activity.mInputSearchMoreList.clear();
//                activity.mViewModel.initAllSearch(activity.mSearchText);
//            }
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        getFragmentManager().putFragment(outState, "conv-frag", mChatbotMainFragment);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (mIsInSearchMode) {
//            imm.hideSoftInputFromWindow(mSvSearch.getWindowToken(), 0);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    protected ChatbotMainViewModel initViewModel() {
//        return new ChatbotMainViewModel();
//    }
//
//    @Override
//    protected void initView(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.maap_chatbot_main_activity);
//        if (MessageUtils.isOverRomVersion(4.0f)) {
//            getWindow().setBackgroundDrawableResource(vivo.R.drawable.vigour_window_settting_background_light);
//        }
//        initViews();
//        initFragment(savedInstanceState);
//        initListener();
//    }
//
//    @Override
//    protected void observerData() {
//        mViewModel.mInputDbSearchList.observe(new Observer<List<ChatbotSearchListEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<ChatbotSearchListEntity> chatbotSearchListEntities) {
//                setInputSearchData(chatbotSearchListEntities);
//            }
//        });
//        mViewModel.mInputNetSearchList.observe(new Observer<List<ChatbotSearchListEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<ChatbotSearchListEntity> list) {
//                // 更多搜索结果或者没有更多搜索结果提示
//                if (ChatbotUtils.isEmptyList(list)) {
//                    setInputSearchData(list);
//                    setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_INIT);
//                } else {
//                    // 对返回的网络查询数据进行处理，只添加三条，多余的在查看更多的时候展示
//                    if (list.size() < ChatbotCommon.SearchList.SHOW_MAX_ITEMS) {
//                        setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_NO_MORE);
//                        setInputSearchData(list);
//                    } else {
//                        setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_MORE);
//                        setInputSearchData(list.subList(0, ChatbotCommon.SearchList.SHOW_MAX_ITEMS));
//                        mInputSearchMoreList = list.subList(ChatbotCommon.SearchList.SHOW_MAX_ITEMS, list.size());
//                    }
//                }
//            }
//        });
//        mViewModel.mLoadMoreNetSearchList.observe(new Observer<List<ChatbotSearchListEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<ChatbotSearchListEntity> list) {
//                setLoadMoreNetSearchData(list);
//            }
//        });
//    }
//
//    private void initViews() {
//        mRlContent = findViewById(R.id.rl_content);
//        mRlTop = findViewById(R.id.rl_top);
//        mBtTitle = findViewById(R.id.bt_title);
//        // 左边返回
//        mBtTitle.showLeftButton();
//        mBtTitle.setLeftButtonIcon(R.drawable.ic_title_back_icon);
//        mBtTitle.getCenterView().setText(getString(R.string.maap_name));
//        mBtTitle.initRightIconButton();
//        // 定位
//        mBtTitle.setIconViewDrawableRes(BbkTitleView.RIGHT_ICON_FIRST, R.drawable.rcs_chatbot_location_layer);
//        // 搜索控件
//        mSvSearch = findViewById(R.id.sv_search);
//        mEmptyView = findViewById(R.id.rl_search_empty);
//        mSmsScanSup = ChatbotUtils.isSmsScanSupport(getApplicationContext());
//        if (mSmsScanSup) {
//            mScanView = findViewById(R.id.fl_scan);
//            mScanView.setVisibility(View.VISIBLE);
//        }
//        mTvEmptyView = findViewById(R.id.tv_search_empty);
//        mLvSearchListView = findViewById(R.id.lv_search_list_view);
//        mHandler = new ChatbotMainHandler(this);
//        initSearchView();
//        initEmptyAndListViewState(false, false);
//        float fontScale = FontScaleBig.getInstance().getFontScale();
//        if (fontScale != 0) {
//            mTvEmptyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontScale * getResources().getDimension(R.dimen.aui_txt_size_4));
//        }
//    }
//
//    private void initSearchView() {
//        mSvSearch.setSearchHint(getString(R.string.maap_chatbot_main_search));
//        mSvSearch.setButtonTextSize(17);
//        mSvSearch.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
//        ViewGroup viewGroup = (ViewGroup) mSvSearch.getChildAt(0);
//        if (viewGroup != null) {
//            int count = viewGroup.getChildCount();
//            for (int i = 0; i < count; i++) {
//                if (viewGroup.getChildAt(i) instanceof EditText) {
//                    mEditTextSearch = (EditText) viewGroup.getChildAt(i);
//                }
//            }
//        }
//        FakeView fakeView = new FakeView(this);
//        fakeView.setFakedView(mBtTitle);
//        fakeView.setVisibility(View.INVISIBLE);
//        ((ViewGroup) mRlContent.getParent()).addView(fakeView);
//        mSearchControl = mSvSearch.getSearchControl();
//        mSearchControl.setSearchBarType(SearchControl.IN_CONTENTVIEW);
//        mSearchControl.setTitleView(mBtTitle);
//        mSearchControl.setSearchList(mLvSearchListView);
//        mSearchControl.setMovingContainer(((View) mBtTitle.getParent().getParent()));
//        mSearchControl.setFakeTitleView(fakeView);
//    }
//
//    private void initFragment(Bundle savedInstanceState) {
//        mFragmentManager = getFragmentManager();
//        if (savedInstanceState != null) {
//            Fragment fragment = mFragmentManager.getFragment(savedInstanceState, "conv-frag");
//            if (fragment != null) {
//                mChatbotMainFragment = (ChatbotMainFragment) fragment;
//            }
//        }
//        if (mChatbotMainFragment == null) {
//            mChatbotMainFragment = new ChatbotMainFragment();
//        }
//        mFragmentManager.beginTransaction().add(R.id.fl_main_fragment_container, mChatbotMainFragment).commit();
//    }
//
//    private void initListener() {
//        mBtTitle.setLeftButtonClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MmsLog.d(TAG, "mBtTitle onClick finish");
//                finish();
//            }
//        });
//        mBtTitle.setIconViewOnClickListner(BbkTitleView.RIGHT_ICON_FIRST, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(ChatbotNeighborActivity.getIntent(ChatbotMainActivity.this));
//            }
//        });
//        // 扫一扫
//        if (mSmsScanSup) {
//            mScanView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MmsLog.d(TAG, "mBtTitle onClick scan");
//                    try {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse("scan://vivo.scanner.com/single?mode=301"));
//                        startActivityForResult(intent, REQUEST_CODE_SCAN_QRCODE);
//                    } catch (Exception e) {
//                        MmsLog.e(TAG, "start scan error: " + e);
//                    }
//                }
//            });
//        }
//        mSearchControl.setAnimationListener(new SearchControl.AnimationListener() {
//
//            public void onAnimationStart(boolean isCloseSearchView) {
//                // isCloseSearchView为true的时候，执行下滑关闭搜索动画，false的时候执行上滑开始搜索动画
//                MmsLog.d(TAG, "mSearchControl: setAnimationListener" + isCloseSearchView);
//                if (mSmsScanSup) {
//                    if (isCloseSearchView) {
//                        // 关闭搜索功能-动画开始
//                        mScanView.setVisibility(View.GONE);
//                    } else {
//                        // 开启搜索功能-动画开始
//                        mScanView.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//            public void onAnimationEnd(boolean isCloseSearchView) {
//                MmsLog.d(TAG, "mSearchControl: onAnimationEnd" + isCloseSearchView);
//                if (isCloseSearchView) {
//                    // 关闭搜索功能-动画结束
//                    mIsInSearchMode = false;
//                    mSearchText = "";
//                    if (mSmsScanSup) {
//                        mScanView.setVisibility(View.VISIBLE);
//                    }
//                    initEmptyAndListViewState(false, false);
//                    mFragmentManager.beginTransaction().show(mChatbotMainFragment).commit();
//                } else {
//                    // 开启搜索功能-动画结束
//                    if (mSmsScanSup) {
//                        mScanView.setVisibility(View.GONE);
//                    }
//                    initEmptyAndListViewState(false, false);
//                    mFragmentManager.beginTransaction().hide(mChatbotMainFragment).commit();
//                }
//            }
//        });
//        mSvSearch.setSearchLinstener(new SearchView.SearchLinstener() {
//            @Override
//            public void onSearchTextChanged(String text) {
//                // 搜索内容变化的时候调用
//                startSearch(text + "".trim());
//            }
//
//            @Override
//            public boolean processSearchClick() {
//                // 搜索框进入搜索状态
//                MmsLog.d(TAG, "SearchView processSearchClick");
//                if (mSvSearch.getHeight() > mSvSearch.getBottom()) {
//                    mSvSearch.setTop(0);
//                    return false;
//                }
//                return true;
//            }
//        });
//        if (mEditTextSearch != null) {
//            mEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 监听键盘点击搜索按钮
//                        startSearch(mSearchText);
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
//        //搜索框取消按钮按下
//        mSvSearch.setOnButtonClickLinster(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSearchControl.switchToNormal();
//            }
//        });
//        mLvSearchListView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    InputMethodManager imm = (InputMethodManager) mActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(mSvSearch.getWindowToken(), 0);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (TextUtils.isEmpty(mSearchText)) {
//                        mSearchControl.switchToNormal();
//                    }
//                }
//                return false;
//            }
//        });
//        mLvSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (view instanceof ChatbotSearchItem) {
//                    ChatbotSearchListEntity entity = ((ChatbotSearchItem) view).getData();
//                    if (entity == null) {
//                        return;
//                    }
//                    MmsLog.d(TAG, "mLvSearchListView OnItemClick");
//
//                }
//            }
//        });
//        // 加载更多
//        mLvSearchListView.setOnRefreshListener(new ChatbotSearchListView.OnRefreshListener() {
//            @Override
//            public void onPullUpToRefresh() {
//                if (ChatbotUtils.isNetWorkDisconnect(mActivity)) {
//                    MessageUtils.showToast(mActivity, R.string.maap_no_net, 500);
//                    return;
//                }
//                setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_LOADING);
//                // 有更多，可以点击加载
//                mViewModel.loadMoreNetSearch();
//            }
//        });
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        MmsLog.v(TAG, "onKeyUp:" + keyCode);
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                if (mIsInSearchMode) {
//                    mSearchControl.switchToNormal();
//                    return true;
//                }
//                break;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        MmsLog.d(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode + ", data = " + data);
//        switch (requestCode) {
//            case REQUEST_CODE_SCAN_QRCODE:
//                if (resultCode == Activity.RESULT_OK) {
//                    if (data != null) {
//                        String deeplink = data.getStringExtra("code_value");
//                        MmsLog.d(TAG, "onActivityResult: deeplink = " + deeplink);
//                        DeepLinkData deepData = DeepLinkData.getData(deeplink);
//                        if (deepData != null) {
//                            final String service_id = deepData.getRecipient();
//                            final String body = deepData.getBody();
//                            final String suggest = deepData.getSuggestions();
//                            final String number = ChatbotManager.getNumber(service_id);
//                            MmsLog.d(TAG, "recipient = " + service_id + ", body =" + body + ", suggestions = " + suggest + ", num = " + number);
//                            Intent[] intents = new Intent[2];
//                            intents[0] = new Intent(this, ConversationList.class);
//                            Intent composeIntent = new Intent(this, ComposeMessageActivity.class);
//                            composeIntent.setData(Uri.fromParts("smsto", number + "?body=" + (TextUtils.isEmpty(body) ? "" : body), null));
//                            composeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            if (service_id.contains("@") && !TextUtils.isEmpty(suggest)) {
//                                composeIntent.putExtra(ComposeMessageActivity.EXTRA_SCAN_SUGG, suggest);
//                            }
//                            intents[1] = composeIntent;
//                            startActivities(intents);
//                        } else {
//                            MessageUtils.showToast(getApplicationContext(), R.string.maap_chatbot_scan_fail, Toast.LENGTH_SHORT);
//                        }
//                    } else {
//                        MessageUtils.showToast(getApplicationContext(), R.string.maap_chatbot_scan_fail, Toast.LENGTH_SHORT);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void initEmptyAndListViewState(boolean isEmptyVisible, boolean isListViewVisible) {
//        mEmptyView.setVisibility(isEmptyVisible ? View.VISIBLE : View.GONE);
//        if (isListViewVisible) {
//            mLvSearchListView.setItemsCanFocus(true);
//            mLvSearchListView.setFocusable(true);
//            mLvSearchListView.setClickable(true);
//            mLvSearchListView.setVisibility(View.VISIBLE);
//        } else {
//            mLvSearchListView.setAdapter(null);
//            mChatbotSearchListAdapter = null;
//            mLvSearchListView.setVisibility(View.GONE);
//        }
//    }
//
//    private void startSearch(String text) {
//        MmsLog.d(TAG, "startSearch:" + text);
//        mSearchText = text;
//        mHandler.removeCallbacksAndMessages(null);
//        if (TextUtils.isEmpty(text)) {
//            initEmptyAndListViewState(false, false);
//            return;
//        }
//        if (ChatbotUtils.isNetWorkDisconnect(mActivity)) {
//            MessageUtils.showToast(mActivity, R.string.maap_no_net, 500);
//            return;
//        }
//        mHandler.sendEmptyMessageDelayed(WHAT_SEARCH, ChatbotCommon.SearchList.INTERVAL_TIME);
//    }
//
//    private void setInputSearchData(List<ChatbotSearchListEntity> list) {
//        MmsLog.d(TAG, "setInputData");
//        // 对数据库查询和网络请求回来的数据进行同步
//        synchronized (mObject) {
//            if (!ChatbotUtils.isEmptyList(list)) {
//                mAllList.addAll(list);
//                if (mChatbotSearchListAdapter == null) {
//                    mChatbotSearchListAdapter = new ChatbotSearchListAdapter(mActivity);
//                }
//                mLvSearchListView.setAdapter(mChatbotSearchListAdapter);
//                mChatbotSearchListAdapter.setInputSearchData(mAllList, mSearchText);
//            }
//            // 数据显示逻辑处理
//            if (ChatbotUtils.isEmptyList(mAllList)) {
//                // 说明本地和网络都没有数据
//                initEmptyAndListViewState(true, false);
//            } else {
//                initEmptyAndListViewState(false, true);
//            }
//        }
//    }
//
//    private void setLoadMoreNetSearchData(List<ChatbotSearchListEntity> list) {
//        MmsLog.d(TAG, "setLoadMoreNetSearchData");
//        if (ChatbotUtils.isEmptyList(list)) {
//            setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_NO_MORE);
//        } else {
//            if (list.size() < ChatbotCommon.SearchList.SEARCH_ITEMS) {
//                setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_NO_MORE);
//            } else {
//                setTvMoreData(ChatbotCommon.SearchList.LOAD_MORE_STATE_MORE);
//            }
//            list.addAll(0, mInputSearchMoreList);
//            mChatbotSearchListAdapter.setLoadMoreNetSearchData(list);
//        }
//    }
//
//    private void setTvMoreData(int state) {
//        mLvSearchListView.setState(state);
//    }
//}

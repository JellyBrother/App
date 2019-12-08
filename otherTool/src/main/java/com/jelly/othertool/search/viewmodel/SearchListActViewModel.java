package com.jelly.othertool.search.viewmodel;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.jelly.baselibrary.base.BaseLiveData;
import com.jelly.baselibrary.base.BaseViewModel;
import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.othertool.main.common.OtherToolCommon;
import com.jelly.othertool.search.entity.SearchListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：liuguodong 72071152
 * Date：2019.11.20 14:21
 * Description：ChatbotMainActivity的ViewModel
 */
public class SearchListActViewModel extends BaseViewModel {
    private static final String TAG = "SearchListActViewModel";
    // 输入框搜索的数据库查询的数据
    public BaseLiveData<List<SearchListEntity>> mInputDbSearchList;
    // 输入框搜索的网络查询的数据
    public BaseLiveData<List<SearchListEntity>> mInputNetSearchList;
    // 上拉加载的网络查询的数据
    public BaseLiveData<List<SearchListEntity>> mLoadMoreNetSearchList;
    // 搜索文本
    private String mSearchText;
    // 搜索位置
    private int mStart = 0;

    public SearchListActViewModel() {
        mInputDbSearchList = newLiveData();
        mInputNetSearchList = newLiveData();
        mLoadMoreNetSearchList = newLiveData();
    }

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public void destroyData() {
        super.destroyData();
//        mQueryResultHandler.cancelOperation(OtherToolCommon.SearchList.TOKEN_SEARCH_CHATBOT_LOCAL);
//        mQueryResultHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Author：
     * Date：2019.11.20 20:42
     * Description：开始数据库和网络搜索
     */
    public void initAllSearch(String searchText) {
        mSearchText = searchText + "";
        inputDbSearch();
        inputNetSearch();
    }

    /**
     * Author：
     * Date：2019.11.20 20:42
     * Description：输入框输入开始数据库搜索
     */
    public void inputDbSearch() {
//        LogUtil.getInstance().d(TAG, "inputDbSearch");
//        mQueryResultHandler.cancelOperation(OtherToolCommon.SearchList.TOKEN_SEARCH_CHATBOT_LOCAL);
//        Uri searchUri = Uri.parse("content://vivo-rcs-chatbot/search").buildUpon().appendQueryParameter("pattern", mSearchText).build();
//        mQueryResultHandler.startQuery(OtherToolCommon.SearchList.TOKEN_SEARCH_CHATBOT_LOCAL, mSearchText, searchUri, null, null, null, null);

        ArrayList<SearchListEntity> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            SearchListEntity entity = new SearchListEntity();
            entity.chatbotTitle = "标题" + i;
            entity.isSelect = false;
            entity.chatbotContent = "描述" + i;
            entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT;
            entity.dataFrom = OtherToolCommon.SearchList.DATAFROM_DB;
            list.add(entity);
        }
//        mInputDbSearchList.setValue(list);
    }

    /**
     * Author：
     * Date：2019.11.20 20:42
     * Description：输入框输入开始网络搜索
     */
    public void inputNetSearch() {
        LogUtil.getInstance().d(TAG, "inputNetSearch");
        ArrayList<SearchListEntity> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SearchListEntity entity = new SearchListEntity();
            entity.chatbotTitle = "标题" + i;
            entity.isSelect = false;
            entity.chatbotContent = "描述" + i;
            entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT;
            entity.dataFrom = OtherToolCommon.SearchList.DATAFROM_NET;
            list.add(entity);
        }
        mInputNetSearchList.setValue(list);
    }

    /**
     * Author：
     * Date：2019.11.20 20:42
     * Description：上拉加载开始网络搜索
     */
    public void loadMoreNetSearch() {
        LogUtil.getInstance().d(TAG, "loadMoreNetSearch");
        ArrayList<SearchListEntity> list = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            SearchListEntity entity = new SearchListEntity();
            entity.chatbotTitle = "标题" + i;
            entity.isSelect = false;
            entity.chatbotContent = "描述" + i;
            entity.itemType = OtherToolCommon.SearchList.ADAPTER_ITEM_TYPE_CHATBOT;
            entity.dataFrom = OtherToolCommon.SearchList.DATAFROM_NET;
            list.add(entity);
        }
        mLoadMoreNetSearchList.setValue(list);
    }

    @SuppressLint("HandlerLeak")
    private AsyncQueryHandler mQueryResultHandler = new AsyncQueryHandler(BaseCommon.Base.application.getContentResolver()) {

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            switch (token) {
                case OtherToolCommon.SearchList.TOKEN_SEARCH_CHATBOT_LOCAL:
                    if (cookie != null && mSearchText != null) {
                        if (!cookie.equals(mSearchText)) {
                            LogUtil.getInstance().d(TAG, "Cookie not equal mSearchText,we need requery;" + "cookie: " + cookie + " ;mSearchText: " + mSearchText);
                            return;
                        }
                    }
                    List<SearchListEntity> dbList = new ArrayList<>();
                    try {
//                        if (cursor != null) {
//                            List<Chatbot> localList = new ArrayList<>();
//                            cursor.moveToFirst();
//                            while (!cursor.isAfterLast()) {
//                                Chatbot chatbot = ChatbotManager.fillFromCursor(cursor);
//                                localList.add(chatbot);
//                                cursor.moveToNext();
//                            }
//                            ChatbotUtils.resetData(localList, dbList, true);
//                        }
                        mInputDbSearchList.postValue(dbList);
                    } catch (Exception e) {
                        LogUtil.getInstance().d(TAG, "AsyncQueryHandler exception" + e.toString());
                        mInputDbSearchList.postValue(dbList);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

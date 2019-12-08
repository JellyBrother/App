package com.jelly.wxtool.main;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.os.Bundle;

import com.jelly.baselibrary.base.BaseLiveData;
import com.jelly.baselibrary.base.BaseViewModel;
import com.jelly.baselibrary.common.BaseCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：liuguodong 72071152
 * Date：2019.11.20 14:21
 * Description：ChatbotSearchList的ViewModel
 */
public class WxToolMainActViewModel extends BaseViewModel {
    private static final String TAG = "WxToolMainActViewModel";
    private static final int TOKEN_SEARCH_CHATBOT_LOCAL = 1;
    // 输入框搜索的数据库查询的数据
    public BaseLiveData<List<String>> mInputDbSearchList;

    public WxToolMainActViewModel() {
        mInputDbSearchList = newLiveData();
    }

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public void destroyData() {
        super.destroyData();
        mQueryResultHandler.cancelOperation(TOKEN_SEARCH_CHATBOT_LOCAL);
        mQueryResultHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Author：liuguodong 72071152
     * Date：2019.11.20 20:42
     * Description：输入框输入开始数据库搜索
     */
    public void inputDbSearch() {
        mQueryResultHandler.cancelOperation(TOKEN_SEARCH_CHATBOT_LOCAL);
//        Uri searchUri = ChatbotConstant.URI_CHATBOT_SEARCH.buildUpon().appendQueryParameter("pattern", mSearchText).build();
//        mQueryResultHandler.startQuery(ChatbotConstant.TOKEN_SEARCH_CHATBOT_LOCAL, mSearchText, searchUri, null, null, null, null);
    }

    @SuppressLint("HandlerLeak")
    private AsyncQueryHandler mQueryResultHandler = new AsyncQueryHandler(BaseCommon.Base.application.getContentResolver()) {

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            switch (token) {
                case TOKEN_SEARCH_CHATBOT_LOCAL:
                    if (cookie != null ) {
//                        if (!cookie.equals(mSearchText)) {
//                            MmsLog.d(TAG, "Cookie not equal mSearchText,we need requery;" + "cookie: " + cookie + " ;mSearchText: " + mSearchText);
//                            return;
//                        }
                    }
                    List<String> dbList = new ArrayList<>();
                    try {
                        if (cursor != null) {
                            List<String> localList = new ArrayList<>();
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
//                                String chatbot = ChatbotManager.fillFromCursor(cursor);
//                                localList.add(chatbot);
                                cursor.moveToNext();
                            }
//                            ChatbotUtils.resetData(localList, dbList, true);
                        }
                        mInputDbSearchList.postValue(dbList);
                    } catch (Exception e) {
                        mInputDbSearchList.postValue(dbList);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

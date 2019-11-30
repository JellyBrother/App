package com.jelly.chatlibrary.message.viewmodel;//package com.jelly.chat.message.viewmodel;
//
//import android.annotation.SuppressLint;
//import android.content.AsyncQueryHandler;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.RemoteException;
//import android.text.TextUtils;
//
//import com.jelly.baselibrary.base.BaseViewModel;
//
//import java.util.ArrayList;
//
///**
// * Author：liuguodong 72071152
// * Date：2019.11.20 14:21
// * Description：ChatbotSearchList的ViewModel
// */
//public class MessageMainViewModel extends BaseViewModel {
//    private static final String TAG = "ChatbotMainViewModel";
//    // 输入框搜索的数据库查询的数据
//    public BaseLiveData<List<ChatbotSearchListEntity>> mInputDbSearchList;
//
//    public MessageMainViewModel() {
//        mInputDbSearchList = newLiveData();
//    }
//
//    @Override
//    public void initData(Bundle bundle) {
//    }
//
//    @Override
//    public void destroyData() {
//        super.destroyData();
//        mQueryResultHandler.cancelOperation(ChatbotConstant.TOKEN_SEARCH_CHATBOT_LOCAL);
//        mQueryResultHandler.removeCallbacksAndMessages(null);
//    }
//
//    /**
//     * Author：liuguodong 72071152
//     * Date：2019.11.20 20:42
//     * Description：开始数据库和网络搜索
//     */
//    public void initAllSearch(String searchText) {
//        mSearchText = searchText + "";
//        inputDbSearch();
//        inputNetSearch();
//    }
//
//    /**
//     * Author：liuguodong 72071152
//     * Date：2019.11.20 20:42
//     * Description：输入框输入开始数据库搜索
//     */
//    public void inputDbSearch() {
//        MmsLog.d(TAG, "inputDbSearch");
//        mQueryResultHandler.cancelOperation(ChatbotConstant.TOKEN_SEARCH_CHATBOT_LOCAL);
//        Uri searchUri = ChatbotConstant.URI_CHATBOT_SEARCH.buildUpon().appendQueryParameter("pattern", mSearchText).build();
//        mQueryResultHandler.startQuery(ChatbotConstant.TOKEN_SEARCH_CHATBOT_LOCAL, mSearchText, searchUri, null, null, null, null);
//    }
//
//    @SuppressLint("HandlerLeak")
//    private AsyncQueryHandler mQueryResultHandler = new AsyncQueryHandler(MmsApp.getApplication().getContentResolver()) {
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            super.onQueryComplete(token, cookie, cursor);
//            switch (token) {
//                case ChatbotConstant.TOKEN_SEARCH_CHATBOT_LOCAL:
//                    if (cookie != null && mSearchText != null) {
//                        if (!cookie.equals(mSearchText)) {
//                            MmsLog.d(TAG, "Cookie not equal mSearchText,we need requery;" + "cookie: " + cookie + " ;mSearchText: " + mSearchText);
//                            return;
//                        }
//                    }
//                    List<ChatbotSearchListEntity> dbList = new ArrayList<>();
//                    try {
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
//                        mInputDbSearchList.postValue(dbList);
//                    } catch (Exception e) {
//                        MmsLog.d(TAG, "AsyncQueryHandler exception" + e.toString());
//                        mInputDbSearchList.postValue(dbList);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//}

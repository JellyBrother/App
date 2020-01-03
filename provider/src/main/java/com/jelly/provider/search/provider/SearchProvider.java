package com.jelly.provider.search.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.providerlibrary.common.BaseProviderCommon;

public class SearchProvider extends ContentProvider {
    private static final String TAG = "SearchProvider";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteOpenHelper mOpenHelper;

    static {
        sUriMatcher.addURI(BaseProviderCommon.Search.AUTHORITY, "search", UriType.Chatbot.CHATBOT_SEARCH);// 小程序本地搜索查询
        sUriMatcher.addURI(BaseProviderCommon.Search.AUTHORITY, "local", UriType.Chatbot.CHATBOT_LOCAL);//本地小程序
    }

    @Override
    public boolean onCreate() {
//        mOpenHelper = MmsSmsDatabaseHelper.getDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
//        switch (sUriMatcher.match(uri)) {
//            case UriType.Chatbot.CHATBOT_ALL:
//                return CursorType.Chatbot.TYPE_DIRECTORY;
//            case UriType.Chatbot.CHATBOT_ID:
//                return CursorType.Chatbot.TYPE_ITEM;
//            default:
//                throw new IllegalArgumentException("Unsupported URI " + uri + "!");
//        }
        return "";
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        Cursor cursor = null;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        try {
            switch (sUriMatcher.match(uri)) {
                case UriType.Chatbot.CHATBOT_SEARCH:
                    // 搜索本地
                    String searchText = uri.getQueryParameter("pattern");
                    String sqliteEscapeText = sqliteEscape(searchText);
                    String searchRawQuery = "SELECT * FROM rcs_chatbot WHERE " +
                            "(rcs_chatbot.favorite = 1) AND" +
                            "((rcs_chatbot.service_name LIKE '%" + sqliteEscapeText + "%') OR (rcs_chatbot.service_description LIKE '%" + sqliteEscapeText + "%'))";
                    cursor = db.rawQuery(searchRawQuery, null);
                    break;
                case UriType.Chatbot.CHATBOT_LOCAL:
                    //本地小程序
                    String selections = BaseProviderCommon.Search.KEY_FAVORITE + " = 1";
                    cursor = db.query(BaseProviderCommon.Search.TABLE_CHATBOT_NAME, projection, selections, null, null, null, null);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported URI " + uri + "!");
            }
        } catch (RuntimeException e) {
            if (cursor != null) {
                cursor.close();
            }
            throw e;
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int affectedRows = 0;
        LogUtil.getInstance().d(TAG, "threadId update uri: " + uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String extraWhere = null;
        String tableName = null;
        Uri notifyUri = null;
        boolean isNotify = uri.getBooleanQueryParameter("notify", true);
//        switch (sUriMatcher.match(uri)) {
//            case UriType.Chatbot.ALL:
//                tableName = BaseProviderCommon.Search.TABLE_CHATBOT_NAME;
//                break;
//            case UriType.Chatbot.ID:
//                extraWhere = BaseProviderCommon.Search._ID + " = " + uri.getPathSegments().get(0);
//                selection = DatabaseUtils.concatenateWhere(selection, extraWhere);
//                tableName = BaseProviderCommon.Search.TABLE_CHATBOT_NAME;
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported URI " + uri + "!");
//        }
        affectedRows = db.update(tableName, values, selection, selectionArgs);
        if (affectedRows > 0) {
            if (notifyUri == null) {
                notifyUri = BaseProviderCommon.Search.CONTENT_URI;
            }
            if (isNotify) {
                getContext().getContentResolver().notifyChange(notifyUri, null, true);
            }
        }
        return affectedRows;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int affectedRows = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        boolean noupdate = uri.getBooleanQueryParameter("noupdate", false);
        LogUtil.getInstance().d(TAG, "delete noupdate: " + noupdate);
//        switch (sUriMatcher.match(uri)) {
//            case UriType.Chatbot.ID:
//                long chatbotId = ContentUris.parseId(uri);
//                selection = BaseProviderCommon.Search._ID + " = " + chatbotId;
//                affectedRows = db.delete(BaseProviderCommon.Search.TABLE_CHATBOT_NAME, selection, selectionArgs);
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported URI " + uri + "!");
//        }
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(BaseProviderCommon.Search.CONTENT_URI, null, true);
        }
        return affectedRows;
    }

    private String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("%", "/%");
        return keyWord;
    }

    private static final class UriType {
        private static final class Chatbot {
            private static final int CHATBOT_SEARCH = 4;// 本地搜索
            private static final int CHATBOT_LOCAL = 5;
        }
    }
}

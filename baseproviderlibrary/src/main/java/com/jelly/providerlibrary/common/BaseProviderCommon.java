package com.jelly.providerlibrary.common;

import android.net.Uri;

/**
 * Description：常量类
 */
public class BaseProviderCommon {
    public static final class Search {
        public static final String AUTHORITY = "com.jelly.provider";
        public static Uri CONTENT_URI = Uri.parse("content://com.jelly.provider");
        public static final String TABLE_CHATBOT_NAME = "rcs_chatbot";
        public static final int INVALID_ROW_ID = -1;
    }
}

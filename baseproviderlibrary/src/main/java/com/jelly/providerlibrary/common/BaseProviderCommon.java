package com.jelly.providerlibrary.common;

import android.net.Uri;

/**
 * Author：
 * Date：2019.11.21 10:46
 * Description：常量类
 */
public class BaseProviderCommon {
    public static final class Search {
        public static final String AUTHORITY = "com.jelly.provider";
        public static Uri CONTENT_URI = Uri.parse("content://com.jelly.provider");
        public static final String TABLE_CHATBOT_NAME = "rcs_chatbot";
        public static final int INVALID_ROW_ID = -1;

        public static final String _ID = "_id";
        public static final String KEY_SERVICE_ID = "service_id"; //chatbot id
        public static final String KEY_SERVICE_NAME = "service_name"; //名称
        public static final String KEY_SERVICE_DESCRIPTION = "service_description"; //简介
        public static final String KEY_CALLBACK_PHONE_NUMBER = "callback_phone_number"; //服务号码
        public static final String KEY_SMS = "sms"; //短信号码
        public static final String KEY_SERVICE_ICON = "service_icon"; //Icon路径
        public static final String KEY_CATEGORY_LIST = "category_list"; //分类
        public static final String KEY_BRIEF = "brief"; //是否简要信息。默认为0，获取详情后位1
        public static final String KEY_FAVORITE = "favorite"; //是否收藏
        public static final String KEY_EMAIL = "email"; //邮箱
        public static final String KEY_WEBSITE = "website"; //网址
        public static final String KEY_ADDRESS = "address"; //地址
        public static final String KEY_ADDRESS_LABLE = "address_lable"; //地址标签
        public static final String KEY_LAST_SUGGESTED_LIST = "last_suggested_list"; //最后一条suggestion消息
        public static final String KEY_PINYIN = "pinyin"; //全拼
        public static final String KEY_PINYIN_SHORT = "pinyin_short"; //拼音首字母
        public static final String KEY_COLOUR = "colour";
        public static final String KEY_BACKGROUND_IMAGE = "background_image";
        public static final String KEY_VERIFIED = "verified";
        public static final String KEY_VERIFIED_BY = "verified_by";
        public static final String KEY_VERIFIED_EXPIRES = "verified_expires";
        public static final String KEY_EXPIRES = "expires";
        public static final String KEY_CACHE_CONTROL = "cache_control";
        public static final String KEY_E_TAG = "e_tag";
        public static final String KEY_RECENT_TIME = "recent_time";
    }
}

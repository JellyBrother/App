package com.example.myapp.base.utils;

import android.text.TextUtils;

import com.example.myapp.base.constant.BaseConstant;

public final class UserUtil {
    private static final String TAG = "UserUtil";
    private static final String FILE_USER = "file_user";
    private static final String USER_KEY_UID = "user_id";

    public static void putUserId(String uid) {
        SPUtils.getInstance(FILE_USER).put(USER_KEY_UID, uid);
    }

    public static String getUserId() {
        String uid = SPUtils.getInstance(FILE_USER).getString(USER_KEY_UID);
        LogUtils.d(TAG, "getUserId uid:" + uid);
        return uid;
    }

    public static String getUserIdByMd5() {
        String md5 = EncryptUtils.md5(getUserId());
        if (TextUtils.isEmpty(md5)) {
            md5 = BaseConstant.Path.DEFAULT_USER_ID_MD5;
        }
        LogUtils.d(TAG, "getUserIdByMd5 md5:" + md5);
        return md5;
    }
}
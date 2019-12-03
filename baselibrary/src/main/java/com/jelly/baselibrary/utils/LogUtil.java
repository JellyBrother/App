package com.jelly.baselibrary.utils;

import android.util.Log;

import com.jelly.baselibrary.common.BaseCommon;

public class LogUtil {
    private static LogUtil mLogUtil = null;

    private LogUtil() {
    }

    public static LogUtil getInstance() {
        if (mLogUtil == null) {
            synchronized (LogUtil.class) {
                if (mLogUtil == null) {
                    mLogUtil = new LogUtil();
                }
            }
        }
        return mLogUtil;
    }

    public void v(String tag, String str) {
        if (BaseCommon.Base.enableLog) {
            Log.v(tag, str);
        }
    }

    public void d(String tag, String str) {
        if (BaseCommon.Base.enableLog) {
            Log.d(tag, str);
        }
    }

    public void i(String tag, String str) {
        if (BaseCommon.Base.enableLog) {
            Log.d(tag, str);
        }
    }

    public void w(String tag, String str) {
        if (BaseCommon.Base.enableLog) {
            Log.d(tag, str);
        }
    }

    public void e(String tag, String str) {
        if (BaseCommon.Base.enableLog) {
            Log.d(tag, str);
        }
    }
}
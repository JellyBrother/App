/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jelly.app.base.fix.tinker;

import android.util.Log;

/**
 * Created by zhangshaowen on 16/3/17.
 */
public class ShareTinkerLog {
    private static final String TAG = "-*-";

    public static void i(final String tag, final String fmt, final Object... values) {
        printLog(Log.INFO, tag, fmt, values);
    }

    public static void w(final String tag, final String fmt, final Object... values) {
        printLog(Log.WARN, tag, fmt, values);
    }

    public static void e(final String tag, final String fmt, final Object... values) {
        printLog(Log.ERROR, tag, fmt, values);
    }

    public static void printErrStackTrace(String tag, Throwable thr, final String format, final Object... values) {
        printLog(tag, thr, format, values);
    }

    private static void printLog(int priority, String tag, String format, Object... params) {
        String log = (params == null || params.length == 0) ? format : String.format(format, params);
        Log.e(TAG + tag, log);
    }

    private static void printLog(String tag, Throwable thr, String format, Object... params) {
        String log = (params == null || params.length == 0) ? format : String.format(format, params);
        if (log == null) {
            log = "";
        }
        Log.e(TAG + tag, log, thr);
    }
}

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

package com.example.myapplication.fix.tinker;

import android.os.Build.VERSION;

/**
 * Created by zhangshaowen on 16/3/10.
 */
public class ShareTinkerInternals {
    private static final String TAG = ShareTinkerInternals.class.getSimpleName() + "Int";

    public static boolean isNewerOrEqualThanVersion(int apiLevel, boolean includePreviewVer) {
        if (includePreviewVer && VERSION.SDK_INT >= 23) {
            return VERSION.SDK_INT >= apiLevel
                    || ((VERSION.SDK_INT == apiLevel - 1) && VERSION.PREVIEW_SDK_INT > 0);
        } else {
            return VERSION.SDK_INT >= apiLevel;
        }
    }
}

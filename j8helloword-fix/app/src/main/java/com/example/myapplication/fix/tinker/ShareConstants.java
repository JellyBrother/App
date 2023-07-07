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

import java.util.regex.Pattern;

/**
 * Created by zhangshaowen on 16/3/24.
 */
public class ShareConstants {
    public static final String TEST_DEX_NAME = "test.dex";
    public static final Pattern CLASS_N_PATTERN = Pattern.compile("classes(?:[2-9]?|[1-9][0-9]+)\\.dex(\\.jar)?");
    public static final String CHECK_RES_INSTALL_FAIL = "checkResInstall failed";
}
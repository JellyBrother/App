package com.jelly.wechatbusinesstoolmodule.main.common;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.wechatbusinesstoolmodule.BuildConfig;

public class AppCommon {
    public static void init() {
        BaseCommon.Base.enableLog = BuildConfig.enableLog;
        BaseCommon.Base.enableWechatBusinessTool = BuildConfig.enableWechatBusinessTool;
    }

    public static final class App {

    }
}

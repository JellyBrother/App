package com.jelly.wxtoolmodule.main.common;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.wxtoolmodule.BuildConfig;

public class AppCommon {
    public static void init() {
        BaseCommon.Base.enableLog = BuildConfig.enableLog;
        BaseCommon.Base.enableChat = BuildConfig.enableChat;
        BaseCommon.Base.enableOtherTool = BuildConfig.enableOtherTool;
        BaseCommon.Base.enableWxTool = BuildConfig.enableWxTool;
    }

    public static final class App {
        public static final int LOAD_MORE_STATE_INIT = 0;
    }
}

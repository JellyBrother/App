package com.jelly.app.main.common;

import com.jelly.app.BuildConfig;
import com.jelly.baselibrary.common.BaseCommon;

public class AppCommon {
    public static void init() {
        BaseCommon.Base.enableLog = BuildConfig.enableLog;
        BaseCommon.Base.enableChat = BuildConfig.enableChat;
        BaseCommon.Base.enableOtherTool = BuildConfig.enableOtherTool;
        BaseCommon.Base.enableWxTool = BuildConfig.enableWxTool;
        BaseCommon.Base.enableWechatBusinessTool = BuildConfig.enableWechatBusinessTool;
    }

    public static final class App {
        public static final int LOAD_MORE_STATE_INIT = 0;
    }
}

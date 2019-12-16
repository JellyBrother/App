package com.jelly.othertool.main.common;

public class OtherToolCommon {

    public static final class OtherTool {
        public static final int LOAD_MORE_STATE_INIT = 0;
    }

    public static final class SearchList {
        // 网络搜索的数据最多展示条数
        public static final int SHOW_MAX_ITEMS = 3;
        // 网络搜索每次加载条数
        public static final int SEARCH_ITEMS = 20;
        // 限制时间间隔
        public static final long INTERVAL_TIME = 500;
        // 适配器条目展示标题类型
        public static final int ADAPTER_ITEM_TYPE_OUTER = 0;
        // 适配器条目展示条目类型
        public static final int ADAPTER_ITEM_TYPE_CHATBOT = 1;
        // 适配器条目展示类型数
        public static final int ADAPTER_ITEM_TYPE_COUNT = 2;
        // 数据来源本地数据库
        public static final int DATAFROM_DB = 0;
        // 数据来源网络请求
        public static final int DATAFROM_NET = 1;
        // 本地搜索查询
        public static final int TOKEN_SEARCH_CHATBOT_LOCAL = 3;
    }
}

package com.jelly.wxtool.main.common;

/**
 * Description：常量类
 */
public class WxToolCommon {

    /**
     * Description：消息列表常量类
     */
    public static final class WxTool {
        // 是否跳转从聊天开始添加好友
        public static boolean enableChat = false;
        // 是否跳转从搜索开始添加好友
        public static boolean enableSearch = true;
    }

    public static final class AdbExec {
        //点击屏幕上的一点(微信群聊右上角的图片)，eg：这点的像素坐标是（1000,150）
        public static final String INPUT_TAP_1000_150 = "input tap 1000 150\n";

        // 可以用来模拟长按，原理：在小的距离内，在较长的持续时间内进行滑动，最后表现出来的结果就是长按动作。
        public static final String INPUT_swipe_500_500_501_501_2000 = "input swipe 500 500 501 501 2000\n";

        //按下按键，eg：该按键的按键值是4（系统的返回键）。按键值参考https://www.cnblogs.com/sharecenter/p/5621048.html
        public static final String INPUT_KEYEVENT_4 = "input keyevent 4\n";

        //输入文本，eg：文本内容是1234567890
        public static final String INPUT_TEXT_1234567890 = "input text 1234567890\n";
    }

    public static final class Search {
        // 是否跳转从聊天开始添加好友
        public static int THREAD_SLEEP_TIME = 1000;
    }
}

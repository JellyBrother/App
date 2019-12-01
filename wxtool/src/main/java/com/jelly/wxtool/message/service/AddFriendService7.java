package com.jelly.wxtool.message.service;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.wxtool.message.utils.AccessibilityUtil;
import com.jelly.wxtool.message.widget.AddFriendSettingView;

/**
 * 辅助服务自动安装APP，该服务在单独进程中允许
 */
public class AddFriendService7 extends BaseAccessibilityService {
    private static final String TAG = "AddFriendService";
    public static boolean isServiceStarted = false;
    private AddFriendSettingView addFriendSettingView;
    private AccessibilityEvent event;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().d(TAG, "onCreate");
        isServiceStarted = true;
        addFriendSettingView = new AddFriendSettingView(BaseCommon.Base.application);
        addFriendSettingView.setOnClickListener(new AddFriendSettingView.OnClickListener() {
            @Override
            public void beginOnClick() {
                startAdd();
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        LogUtil.getInstance().d(TAG, "onServiceConnected");
        isServiceStarted = true;
        addFriendSettingView.showFloatingWindow();
    }

    @Override
    public void onDestroy() {
        LogUtil.getInstance().d(TAG, "onDestroy");
        isServiceStarted = false;
        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        this.event = event;
        startAdd();
    }

    @Override
    public void onInterrupt() {
    }

    private void startAdd() {
        try {
//            if (!addFriendSettingView.isBegin) {
//                return;
//            }
            if (event == null) {
                return;
            }
            String packageName = event.getPackageName().toString();
            if (!packageName.contains("com.tencent.mm")) {
                return;
            }
            AccessibilityNodeInfo eventNode = event.getSource();
            if (eventNode == null) {
                LogUtil.getInstance().d(TAG, "eventNode: null, 重新获取eventNode...");
                return;
            }
            //当前窗口根节点
//            rootNode = getRootInActiveWindow();
//            if (rootNode == null) {
//                return;
//            }
            int eventType = event.getEventType();
            String className = event.getClassName().toString();
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    if (className.equals("com.tencent.mm.ui.chatting.ChattingUI")) {
                        // 群聊界面
                        findViewByContentDescriptionClick("聊天信息", true);
                    }
                    if (className.equals("com.tencent.mm.chatroom.ui.ChatroomInfoUI")) {
                        // 群聊详情界面

                    }
                    if (className.equals("com.tencent.mm.chatroom.ui.ChatroomInfoUI")) {
                        // 群聊详情好友搜索界面

                    }
                    break;
            }
            // 回收节点实例来重用
            eventNode.recycle();
        } catch (Exception e) {
            LogUtil.getInstance().d(TAG, "onAccessibilityEvent Exception: " + e.toString());
        }
    }



    private void addAllFriend() {
        LogUtil.getInstance().d(TAG, "addFriend");
        if (addFriendSettingView.isAddStartSearch) {
            addFriendWithGroupSearch();
        } else {
            addFriendWithGroupChat();
        }
    }

    private void addFriendWithGroupChat() {
        LogUtil.getInstance().d(TAG, "addFriendWithGroupChat");

    }

    private void addFriendWithGroupSearch() {
        LogUtil.getInstance().d(TAG, "addFriendWithGroupSearch");
    }

    private void addSingleFriend() {
        LogUtil.getInstance().d(TAG, "addSingleFriend");
    }
}
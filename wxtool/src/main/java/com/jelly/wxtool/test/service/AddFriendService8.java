package com.jelly.wxtool.test.service;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.wxtool.test.utils.AccessibilityUtil;
import com.jelly.wxtool.test.widget.AddFriendSettingView;

public class AddFriendService8 extends BaseAccessibilityService {
    private static final String TAG = "AddFriendService";
    public static boolean isServiceStarted = false;
    private AddFriendSettingView addFriendSettingView;
    private AccessibilityEvent event;
    private AccessibilityNodeInfo rootNode;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().d(TAG, "onCreate");
        isServiceStarted = true;
        addFriendSettingView = new AddFriendSettingView(BaseCommon.Base.application);
        addFriendSettingView.setOnClickListener(new AddFriendSettingView.OnClickListener() {
            @Override
            public void beginOnClick() {
                // 群聊界面
                AccessibilityNodeInfo node = findViewByViewIdResourceName(getRootInActiveWindow(), "com.tencent.mm:id/lo");// 右上角三个点
//                AccessibilityNodeInfo node = findViewByContentDescription(getRootInActiveWindow(), "聊天信息");// 右上角三个点
                if (node != null) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
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
            if (!addFriendSettingView.isBegin) {
                return;
            }
            if (event == null) {
                return;
            }
            //当前窗口根节点
            rootNode = getRootInActiveWindow();
            if (rootNode == null) {
                return;
            }
            String packageName = rootNode.getPackageName().toString();
            if (!packageName.contains("com.tencent.mm")) {
                return;
            }
            int eventType = event.getEventType();
            String className = event.getClassName().toString();
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if (className.equals("com.tencent.mm.ui.chatting.ChattingUI")) {
                        // 群聊界面
                        AccessibilityNodeInfo node = findViewById("lo");// 右上角三个点
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (className.equals("com.tencent.mm.chatroom.ui.ChatroomInfoUI")) {
                        // 群聊详情界面
                        AccessibilityNodeInfo node = findViewById("ej5");// 群单个人员头像
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                        // 群聊详情界面
                        AccessibilityNodeInfo node = findViewById("ej5");// 群单个人员头像
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    break;
            }
            // 回收节点实例来重用
            rootNode.recycle();
        } catch (Exception e) {
            LogUtil.getInstance().d(TAG, "onAccessibilityEvent Exception: " + e.toString());
        }
    }
}
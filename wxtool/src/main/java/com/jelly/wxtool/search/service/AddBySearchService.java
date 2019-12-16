package com.jelly.wxtool.search.service;

import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.search.utils.AccessibilityUtil;
import com.jelly.wxtool.search.utils.WxUtil;
import com.jelly.wxtool.search.widget.AddBySearchView;

import java.util.List;

public class AddBySearchService extends BaseService {
    private static final String TAG = "AddBySearchService";
    public static boolean isServiceStarted = false;
    private AddBySearchView addFriendSettingView;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().d(TAG, "onCreate");
        isServiceStarted = true;
        if (addFriendSettingView == null) {
            addFriendSettingView = new AddBySearchView(BaseCommon.Base.application);
        }
        addFriendSettingView.setOnClickListener(new AddBySearchView.OnClickListener() {
            @Override
            public void beginOnClick() {
                WxUtil.beginOnClick(mService, addFriendSettingView);
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
        addFriendSettingView.setStatus(View.GONE);
        ToastUtil.makeText("辅助服务停止了");
        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        startAdd(event);
    }

    @Override
    public void onInterrupt() {
    }

    private void startAdd(AccessibilityEvent event) {
        try {
            if (!addFriendSettingView.isBegin) {
                return;
            }
            if (event == null) {
                return;
            }
            //当前窗口根节点
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode == null) {
                return;
            }
            String packageName = rootNode.getPackageName().toString();
            if (TextUtils.isEmpty(packageName)) {
                return;
            }
            if (!packageName.contains("com.tencent.mm")) {
                return;
            }
            String className = event.getClassName().toString();
            if (TextUtils.isEmpty(className)) {
                return;
            }
            int eventType = event.getEventType();
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    if (TextUtils.equals(className, "com.tencent.mm.ui.chatting.ChattingUI")) {
                        // 群聊界面
                        WxUtil.clickChattingUI(mService);
                    }
                    if (TextUtils.equals(className, "com.tencent.mm.chatroom.ui.ChatroomInfoUI")) {
                        // 群聊详情界面
                        WxUtil.clickChatroomInfoUI(mService, addFriendSettingView);
                    }
                    if (TextUtils.equals(className, "com.tencent.mm.chatroom.ui.SeeRoomMemberUI")) {
                        // 群成员列表界面
                        WxUtil.clickSeeRoomMemberUI(mService, addFriendSettingView);
                    }
                    if (TextUtils.equals(className, "com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                        // 联系人详情界面
                        WxUtil.clickContactInfoUI(mService, addFriendSettingView);
                    }
                    if (TextUtils.equals(className, "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI")) {
                        // 申请添加朋友界面
                        WxUtil.clickSayHiWithSnsPermissionUI(mService, addFriendSettingView);
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
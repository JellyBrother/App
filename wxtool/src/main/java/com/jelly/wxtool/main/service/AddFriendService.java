package com.jelly.wxtool.main.service;

import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.main.utils.AccessibilityUtil;
import com.jelly.wxtool.main.widget.AddFriendSettingView;

import java.util.List;

public class AddFriendService extends BaseAccessibilityService {
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
        if (addFriendSettingView == null) {
            addFriendSettingView = new AddFriendSettingView(BaseCommon.Base.application);
        }
        addFriendSettingView.setVisibility(View.VISIBLE);
        addFriendSettingView.setOnClickListener(new AddFriendSettingView.OnClickListener() {
            @Override
            public void beginOnClick() {
                // 群聊界面
                clickTextViewByID("com.tencent.mm:id/lo");
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
        addFriendSettingView.setVisibility(View.GONE);
        ToastUtil.makeText("辅助服务停止了");
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
                        addFriendSettingView.addFriend = 0;
                        clickTextViewByID("com.tencent.mm:id/lo");
                    }
                    if (className.equals("com.tencent.mm.chatroom.ui.ChatroomInfoUI")) {
                        // 群聊详情界面
                        List<AccessibilityNodeInfo> list = findViewByIdReturnList("com.tencent.mm:id/ej5");
                        if (!PublicUtil.isEmptyList(list)) {
                            if (list.size() > addFriendSettingView.addFriend) {
                                AccessibilityNodeInfo nodeInfo = list.get(addFriendSettingView.addFriend);
                                CharSequence contentDescription = nodeInfo.getContentDescription();
                                if (!TextUtils.isEmpty(contentDescription) && (TextUtils.equals(contentDescription, "添加成员") ||
                                        TextUtils.equals(contentDescription, "删除成员"))) {
                                    // 最后都添加完成后，剩下添加成员和删除成员按钮
                                    addFriendSettingView.setStop();
                                } else {
                                    performViewClick(nodeInfo);
                                }
                            } else {
                                //要不就是群人员很多，list不知这些人
                                // 要不就是需要点击查看更多，继续添加
                                //最后都添加完成后，服务停止，提交添加个数
                                addFriendSettingView.setStop();
                            }
                        }
                    }
                    if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                        // 群聊人员详情页面
                        addFriendSettingView.addFriend++;
                        performBackClick();
                    }
                    if (className.equals("com.tencent.mm.ui.contact.SelectContactUI")) {
                        // 群聊人员搜索页面

                    }
                    if (className.equals("com.tencent.mm.ui.contact.SelectContactUI")) {
                        // 添加群聊人员页面

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
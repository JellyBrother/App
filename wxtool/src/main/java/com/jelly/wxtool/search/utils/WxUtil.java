package com.jelly.wxtool.search.utils;

import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.wxtool.main.common.WxToolCommon;
import com.jelly.wxtool.search.widget.AddBySearchView;

import java.util.List;

/**
 * 辅助功能/无障碍相关工具
 */
public class WxUtil {
    private static final String TAG = "WxUtil";
    private static boolean isBackSayHiWithSnsPermissionUI = false;

    public static void beginOnClick(AccessibilityService service, AddBySearchView view) {
        view.isBegin = true;
        clickChattingUI(service);
        clickChatroomInfoUI(service, view);
        clickSeeRoomMemberUI(service, view);
        clickContactInfoUI(service, view);
        clickSayHiWithSnsPermissionUI(service, view);
    }

    public static void clickChattingUI(AccessibilityService service) {
        LogUtil.getInstance().d(TAG, "clickChattingUI");
        // 群聊界面 com.tencent.mm.ui.chatting.ChattingUI
        AccessibilityNodeInfo chattingBackNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/lt",
                "", "", "android.widget.TextView");
        AccessibilityNodeInfo chattingMoreNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/lo",
                "聊天信息", null, "android.widget.ImageButton");
        if (chattingBackNode != null && chattingMoreNode != null) {
            AccessibilityServiceUtil.performViewClick(chattingMoreNode);
        }
    }

    public static void clickChatroomInfoUI(AccessibilityService service, AddBySearchView view) {
        try {
            Thread.sleep(WxToolCommon.Search.THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.getInstance().d(TAG, "clickChatroomInfoUI");
        // 群聊详情界面 com.tencent.mm.chatroom.ui.ChatroomInfoUI
        AccessibilityNodeInfo roomInfoListViewNode = AccessibilityServiceUtil.findNode(service, "android:id/list",
                "", null, "android.android.widget.ListView");
        List<AccessibilityNodeInfo> roomInfoHeadNode = AccessibilityServiceUtil.findNodeById(service, "com.tencent.mm:id/ej5");
        List<AccessibilityNodeInfo> roomInfoNameNode = AccessibilityServiceUtil.findNodeById(service, "com.tencent.mm:id/ej_");
        List<AccessibilityNodeInfo> roomInfoViewNode = AccessibilityServiceUtil.findNodeByText(service, "查看全部群成员");
        List<AccessibilityNodeInfo> roomNameNode = AccessibilityServiceUtil.findNodeByText(service, "群聊名称");
        if (roomInfoListViewNode != null && !PublicUtil.isEmptyList(roomInfoHeadNode) && !PublicUtil.isEmptyList(roomInfoNameNode)) {
            if (PublicUtil.isEmptyList(roomInfoViewNode)) {
                if (PublicUtil.isEmptyList(roomNameNode)) {
                    //需要上滑
                    AccessibilityServiceUtil.performScrollForward(service);
                    clickChattingUI(service);
                } else {
                    //进行添加
                    for (int i = 0; i < roomInfoHeadNode.size(); i++) {
                        if (roomInfoHeadNode.size() > view.addFriend) {
                            AccessibilityNodeInfo nodeInfo = roomInfoHeadNode.get(view.addFriend);
                            CharSequence contentDescription = nodeInfo.getContentDescription();
                            if (!TextUtils.isEmpty(contentDescription) && (TextUtils.equals(contentDescription, "添加成员") ||
                                    TextUtils.equals(contentDescription, "删除成员"))) {
                                // 最后都添加完成后，剩下添加成员和删除成员按钮
                                view.setStop();
                            } else {
                                AccessibilityServiceUtil.performViewClick(nodeInfo);
                            }
                        } else {
                            view.setStop();
                        }
                    }
                }
            } else {
                AccessibilityServiceUtil.performViewClick(roomInfoViewNode.get(0));
            }
        }
    }

    public static void clickSeeRoomMemberUI(AccessibilityService service, AddBySearchView view) {
        try {
            Thread.sleep(WxToolCommon.Search.THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.getInstance().d(TAG, "clickSeeRoomMemberUI");
        //群成员列表界面 com.tencent.mm.chatroom.ui.SeeRoomMemberUI
        AccessibilityNodeInfo memberSearchEdiNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/bdo",
                "搜索", null, "android.widget.EditText");
        AccessibilityNodeInfo memberSearchGriNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/en8",
                "", null, "android.widget.GridView");
        List<AccessibilityNodeInfo> memberSearchNodeList = AccessibilityServiceUtil.findNodeById(service, "com.tencent.mm:id/a");
        if (memberSearchEdiNode != null && memberSearchGriNode != null && !PublicUtil.isEmptyList(memberSearchNodeList)) {
            //一个个点击添加

//            for (int i = 0; i < root.getChildCount(); i++) {
//                AccessibilityNodeInfo result = findNodeByDescription(root.getChild(i), description);
//                if (result == null) {
//                    continue;
//                } else {
//                    return result;
//                }
//            }
        }
    }

    public static void clickContactInfoUI(AccessibilityService service, AddBySearchView view) {
        try {
            Thread.sleep(WxToolCommon.Search.THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.getInstance().d(TAG, "clickContactInfoUI");
        if (!isBackSayHiWithSnsPermissionUI) {
            view.addFriend++;
        }
        // 联系人详情界面 com.tencent.mm.plugin.profile.ui.ContactInfoUI
        AccessibilityNodeInfo tipNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/djs",
                "提示", null, "android.widget.TextView");
        List<AccessibilityNodeInfo> contentNodeList = AccessibilityServiceUtil.findNodeById(service, "com.tencent.mm:id/djx");
        AccessibilityNodeInfo yesNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/b49",
                "确定", null, "android.widget.Button");
        if (tipNode != null && PublicUtil.isEmptyList(contentNodeList) && yesNode != null) {
            // 添加好友界面,弹框不能加好友
            AccessibilityServiceUtil.performBackClick(service);
            AccessibilityServiceUtil.performBackClick(service);
            return;
        }
        List<AccessibilityNodeInfo> settingNodeList = AccessibilityServiceUtil.findNodeByText(service, "设置备注和标签");
        AccessibilityNodeInfo powerNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/dd",
                "朋友权限", null, "android.widget.TextView");
        List<AccessibilityNodeInfo> moreNodeList = AccessibilityServiceUtil.findNodeByText(service, "更多信息");
        List<AccessibilityNodeInfo> sendMessageNodeList = AccessibilityServiceUtil.findNodeByText(service, "发消息");
        List<AccessibilityNodeInfo> videoCallNodeList = AccessibilityServiceUtil.findNodeByText(service, "音视频通话");
        if (!PublicUtil.isEmptyList(settingNodeList) && powerNode != null && !PublicUtil.isEmptyList(moreNodeList) &&
                !PublicUtil.isEmptyList(sendMessageNodeList) && !PublicUtil.isEmptyList(videoCallNodeList)) {
            // 好友详情界面
            AccessibilityServiceUtil.performBackClick(service);
            return;
        }
        AccessibilityNodeInfo circleFriendsNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/dl4",
                "朋友圈", null, "android.widget.TextView");
        if (circleFriendsNode != null && !PublicUtil.isEmptyList(sendMessageNodeList)) {
            // 个人详情界面
            AccessibilityServiceUtil.performBackClick(service);
            return;
        }
        List<AccessibilityNodeInfo> signatureNodeList = AccessibilityServiceUtil.findNodeByText(service, "个性签名");
        List<AccessibilityNodeInfo> addContactNodeList = AccessibilityServiceUtil.findNodeByText(service, "添加到通讯录");
        if (!PublicUtil.isEmptyList(settingNodeList) && !PublicUtil.isEmptyList(signatureNodeList) &&
                circleFriendsNode != null && !PublicUtil.isEmptyList(addContactNodeList)) {
            // 添加好友界面
            if (isBackSayHiWithSnsPermissionUI) {
                isBackSayHiWithSnsPermissionUI = false;
                AccessibilityServiceUtil.performBackClick(service);
            } else {
                AccessibilityServiceUtil.performViewClick(addContactNodeList.get(0));
            }
            return;
        }
    }

    public static void clickSayHiWithSnsPermissionUI(AccessibilityService service, AddBySearchView view) {
        try {
            Thread.sleep(WxToolCommon.Search.THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.getInstance().d(TAG, "clickSayHiWithSnsPermissionUI");
        // 申请添加朋友 com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI
        AccessibilityNodeInfo applyAddFriendNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/dd",
                "申请添加朋友", null, "android.widget.TextView");
        AccessibilityNodeInfo sendNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/ln",
                "发送", null, "android.widget.Button");
        AccessibilityNodeInfo sendAddNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/x6",
                "发送添加朋友申请", null, "android.widget.TextView");
        AccessibilityNodeInfo settingNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/gbw",
                "设置备注", null, "android.widget.TextView");
        AccessibilityNodeInfo noSeeMeNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/g_l",
                "不让她看我", null, "android.widget.TextView");
        AccessibilityNodeInfo noSeeHeNode = AccessibilityServiceUtil.findNode(service, "com.tencent.mm:id/g_o",
                "不看她", null, "android.widget.TextView");
        if (applyAddFriendNode != null && sendNode != null && sendAddNode != null && settingNode != null &&
                noSeeMeNode != null && noSeeHeNode != null) {
            AccessibilityServiceUtil.performViewClick(sendNode);
            isBackSayHiWithSnsPermissionUI = true;
            AccessibilityServiceUtil.performBackClick(service);
        }
    }
}
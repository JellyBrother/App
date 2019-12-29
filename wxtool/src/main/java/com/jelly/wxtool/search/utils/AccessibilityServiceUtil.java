package com.jelly.wxtool.search.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.PublicUtil;
import com.jelly.wxtool.main.common.WxToolCommon;

import java.util.List;

/**
 * 辅助功能/无障碍相关工具
 */
public class AccessibilityServiceUtil {
    private static final String TAG = "AccessibilityServiceUtil";
    private static final String WIDGET_LIST_VIEW = "android.widget.ListView";

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public static void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }

    /**
     * 模拟返回操作
     */
    public static void performBackClick(AccessibilityService service) {
        if (service == null) {
            return;
        }
        try {
            Thread.sleep(WxToolCommon.Search.THREAD_SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.performGlobalAction(service.GLOBAL_ACTION_BACK);
    }

    /**
     * 模拟返回操作
     */
    public static void fastBackClick(AccessibilityService service) {
        if (service == null) {
            return;
        }
        service.performGlobalAction(service.GLOBAL_ACTION_BACK);
    }

    /**
     * 模拟下滑操作
     */
    public static void performScrollBackward(AccessibilityService service) {
        if (service == null) {
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟上滑操作
     */
    public static void performScrollForward(AccessibilityService service) {
        if (service == null) {
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    public static List<AccessibilityNodeInfo> findNodeById(AccessibilityService service, String id) {
        if (service == null) {
            return null;
        }
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByViewId(id);
        return nodeInfoList;
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public static List<AccessibilityNodeInfo> findNodeByText(AccessibilityService service, String text) {
        if (service == null) {
            return null;
        }
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(text);
        return nodeInfoList;
    }

    /**
     * Author：
     * Date：2019.12.16 10:35
     * Description：根据节点和描述信息查找对应的第一个节点
     *
     * @param root
     * @param description
     * @return 返回找到的第一个节点
     */
    public static AccessibilityNodeInfo findNodeByDescription(AccessibilityNodeInfo root, String description) {
        if (TextUtils.isEmpty(description)) {
            return null;
        }
        if (root == null) {
            return null;
        }
        CharSequence contentDescription = root.getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            return null;
        }
        if (TextUtils.equals(contentDescription, description)) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByDescription(root.getChild(i), description);
            if (result == null) {
                continue;
            } else {
                return result;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByDescription(List<AccessibilityNodeInfo> nodeInfoList, String description) {
        if (TextUtils.isEmpty(description)) {
            return null;
        }
        if (PublicUtil.isEmptyList(nodeInfoList)) {
            return null;
        }
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            CharSequence contentDescription = nodeInfo.getContentDescription();
            if (!TextUtils.isEmpty(contentDescription) && TextUtils.equals(contentDescription, description)) {
                return nodeInfo;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByClassName(AccessibilityNodeInfo root, String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        if (root == null) {
            return null;
        }
        CharSequence name = root.getClassName();
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        if (TextUtils.equals(name, className)) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByClassName(root.getChild(i), className);
            if (result == null) {
                continue;
            } else {
                return result;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeByClassName(List<AccessibilityNodeInfo> nodeInfoList, String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        if (PublicUtil.isEmptyList(nodeInfoList)) {
            return null;
        }
        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            CharSequence name = nodeInfo.getClassName();
            if (!TextUtils.isEmpty(name) && TextUtils.equals(name, className)) {
                return nodeInfo;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNode(AccessibilityService service, String id, String text,
                                                 String description, String className) {
        if (service == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = findNodeById(service, id);
        if (PublicUtil.isEmptyList(nodeInfoList)) {
            nodeInfoList = findNodeByText(service, text);
            if (PublicUtil.isEmptyList(nodeInfoList)) {
                AccessibilityNodeInfo node = findNodeByDescription(service.getRootInActiveWindow(), description);
                if (node == null) {
                    node = findNodeByClassName(service.getRootInActiveWindow(), className);
                }
                return node;
            }
        }
        AccessibilityNodeInfo node = findNodeByDescription(nodeInfoList, description);
        if (node == null) {
            node = findNodeByClassName(nodeInfoList, className);
        }
        if (node == null) {
            node = nodeInfoList.get(0);
        }
        return node;
    }

    public static void findNodeAndClick(AccessibilityService service, String id, String text, String description, String className) {
        AccessibilityNodeInfo nodeInfo = findNode(service, id, text, description, className);
        performViewClick(nodeInfo);
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) BaseCommon.Base.application.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }
}
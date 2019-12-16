package com.jelly.wxtool.test.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jelly.baselibrary.utils.LogUtil;

import java.util.List;

public class BaseAccessibilityService extends AccessibilityService {
    private static final String TAG = "BaseAccessibilityService";
    private AccessibilityManager mAccessibilityManager;
    private Context mContext;
    private static BaseAccessibilityService mInstance;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mAccessibilityManager = (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public static BaseAccessibilityService getInstance() {
        if (mInstance == null) {
            mInstance = new BaseAccessibilityService();
        }
        return mInstance;
    }

    /**
     * Check当前辅助服务是否启用
     *
     * @param serviceName serviceName
     * @return 是否启用
     */
    private boolean checkAccessibilityEnabled(String serviceName) {
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 前往开启辅助服务界面
     */
    public void goAccess() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
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
    public void performBackClick() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    /**
     * 模拟下滑操作
     */
    public void performScrollBackward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟上滑操作
     */
    public void performScrollForward() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewById(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<AccessibilityNodeInfo> findViewByIdReturnList(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        return nodeInfoList;
    }

    public void clickTextViewByText(String text) {
        AccessibilityNodeInfo nodeInfo = findViewByText(text);
        performViewClick(nodeInfo);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickTextViewByID(String id) {
        AccessibilityNodeInfo nodeInfo = findViewById(id);
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
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    /**
     * 查找对应文本的View
     */
    public void findViewByContentDescriptionClick(String mCheckData, boolean mIsEquals) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        int childCount = accessibilityNodeInfo.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo nodeInfo = accessibilityNodeInfo.getChild(i);
                CharSequence text = nodeInfo.getContentDescription();
                if (mIsEquals) {
                    if (text != null && text.toString().equals(mCheckData)) {
                        performViewClick(nodeInfo);
                    }
                } else {
                    if (text != null && text.toString().contains(mCheckData)) {
                        performViewClick(nodeInfo);
                    }
                }
            }
        } else {
            CharSequence text = accessibilityNodeInfo.getContentDescription();
            if (mIsEquals) {
                if (text != null && text.toString().equals(mCheckData)) {
                    performViewClick(accessibilityNodeInfo);
                }
            } else {
                if (text != null && text.toString().contains(mCheckData)) {
                    performViewClick(accessibilityNodeInfo);
                }
            }
        }
    }

    private static final String listViewClassName = "android.widget.ListView";

    /**
     * 根据固定的类名循环查找到listView,查找到目标listView后即退出循环
     */
    public AccessibilityNodeInfo recycleFindListView(AccessibilityNodeInfo node) {
        if (node.getChildCount() == 0) {
            return null;
        } else {//listview下面必定有子元素，所以放在此时判断
            for (int i = 0; i < node.getChildCount(); i++) {
                if (listViewClassName.equals(node.getClassName())) {
                    LogUtil.getInstance().d(TAG, "查找到listview了。。。");
                    return node;
                } else if (node.getChild(i) != null) {
                    AccessibilityNodeInfo result = recycleFindListView(node.getChild(i));
                    if (result == null) {
                        continue;
                    } else {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    /**
     * Author：
     * Date：2019.12.2 11:53
     * Description：在节点下面查找对应文字的节点，返回查找到的节点
     */
    public AccessibilityNodeInfo findViewByText(AccessibilityNodeInfo node, String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = null;
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if (text.equals(node.getText().toString())) {
                    accessibilityNodeInfo = node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    findViewByText(node.getChild(i), text);
                }
            }
        }
        return accessibilityNodeInfo;
    }

    /**
     * Author：
     * Date：2019.12.2 11:53
     * Description：在节点下面查找对应className的节点，返回查找到的节点
     */
    public AccessibilityNodeInfo findViewByClassName(AccessibilityNodeInfo node, String className) {
        if (node.getChildCount() == 0) {
            if (node.getClassName() != null) {
                if (className.equals(node.getClassName().toString())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    findViewByClassName(node.getChild(i), className);
                }
            }
        }
        return null;
    }

    /**
     * Author：
     * Date：2019.12.2 11:53
     * Description：在节点下面查找对应contentDescription的节点，返回查找到的节点
     */
    public AccessibilityNodeInfo findViewByContentDescription(AccessibilityNodeInfo node, String contentDescription) {
        if (node.getChildCount() == 0) {
            if (node.getContentDescription() != null) {
                if (contentDescription.equals(node.getContentDescription().toString())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    findViewByContentDescription(node.getChild(i), contentDescription);
                }
            }
        }
        return null;
    }

    /**
     * Author：
     * Date：2019.12.2 11:53
     * Description：在节点下面查找对应contentDescription的节点，返回查找到的节点
     */
    public AccessibilityNodeInfo findViewByViewIdResourceName(AccessibilityNodeInfo node, String viewIdResourceName) {
        if (node.getChildCount() == 0) {
            if (node.getViewIdResourceName() != null) {
                if (viewIdResourceName.equals(node.getViewIdResourceName())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    findViewByViewIdResourceName(node.getChild(i), viewIdResourceName);
                }
            }
        }
        return null;
    }
}

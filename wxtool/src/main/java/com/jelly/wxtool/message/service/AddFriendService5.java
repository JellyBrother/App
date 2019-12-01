package com.jelly.wxtool.message.service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.R;
import com.jelly.wxtool.message.utils.AccessibilityUtil;

import java.util.List;

/**
 * 辅助服务自动安装APP，该服务在单独进程中允许
 */
public class AddFriendService5 extends AccessibilityService {
    private static final String TAG = "AddFriendService";
    public static boolean isServiceStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Button button;
    private PopupWindow popupWindow;
    private boolean isBegin = false;
    private boolean isAddStartSearch = false;
    private int addFriend = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getInstance().d(TAG, "onCreate");
        isServiceStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 150;
        layoutParams.height = 100;
        layoutParams.x = 300;
        layoutParams.y = 300;
        initPopWindow();
    }

    @Override
    protected void onServiceConnected() {
        LogUtil.getInstance().d(TAG, "onServiceConnected");
        isServiceStarted = true;
        isBegin = false;
        isAddStartSearch = false;
        showFloatingWindow();
    }

    @Override
    public void onDestroy() {
        LogUtil.getInstance().d(TAG, "onDestroy");
        isServiceStarted = false;
        isBegin = false;
        isAddStartSearch = false;
        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
            button.setText("Floating Window");
            button.setBackgroundColor(Color.BLUE);
            windowManager.addView(button, layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopWindow();
                    popupWindow.showAsDropDown(button);
                    LogUtil.getInstance().d(TAG, "点击悬浮按钮");
                }
            });
            button.setOnTouchListener(new View.OnTouchListener() {
                private int x;
                private int y;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = (int) event.getRawX();
                            y = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int nowX = (int) event.getRawX();
                            int nowY = (int) event.getRawY();
                            int movedX = nowX - x;
                            int movedY = nowY - y;
                            x = nowX;
                            y = nowY;
                            layoutParams.x = layoutParams.x + movedX;
                            layoutParams.y = layoutParams.y + movedY;
                            windowManager.updateViewLayout(view, layoutParams);
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private void initPopWindow() {
        if (popupWindow != null) {
            return;
        }
        LogUtil.getInstance().d(TAG, "initPopWindow");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.wxtool_pop_add_friend_service, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 实例化一个ColorDrawable颜色为半透明,不然点击外部不会消失
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        popupWindow.setBackgroundDrawable(dw);
        final Button butBegin = (Button) view.findViewById(R.id.but_begin);
        final Button butAddStart = (Button) view.findViewById(R.id.but_add_start_search);

        butBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow) {
                    if (isBegin) {
                        // 已经开始，就暂停
                        isBegin = false;
                        butBegin.setText("暂停");
                        ToastUtil.makeText("暂停：已经添加了" + addFriend + "个到通讯录");
                        addFriend = 0;
                        LogUtil.getInstance().d(TAG, "暂停");
                    } else {
                        addFriend = 0;
                        butBegin.setText("开始");
                        ToastUtil.makeText("开始添加");
                        LogUtil.getInstance().d(TAG, "开始");
                    }
                }
            }
        });
        butAddStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != popupWindow) {
                    if (isAddStartSearch) {
                        isAddStartSearch = false;
                        butAddStart.setText("从群聊天处开始添加");
                        ToastUtil.makeText("从群聊天处开始添加");
                        LogUtil.getInstance().d(TAG, "从群聊天处开始添加");
                    } else {
                        isAddStartSearch = true;
                        butAddStart.setText("从搜索人员处开始添加");
                        ToastUtil.makeText("从搜索人员处开始添加");
                        LogUtil.getInstance().d(TAG, "从搜索人员处开始添加");
                    }
                }
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event == null) {
                return;
            }
            AccessibilityNodeInfo eventNode = event.getSource();
            if (eventNode == null) {
                LogUtil.getInstance().d(TAG, "eventNode: null, 重新获取eventNode...");
                return;
            }
            AccessibilityNodeInfo rootNode = getRootInActiveWindow(); //当前窗口根节点
            if (rootNode == null) {
                return;
            }
            LogUtil.getInstance().d(TAG, "rootNode: " + rootNode);
        } catch (Exception e) {
            LogUtil.getInstance().d(TAG, "onAccessibilityEvent Exception: " + e.toString());
        }
    }

    // 查找安装,并模拟点击(findAccessibilityNodeInfosByText判断逻辑是contains而非equals)
    private void findTxtClick(AccessibilityNodeInfo nodeInfo, String txt) {
        List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(txt);
        if (nodes == null || nodes.isEmpty())
            return;
        LogUtil.getInstance().d(TAG, "findTxtClick: " + txt + ", " + nodes.size() + ", " + nodes);
        for (AccessibilityNodeInfo node : nodes) {
            if (node.isEnabled() && node.isClickable() && (node.getClassName().equals("android.widget.Button")
                    || node.getClassName().equals("android.widget.CheckBox") // 兼容华为安装界面的复选框
            )) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    // 排除广告[安装]按钮
    private boolean isNotAD(AccessibilityNodeInfo rootNode) {
        return isNotFind(rootNode, "还喜欢") //小米
                && isNotFind(rootNode, "官方安装"); //华为
    }

    private boolean isNotFind(AccessibilityNodeInfo rootNode, String txt) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(txt);
        return nodes == null || nodes.isEmpty();
    }

    @Override
    public void onInterrupt() {
    }
}
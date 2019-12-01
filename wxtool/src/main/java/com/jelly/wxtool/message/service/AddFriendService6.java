package com.jelly.wxtool.message.service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.R;
import com.jelly.wxtool.message.utils.AccessibilityUtil;

import java.util.List;

/**
 * 辅助服务自动安装APP，该服务在单独进程中允许
 */
public class AddFriendService6 extends AccessibilityService {
    private static final String TAG = "AddFriendService";
    public static boolean isServiceStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean isShow = false;
    private boolean isBegin = false;
    private boolean isAddStartSearch = false;
    private int addFriend = 0;
    private AccessibilityNodeInfo rootNode;

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
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 0;
        isShow = false;
        isBegin = false;
        isAddStartSearch = false;
    }

    @Override
    protected void onServiceConnected() {
        LogUtil.getInstance().d(TAG, "onServiceConnected");
        isServiceStarted = true;
        showFloatingWindow();
    }

    @Override
    public void onDestroy() {
        LogUtil.getInstance().d(TAG, "onDestroy");
        isServiceStarted = false;
        // 服务停止，重新进入系统设置界面
        AccessibilityUtil.jumpToSetting(this);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(getApplicationContext())) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.wxtool_pop_add_friend_service, null);
            final Button butShow = (Button) view.findViewById(R.id.but_show);
            final Button butBegin = (Button) view.findViewById(R.id.but_begin);
            final Button butAddStart = (Button) view.findViewById(R.id.but_add_start_search);
            butShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isShow) {
                        // 已经开始，就暂停
                        isShow = false;
                        butShow.setText("隐藏");
                        ToastUtil.makeText("隐藏");
                        LogUtil.getInstance().d(TAG, "显示");
                        butBegin.setVisibility(View.VISIBLE);
                        butAddStart.setVisibility(View.VISIBLE);
                    } else {
                        isShow = true;
                        butShow.setText("显示");
                        ToastUtil.makeText("显示");
                        LogUtil.getInstance().d(TAG, "隐藏");
                        butBegin.setVisibility(View.GONE);
                        butAddStart.setVisibility(View.GONE);
                    }
                }
            });
            butBegin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriend = 0;
                    if (isBegin) {
                        // 已经开始，就暂停
                        isBegin = false;
                        butBegin.setText("开始");
                        ToastUtil.makeText("暂停：已经添加了" + addFriend + "个到通讯录");
                        LogUtil.getInstance().d(TAG, "暂停");
                    } else {
                        isBegin = true;
                        butBegin.setText("暂停");
                        ToastUtil.makeText("开始添加");
                        LogUtil.getInstance().d(TAG, "开始");
                        addAllFriend();
                    }
                }
            });
            butAddStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAddStartSearch) {
                        isAddStartSearch = false;
                        butAddStart.setText("从搜索人员处开始添加");
                        ToastUtil.makeText("从群聊天处开始添加");
                        LogUtil.getInstance().d(TAG, "从群聊天处开始添加");
                    } else {
                        isAddStartSearch = true;
                        butAddStart.setText("从群聊天处开始添加");
                        ToastUtil.makeText("从搜索人员处开始添加");
                        LogUtil.getInstance().d(TAG, "从搜索人员处开始添加");
                    }
                }
            });
            windowManager.addView(view, layoutParams);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event == null) {
                return;
            }
            if(!event.getPackageName().toString().contains("com.tencent.mm")){
                return;
            }
            AccessibilityNodeInfo eventNode = event.getSource();
            if (eventNode == null) {
                LogUtil.getInstance().d(TAG, "eventNode: null, 重新获取eventNode...");
                return;
            }
            //当前窗口根节点
            rootNode = getRootInActiveWindow();
            if (rootNode == null) {
                return;
            }
            LogUtil.getInstance().d(TAG, "rootNode: " + rootNode);
            if(!isBegin){
                return;
            }
            if(!isNotFind(rootNode, "聊天信息")){
                // 群聊天界面
                findTxtClick(rootNode, "聊天信息");
            }
            if(!isNotFind(rootNode, "聊天信息")){
                // 群聊天界面
            }
            // 回收节点实例来重用
            eventNode.recycle();
            rootNode.recycle();
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
            if (node.isEnabled() && (node.getClassName().equals("android.widget.Button")
                    || node.getClassName().equals("android.widget.CheckBox") // 兼容华为安装界面的复选框
                    || node.getClassName().equals("android.widget.ImageButton")
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


    private void addAllFriend() {
        LogUtil.getInstance().d(TAG, "addFriend");
        if (isAddStartSearch) {
            addFriendWithGroupSearch();
        } else {
            addFriendWithGroupChat();
        }
    }

    private void addFriendWithGroupChat() {
        if (rootNode == null) {
            return;
        }
        LogUtil.getInstance().d(TAG, "addFriendWithGroupChat");
        findTxtClick(rootNode, "聊天信息");
        // 判断是否有--查看全部群成员
        if(isNotFind(rootNode, "查看全部群成员")){
            LogUtil.getInstance().d(TAG, "查看全部群成员");
        }else {
            addFriendWithGroupChat();
        }
    }

    private void addFriendWithGroupSearch() {
        LogUtil.getInstance().d(TAG, "addFriendWithGroupSearch");
    }

    private void addSingleFriend(){
        LogUtil.getInstance().d(TAG, "addSingleFriend");
    }
}
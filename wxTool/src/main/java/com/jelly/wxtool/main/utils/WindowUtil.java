package com.jelly.wxtool.main.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.wxtool.R;

public class WindowUtil {
    private static final String TAG = "WindowUtil";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context) {
        if (isShown) {
            LogUtil.getInstance().getInstance().i(TAG, "return cause already shown");
            return;
        }
        isShown = true;
        LogUtil.getInstance().i(TAG, "showPopupWindow");
        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        mWindowManager.addView(mView, params);
        LogUtil.getInstance().i(TAG, "add view");
    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        LogUtil.getInstance().i(TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            LogUtil.getInstance().i(TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }
    }

    private static View setUpView(final Context context) {
        LogUtil.getInstance().i(TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.wxtool_window_float, null);
        Button positiveBtn = (Button) view.findViewById(R.id.positiveBtn);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.getInstance().i(TAG, "ok on click");
                // 打开安装包
                // 隐藏弹窗
                hidePopupWindow();
            }
        });
        Button negativeBtn = (Button) view.findViewById(R.id.negativeBtn);
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.getInstance().i(TAG, "cancel on click");
                hidePopupWindow();
            }
        });
        // 点击窗口外部区域可消除
        // 这点的实现主要将悬浮窗设置为全屏大小，外层有个透明背景，中间一部分视为内容区域
        // 所以点击内容区域外部视为点击悬浮窗外部
        final View popupWindowView = view.findViewById(R.id.popup_window);// 非透明的内容区域
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.getInstance().i(TAG, "onTouch");
                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                popupWindowView.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    hidePopupWindow();
                }
                LogUtil.getInstance().i(TAG, "onTouch : " + x + ", " + y + ", rect: "
                        + rect);
                return false;
            }
        });
        // 点击back键可消除
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        hidePopupWindow();
                        return true;
                    default:
                        return false;
                }
            }
        });
        return view;
    }
}
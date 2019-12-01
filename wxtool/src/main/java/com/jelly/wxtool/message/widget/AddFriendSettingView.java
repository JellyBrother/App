package com.jelly.wxtool.message.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.utils.LogUtil;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.wxtool.R;

import static android.content.Context.WINDOW_SERVICE;

public class AddFriendSettingView extends View {
    private static final String TAG = "AddFriendSettingView";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public boolean isShow = false;
    public boolean isBegin = false;
    public boolean isAddStartSearch = false;
    private int addFriend = 0;
    private OnClickListener onClickListener;

    public AddFriendSettingView(Context context) {
        this(context, null, 0);
    }

    public AddFriendSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddFriendSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AddFriendSettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        windowManager = (WindowManager) BaseCommon.Base.application.getSystemService(WINDOW_SERVICE);
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

    public void showFloatingWindow() {
        if (Settings.canDrawOverlays(BaseCommon.Base.application)) {
            LayoutInflater inflater = LayoutInflater.from(BaseCommon.Base.application);
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
                        if (onClickListener != null) {
                            onClickListener.beginOnClick();
                        }
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

    public interface OnClickListener {
        void beginOnClick();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
 
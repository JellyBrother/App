package com.jelly.app.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jelly.app.R;
import com.jelly.app.main.utils.AppJumpUtil;
import com.jelly.baselibrary.common.BaseCommon;

public class AppMainActivity extends Activity {
    private static final String TAG = "AppMainActivity";
    private TextView mTvJumpChat;
    private TextView mTvJumpWxTool;
    private TextView mTvJumpOtherTool;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_act_main);
        initViews();
        initListeners();
    }

    private void initViews() {
        mActivity = this;
        mTvJumpChat = findViewById(R.id.tv_jump_chat);
        mTvJumpWxTool = findViewById(R.id.tv_jump_wx_tool);
        mTvJumpOtherTool = findViewById(R.id.tv_jump_other_tool);
        if (BaseCommon.Base.enableChat) {
            mTvJumpChat.setVisibility(View.VISIBLE);
        }
        if (BaseCommon.Base.enableWxTool) {
            mTvJumpWxTool.setVisibility(View.VISIBLE);
        }
        if (BaseCommon.Base.enableOtherTool) {
            mTvJumpOtherTool.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        mTvJumpChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppJumpUtil.startChatMainActivity(mActivity);
            }
        });
        mTvJumpWxTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppJumpUtil.startWxToolMainActivity(mActivity);
            }
        });
        mTvJumpOtherTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppJumpUtil.startOtherToolMainActivity(mActivity);
            }
        });
    }
}

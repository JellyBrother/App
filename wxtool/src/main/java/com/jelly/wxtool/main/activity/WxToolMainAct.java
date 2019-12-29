package com.jelly.wxtool.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.wxtool.R;
import com.jelly.wxtool.main.common.WxToolCommon;
import com.jelly.wxtool.main.utils.WxToolJumpUtil;

public class WxToolMainAct extends BaseActivity {
    private static final String TAG = "WxToolMainAct";
    private TextView mTvJumpChat;
    private TextView mTvJumpSearch;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.wxtool_act_main);
        initViews();
        initListeners();
    }

    private void initViews() {
        mActivity = this;
        mTvJumpChat = findViewById(R.id.tv_jump_chat);
        mTvJumpSearch = findViewById(R.id.tv_jump_search);
        if (WxToolCommon.WxTool.enableChat) {
            mTvJumpChat.setVisibility(View.VISIBLE);
        }
        if (WxToolCommon.WxTool.enableSearch) {
            mTvJumpSearch.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        mTvJumpChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxToolJumpUtil.startWxTestActivity(mActivity);
            }
        });
        mTvJumpSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WxToolJumpUtil.startAddBySearchAct(mActivity);
            }
        });
    }
}

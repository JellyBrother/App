package com.jelly.app.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.jelly.app.R;
import com.jelly.app.main.head.HeadConstant;
import com.jelly.app.main.head.HeadFactory;
import com.jelly.app.main.head.HeadType;
import com.jelly.app.main.head.data.HttpHead;
import com.jelly.app.main.head.utils.SaveUtil;
import com.jelly.app.main.security.EncryptionUtils;
import com.jelly.app.main.utils.AppJumpUtil;
import com.jelly.app.main.utils.AppUtil;
import com.jelly.app.main.view.QuestionDialog;
import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.common.BaseCommon;
import com.jelly.baselibrary.thread.ThreadPoolExecutorImpl;
import com.jelly.baselibrary.utils.LogUtil;

public class AppMainAct extends BaseActivity {
    private static final String TAG = "AppMainAct";
    private TextView mTvJumpChat;
    private TextView mTvJumpWxTool;
    private TextView mTvJumpOtherTool;
    private TextView mTvWechatBusinessTool;
    private TextView mTvText;
    private Activity mActivity;

    @Override
    protected void initView(Bundle savedInstanceState) {
//        mTvText = new TextView(this);
//        mTvText.setText("111");
//        setContentView(mTvText);
        setContentView(R.layout.app_act_main);
        initViews();
        initListeners();
//        initData();
    }

    private void initViews() {
        mActivity = this;
        mTvJumpChat = findViewById(R.id.tv_jump_chat);
        mTvJumpWxTool = findViewById(R.id.tv_jump_wx_tool);
        mTvJumpOtherTool = findViewById(R.id.tv_jump_other_tool);
        mTvWechatBusinessTool = findViewById(R.id.tv_jump_WechatBusinessTool);
//        mTvText = findViewById(R.id.tv_text);
        if (BaseCommon.Base.enableChat) {
            mTvJumpChat.setVisibility(View.VISIBLE);
        }
        if (BaseCommon.Base.enableWxTool) {
            mTvJumpWxTool.setVisibility(View.VISIBLE);
        }
        if (BaseCommon.Base.enableOtherTool) {
            mTvJumpOtherTool.setVisibility(View.VISIBLE);
        }
        if (BaseCommon.Base.enableWechatBusinessTool) {
            mTvWechatBusinessTool.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        mTvJumpChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
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
        mTvWechatBusinessTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppJumpUtil.startWechatBusinessToolMainAct(mActivity);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initData();
    }

    private void initData() {
        new Handler(Looper.myLooper());
        Looper.myLooper().quit();
        ThreadPoolExecutorImpl.getInstance().executeBgTask(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Looper.prepare();
                QuestionDialog questionDialog = new QuestionDialog(AppMainAct.this);
                questionDialog.show("2222222");

//                mTvText = findViewById(R.id.tv_text);
//                mTvText.setText("222222222222222222");

                Looper.loop();
            }
        });

//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
////                mTvText = findViewById(R.id.tv_text);
//                mTvText.setText("222222222222222222");
//            }
//        }.start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                mTvText = findViewById(R.id.tv_text);
//                mTvText.setText("222222222222222222");
//            }
//        }).run();
    }
}

package com.jelly.othertool.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jelly.baselibrary.base.BaseLifecycleActivity;
import com.jelly.baselibrary.thread.ThreadPoolExecutorImpl;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.othertool.R;
import com.jelly.othertool.main.utils.OtherToolJumpUtil;
import com.jelly.othertool.main.viewmodel.OtherToolMainActViewModel;

public class OtherToolMainAct extends BaseLifecycleActivity<OtherToolMainActViewModel> {
    private static final String TAG = "OtherToolMainAct";
    private TextView mTvCopy;
    private TextView mTvJumpPullList;
    private TextView mTvJumpColumn;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.othertool_act_main);
        initViews();
        initListeners();
    }

    @Override
    protected OtherToolMainActViewModel initViewModel() {
        return new OtherToolMainActViewModel();
    }

    @Override
    protected void observerData() {
        mViewModel.mToastText.observe(new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ToastUtil.makeText(s.toString());
            }
        });
    }

    private void initViews() {
        mTvCopy = findViewById(R.id.tv_copy);
        mTvJumpPullList = findViewById(R.id.tv_jump_pull_list);
        mTvJumpColumn = findViewById(R.id.tv_jump_column);
    }

    private void initListeners() {
        mTvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadPoolExecutorImpl.getInstance().executeBgTask(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.copyFilesFassets(mActivity, "data", getApplication().getExternalCacheDir().getPath());
                    }
                });
            }
        });
        mTvJumpPullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherToolJumpUtil.startSearchListAct(mActivity);
            }
        });
        mTvJumpColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherToolJumpUtil.startColumnMainAct(mActivity);
            }
        });
    }
}

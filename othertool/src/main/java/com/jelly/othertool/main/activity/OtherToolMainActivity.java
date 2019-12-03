package com.jelly.othertool.main.activity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseActivity;
import com.jelly.baselibrary.thread.ThreadPoolExecutorImpl;
import com.jelly.baselibrary.utils.ToastUtil;
import com.jelly.othertool.R;
import com.jelly.othertool.main.viewmodel.OtherToolMainViewModel;

public class OtherToolMainActivity extends BaseActivity<OtherToolMainViewModel> {
    private static final String TAG = "OtherToolMainActivity";
    private TextView mTvCopy;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.othertool_act_main);
        initViews();
        initListeners();
    }

    @Override
    protected OtherToolMainViewModel initViewModel() {
        return new OtherToolMainViewModel();
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
    }
}

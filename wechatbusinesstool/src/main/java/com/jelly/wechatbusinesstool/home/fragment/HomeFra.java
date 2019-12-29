package com.jelly.wechatbusinesstool.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jelly.baselibrary.base.BaseFragment;
import com.jelly.wechatbusinesstool.R;

public class HomeFra extends BaseFragment {
    private static final String TAG = "HomeFra";
    private TextView mTvDesc;

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.wbt_fra_main, container, false);
        } else if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;

//        if (mRootView == null) {
//            Context themeContext = new ContextThemeWrapper(AppEnvironment.getEnvironment().getApplicationContext(),
//                    ThemeUtils.getCurrentTheme());
//            LayoutInflater themeInflater = inflater.cloneInContext(themeContext);
//            mRootView = themeInflater.inflate(R.layout.knowledge_fragment_browser_list, container, false);
//        } else if (mRootView.getParent() != null) {
//            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
//        }
//        return mRootView;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        initViews(rootView);
        initListeners();
    }

    private void initViews(View rootView) {
        mTvDesc = rootView.findViewById(R.id.tv_desc);
        mTvDesc.setText(R.string.wbt_home_fra_name);
    }

    private void initListeners() {

    }
}

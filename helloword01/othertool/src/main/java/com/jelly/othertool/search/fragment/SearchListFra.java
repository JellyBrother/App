package com.jelly.othertool.search.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelly.baselibrary.base.BaseFragment;
import com.jelly.othertool.R;

public class SearchListFra extends BaseFragment {

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.othertool_act_main, container, false);
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

    }
}

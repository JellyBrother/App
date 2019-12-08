package com.jelly.chat.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelly.baselibrary.base.BaseFragment;
import com.jelly.chat.R;

public class FriendFra extends BaseFragment {

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fra_test, container, false);
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

    }
}

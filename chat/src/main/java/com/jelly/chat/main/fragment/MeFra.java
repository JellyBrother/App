package com.jelly.chat.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelly.baselibrary.base.BaseLifecycleFragment;
import com.jelly.chat.R;
import com.jelly.chat.main.viewmodel.MeFraViewModel;

public class MeFra extends BaseLifecycleFragment<MeFraViewModel> {

    @Override
    protected View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fra_test, container, false);
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

    }

    @Override
    protected MeFraViewModel initViewModel() {
        return new MeFraViewModel();
    }

    @Override
    protected void observerData() {

    }
}

package com.example.myapp.home.ui.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.myapp.base.bridge.constant.ARouterConstant;
import com.example.myapp.base.widget.status.BaseListFragment;
import com.example.myapp.home.databinding.HomeFraMainBinding;
import com.example.myapp.home.viewmodel.HomeFragmentVm;

import org.greenrobot.eventbus.EventBus;

/**
 * 首页
 */
@Route(path = ARouterConstant.Home.HOME_FRAGMENT)
public class HomeFragment extends BaseListFragment<HomeFragmentVm> {
    private HomeFraMainBinding mBinding;

    public HomeFragment() {
    }

    @Override
    protected View getLayoutView() {
        mBinding = HomeFraMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initData() {
        super.initData();
        startRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void startRequest() {
        super.startRequest();
    }

    @Override
    protected void startRequestNoNet() {
        super.startRequestNoNet();
    }
}

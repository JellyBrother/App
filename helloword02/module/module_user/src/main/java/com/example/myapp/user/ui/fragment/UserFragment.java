package com.example.myapp.user.ui.fragment;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.myapp.base.bridge.constant.ARouterConstant;
import com.example.myapp.base.ui.fragment.BaseFragment;
import com.example.myapp.user.databinding.UserFraMainBinding;

@Route(path = ARouterConstant.User.USER_FRAGMENT)
public class UserFragment extends BaseFragment {
    private UserFraMainBinding mBinding;

    public UserFragment() {
    }

    @Override
    protected View getLayoutView() {
        mBinding = UserFraMainBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }
}

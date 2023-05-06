package com.jelly.jetpack

import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.jelly.jetpack.databinding.AppActSplashBinding
import com.jelly.jetpack.module.base.constant.ARouterConstant
import com.jelly.jetpack.module.base.ui.BaseActivity

class SplashActivity : BaseActivity() {
    private lateinit var mBinding: AppActSplashBinding

    override fun getLayoutView(): View {
        mBinding = AppActSplashBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initListener() {
        super.initListener()
        ARouter.getInstance().build(ARouterConstant.Main.MAIN).navigation()
    }
}
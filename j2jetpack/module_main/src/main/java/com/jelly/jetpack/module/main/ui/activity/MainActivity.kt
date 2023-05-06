package com.jelly.jetpack.module.main.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.jelly.jetpack.module.base.constant.ARouterConstant
import com.jelly.jetpack.module.base.ui.BaseActivity
import com.jelly.jetpack.module.base.utils.ToastUtil
import com.jelly.jetpack.module.main.databinding.MainActMainBinding

@Route(path = ARouterConstant.Main.MAIN)
class MainActivity : BaseActivity() {
    private lateinit var mBinding: MainActMainBinding

    override fun getLayoutView(): View {
        mBinding = MainActMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvwTest1.setOnClickListener {
            ToastUtil.showShort("tvwTest1")
        }
    }
}
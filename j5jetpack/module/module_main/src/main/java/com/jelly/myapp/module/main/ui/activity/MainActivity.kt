package com.jelly.myapp.module.main.ui.activity

import android.content.Intent
import android.view.View
import com.jelly.myapp.base.ui.BaseActivity
import com.jelly.myapp.module.main.databinding.MainActMainBinding

class MainActivity : BaseActivity() {
    private lateinit var mBinding: MainActMainBinding

    override fun getLayoutView(): View {
        mBinding = MainActMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvwTest1.setOnClickListener {
            startActivity(Intent(it.context, LocationActivity::class.java))
        }
    }
}
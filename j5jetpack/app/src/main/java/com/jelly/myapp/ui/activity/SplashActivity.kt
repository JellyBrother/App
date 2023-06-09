package com.jelly.myapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.jelly.myapp.base.ui.BaseActivity
import com.jelly.myapp.databinding.AppActSplashBinding
import com.jelly.myapp.module.main.ui.activity.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var mBinding: AppActSplashBinding

    override fun getLayoutView(): View {
        mBinding = AppActSplashBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        super.initView()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
package com.example.myapp.main.ui.activity

import android.view.KeyEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.myapp.base.bridge.constant.ARouterConstant
import com.example.myapp.base.ui.activity.BaseActivity
import com.example.myapp.base.ui.adapter.BaseFragmentsAdapter
import com.example.myapp.base.ui.fragment.BaseFragment
import com.example.myapp.main.constant.MainConstant
import com.example.myapp.main.databinding.MainActMainBinding
import com.gyf.immersionbar.ImmersionBar

/**
 * 主界面
 */
@Route(path = ARouterConstant.Main.MAIN)
class MainActivity : BaseActivity() {
    private lateinit var mBinding: MainActMainBinding

    override fun getLayoutView(): View {
        mBinding = MainActMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    private val mOnPageChangeCallback: OnPageChangeCallback by lazy {
        object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setNavigationStatus(position)
            }
        }
    }

    override fun initView() {
        super.initView()
        //实现状态栏图标和文字颜色为暗色
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        mBinding.viewPager.isUserInputEnabled = false
//        H5JumpUtil.jumpBySplashUri()

        ImmersionBar.with(this@MainActivity).navigationBarColor(android.R.color.white)
            .statusBarDarkFont(true) // 状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(true) // 导航栏图标是深色，不写默认为亮色
            .init()
        mBinding.viewPager.registerOnPageChangeCallback(mOnPageChangeCallback)
    }

    override fun initListener() {
        super.initListener()
        mBinding.lltHome.setOnClickListener { v: View? ->
            mBinding.viewPager.currentItem = MainConstant.Index.NAVIGATION_POSITION_HOME
        }
        mBinding.lltCommunity.setOnClickListener { v: View? ->
            if ((mBinding.viewPager.adapter?.itemCount
                    ?: 0) > MainConstant.Index.NAVIGATION_POSITION_COMMUNITY
            ) {
                mBinding.viewPager.currentItem = MainConstant.Index.NAVIGATION_POSITION_COMMUNITY
            }
        }
        mBinding.lltUser.setOnClickListener { v: View? ->
            if ((mBinding.viewPager.adapter?.itemCount
                    ?: 0) > MainConstant.Index.NAVIGATION_POSITION_USER
            ) {
                mBinding.viewPager.currentItem = MainConstant.Index.NAVIGATION_POSITION_USER
            }
        }
    }

    override fun initData() {
        super.initData()
        // 首页 主模块的跳转不能用ARouter，因为ARouter初始化耗时，放在了子线程，如果用ARouter的跳转的话，可能导致ARouter没初始化完成
        val homeFragment =
            ARouter.getInstance().build(ARouterConstant.Home.HOME_FRAGMENT).navigation()
        // 社区
        val communityFragment =
            ARouter.getInstance().build(ARouterConstant.Home.HOME_FRAGMENT).navigation()
        // 用户
        val userFragment =
            ARouter.getInstance().build(ARouterConstant.User.USER_FRAGMENT).navigation()

        val mFragments: MutableList<BaseFragment> = ArrayList()
        if (homeFragment != null) {
            mFragments.add(homeFragment as BaseFragment)
        }
        if (communityFragment != null) {
            mFragments.add(communityFragment as BaseFragment)
        }
        if (userFragment != null) {
            mFragments.add(userFragment as BaseFragment)
        }
        //实例化适配器
        val mainAdapter = BaseFragmentsAdapter(supportFragmentManager, lifecycle)
        mainAdapter.setList(mFragments)
        //设置适配器
        mBinding.viewPager.adapter = mainAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewPager.unregisterOnPageChangeCallback(mOnPageChangeCallback)
    }

    private fun setNavigationStatus(position: Int) {
        if (position == MainConstant.Index.NAVIGATION_POSITION_HOME) {
            setHomeChecked()
            setCommunityUnChecked()
            setUserUnChecked()
            ImmersionBar.with(this).statusBarDarkFont(true) // 状态栏字体是深色，不写默认为亮色
                .init()
            return
        }
        if (position == MainConstant.Index.NAVIGATION_POSITION_COMMUNITY) {
            setHomeUnChecked()
            setCommunityChecked()
            setUserUnChecked()
            ImmersionBar.with(this).statusBarDarkFont(true) // 状态栏字体是深色，不写默认为亮色
                .init()
            return
        }
        if (position == MainConstant.Index.NAVIGATION_POSITION_USER) {
            setHomeUnChecked()
            setCommunityUnChecked()
            setUserChecked()
            ImmersionBar.with(this).statusBarDarkFont(true).init()
        }
    }

    private fun setHomeChecked() {
//        mBinding.ivwHome.setImageResource(R.drawable.main_navigation_home_checked)
//        mBinding.tvwHome.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_checked
//            )
//        )
    }

    private fun setHomeUnChecked() {
//        mBinding.ivwHome.setImageResource(R.drawable.main_navigation_home_unchecked)
//        mBinding.tvwHome.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_unchecked
//            )
//        )
    }

    private fun setCommunityChecked() {
//        mBinding.ivwCommunity.setImageResource(R.drawable.main_navigation_community_checked)
//        mBinding.tvwCommunity.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_checked
//            )
//        )
    }

    private fun setCommunityUnChecked() {
//        mBinding.ivwCommunity.setImageResource(R.drawable.main_navigation_community_unchecked)
//        mBinding.tvwCommunity.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_unchecked
//            )
//        )
    }

    private fun setUserChecked() {
//        mBinding.ivwUser.setImageResource(R.drawable.main_navigation_user_checked)
//        mBinding.tvwUser.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_checked
//            )
//        )
    }

    private fun setUserUnChecked() {
//        mBinding.ivwUser.setImageResource(R.drawable.main_navigation_user_unchecked)
//        mBinding.tvwUser.setTextColor(
//            ContextCompat.getColor(
//                this, R.color.main_navigation_text_unchecked
//            )
//        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (ClickUtils.isDupClick(
//                    "main_MainActivity_onKeyDown", ClickUtils.INTERVAL_TIME_1000
//                )
//            ) {
            moveTaskToBack(false)
//            } else {
//                ToastHelper.showShort("再点击一次，退出app")
//            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
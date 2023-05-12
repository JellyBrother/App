package com.jelly.hook.module.main.ui.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jelly.hook.module.base.constant.ARouterConstant
import com.jelly.hook.module.base.ui.BaseActivity
import com.jelly.hook.module.base.ui.BaseStaggeredGridLayoutManager
import com.jelly.hook.module.main.data.MainItem
import com.jelly.hook.module.main.databinding.MainActMainBinding
import com.jelly.hook.module.main.ui.adapter.MainAdapter

@Route(path = ARouterConstant.Main.MAIN)
class MainActivity : BaseActivity() {
    private lateinit var mBinding: MainActMainBinding

    override fun getLayoutView(): View {
        mBinding = MainActMainBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        super.initView()
        val adapter = MainAdapter()
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager = BaseStaggeredGridLayoutManager(
            2, LinearLayoutManager.VERTICAL
        )
        adapter.setList(
            mutableListOf<MainItem>(
                MainItem("java的动态代理") {
                    itemClick(HookActivity::class.java)
                },
                MainItem("画中画") {
                    itemClick(PictureInPictureActivity::class.java)
                },
            )
        )
        adapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View?, position: Int ->
            adapter.data[position].also {
                it.onClickListener.onClick(view)
            }
        }
    }

    private fun itemClick(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
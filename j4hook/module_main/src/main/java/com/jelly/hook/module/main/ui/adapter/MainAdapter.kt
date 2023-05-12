package com.jelly.hook.module.main.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jelly.hook.module.main.R
import com.jelly.hook.module.main.data.MainItem

class MainAdapter : BaseQuickAdapter<MainItem, BaseViewHolder>(R.layout.main_ada_main) {

    override fun convert(holder: BaseViewHolder, item: MainItem) {
        holder.setText(R.id.title, item.text)
    }
}
package com.example.myapp.base.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.myapp.base.ui.fragment.BaseFragment
import com.example.myapp.base.utils.LogUtils

class BaseFragmentsAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val fragments: MutableList<BaseFragment> by lazy { mutableListOf() }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: List<Any?>) {
        try {
            // 防止IllegalStateException
            super.onBindViewHolder(holder, position, payloads)
        } catch (t: Throwable) {
            LogUtils.e(t)
        }
    }

    fun setList(fragments: List<BaseFragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): BaseFragment? {
        if (position in 0..itemCount) {
            return this.fragments[position]
        }
        return null
    }
}
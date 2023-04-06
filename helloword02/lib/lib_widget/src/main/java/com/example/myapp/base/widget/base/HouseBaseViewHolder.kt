package com.example.myapp.base.widget.base

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.myapp.base.utils.Utils

@Keep
open class HouseBaseViewHolder(view: View) : BaseViewHolder(view) {
    protected var TAG: String = "HouseBaseViewHolder"
    var activity: FragmentActivity? = null
    var rootView: View? = null
    var res: Resources? = null

    constructor(
        viewGroup: ViewGroup,
        @LayoutRes resId: Int
    ) : this(LayoutInflater.from(viewGroup.context).inflate(resId, viewGroup, false))

    init {
        TAG = javaClass.simpleName
        activity = Utils.getActivity(view.context) as FragmentActivity?
        res = activity?.resources
        rootView = itemView
    }

    open fun getLayoutPosition2(): Int {
        return layoutPosition
    }

    open fun getAdapterPosition2(): Int {
        return adapterPosition
    }

    open fun getItemViewType2(): Int {
        return itemViewType
    }
}
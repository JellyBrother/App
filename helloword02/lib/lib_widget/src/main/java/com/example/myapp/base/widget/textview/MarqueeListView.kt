package com.example.myapp.base.widget.textview

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.widget.ViewFlipper

class MarqueeListView : ViewFlipper {

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun initView(context: Context) {
        visibility = GONE
        stopFlipping()
        // 设置轮播时长
        flipInterval = 4000
//        inAnimation =
//            AnimationUtils.loadAnimation(getContext(), R.anim.base_marquee_in)
//        outAnimation =
//            AnimationUtils.loadAnimation(getContext(), R.anim.base_marquee_out)
    }

//    fun setList(texts: List<BannerEntity>?) {
//        stopFlipping()
//        removeAllViews()
//        if (ListUtil.isEmpty(texts)) {
//            visibility = GONE
//            return
//        }
//        visibility = VISIBLE
//        for (s in texts!!) {
//            val textView = LayoutInflater.from(context)
//                .inflate(R.layout.base_marquee_item, null, false) as TextView
//            textView.text = s.title
//            addView(textView)
//            textView.singleClick {
//                H5JumpUtil.jumpByUri(s.link)
//            }
//        }
//        if (texts.size > 1) {
//            // 开始轮播
//            startFlipping()
//        }
//    }
}
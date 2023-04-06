package com.example.myapp.base.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.base.utils.KeyboardUtils

class AutoHideKeyboardRecyclerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(
    context!!, attrs, defStyle
) {
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action != MotionEvent.ACTION_CANCEL) {
            KeyboardUtils.hideSoftInput(this)
        }
        return super.onTouchEvent(e)
    }

    init {
        layoutManager = LinearLayoutManager(context)
    }
}
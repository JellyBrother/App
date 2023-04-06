package com.example.myapp.base.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat


/**
 * 自定义带清除功能的EditText
 */
class ClearEditText : AppCompatEditText, OnFocusChangeListener {

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(
        context, attrs,
        androidx.appcompat.R.attr.editTextStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    /** 删除图标  */
    private var mClearDrawable: Drawable? = null

    /** 是否有焦点  */
    private var mFocus = false
    private fun init() {
        mClearDrawable = compoundDrawables[2]
        //如果EditText右边的删除图标为null则设置默认图标
        if (mClearDrawable == null) {
//            mClearDrawable = ContextCompat.getDrawable(context, R.drawable.base_delete)
        }
        mClearDrawable!!.setBounds(
            0,
            0,
            mClearDrawable!!.intrinsicWidth,
            mClearDrawable!!.intrinsicHeight
        )
        setClearIconVisible(false)
        onFocusChangeListener = this
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                setClearIconVisible(charSequence.isNotEmpty())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    /** 设置清除按钮是否可见  */
    private fun setClearIconVisible(visible: Boolean) {
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(
            compoundDrawables[0], compoundDrawables[1], right,
            compoundDrawables[3]
        )
    }

    /**
     * 如果有焦点并且输入内容长度 > 0 则删除按钮显示，否则隐藏
     * @param view EditText
     * @param focus 是否聚焦
     */
    override fun onFocusChange(view: View, focus: Boolean) {
        mFocus = focus
        setClearIconVisible(mFocus && text!!.isNotEmpty())
    }

    /**
     * 由于不能直接给EditText设置点击事件，所以可以根据点击的位置来处理点击响应
     * (editText的宽度 - 删除图标到控件右边的距离 - 图标宽度) 与 (editText的宽度 - 删除图标到控件右边的距离)之间
     * @param event 事件
     * @return 事件处理
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {
                val touchable =
                    event.x > width - totalPaddingRight && event.x < width - paddingRight
                if (touchable) {
                    setText("") //清空 相当于点击删除图标清除文字处理
                }
            }
        }
        return super.onTouchEvent(event)
    }
}

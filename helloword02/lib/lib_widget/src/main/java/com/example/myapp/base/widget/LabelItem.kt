package com.example.myapp.base.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.myapp.base.widget.ext.drawableEnd
import com.example.myapp.base.widget.ext.drawableStart
import com.example.myapp.base.widget.databinding.BaseLabelItemBinding

class LabelItem : LinearLayout {
    private lateinit var binding: BaseLabelItemBinding

    //左侧图片
    private var labelIconId: Int = -1

    //Label文本
    private var labelText: String? = null

    //Label文本颜色资源Id
    private var labelTextColorId: Int = -1

    //Label文本字体大小
    private var labelTextSize: Float = -1f

    //右侧文本
    private var contentText: String? = null

    //右侧文本文本颜色资源Id
    private var contentTextColorId: Int = -1

    //右侧文本文本字体大小
    private var contentTextSize: Float = -1f

    //右侧图片
    private var contentIconId: Int = -1

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelItem)

        labelIconId = typedArray.getResourceId(R.styleable.LabelItem_labelIconId, -1)
        labelText = typedArray.getString(R.styleable.LabelItem_labelText)
        labelTextColorId = typedArray.getResourceId(R.styleable.LabelItem_labelTextColor, -1)
        labelTextSize = typedArray.getDimension(R.styleable.LabelItem_labelTextSize, -1f)
        contentText = typedArray.getString(R.styleable.LabelItem_contentText)
        contentIconId = typedArray.getResourceId(R.styleable.LabelItem_contentIconId, -1)
        contentTextColorId = typedArray.getResourceId(R.styleable.LabelItem_contentTextColor, -1)
        contentTextSize = typedArray.getDimension(R.styleable.LabelItem_contentTextSize, -1f)

        initView()

        typedArray.recycle()
    }

    private fun initView() {
        orientation = HORIZONTAL

        binding = if (context is Activity) {
            BaseLabelItemBinding.inflate((context as Activity).layoutInflater, this)
        } else {
            BaseLabelItemBinding.inflate(LayoutInflater.from(context), this)
        }

        //左侧图片不为空，设置值
        if (labelIconId != -1) {
            binding.baseLabelText.drawableStart(labelIconId)
        }

        //左侧Label不为空，设置值
        labelText?.let {
            binding.baseLabelText.text = it
        }

        if (labelTextColorId != -1) {
            binding.baseLabelText.setTextColor(ContextCompat.getColor(context, labelTextColorId))
        }

        if (labelTextSize != -1f) {
            binding.baseLabelText.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize)
        }

        //右侧文本不为空，设置值
        contentText?.let {
            binding.baseRightText.text = it
        }

        if (contentTextColorId != -1) {
            binding.baseRightText.setTextColor(ContextCompat.getColor(context, contentTextColorId))
        }

        if (contentTextSize != -1f) {
            binding.baseRightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize)
        }

        //右侧图片不为-1，设置值
        if (contentIconId != -1) {
            binding.baseRightText.drawableEnd(contentIconId)
        }
    }

    fun setContentText(contentText: String?) {
        binding.baseRightText.text = contentText
    }

    fun removeRightIcon() {
        binding.baseRightText.drawableEnd(null)
    }

    fun setRightIcon(@DrawableRes drawableId: Int) {
        binding.baseRightText.drawableEnd(drawableId)
    }
}
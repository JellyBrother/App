package com.example.myapp.base.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.myapp.base.widget.ext.drawableEnd
import com.example.myapp.base.widget.ext.setTintDrawable
import com.example.myapp.base.widget.ext.singleClick
import com.example.myapp.base.widget.databinding.BaseToolbarLayoutBinding

class HouseToolbar : Toolbar {
    private lateinit var binding: BaseToolbarLayoutBinding

    //左侧View的marginStart
    private var leftMarginStart: Int = -1

    //是否隐藏"返回"图标
    private var isShowBack = false

    //右侧图片
    private var backIconId: Int = -1

    //"返回"图标颜色资源Id
    private var backTintColorId: Int = -1

    //Title文字
    private var titleContentText: String? = null

    //Title文本颜色资源Id
    private var titleTextColorId: Int = -1

    //右侧文字
    private var rightText: String? = null

    //右侧View文本颜色资源Id
    private var rightTextColorId: Int = -1

    //右侧View文本字体大小
    private var rightTextSize: Float = -1f

    //右侧图片
    private var rightIconId: Int = -1

    //右侧View的marginEnd
    private var rightMarginEnd: Int = -1

    //左侧返回键点击事件
    private var leftOnClick: ((v: View) -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HouseToolbar)

        leftMarginStart =
            typedArray.getDimensionPixelOffset(R.styleable.HouseToolbar_leftViewMarginStart, -1)
        isShowBack = typedArray.getBoolean(R.styleable.HouseToolbar_isShowBack, true)
        backIconId = typedArray.getResourceId(R.styleable.HouseToolbar_backIconId, -1)
        backTintColorId = typedArray.getResourceId(R.styleable.HouseToolbar_backTintColorId, -1)
        titleContentText = typedArray.getString(R.styleable.HouseToolbar_titleContentText)
        titleTextColorId = typedArray.getResourceId(R.styleable.HouseToolbar_titleTextColorId, -1)
        rightText = typedArray.getString(R.styleable.HouseToolbar_rightText)
        rightTextColorId = typedArray.getResourceId(R.styleable.HouseToolbar_rightTextColor, -1)
        rightTextSize = typedArray.getDimension(R.styleable.HouseToolbar_rightTextSize, -1f)
        rightIconId = typedArray.getResourceId(R.styleable.HouseToolbar_rightIconId, -1)
        rightMarginEnd =
            typedArray.getDimensionPixelOffset(R.styleable.HouseToolbar_rightViewMarginEnd, -1)

        initView()

        typedArray.recycle()
    }

    private fun initView() {
        binding = if (context is Activity) {
            BaseToolbarLayoutBinding.inflate((context as Activity).layoutInflater, this)
        } else {
            BaseToolbarLayoutBinding.inflate(LayoutInflater.from(context), this)
        }
        binding.baseTitleBack.visibility = if (isShowBack) View.VISIBLE else View.GONE

        //右侧图片不为-1，设置值
        if (backIconId != -1) {
            binding.baseTitleBack.setImageResource(backIconId)
        }

        if (backTintColorId != -1) {
            setLeftTintColorId(backTintColorId)
        }

        if (leftMarginStart != -1) {
            (binding.baseTitleBack.layoutParams as MarginLayoutParams).marginStart = leftMarginStart
        }

        //标题不为空，设置值
        titleContentText?.let {
            binding.baseTitleContent.text = it
        }

        if (titleTextColorId != -1) {
            setTitleTextColorId(titleTextColorId)
        }

        //右侧文字不为空，设置值
        rightText?.let {
            binding.baseTitleRight.text = it
        }

        if (rightTextColorId != -1) {
            binding.baseTitleRight.setTextColor(ContextCompat.getColor(context, rightTextColorId))
        }

        if (rightTextSize != -1f) {
            binding.baseTitleRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize)
        }

        //右侧图片不为-1，设置值
        if (rightIconId != -1) {
            setRightIconId(rightIconId)
        }

        if (rightMarginEnd != -1) {
            (binding.baseTitleRight.layoutParams as MarginLayoutParams).marginEnd = rightMarginEnd
        }

        //返回图标默认实现（关闭Activity）
        binding.baseTitleBack.singleClick {
            if (leftOnClick == null) {
                (context as? Activity)?.onBackPressed()
                return@singleClick
            }
            leftOnClick!!.invoke(it)
        }
    }

    /**
     * 获取左侧视图
     */
    fun getLeftView(): ImageView {
        return binding.baseTitleBack
    }

    /**
     * 左侧图标颜色
     */
    fun setLeftTintColorId(backTintColorId: Int) {
        if (backTintColorId > -1) {
            this.backTintColorId = backTintColorId
            binding.baseTitleBack.setTintDrawable(binding.baseTitleBack.drawable, backTintColorId)
        }
    }

    /**
     * 获取中间视图
     */
    fun getContentView(): TextView {
        return binding.baseTitleContent
    }

    /**
     * 获取右侧文本View
     */
    fun getRightView(): TextView {
        return binding.baseTitleRight
    }

    /**
     * 设置中间标题
     */
    fun setTitleContent(title: String?) {
        binding.baseTitleContent.text = title
    }

    /**
     * 设置中间标题颜色
     */
    fun setTitleTextColorId(titleTextColorId: Int) {
        this.titleTextColorId = titleTextColorId
        binding.baseTitleContent.setTextColor(ContextCompat.getColor(context, titleTextColorId))
    }

    /**
     * 右侧文本View点击事件
     */
    fun setRightClickListener(onClick: (v: View) -> Unit) {
        binding.baseTitleRight.singleClick {
            onClick(it)
        }
    }

    /**
     * 右侧文本View点击事件
     */
    fun setLeftClickListener(leftOnClick: (v: View) -> Unit) {
        this.leftOnClick = leftOnClick
    }

    /**
     * 右侧图标
     */
    fun setRightIconId(rightIconId: Int) {
        if (rightIconId > -1) {
            this.rightIconId = rightIconId
            binding.baseTitleRight.drawableEnd(rightIconId)
        }
    }

    fun addCustomLayout() {
        addView(TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(100,100)
            text = "fasdjfljasdlkfjalksdjf"
        })
    }
}
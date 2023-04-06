package com.example.myapp.base.widget

import android.content.Context
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.myapp.base.widget.ext.setTintDrawable
import kotlin.math.min

abstract class BaseNumberButton : LinearLayout {
    //库存
    private var inventory = Int.MAX_VALUE

    //最大购买数，默认999
    private var buyMax = 999

    //最小购买数，默认0
    protected var buyMin = 0
    private var mOnWarnListener: OnWarnListener? = null
    abstract val etCount: EditText
    abstract val btnPlus: ImageView
    abstract val btnMinus: ImageView

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
        orientation = HORIZONTAL
        initAttrs(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
        initListener()
    }

    abstract fun initAttrs(attrs: AttributeSet?)

    abstract fun initView()

    abstract fun initListener()

    protected fun minus(callBack: (count: Int) -> Unit) {
        val count = getNumber()
        if (count > buyMin) {
            // 正常减
            callBack.invoke(count)
        }
    }

    protected fun plus(callBack: (count: Int) -> Unit) {
        val count = getNumber()
        if (count < getMaxLimit()) {
            //正常添加
            callBack.invoke(count)
        } else if (inventory < buyMax) {
            //库存不足
            warningForInventory()
        } else {
            //超过最大购买数
            warningForBuyMax()
        }
    }

    protected fun inputChange() {
        //当前数量
        val count = getNumber()
        if (count < buyMin) {
            //手动输入
            setCurrentNumber(buyMin)
            return
        }
        if (count > getMaxLimit()) {
            //超过了数量
            setCurrentNumber(getMaxLimit())
            if (inventory < buyMax) {
                //库存不足
                warningForInventory()
            } else {
                //超过最大购买数
                warningForBuyMax()
            }
        } else {
            setMinusPlusStatus(count)
        }
    }

    fun setEditable(editable: Boolean) {
        if (editable) {
            etCount.isFocusable = true
            etCount.keyListener = DigitsKeyListener()
        } else {
            etCount.isFocusable = false
            etCount.keyListener = null
        }
    }

    private fun setPlusEnable(enable: Boolean) {
//        if (enable) {
//            btnPlus.isEnabled = true
//            btnPlus.setImageResource(R.drawable.base_ic_plus)
//        } else {
//            btnPlus.isEnabled = false
//            btnPlus.setTintDrawable(
//                R.drawable.base_ic_plus,
//                com.example.myapp.resource.R.color.common_text_gray
//            )
//        }
    }

    private fun setMinusEnable(enable: Boolean) {
//        if (enable) {
//            btnMinus.isEnabled = true
//            btnMinus.setImageResource(R.drawable.base_ic_minus)
//        } else {
//            btnMinus.isEnabled = false
//            btnMinus.setTintDrawable(
//                R.drawable.base_ic_minus,
//                com.example.myapp.resource.R.color.common_text_gray
//            )
//        }
    }

    /**
     * 超过的库存限制
     * Warning for inventory.
     */
    private fun warningForInventory() {
        mOnWarnListener?.onWarningForInventory(inventory)
    }

    /**
     * 超过的最大购买数限制
     * Warning for buy max.
     */
    private fun warningForBuyMax() {
        mOnWarnListener?.onWarningForBuyMax(buyMax)
    }

    fun setInventory(inventory: Int) {
        this.inventory = inventory
    }

    fun setBuyMax(buyMax: Int) {
        this.buyMax = buyMax
    }

    fun setOnWarnListener(onWarnListener: OnWarnListener?) {
        mOnWarnListener = onWarnListener
    }

    fun setCurrentNumber(currentNumber: Int) {
        setMinusPlusStatus(currentNumber)
        when (currentNumber) {
            in -Int.MAX_VALUE..buyMin -> {
                etCount.setText("$buyMin")
            }
            in buyMin..getMaxLimit() -> {
                etCount.setText("$currentNumber")
            }
            in getMaxLimit()..Int.MAX_VALUE -> {
                etCount.setText("${getMaxLimit()}")
            }
        }
    }

    fun setMinusPlusStatus(currentNumber: Int) {
        when (currentNumber) {
            in -Int.MAX_VALUE..buyMin -> {
                // -置灰
                setMinusEnable(false)
                // +置亮
                setPlusEnable(true)
            }
            in buyMin until getMaxLimit() -> {
                // -置亮
                setMinusEnable(true)
                // +置亮
                setPlusEnable(true)
            }
            in getMaxLimit()..Int.MAX_VALUE -> {
                // -置亮
                setMinusEnable(true)
                // +置灰
                setPlusEnable(false)
            }
        }
    }

    private fun getMaxLimit(): Int {
        return min(buyMax, inventory)
    }

    fun getEditText(): EditText {
        return etCount
    }

    fun getNumber(): Int {
        return try {
            etCount.text.toString().toInt()
        } catch (e: NumberFormatException) {
            etCount.setText("$buyMin")
            buyMin
        }
    }

    interface OnWarnListener {
        fun onWarningForInventory(inventory: Int)
        fun onWarningForBuyMax(max: Int)
    }
}
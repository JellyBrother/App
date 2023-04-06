package com.example.myapp.base.widget.swipe

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import com.example.myapp.base.utils.LogUtils
import com.example.myapp.base.widget.R
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by guanaj on 2017/6/5.
 */
class SwipeMenuLayout constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ViewGroup(context, attrs) {
    private val mMatchParentChildren = ArrayList<View>(1)
    private var mLeftViewResID = 0
    private var mRightViewResID = 0
    private var mContentViewResID = 0
    private var mLeftView: View? = null
    private var mRightView: View? = null
    private var mContentView: View? = null
    private var mContentViewLp: MarginLayoutParams? = null
    private var isSwiping = false
    private var mLastP: PointF? = null
    private var mFirstP: PointF? = null
    var fraction = 0.3f
    var isCanLeftSwipe = true
    var isCanRightSwipe = true
    private var mScaledTouchSlop = 0
    private var mScroller: Scroller = Scroller(context)
    private val distanceX = 0f
    private var finallyDistanceX = 0f

    /**
     * 初始化方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        //创建辅助对象
        val viewConfiguration: ViewConfiguration = ViewConfiguration.get(context)
        mScaledTouchSlop = viewConfiguration.scaledTouchSlop
        //1、获取配置的属性值
        val typedArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SwipeMenuLayout,
            defStyleAttr,
            0
        )
        try {
            val indexCount: Int = typedArray.indexCount
            for (i in 0 until indexCount) {
                when (typedArray.getIndex(i)) {
                    R.styleable.SwipeMenuLayout_leftMenuView -> {
                        mLeftViewResID =
                            typedArray.getResourceId(R.styleable.SwipeMenuLayout_leftMenuView, -1)
                    }
                    R.styleable.SwipeMenuLayout_rightMenuView -> {
                        mRightViewResID =
                            typedArray.getResourceId(R.styleable.SwipeMenuLayout_rightMenuView, -1)
                    }
                    R.styleable.SwipeMenuLayout_contentView -> {
                        mContentViewResID =
                            typedArray.getResourceId(R.styleable.SwipeMenuLayout_contentView, -1)
                    }
                    R.styleable.SwipeMenuLayout_canLeftSwipe -> {
                        isCanLeftSwipe =
                            typedArray.getBoolean(R.styleable.SwipeMenuLayout_canLeftSwipe, true)
                    }
                    R.styleable.SwipeMenuLayout_canRightSwipe -> {
                        isCanRightSwipe =
                            typedArray.getBoolean(R.styleable.SwipeMenuLayout_canRightSwipe, true)
                    }
                    R.styleable.SwipeMenuLayout_fraction -> {
                        fraction = typedArray.getFloat(R.styleable.SwipeMenuLayout_fraction, 0.5f)
                    }
                }
            }
        } catch (e: Exception) {
            LogUtils.e("init", e)
        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取childView的个数
        var count = childCount
        isClickable = true
        //参考frameLayout测量代码
        val measureMatchParentChildren =
            MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                    MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY
        mMatchParentChildren.clear()
        var maxHeight = 0
        var maxWidth = 0
        var childState = 0
        //遍历childViews
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child.visibility != View.GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
                val lp: MarginLayoutParams = child.layoutParams as MarginLayoutParams
                maxWidth = max(
                    maxWidth,
                    child.measuredWidth + lp.leftMargin + lp.rightMargin
                )
                maxHeight = max(
                    maxHeight,
                    child.measuredHeight + lp.topMargin + lp.bottomMargin
                )
                childState = View.combineMeasuredStates(childState, child.measuredState)
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                        lp.height == LayoutParams.MATCH_PARENT
                    ) {
                        mMatchParentChildren.add(child)
                    }
                }
            }
        }
        // Check against our minimum height and width
        maxHeight = max(maxHeight, suggestedMinimumHeight)
        maxWidth = max(maxWidth, suggestedMinimumWidth)
        setMeasuredDimension(
            View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
            View.resolveSizeAndState(
                maxHeight, heightMeasureSpec,
                childState shl View.MEASURED_HEIGHT_STATE_SHIFT
            )
        )
        count = mMatchParentChildren.size
        if (count > 1) {
            for (i in 0 until count) {
                val child = mMatchParentChildren[i]
                val lp: MarginLayoutParams = child.layoutParams as MarginLayoutParams
                val childWidthMeasureSpec: Int = if (lp.width == LayoutParams.MATCH_PARENT) {
                    val width = Math.max(
                        0, measuredWidth
                                - lp.leftMargin - lp.rightMargin
                    )
                    MeasureSpec.makeMeasureSpec(
                        width, MeasureSpec.EXACTLY
                    )
                } else {
                    getChildMeasureSpec(
                        widthMeasureSpec,
                        lp.leftMargin + lp.rightMargin,
                        lp.width
                    )
                }
                val childHeightMeasureSpec = if (lp.height == LayoutParams.MATCH_PARENT) {
                    val height = max(
                        0, measuredHeight
                                - lp.topMargin - lp.bottomMargin
                    )
                    MeasureSpec.makeMeasureSpec(
                        height, MeasureSpec.EXACTLY
                    )
                } else {
                    getChildMeasureSpec(
                        heightMeasureSpec,
                        lp.topMargin + lp.bottomMargin,
                        lp.height
                    )
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count: Int = childCount
        val left: Int = 0 + paddingLeft
        val right: Int = 0 + paddingLeft
        val top: Int = 0 + paddingTop
        val bottom: Int = 0 + paddingTop
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (mLeftView == null && child.id == mLeftViewResID) {
                // Log.i(TAG, "找到左边按钮view");
                mLeftView = child
                mLeftView!!.isClickable = true
            } else if (mRightView == null && child.id == mRightViewResID) {
                // Log.i(TAG, "找到右边按钮view");
                mRightView = child
                mRightView!!.isClickable = true
            } else if (mContentView == null && child.id == mContentViewResID) {
                // Log.i(TAG, "找到内容View");
                mContentView = child
                mContentView!!.isClickable = true
            }
        }
        //布局contentView
        mContentView?.let { v ->
            mContentViewLp = v.layoutParams as? MarginLayoutParams
            mContentViewLp?.let { lp ->
                val cTop: Int = top + lp.topMargin
                val cLeft: Int = left + lp.leftMargin
                val cRight = left + lp.leftMargin + v.measuredWidth
                val cBottom = cTop + v.measuredHeight
                v.layout(cLeft, cTop, cRight, cBottom)
            }
        }

        mLeftView?.let { v ->
            val leftViewLp = v.layoutParams as? MarginLayoutParams
            leftViewLp?.let { lp ->
                val lTop: Int = top + lp.topMargin
                val lLeft: Int =
                    0 - v.measuredWidth + lp.leftMargin + lp.rightMargin
                val lRight: Int = 0 - lp.rightMargin
                val lBottom = lTop + v.measuredHeight
                v.layout(lLeft, lTop, lRight, lBottom)
            }
        }

        mRightView?.let { v ->
            val rightViewLp = v.layoutParams as? MarginLayoutParams
            rightViewLp?.let { lp ->
                val lTop: Int = top + lp.topMargin
                val lLeft: Int =
                    (mContentView?.right ?: 0) + (mContentViewLp?.rightMargin ?: 0) + lp.leftMargin
                val lRight = lLeft + v.measuredWidth
                val lBottom = lTop + v.measuredHeight
                v.layout(lLeft, lTop, lRight, lBottom)
            }
        }
    }

    var result: State? = null
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                //   System.out.println(">>>>dispatchTouchEvent() ACTION_DOWN");
                isSwiping = false
                if (mLastP == null) {
                    mLastP = PointF()
                }
                mLastP!!.set(ev.rawX, ev.rawY)
                if (mFirstP == null) {
                    mFirstP = PointF()
                }
                mFirstP!!.set(ev.rawX, ev.rawY)
                if (viewCache != null) {
                    if (viewCache !== this) {
                        viewCache!!.handlerSwipeMenu(State.CLOSE)
                    }
                    // Log.i(TAG, ">>>有菜单被打开");
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {

                //   System.out.println(">>>>dispatchTouchEvent() ACTION_MOVE getScrollX:" + getScrollX());
                val distanceX: Float = mLastP!!.x - ev.rawX
                val distanceY: Float = mLastP!!.y - ev.rawY
                if (abs(distanceY) > mScaledTouchSlop && abs(distanceY) > abs(distanceX)) {
                    return super.dispatchTouchEvent(ev)
                }
                //                if (Math.abs(distanceX) <= mScaledTouchSlop){
//                    break;
//                }
                // Log.i(TAG, ">>>>>distanceX:" + distanceX);
                scrollBy(distanceX.toInt(), 0) //滑动使用scrollBy
                //越界修正
                if (scrollX < 0) {
                    if (!isCanRightSwipe || mLeftView == null) {
                        scrollTo(0, 0)
                    } else { //左滑
                        if (scrollX < mLeftView!!.left) {
                            scrollTo(mLeftView!!.left, 0)
                        }
                    }
                } else if (scrollX > 0) {
                    if (!isCanLeftSwipe || mRightView == null) {
                        scrollTo(0, 0)
                    } else {
                        if (scrollX > mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin) {
                            scrollTo(
                                mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin,
                                0
                            )
                        }
                    }
                }
                //当处于水平滑动时，禁止父类拦截
                if (abs(distanceX) > mScaledTouchSlop) {
                    //  Log.i(TAG, ">>>>当处于水平滑动时，禁止父类拦截 true");
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                mLastP!!.set(ev.rawX, ev.rawY)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                finallyDistanceX = mFirstP!!.x - ev.rawX
                if (abs(finallyDistanceX) > mScaledTouchSlop) {
                    isSwiping = true
                }
                result = isShouldOpen(scrollX)
                handlerSwipeMenu(result)
            }
            else -> {}
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        //  Log.d(TAG, "<<<<dispatchTouchEvent() called with: " + "ev = [" + event + "]");
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {

                //滑动时拦截点击时间
                if (Math.abs(finallyDistanceX) > mScaledTouchSlop) {
                    // 当手指拖动值大于mScaledTouchSlop值时，认为应该进行滚动，拦截子控件的事件
                    //   Log.i(TAG, "<<<onInterceptTouchEvent true");
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                //滑动后不触发contentView的点击事件
                if (isSwiping) {
                    isSwiping = false
                    finallyDistanceX = 0f
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    /**
     * 自动设置状态
     *
     * @param result
     */
    private fun handlerSwipeMenu(result: State?) {
        if (result === State.LEFT_OPEN) {
            mScroller.startScroll(scrollX, 0, mLeftView!!.left - scrollX, 0)
            viewCache = this
            stateCache = result
        } else if (result === State.RIGHT_OPEN) {
            viewCache = this
            mScroller.startScroll(
                scrollX,
                0,
                mRightView!!.right - mContentView!!.right - mContentViewLp!!.rightMargin - scrollX,
                0
            )
            stateCache = result
        } else {
            mScroller.startScroll(scrollX, 0, -scrollX, 0)
            viewCache = null
            stateCache = null
        }
        invalidate()
    }

    override fun computeScroll() {
        //判断Scroller是否执行完毕：
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            //通知View重绘-invalidate()->onDraw()->computeScroll()
            invalidate()
        }
    }

    /**
     * 根据当前的scrollX的值判断松开手后应处于何种状态
     *
     * @param
     * @param scrollX
     * @return
     */
    private fun isShouldOpen(scrollX: Int): State? {
        if (mScaledTouchSlop >= abs(finallyDistanceX)) {
            return stateCache
        }
        LogUtils.i(TAG, ">>>finalyDistanceX:$finallyDistanceX")
        if (finallyDistanceX < 0) {
            //➡滑动
            //1、展开左边按钮
            //获得leftView的测量长度
            if (getScrollX() < 0 && mLeftView != null) {
                if (abs(mLeftView!!.width * fraction) < abs(getScrollX())) {
                    return State.LEFT_OPEN
                }
            }
            //2、关闭右边按钮
            if (getScrollX() > 0 && mRightView != null) {
                return State.CLOSE
            }
        } else if (finallyDistanceX > 0) {
            //⬅️滑动
            //3、开启右边菜单按钮
            if (getScrollX() > 0 && mRightView != null) {
                if (Math.abs(mRightView!!.width * fraction) < Math.abs(getScrollX())) {
                    return State.RIGHT_OPEN
                }
            }
            //关闭左边
            if (getScrollX() < 0 && mLeftView != null) {
                return State.CLOSE
            }
        }
        return State.CLOSE
    }

    override fun onDetachedFromWindow() {
        if (this === viewCache) {
            viewCache!!.handlerSwipeMenu(State.CLOSE)
        }
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (this === viewCache) {
            viewCache!!.handlerSwipeMenu(stateCache)
        }
    }

    fun resetStatus() {
        if (viewCache != null) {
            if (stateCache != null && stateCache !== State.CLOSE) {
                mScroller.startScroll(viewCache!!.scrollX, 0, -viewCache!!.scrollX, 0)
                viewCache!!.invalidate()
                viewCache = null
                stateCache = null
            }
        }
    }

    //➡滑动
    private fun isLeftToRight(): Boolean {
        return distanceX < 0
    }

    companion object {
        private const val TAG = "SwipeMenuLayout"
        var viewCache: SwipeMenuLayout? = null
            private set
        var stateCache: State? = null
            private set
    }

    init {
        init(context, attrs, 0)
    }
}
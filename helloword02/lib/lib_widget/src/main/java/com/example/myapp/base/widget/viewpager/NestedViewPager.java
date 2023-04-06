package com.example.myapp.base.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.myapp.base.utils.LogUtils;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2020/08/18
 * desc   : 支持嵌套滚动的 ViewPager
 */
public class NestedViewPager extends ViewPager implements NestedScrollingParent, NestedScrollingChild {
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;

    public NestedViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if (mChildHelper == null) {
            mChildHelper = new NestedScrollingChildHelper(this);
            setNestedScrollingEnabled(true);
        }
        if (mParentHelper == null) {
            mParentHelper = new NestedScrollingParentHelper(this);
            setNestedScrollingEnabled(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.d("dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        LogUtils.d("onInterceptTouchEvent");
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        LogUtils.d("onTouchEvent");
        return super.onTouchEvent(e);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        LogUtils.d("setNestedScrollingEnabled");
        init(getContext());
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        LogUtils.d("isNestedScrollingEnabled");
        init(getContext());
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        LogUtils.d("startNestedScroll");
        init(getContext());
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        LogUtils.d("stopNestedScroll");
        init(getContext());
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        LogUtils.d("hasNestedScrollingParent");
        init(getContext());
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        LogUtils.d("dispatchNestedScroll");
        init(getContext());
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        LogUtils.d("dispatchNestedPreScroll");
        init(getContext());
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        LogUtils.d("dispatchNestedFling3");
        init(getContext());
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        LogUtils.d("dispatchNestedPreFling");
        init(getContext());
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        LogUtils.d("onStartNestedScroll");
        init(getContext());
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        LogUtils.d("onNestedScrollAccepted");
        init(getContext());
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        LogUtils.d("onStopNestedScroll");
        init(getContext());
        mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        LogUtils.d("onNestedScroll");
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        LogUtils.d("onNestedPreScroll");
        dispatchNestedPreScroll(dx, dy, consumed, null);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        LogUtils.d("onNestedFling");
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        LogUtils.d("onNestedPreFling");
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        LogUtils.d("getNestedScrollAxes");
        init(getContext());
        return mParentHelper.getNestedScrollAxes();
    }
}
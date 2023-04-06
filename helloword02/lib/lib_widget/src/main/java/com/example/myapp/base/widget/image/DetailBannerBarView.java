package com.example.myapp.base.widget.image;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapp.base.utils.LogUtils;

public class DetailBannerBarView extends View {

    public DetailBannerBarView(Context context) {
        super(context);
        initView(context);
    }

    public DetailBannerBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DetailBannerBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d("onConfigurationChanged");
        setViewSize();
    }

    private void initView(Context context) {
        LogUtils.d("initView");
        setViewSize();
        initListener();
    }

    public void setViewSize() {
    }

    private void initListener() {
        setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LogUtils.d("setOnScrollChangeListener  onScrollChange scrollX:" + scrollX + ",scrollY:" + scrollY);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.d("onAttachedToWindow");
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        LogUtils.d("onVisibilityChanged visibility:" + visibility);
    }

    @Override
    protected void dispatchVisibilityChanged(@NonNull View changedView, int visibility) {
        super.dispatchVisibilityChanged(changedView, visibility);
        LogUtils.d("dispatchVisibilityChanged visibility:" + visibility);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        LogUtils.d("onVisibilityAggregated isVisible:" + isVisible);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        LogUtils.d("onWindowVisibilityChanged visibility:" + visibility);
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        super.onWindowSystemUiVisibilityChanged(visible);
        LogUtils.d("onWindowSystemUiVisibilityChanged visible:" + visible);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        LogUtils.d("onFocusChanged gainFocus:" + gainFocus);
    }

    @Override
    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        LogUtils.d("onDisplayHint hint:" + hint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d("onDetachedFromWindow");
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
        LogUtils.d("scrollBy x:" + x + ",y:" + y);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        LogUtils.d("scrollTo x:" + x + ",y:" + y);
    }

    @Override
    public void setScrollCaptureHint(int hint) {
        super.setScrollCaptureHint(hint);
        LogUtils.d("setScrollCaptureHint hint:" + hint);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        LogUtils.d("onOverScrolled scrollX:" + scrollX + ",scrollY:" + scrollY);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtils.d("onScrollChanged l:" + l + ",t:" + t);
    }

    public int getLocation() {
        int[] windowLocation = new int[2];
        getLocationInWindow(windowLocation);
        LogUtils.d("DetailBannerBarView getLocation getLocationInWindow scrollY:" + windowLocation[1]);
        int[] screenLocation = new int[2];
        getLocationOnScreen(screenLocation);
        LogUtils.d("DetailBannerBarView getLocation getLocationOnScreen:" + screenLocation[1]);
        Rect rect = new Rect();
        getFocusedRect(rect);
        LogUtils.d("DetailBannerBarView getLocation getFocusedRect:" + rect);
        Rect rect2 = new Rect();
        getGlobalVisibleRect(rect2);
        LogUtils.d("DetailBannerBarView getLocation getGlobalVisibleRect:" + rect2);
        Rect rect3 = new Rect();
        getLocalVisibleRect(rect3);
        LogUtils.d("DetailBannerBarView getLocation getLocalVisibleRect:" + rect3);
        return windowLocation[1];
    }
}
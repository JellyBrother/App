package com.example.myapp.base.widget.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.base.utils.Utils;
import com.example.myapp.base.widget.R;

public class BasePopupWindow extends PopupWindow {
    protected String TAG = "BasePopupWndow";
    protected Context mContext;
    protected Activity mActivity;
    protected View mRootView;
    protected RecyclerView mRecyclerView;

    private BasePopupWindow() {
    }

    public BasePopupWindow(Context context) {
        super(context);
        initView(context);
    }

    public BasePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BasePopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public BasePopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public BasePopupWindow(View contentView) {
        super(contentView);
        initView(contentView.getContext());
    }

    private BasePopupWindow(int width, int height) {
        super(width, height);
    }

    public BasePopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        initView(contentView.getContext());
    }

    public BasePopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        initView(contentView.getContext());
    }

    protected void initView(Context context) {
        TAG = getClass().getSimpleName();
        mContext = context;
        mActivity = Utils.getActivity(context);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        mRootView = LayoutInflater.from(context).inflate(R.layout.base_popupwindow, null);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(mRootView);
//        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public View getRootView() {
        return mRootView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private void measure() {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        getContentView().measure(width, height);
    }

    public int getMeasureWidth() {
        int width = getContentView().getWidth();
        if (width == 0) {
            measure();
            return getContentView().getMeasuredWidth();
        } else {
            return width;
        }
    }

    public int getMeasureHeight() {
        int height = getContentView().getHeight();
        if (height == 0) {
            measure();
            return getContentView().getMeasuredHeight();
        } else {
            return height;
        }
    }

    public void showBottom(View anchor) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        showAtLocation(
                anchor,
                Gravity.TOP | Gravity.START,
                location[0] + anchor.getWidth() - getMeasureWidth(),
                location[1] + anchor.getHeight()
        );
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}

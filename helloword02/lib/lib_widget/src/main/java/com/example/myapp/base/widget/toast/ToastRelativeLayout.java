package com.example.myapp.base.widget.toast;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.myapp.base.utils.ScreenUtils;
import com.example.myapp.base.utils.SizeUtils;

public class ToastRelativeLayout extends RelativeLayout {

    public ToastRelativeLayout(Context context) {
        super(context);
    }

    public ToastRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMaxSpec = MeasureSpec.makeMeasureSpec(ScreenUtils.getScreenWidth() - SizeUtils.dp2px(80), MeasureSpec.AT_MOST);
        super.onMeasure(widthMaxSpec, heightMeasureSpec);
    }
}
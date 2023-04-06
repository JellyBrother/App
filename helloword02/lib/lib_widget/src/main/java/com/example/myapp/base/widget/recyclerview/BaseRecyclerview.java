package com.example.myapp.base.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerview extends RecyclerView {
    private LinearTopSmoothScroller smoothScroller;

    public BaseRecyclerview(@NonNull Context context) {
        super(context);
    }

    public BaseRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 指定item并置顶
     *
     * @param position item索引
     */
    public void scrollItemToTop(int position) {
        if (smoothScroller == null) {
            smoothScroller = new LinearTopSmoothScroller(getContext());
        }
        smoothScroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(smoothScroller);
    }
}

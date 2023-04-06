package com.example.myapp.base.widget.tag;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapp.base.utils.ListUtil;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.SizeUtils;
import com.example.myapp.base.widget.R;

import java.util.List;

/**
 * 列表标签控件
 */
public class TagView extends LinearLayout {
    private int dp2;
    private int dp11;
    private int dp12;
    private int dp13;
    private String name;

    public TagView(Context context) {
        super(context);
        init(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        dp2 = SizeUtils.dp2px(2);
        dp11 = SizeUtils.dp2px(11);
        dp12 = SizeUtils.dp2px(12);
        dp13 = SizeUtils.dp2px(13);
    }

    public void setData(List<String> list) {
        removeAllViews();
        if (ListUtil.isEmpty(list)) {
            return;
        }
//        measure(0, 0);
        int width = getWidth();
        int measuredWidth = getMeasuredWidth();
        LogUtils.d("TagView setData name:" + name + ",width:" + width + ",measuredWidth:" + measuredWidth);
        if (measuredWidth > 0) {
            putChildView(list);
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                putChildView(list);
            }
        }, 2);
    }

    private void putChildView(List<String> tags) {
        int childWidth = 0;
        int measuredWidth = getMeasuredWidth();
        int size = tags.size();
        for (int i = 0; i < size; i++) {
            if (measuredWidth < childWidth) {
                return;
            }
            View view = null;
            TextView textView = null;
            if (i == 0) {
                childWidth = dp11;
//                view = LayoutInflater.from(getContext()).inflate(R.layout.base_tag_item_red, null, false);
//                textView = view.findViewById(R.id.tvw_tag);
            } else {
//                view = LayoutInflater.from(getContext()).inflate(R.layout.base_tag_item_blue, null, false);
//                textView = view.findViewById(R.id.tvw_tag);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                if (i == size - 1) {
                    layoutParams.rightMargin = 0;
                    childWidth = childWidth + dp11;
                } else {
                    layoutParams.rightMargin = dp2;
                    childWidth = childWidth + dp13;
                }
                textView.setLayoutParams(layoutParams);
            }
            String title = tags.get(i);
            LogUtils.d("TagView setData name:" + name + ",size:" + size + ",title:" + title + ",childWidth:" + childWidth + ",measuredWidth:" + measuredWidth);
            textView.setText(title);

            TextPaint paint = textView.getPaint();
            float textWidth = paint.measureText(title);
            childWidth = (int) (childWidth + textWidth);
            if (measuredWidth < childWidth) {
                if (i == 0) {
                    textView.setMaxWidth(measuredWidth - dp12);
                    addView(view);
                }
                // 有更多
//                view = LayoutInflater.from(getContext()).inflate(R.layout.base_tag_item_blue_end, null, false);
            }
            addView(view);
        }
    }
}

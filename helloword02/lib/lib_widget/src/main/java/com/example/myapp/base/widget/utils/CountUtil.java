package com.example.myapp.base.widget.utils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapp.base.utils.SizeUtils;
import com.example.myapp.base.utils.TypeConversionUtil;
import com.example.myapp.base.widget.R;

public class CountUtil {
    private static final String TAG = "CountUtil";

    public static String get99Count(String count) {
        long c = TypeConversionUtil.getLong(count);
        return get99Count(c);
    }

    public static String get99Count(long count) {
        if (count <= 0) {
            return "";
        }
        if (count > 99) {
            return "99+";
        }
        return count + "";
    }

    /**
     * 当数字变化的时候，保持红色圆点圆形
     */
    public static void set99Count(TextView textView, String count) {
        if (textView == null) {
            return;
        }
        String c = get99Count(count);
        textView.setText(c);
        if (TextUtils.isEmpty(c)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.VISIBLE);
        if (c.length() == 1) {
            int dp12 = SizeUtils.dp2px(12);
            setTextViewSize(textView, dp12);
//            textView.setBackground(ContextCompat.getDrawable(textView.getContext(), R.drawable.base_red_dot_num_bg));
            return;
        }
        if (c.length() == 2) {
            int dp12 = SizeUtils.dp2px(12);
            setTextViewSize(textView, dp12);
//            textView.setBackground(ContextCompat.getDrawable(textView.getContext(), R.drawable.base_red_dot_num_bg2));
            return;
        }
        int dp14 = SizeUtils.dp2px(16);
        setTextViewSize(textView, dp14);
//        textView.setBackground(ContextCompat.getDrawable(textView.getContext(), R.drawable.base_red_dot_num_bg3));
    }

    public static void setTextViewSize(TextView textView, int dp) {
        if (textView == null || dp < 1) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        layoutParams.width = dp;
        layoutParams.height = dp;
        textView.setLayoutParams(layoutParams);
    }
}

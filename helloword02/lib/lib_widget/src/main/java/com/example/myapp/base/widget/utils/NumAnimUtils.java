package com.example.myapp.base.widget.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;

import com.example.myapp.base.widget.R;

import java.util.LinkedList;
import java.util.Random;

public class NumAnimUtils {
    //每秒刷新多少次
    private static final int COUNTPERS = 100;

    public static void startAnim(TextView textV, long num) {
        startAnim(textV, num, 500, 0);
    }

    /**
     * 数字滚动效果
     *
     * @param textV 控件
     * @param num   填充数字
     * @param time  滚动次数
     * @param m     多少位小数点
     */
    public static void startAnim(TextView textV, long num, long time, int m) {
        if (num == 0) {
            textV.setText(numberFormat(num, m));
            return;
        }
        Float[] nums = splitnum(num, (int) ((time / 1000f) * COUNTPERS), m);
        Counter counter = new Counter(textV, nums, time, m);
        textV.removeCallbacks(counter);
        textV.post(counter);
    }

    private static Float[] splitnum(float num, int count, int m) {
        Random random = new Random();
        float numtemp = num;
        float sum = 0;
        LinkedList<Float> nums = new LinkedList<Float>();
        nums.add(0f);
        while (true) {
            float nextFloat = numberFormatFloat(
                    (random.nextFloat() * num * 2f) / (float) count,
                    m);
            if (nextFloat == 0) {
                nextFloat = 1;
            }
            if (numtemp - nextFloat >= 0) {
                sum = numberFormatFloat(sum + nextFloat, m);
                nums.add(sum);
                numtemp -= nextFloat;
            } else {
                nums.add(num);
                return nums.toArray(new Float[0]);
            }
        }
    }

    static class Counter implements Runnable {

        private final TextView view;
        private final Float[] nums;
        private final long pertime;
        private int m;
        private String text;
        private int i = 0;
        private Context context;

        Counter(TextView view, Float[] nums, long time, int m) {
            this.view = view;
            this.nums = nums;
            this.m = m;
            this.pertime = time / nums.length;
        }

        Counter(TextView view, Float[] nums, long time, String text, Context context) {
            this.view = view;
            this.nums = nums;
            this.context = context;
            this.text = text;
            this.m = m;
            this.pertime = time / nums.length;
        }

        @Override
        public void run() {
            if (i > nums.length - 1) {
                view.removeCallbacks(Counter.this);
                return;
            }
            if (text != null) {
                view.setText(getSpanText(numberFormat2(nums[i++], 0), text, context), TextView.BufferType.SPANNABLE);
            } else {
                view.setText(numberFormat2(nums[i++], m));
            }
            view.removeCallbacks(Counter.this);
            view.postDelayed(Counter.this, pertime);
        }
    }

    public static String numberFormat(float f, int m) {
        return String.format("%." + m + "f", f);
    }

    public static String numberFormat2(float f, int m) {
        return String.format("%." + m + "f", f) + "+";
    }

    public static float numberFormatFloat(float f, int m) {
        String strfloat = numberFormat(f, m);
        return Float.parseFloat(strfloat);
    }

    public static SpannableString getSpanText(String s, String text, Context context) {
        SpannableString ss = new SpannableString(s + text);
//        ss.setSpan(new TextAppearanceSpan(context, R.style.base_text_before), 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        ss.setSpan(new TextAppearanceSpan(context, R.style.base_es_text_after), s.length(), (s + text).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return ss;
    }
}

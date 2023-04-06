package com.example.myapp.base.widget.textview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myapp.base.widget.R;

public class ExpandTextView extends androidx.appcompat.widget.AppCompatTextView {
    protected Resources resources;
    protected CharSequence allText;
    protected int maxTextLines = 3;
    private Handler handler;
    private String expand;
    private String collapse;

    public ExpandTextView(Context context) {
        super(context);
        initView(context);
    }

    public ExpandTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ExpandTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setViewSize(newConfig);
    }

    private void initView(Context context) {
        resources = getResources();
        handler = new Handler();
//        expand = resources.getString(R.string.base_expand);
//        collapse = resources.getString(R.string.base_collapse);
        setViewSize(resources.getConfiguration());
    }

    public void setViewSize(Configuration configuration) {
    }

    public int getMaxTextLines() {
        return maxTextLines;
    }

    public void setMaxTextLines(int maxTextLines) {
        this.maxTextLines = maxTextLines;
    }

    public void setContent(CharSequence text) {
        allText = text;
        setText(text);
        handler.post(new Runnable() {
            @Override
            public void run() {
                expandText(text);
            }
        });
    }

    private void expandText(CharSequence text) {
        Layout layout = getLayout();
        if (layout == null) {
            return;
        }
        int line = layout.getLineCount();
        if (line <= maxTextLines) {
            return;
        }
        int lastLineIndex = maxTextLines - 1;
        int start = layout.getLineStart(lastLineIndex);
        int end = layout.getLineVisibleEnd(lastLineIndex);
        CharSequence lastLine = text.subSequence(start, end);
        TextPaint paint = getPaint();
        float expandWidth = paint.measureText(expand);
        int width = getWidth();
        float remain = width - expandWidth;
        CharSequence ellipsize = TextUtils.ellipsize(lastLine, paint, remain, TextUtils.TruncateAt.END);
        ClickSpan clickableSpan = new ClickSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                collapseText(allText);
            }
        };
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(text.subSequence(0, start));
        ssb.append(ellipsize);
        ssb.append(expand);
//        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), com.example.myapp.resource.R.color.common_blue)),
//                ssb.length() - expand.length(), ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.setSpan(clickableSpan, ssb.length() - expand.length(), ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setMovementMethod(LinkMovementMethod.getInstance());
        setText(ssb);
    }

    private void collapseText(CharSequence text) {
        // 默认此时文本肯定超过行数了，直接在最后拼接文本
        ClickSpan clickableSpan = new ClickSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                expandText(allText);
            }
        };
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(text);
        ssb.append(collapse);
//        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), com.example.myapp.resource.R.color.common_blue)),
//                ssb.length() - collapse.length(), ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.setSpan(clickableSpan, ssb.length() - collapse.length(), ssb.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(ssb);
    }
}

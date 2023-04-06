package com.example.myapp.base.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class ViewUtil {
    private static final String TAG = "ViewUtil";

    public static void setText(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
            return;
        }
        textView.setText(text);
    }

    public static void setTextAndGone(TextView textView, String text) {
        if (textView == null) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }

    public static void setImageAndGone(ImageView imageView, int imgId) {
        if (imageView == null) {
            return;
        }
        if (imgId < 1) {
            imageView.setVisibility(View.GONE);
            return;
        }
        imageView.setImageResource(imgId);
        imageView.setVisibility(View.VISIBLE);
    }

    public static void setDrawableTint(ImageView imageView, int colorId) {
        if (imageView == null) {
            return;
        }
        setDrawableTint(imageView, imageView.getDrawable(), colorId);
    }

    public static void setDrawableTint(ImageView imageView, int drawableId, int colorId) {
        Drawable wrapDrawable = DrawableCompat.wrap(imageView.getContext().getDrawable(drawableId)).mutate();
        setDrawableTint(imageView, wrapDrawable, colorId);
    }

    public static void setDrawableTint(ImageView imageView, Drawable wrapDrawable, int colorId) {
        if (imageView == null || wrapDrawable == null) {
            return;
        }
        wrapDrawable.setBounds(0, 0, wrapDrawable.getIntrinsicWidth(), wrapDrawable.getIntrinsicHeight());
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(imageView.getContext(), colorId));
        imageView.setImageDrawable(wrapDrawable);
    }

    public static Rect getViewRect(View view) {
        int[] startLocation = new int[2];
        view.getLocationOnScreen(startLocation);
        Rect rect = new Rect(startLocation[0], startLocation[1], startLocation[0] + view.getWidth(), startLocation[1] + view.getHeight());
        return rect;
    }

    public static ViewGroup getRootView(Activity activity) {
        if (Utils.isActivityDestroy(activity)) {
            return null;
        }
        return activity.findViewById(android.R.id.content);
    }
}

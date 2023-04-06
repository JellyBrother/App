package com.example.myapp.base.widget.base;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapp.base.utils.LogUtils;

public class SimpleImageTarget<Z> extends SimpleTarget<Z> {

    @Override
    public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
        LogUtils.d("SimpleImageTarget onResourceReady");
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        LogUtils.d("SimpleImageTarget onLoadFailed");
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        super.onLoadCleared(placeholder);
        LogUtils.d("SimpleImageTarget onLoadCleared");
    }

    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        LogUtils.d("SimpleImageTarget onLoadStarted");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("SimpleImageTarget onDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d("SimpleImageTarget onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d("SimpleImageTarget onStop");
    }
}

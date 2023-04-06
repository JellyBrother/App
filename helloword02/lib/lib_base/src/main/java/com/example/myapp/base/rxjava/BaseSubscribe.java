package com.example.myapp.base.rxjava;

import com.example.myapp.base.utils.LogUtils;

import io.reactivex.subscribers.ResourceSubscriber;

public abstract class BaseSubscribe<T> extends ResourceSubscriber<T> {

    @Override
    public void onComplete() {
        LogUtils.d("onComplete");
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(e);
    }
}

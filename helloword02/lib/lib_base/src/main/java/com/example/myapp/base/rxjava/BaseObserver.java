package com.example.myapp.base.rxjava;

import com.example.myapp.base.utils.LogUtils;

import io.reactivex.observers.ResourceObserver;

public abstract class BaseObserver<T> extends ResourceObserver<T> {

    @Override
    public void onComplete() {
        LogUtils.d("onComplete");
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(e);
    }
}

package com.example.myapp.base.rxjava;

import com.example.myapp.base.utils.LogUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class BaseConsumer implements Consumer<Throwable> {
    @Override
    public void accept(@NonNull Throwable throwable) throws Exception {
        LogUtils.e(throwable);
    }
}

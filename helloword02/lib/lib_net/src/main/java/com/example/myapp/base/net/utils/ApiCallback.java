package com.example.myapp.base.net.utils;

import androidx.annotation.NonNull;

public interface ApiCallback<T> {

    /**
     * 加载成功
     *
     * @param response 接口回调
     */
    void onSuccess(@NonNull ApiResponse<T> response);

    /**
     * 加载失败
     *
     * @param t 异常
     */
    void onError(@NonNull Throwable t);
}
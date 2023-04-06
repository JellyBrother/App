package com.example.myapp.base.net.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * 网络请求 for Java后台
 */
class JaRetrofit(baseURL: String, val okHttpClient: OkHttpClient) {
    val retrofit: Retrofit = RetrofitCreateHelper.getInstance().initRetrofit(
        baseURL,
        okHttpClient
    )
}
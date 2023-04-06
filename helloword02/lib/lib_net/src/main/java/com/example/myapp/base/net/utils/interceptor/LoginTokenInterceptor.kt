package com.example.myapp.base.net.utils.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoginTokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest: Request = chain.request()
        if (LoginTokenHelper.isLogin()) {
            val newRequest = addCommonHeader(oldRequest)
            return chain.proceed(newRequest)
        }
        return chain.proceed(oldRequest)
    }

    private fun addCommonHeader(oldRequest: Request): Request {
        if (hasAuthorizationHeader(oldRequest)) return oldRequest
        return oldRequest
            .newBuilder()
            .addHeader("Authorization", LoginTokenHelper.getLoginToken())
            .addHeader("XAuthorization", LoginTokenHelper.getJaLoginToken())
            .build()
    }

    private fun hasAuthorizationHeader(oldRequest: Request): Boolean {
        return !oldRequest.header("Authorization")
            .isNullOrEmpty() && !oldRequest.header("XAuthorization").isNullOrEmpty()
    }
}
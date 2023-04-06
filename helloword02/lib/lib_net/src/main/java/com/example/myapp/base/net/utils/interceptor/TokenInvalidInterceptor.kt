package com.example.myapp.base.net.utils.interceptor

import com.google.gson.reflect.TypeToken
import com.example.myapp.base.net.utils.NetUtil
import com.example.myapp.base.net.utils.NetworkResponse
import com.example.myapp.base.utils.GsonUtils
import com.example.myapp.base.utils.LogUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

class TokenInvalidInterceptor : Interceptor {
    private val UTF8: Charset = Charset.forName("UTF-8")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        // 加上通用的请求头参数
        var request = NetUtil.builderCommonHeads(chain.request())
        val response: Response = chain.proceed(request)

        val responseBody = response.body ?: return response
        val contentLength = responseBody.contentLength()

        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer

        var charset = UTF8
        val contentType = responseBody.contentType()
        if (contentType != null) {
            try {
                contentType.charset(UTF8)?.let {
                    charset = it
                }
            } catch (e: UnsupportedCharsetException) {
                return response
            }
        }

        if (contentLength != 0L) {
            try {
                val result = buffer.clone().readString(charset)
                val networkResponse = GsonUtils.getGson().fromJson<NetworkResponse<Nothing>>(
                    result,
                    object : TypeToken<NetworkResponse<Nothing>>() {}.type
                )
                if (networkResponse != null && LoginTokenHelper.isLogin() && networkResponse.isTokenInvalid()) {
                    LogUtils.dTag("InvalidToken", "response.body():${result}")
                    // Token失效跳转登录
                    LoginTokenHelper.loginTokenInvalidJump()
                }
            } catch (e: Throwable) {
            }
        }

        return response
    }
}
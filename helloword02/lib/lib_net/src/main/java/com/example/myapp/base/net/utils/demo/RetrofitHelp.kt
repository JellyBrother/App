package com.example.myapp.base.net.utils.demo

import com.example.myapp.base.cache.CachePath
import com.example.myapp.base.constant.BaseConstant
import com.example.myapp.base.net.utils.RetrofitCreateHelper
import com.example.myapp.base.net.utils.gson.GsonConverterFactory
import com.example.myapp.base.utils.LogUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelp {
    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    private fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            var builder = OkHttpClient().newBuilder()
                .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(15, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(15, TimeUnit.SECONDS)//设置写入超时时间
                .retryOnConnectionFailure(true);//设置出现错误进行重新连接。
            if (BaseConstant.Base.isDebug) {
                val interceptor = HttpLoggingInterceptor { message: String ->
                    LogUtils.d(
                        RetrofitCreateHelper.TAG + message
                    )
                }
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(interceptor)
            }
            // httpDns 优化
            builder.dns(OkHttpDns.getIns(BaseConstant.Base.sApp));
            //设置 请求的缓存的大小跟位置
            val cache = Cache(CachePath.getHttpCacheDir(), 1024 * 1024 * 50)
            builder.cache(cache)
            okHttpClient = builder.build()
        }
        return okHttpClient
    }

    public fun getRetrofit(baseUrl: String): Retrofit {
        if (retrofit == null) {
            var builder = Retrofit.Builder()
            builder.client(getOkHttpClient())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
            retrofit = builder.build()
        }
        return retrofit
    }
}
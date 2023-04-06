package com.example.myapp.base.net.utils;

import androidx.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.myapp.base.cache.CachePath;
import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.net.utils.gson.GsonConverterFactory;
import com.example.myapp.base.net.utils.interceptor.LoginTokenInterceptor;
import com.example.myapp.base.net.utils.interceptor.TokenInvalidInterceptor;
import com.example.myapp.base.net.utils.ssl.SSLUtil;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Zlx on 2017/12/12.
 */
public class RetrofitCreateHelper {
    public static final String TAG = "RetrofitCreateHelper ";
    private Retrofit javaRetrofit;
    private OkHttpClient client;
    private HttpProxyCacheServer videoCacheServer;

    private RetrofitCreateHelper() {
    }

    private static class Instance {
        private static final RetrofitCreateHelper INSTANCE = new RetrofitCreateHelper();
    }

    public static RetrofitCreateHelper getInstance() {
        return Instance.INSTANCE;
    }

    public <T> T createJava(Class<T> service) {
        if (javaRetrofit == null) {
            javaRetrofit = initRetrofit(BaseConstant.Config.URL_JA_PALMHOUSE, initOkHttp());
        }
        return javaRetrofit.create(service);
    }

    /**
     * 初始化Retrofit
     */
    @NonNull
    public Retrofit initRetrofit(String baseURL, OkHttpClient client) {
        BaseConstant.Config.initPalmHouseBaseUrl();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(baseURL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(ApiCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 初始化okhttp
     */
    @NonNull
    public OkHttpClient initOkHttp() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .readTimeout(BaseConstant.HttpConfig.TIMEOUT_CONNECTION, TimeUnit.SECONDS)//设置读取超时时间
                    .connectTimeout(BaseConstant.HttpConfig.TIMEOUT_READ, TimeUnit.SECONDS)//设置请求超时时间
                    .writeTimeout(BaseConstant.HttpConfig.TIMEOUT_READ, TimeUnit.SECONDS)//设置写入超时时间
                    .addInterceptor(new LoginTokenInterceptor())
                    .addInterceptor(new TokenInvalidInterceptor())
                    .retryOnConnectionFailure(true);//设置出现错误进行重新连接。
            if (BaseConstant.Base.isDebug) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> {
                    LogUtils.d(TAG + message);
                });
                interceptor.level(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }
            SSLUtil.configSSL(builder);
            Cache cache = new Cache(CachePath.getHttpCacheDir(), 1024 * 1024 * 50);
            builder.cache(cache);
            this.client = builder.build();
        }
        return client;
    }

    public HttpProxyCacheServer getVideoCacheServer() {
        if (videoCacheServer == null) {
            videoCacheServer = new HttpProxyCacheServer.Builder(Utils.getApp())
                    .cacheDirectory(CachePath.getVideoCacheDir())
                    .build();
        }
        return videoCacheServer;
    }

    public void clean() {
        javaRetrofit = null;
        client = null;
        videoCacheServer = null;
    }
}
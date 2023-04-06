package com.example.myapp.base.net.utils.ssl;

import com.example.myapp.base.net.utils.RetrofitCreateHelper;
import com.example.myapp.base.utils.LogUtils;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class SSLSocketFactoryUnSafeUtil {

    public static SSLParams getSSLParams() {
        SSLParams sslParams = new SSLParams();
        sslParams.trustManager = TrustManagerUtil.getUnSafeTrustManager();
        sslParams.sSLSocketFactory = createTrustAllSSLSocketFactory(sslParams.trustManager);
        return sslParams;
    }

    /**
     * 创建信任所有证书的套接字工厂
     */
    public static SSLSocketFactory createTrustAllSSLSocketFactory(TrustManager manager) {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{manager}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Throwable ignored) {
            LogUtils.e(RetrofitCreateHelper.TAG + "createTrustAllSSLSocketFactory Throwable:" + ignored);
        }
        return sSLSocketFactory;
    }
}

package com.example.myapp.base.net.utils.ssl;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.net.utils.RetrofitCreateHelper;
import com.example.myapp.base.utils.LogUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class SSLUtil {

    public static void configSSL(OkHttpClient.Builder builder) {
        if (builder == null) {
            LogUtils.e("configSSL builder == null");
            return;
        }
        if (!BaseConstant.Base.isDebug) {
            // 功能未完善
//            SSLParams sslParams = SSLSocketFactorySafeUtil.getSSLParams();
            return;
        }
        try {
            // 不校验证书，方便测试抓包
            LogUtils.d("SSLUtil Debug configSSL start");
            builder.hostnameVerifier(getUnSafeHostnameVerifier());
            SSLParams sslParams = SSLSocketFactoryUnSafeUtil.getSSLParams();
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        } catch (Throwable t) {
            LogUtils.e(RetrofitCreateHelper.TAG + "configSSL Throwable:" + t);
        }
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public static HostnameVerifier getUnSafeHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                LogUtils.d(RetrofitCreateHelper.TAG + "getUnSafeHostnameVerifier hostname:" + hostname + ",session:" + session);
                return true;
            }
        };
    }
}

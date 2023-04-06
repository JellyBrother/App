package com.example.myapp.base.net.utils.ssl;

import android.text.TextUtils;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.net.utils.RetrofitCreateHelper;
import com.example.myapp.base.utils.ListUtil;
import com.example.myapp.base.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 证书校验还有问题，需要自测
 * 证书名称 certificate 开头
 */
public class SSLSocketFactorySafeUtil {

    public static SSLParams getSSLParams() {
        SSLParams sslParams = new SSLParams();
        sslParams.trustManager = TrustManagerUtil.getUnSafeTrustManager();
        sslParams.sSLSocketFactory = getSSLSocketFactory();
        return sslParams;
    }

    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            String[] files = BaseConstant.Base.sApp.getAssets().list("");
            if (ListUtil.isEmpty(files)) {
                LogUtils.e(RetrofitCreateHelper.TAG + "getSSLSocketFactory ListUtil.isEmpty(files)");
                return null;
            }
            TrustManager[] trustManagers = new TrustManager[]{};
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i];
                if (TextUtils.isEmpty(fileName) || !fileName.startsWith("certificate")) {
                    continue;
                }
                trustManagers = addTrustManagers(trustManagers, getTrustManagers(fileName));
            }
            if (ListUtil.isEmpty(trustManagers)) {
                LogUtils.e(RetrofitCreateHelper.TAG + "getSSLSocketFactory ListUtil.isEmpty(trustManagers)");
                return null;
            }
            // Create an SSLContext that uses our TrustManager
            SSLContext s = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
            s.init(null, trustManagers, null);
            return s.getSocketFactory();
        } catch (Throwable t) {
            LogUtils.e(RetrofitCreateHelper.TAG + "getSSLSocketFactory Throwable:" + t);
        }
        return null;
    }

    public static TrustManager[] getTrustManagers(String fileName) {
        try {
            LogUtils.d(RetrofitCreateHelper.TAG + "getTrustManagers fileName:" + fileName);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //把证书打包在asset文件夹中 BuildConfig.AUTH_CERT：证书名称
            InputStream caInput = new BufferedInputStream(BaseConstant.Base.sApp.getAssets().open(fileName));
            X509Certificate ca;
            try {
                ca = (X509Certificate) cf.generateCertificate(caInput);
                LogUtils.d(RetrofitCreateHelper.TAG + "getSSlFactory ca SubjectDN:" + ca.getSubjectDN() + ",PublicKey:" + ca.getPublicKey());
            } finally {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            return tmf.getTrustManagers();
        } catch (Throwable e) {
            LogUtils.e(RetrofitCreateHelper.TAG + "getTrustManagers Throwable:" + e);
        }
        return null;
    }

    /**
     * 需求：按照下标相同的位置依次将以上两个数组中的元素插入新的数组
     * 思路	1：声明一个新的数组长度为两个数组的和
     * 2:将第一个数组循环赋值给新数组，注意下标为2n
     * 3:将第二个数组循环赋值给新数组，注意下标为2n+1
     * 4:第二个数组长度如果与第一个数组长度不一样怎么办？
     * 思路： 当第一个数组赋值完成之后第二个数组依次赋值即可
     */
    public static TrustManager[] addTrustManagers(TrustManager[] arr0, TrustManager[] arr1) {
        TrustManager[] arr2 = new TrustManager[arr0.length + arr1.length];
        // 循环次数找两个数组长度最大的那个数组
        int max = arr0.length > arr1.length ? arr0.length : arr1.length;
        int min = arr0.length < arr1.length ? arr0.length : arr1.length;
        TrustManager[] maxArray = arr0.length > arr1.length ? arr0 : arr1;
        for (int i = 0; i < max; i++) {
            if (i < min) {
                arr2[2 * i] = arr0[i];
                arr2[2 * i + 1] = arr1[i];
            } else {
                arr2[i + min] = maxArray[i];
            }
        }
        return arr2;
    }

    private static SSLSocketFactory getSslSocketFactoryBase(X509TrustManager trustManager, InputStream bksFile, String password, InputStream... certificates) {
        try {
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            // 创建TLS类型的SSLContext对象， that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            // 通过sslContext获取SSLSocketFactory对象
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;
            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientKeyStore, password.toCharArray());
            return kmf.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

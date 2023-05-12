package com.jelly.hook.module.main.utils;

public class ProxyEntity {

    public interface ProxyEntityListener {

        String getText(int num);
    }

    public static ProxyEntityListener getProxyEntityListener(int num2) {
        return new ProxyEntityListener() {
            @Override
            public String getText(int num) {
                return "ProxyEntityListener num:" + num + ",num2:" + num2;
            }
        };
    }
}

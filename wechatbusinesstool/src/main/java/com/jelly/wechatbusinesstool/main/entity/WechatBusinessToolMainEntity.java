package com.jelly.wechatbusinesstool.main.entity;

import androidx.fragment.app.Fragment;

public class WechatBusinessToolMainEntity {
    public int position;
    public int title;
    public int navigationItemId;
    public Fragment fragment;

    public WechatBusinessToolMainEntity() {
    }

    public WechatBusinessToolMainEntity(int position, int title, int navigationItemId, Fragment fragment) {
        this.position = position;
        this.title = title;
        this.navigationItemId = navigationItemId;
        this.fragment = fragment;
    }
}

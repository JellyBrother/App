package com.jelly.wechatbusinesstool.main.viewmodel;

import android.os.Bundle;

import com.jelly.baselibrary.base.BaseLiveData;
import com.jelly.baselibrary.base.BaseViewModel;

public class WechatBusinessToolMainActViewModel extends BaseViewModel {
    private static final String TAG = "WechatBusinessToolMainActViewModel";
    public BaseLiveData<String> mToastText;

    public WechatBusinessToolMainActViewModel() {
        mToastText = newLiveData();
    }

    @Override
    public void initData(Bundle bundle) {
    }

    @Override
    public void destroyData() {
        super.destroyData();
    }
}

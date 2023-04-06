package com.jelly.chat.main.viewmodel;

import android.os.Bundle;

import com.jelly.baselibrary.base.BaseLiveData;
import com.jelly.baselibrary.base.BaseViewModel;

public class MeFraViewModel extends BaseViewModel {
    private static final String TAG = "MeFraViewModel";
    public BaseLiveData<String> mToastText;

    public MeFraViewModel() {
        mToastText = newLiveData();
    }

    @Override
    public void initData(Bundle bundle) {
    }
}

package com.example.myapp.base.widget.helper;

import android.text.Editable;
import android.text.TextWatcher;

import com.example.myapp.base.utils.LogUtils;

public class PalmTextWatcher implements TextWatcher {
    protected String TAG = "PalmTextWatcher";

    public PalmTextWatcher() {
        TAG = getClass().getSimpleName();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        LogUtils.d(TAG, "beforeTextChanged CharSequence:" + s + ",start:" + start + ",count:" + count + ",after:" + after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        LogUtils.d(TAG, "onTextChanged CharSequence:" + s + ",start:" + start + ",before:" + before + ",count:" + count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        LogUtils.d(TAG, "afterTextChanged Editable:" + s);
    }
}

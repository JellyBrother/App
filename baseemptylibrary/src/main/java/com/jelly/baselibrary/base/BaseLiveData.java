package com.jelly.baselibrary.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

/**
 * Author：
 * Date：2019.11.20 14:09
 * Description：LiveData的实现类，自动根据上下文的生命周期管理数据。
 */
public class BaseLiveData<T> extends LiveData<T> {
    private Observer<T> mObserver;

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    public void observeForever(@NonNull Observer<T> observer) {
        if (mObserver == null) {
            super.observeForever(observer);
            mObserver = observer;
        }
    }

    /**
     * Author：
     * Date：2019.11.20 14:11
     * Description：开始订阅
     *
     * @param observer 观察者
     */
    public void observe(@NonNull Observer<T> observer) {
        this.observeForever(observer);
    }

    /**
     * Author：
     * Date：2019.11.20 14:11
     * Description：移除观察者
     */
    public void removeObserver() {
        if (mObserver != null) {
            super.removeObserver(mObserver);
            mObserver = null;
        }
    }
}

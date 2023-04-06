package com.example.myapp.base.rxjava;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RxjavaUtil {

    public static <T> Observable<T> setObservable(Observable<T> observable) {
        if (observable == null) {
            return null;
        }
        return observable.subscribeOn(Schedulers.io()).onErrorReturn(new BaseOnErrorReturn());
    }

    public static <T> Observable<T> setObservable2(Observable<T> observable) {
        if (observable == null) {
            return null;
        }
        return observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).onErrorReturn(new BaseOnErrorReturn());
    }
}

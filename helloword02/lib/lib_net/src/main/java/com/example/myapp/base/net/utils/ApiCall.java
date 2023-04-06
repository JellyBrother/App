package com.example.myapp.base.net.utils;

import com.example.myapp.base.net.utils.annotation.RetryCount;
import com.example.myapp.base.net.utils.annotation.RetryDelay;
import com.example.myapp.base.net.utils.annotation.RetryIncreaseDelay;

import java.io.IOException;
import java.lang.annotation.Annotation;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

public class ApiCall<R> {
    private final Observable<Response<ApiResponse<R>>> mEnqueueObservable;
    private int mRetryCount;
    private long mRetryDelay;
    private long mRetryIncreaseDelay;
    private Disposable mDisposable;
    private final Call<ApiResponse<R>> mCall;

    ApiCall(Annotation[] annotations, Call<ApiResponse<R>> call) {
        mCall = call;
        mEnqueueObservable = RxJavaPlugins.onAssembly(new CallEnqueueObservable<>(call));
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> clazz = annotation.annotationType();
            if (clazz == RetryCount.class) {
                RetryCount retryCount = (RetryCount) annotation;
                mRetryCount = retryCount.value();
            } else if (clazz == RetryDelay.class) {
                RetryDelay retryDelay = (RetryDelay) annotation;
                mRetryDelay = retryDelay.value();
            } else if (clazz == RetryIncreaseDelay.class) {
                RetryIncreaseDelay retryIncreaseDelay = (RetryIncreaseDelay) annotation;
                mRetryIncreaseDelay = retryIncreaseDelay.value();
            }
        }
    }

    /**
     * 进入请求队列
     *
     * @param callback 请求回调
     */
    public void enqueue(ApiCallback<R> callback) {
        Observable<Response<ApiResponse<R>>> observable;
        observable = mEnqueueObservable;
        mDisposable = observable.retryWhen(new RetryHandler<R>(mRetryCount, mRetryDelay, mRetryIncreaseDelay))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
//                        callback.onStart();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ApiResponse<R>>>() {
                    @Override
                    public void accept(Response<ApiResponse<R>> response) throws Exception {
                        ApiResponse<R> body = response.body();
                        if (!NetUtil.isSuccess(response.code()) || body == null) {
                            onError(callback, new HttpException(response));
                            cancel();
                            return;
                        }
                        callback.onSuccess(body);
                        cancel();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        onError(callback, throwable);
                        cancel();
                    }
                });
    }

    /**
     * Synchronously send the request and return its response.
     *
     * @throws IOException if a problem occurred talking to the server.
     */
    public Response<ApiResponse<R>> exectue() throws IOException {
        return mCall.clone().execute();
    }

    /**
     * 处理错误
     *
     * @param callback  回调
     * @param throwable 错误
     */
    private void onError(ApiCallback<R> callback, Throwable throwable) {
        callback.onError(throwable);
    }

    public void cancel() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
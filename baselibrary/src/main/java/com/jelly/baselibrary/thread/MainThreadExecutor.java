package com.jelly.baselibrary.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Author：
 * Date：2019.10.17 15:05
 * Description：post 线程到主线程执行
 */
public class MainThreadExecutor implements Executor {
    private final Handler mHandler;

    public MainThreadExecutor() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(Runnable runnable) {
        mHandler.post(runnable);
    }

}

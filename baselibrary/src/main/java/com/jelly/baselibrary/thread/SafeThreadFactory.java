package com.jelly.baselibrary.thread;

import java.util.concurrent.ThreadFactory;

/**
 * Created by YangLei-11036126 on 2019/01/26.
 * 对线程池中新创建的线程作保护
 */
public class SafeThreadFactory implements ThreadFactory {
    private String mThreadName;

    private Thread.UncaughtExceptionHandler mExceptionHandler;

    public SafeThreadFactory(String threadName) {
        mThreadName = threadName;
    }

    public SafeThreadFactory(String threadName, Thread.UncaughtExceptionHandler exceptionHandler) {
        this(threadName);
        mExceptionHandler = exceptionHandler;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, mThreadName);
        if(mExceptionHandler == null) {
            mExceptionHandler = new ThreadExceptionHandler();
        }
        thread.setUncaughtExceptionHandler(mExceptionHandler);
        return thread;
    }
}

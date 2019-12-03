package com.jelly.baselibrary.thread;

import com.jelly.baselibrary.utils.LogUtil;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 提供安全的线程池，和同步/异步调用方法的包装。特点如下：
 * 1. 对同一个module，只创建唯一的后台线程，来保证线程安全
 * 2. 如果后台线程发生未知的崩溃(uncaughtException)，将触发保护机制，不会导致主进程崩溃。同时上传错误日志，帮助分析
 * 3. 同步方法提供了超时机制(以毫秒为单位)，避免发生阻塞
 */
public class SafeExecutor {
    private static final String TAG = "SafeExecutor";
    protected ExecutorService mExeService;
    protected static HashMap<String, ExecutorService> mServiceMap = new HashMap<>();

    protected SafeExecutor() {
    }

    public SafeExecutor(String moduleName) {
        if (mServiceMap.containsKey(moduleName)) {
            mExeService = mServiceMap.get(moduleName);
        } else {
            ThreadFactory threadFactory = new SafeThreadFactory(moduleName + "Thread");
            mExeService = Executors.newSingleThreadExecutor(threadFactory);
            mServiceMap.put(moduleName, mExeService);
        }
    }

    public static void shutDown(String mouduleName) {
        if (mServiceMap.containsKey(mouduleName)) {
            mServiceMap.remove(mouduleName);
        }
    }

    /**
     * 同步接口包装
     *
     * @param callable 被调用接口的包装
     * @param timeout  超时时间，以毫秒为单位
     * @param <Result> 返回值类型
     * @return
     */
    public <Result> Result syncExecute(final Callable<Result> callable, long timeout) {
        Future<Result> future = mExeService.submit(callable);
        Result result = null;

        try {
            result = future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
            LogUtil.getInstance().e(TAG, "syncExecute error " + e.toString());
        }

        return result;
    }

    public <Result> Result syncExecute(final Callable<Result> callable) {
        Future<Result> future = mExeService.submit(callable);
        Result result = null;

        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            LogUtil.getInstance().e(TAG, "syncExecute error " + e.toString());
        }

        return result;
    }

    /**
     * 异步接口包装
     *
     * @param runnable
     */
    public void asyncExecute(final Runnable runnable) {
        mExeService.execute(runnable);
    }

    public ExecutorService getExecutorService(String tag) {
        return mServiceMap.get(tag);
    }
}

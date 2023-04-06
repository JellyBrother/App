package com.example.myapp.base.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public abstract class BaseHandler<T> extends Handler {
    private static final int MESSAGE_PAGE_GONE = 100;
    // 弱引用
    private final WeakReference<T> wPage;

    public BaseHandler(T page) {
        this.wPage = new WeakReference<T>(page);
    }

    public BaseHandler(T page, Looper looper) {
        super(looper);
        this.wPage = new WeakReference<T>(page);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (wPage == null || wPage.get() == null) {
            return;
        }
        T t = wPage.get();
        int what = msg.what;
        Object obj = msg.obj;
        if (t instanceof Activity) {
            if (Utils.isActivityDestroy((Activity) t)) {
                handleMessage(t, MESSAGE_PAGE_GONE, obj, msg);
                return;
            }
        }
        if (t instanceof Fragment) {
            if (((Fragment) t).isRemoving()) {
                handleMessage(t, MESSAGE_PAGE_GONE, obj, msg);
                return;
            }
        }
        if (t instanceof View) {
            View view = (View) t;
            Activity activity = Utils.getActivity(view.getContext());
            if (Utils.isActivityDestroy(activity)) {
                handleMessage(t, MESSAGE_PAGE_GONE, obj, msg);
                return;
            }
        }
        handleMessage(t, what, obj, msg);
    }

    public abstract void handleMessage(T t, int what, Object obj, Message msg);
}

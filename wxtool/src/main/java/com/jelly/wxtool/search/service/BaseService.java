package com.jelly.wxtool.search.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class BaseService extends AccessibilityService {
    protected static String TAG = "BaseService";
    protected AccessibilityService mService;

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}

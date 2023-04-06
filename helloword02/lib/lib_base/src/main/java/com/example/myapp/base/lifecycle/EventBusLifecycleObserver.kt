package com.example.myapp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import org.greenrobot.eventbus.EventBus

open class EventBusLifecycleObserver : BaseLifecycleObserver() {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate(owner: LifecycleOwner) {
        EventBus.getDefault().register(owner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        EventBus.getDefault().unregister(owner)
    }
}
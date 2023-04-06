package com.example.myapp.base.viewbinding

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class ViewBindingProperty<in R : LifecycleOwner, T : ViewBinding>(
    private val viewBinder: (R) -> T
) : ReadOnlyProperty<R, T> {
    private var viewBinding: T? = null
    private val lifecycleObserver = ClearLifecycleObserver()
    private var thisRef: R? = null

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        viewBinding?.let { return it }

        this.thisRef = thisRef
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            lifecycle.addObserver(lifecycleObserver)
        } else {
            mainHandler.post { viewBinding = null }
        }

        return viewBinder(thisRef).also { viewBinding = it }
    }

    private inner class ClearLifecycleObserver : DefaultLifecycleObserver {

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            val ref = thisRef ?: return
            thisRef = null
            getLifecycleOwner(ref).lifecycle.removeObserver(lifecycleObserver)
            mainHandler.post { viewBinding = null }
        }
    }

    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}

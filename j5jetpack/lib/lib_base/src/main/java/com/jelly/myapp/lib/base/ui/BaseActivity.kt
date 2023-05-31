package com.jelly.myapp.lib.base.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View

/**
 * 基类
 */
open class BaseActivity : Activity() {
    protected var TAG = "BaseActivity"
    protected var mOnCreateTime: Long = 0
    protected var onResumeTime: Long = 0
    protected var mIsOnSaveInstance = false
    protected var mRootView: View? = null

    companion object {
        const val BUNDLE_KEY_IS_ON_SAVE_INSTANCE = "Bundle_key_IsOnSaveInstance"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            TAG = javaClass.simpleName
            mOnCreateTime = System.currentTimeMillis()
            log("onCreate")
            initIntent(savedInstanceState)
            initContentView()
            initObserver()
        } catch (t: Throwable) {
            Log.e(TAG, "onCreate t:", t)
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        try {
            log("onContentChanged")
            initView()
            initListener()
            setViewSize(resources.configuration)
            initData()
        } catch (t: Throwable) {
            Log.e(TAG, "onContentChanged t:", t)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        log("onRestoreInstanceState")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        log("onRestoreInstanceState")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
        mIsOnSaveInstance = false
        onResumeTime = System.currentTimeMillis()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        log("onNewIntent")
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        log("onSaveInstanceState")
        outState.putBoolean(BUNDLE_KEY_IS_ON_SAVE_INSTANCE, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log("onConfigurationChanged")
        setViewSize(newConfig)
    }

    protected fun log(msg: String) {
        val intervalTime = System.currentTimeMillis() - mOnCreateTime
        Log.e("liuguodong", "$TAG $msg,Interval time:$intervalTime")
    }

    protected fun initIntent(bundle: Bundle?) {
        var bundle = bundle
        if (bundle == null) {
            bundle = intent.extras
        }
        if (bundle != null) {
            mIsOnSaveInstance = bundle.getBoolean(BUNDLE_KEY_IS_ON_SAVE_INSTANCE, false)
        }
    }

    protected fun initContentView() {
        mRootView = getLayoutView()
        if (mRootView != null) {
            setContentView(mRootView)
        }
    }

    protected open fun getLayoutView(): View? {
        return null
    }

    protected fun initObserver() {}
    protected fun initView() {}
    protected fun initListener() {}
    protected fun setViewSize(configuration: Configuration?) {}
    protected fun initData() {}
}
package com.jelly.hook.module.main.ui.activity

import android.util.Log
import android.view.View
import com.jelly.hook.module.base.ui.BaseActivity
import com.jelly.hook.module.base.utils.ToastUtil
import com.jelly.hook.module.main.databinding.MainActHookBinding
import com.jelly.hook.module.main.utils.ProxyEntity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

class HookActivity : BaseActivity() {
    private lateinit var mBinding: MainActHookBinding

    companion object {
        private const val TAG = "HookActivity"
    }

    override fun getLayoutView(): View {
        mBinding = MainActHookBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initListener() {
        super.initListener()
        mBinding.tvwTest1.setOnClickListener {
            ToastUtil.showShort("tvwTest1")
            proxyObject()
        }
        mBinding.tvwTest2.setOnClickListener {
            Log.e(TAG, "mBinding.tvwTest2 onClick")
            proxyStaticObject()
        }
        mBinding.tvwTest3.setOnClickListener {
            ToastUtil.showShort("tvwTest3")
        }
    }

    private fun proxyObject() {
        val tvwTest13OnClickListener = View.OnClickListener {
            mBinding.tvwTest3.text = "将点击事件代理到tvw_test3"
        }
        val invocationHandler =
            InvocationHandler { _, method, args ->
                ToastUtil.showShort("代理对象")
                method.invoke(tvwTest13OnClickListener, *args)
            }
        val hookOnClickListener = Proxy.newProxyInstance(
            tvwTest13OnClickListener.javaClass.classLoader,
            tvwTest13OnClickListener.javaClass.interfaces, invocationHandler
        ) as View.OnClickListener
        hookOnClickListener.onClick(mBinding.tvwTest3)
    }

    private fun proxyStaticObject() {
        try {
            val utils = Class.forName("com.jelly.hook.module.main.utils.ProxyEntity")
            val getUtilsListener = utils.getMethod(
                "getProxyEntityListener",
                Int::class.javaPrimitiveType
            )
            val listener = getUtilsListener.invoke(utils, 50)
            val proxyEntityListener: ProxyEntity.ProxyEntityListener = Proxy.newProxyInstance(
                listener.javaClass.classLoader,
                listener.javaClass.interfaces
            ) { _, method, args ->
                Log.e(
                    TAG,
                    "proxyStaticObject invoke1：" + Log.getStackTraceString(Exception())
                )
                val invoke = method.invoke(listener, *args)
                Log.e(TAG, "proxyStaticObject invoke2：$invoke")
                "invoke:$invoke"
            } as ProxyEntity.ProxyEntityListener
            val text: String = proxyEntityListener.getText(20)
            ToastUtil.showShort(text)
            Log.e(TAG, "proxyStaticObject invoke3：$text")
        } catch (t: Throwable) {
            Log.e(TAG, "proxyStaticObject Throwable：" + Log.getStackTraceString(t))
        }
    }
}
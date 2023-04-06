package com.example.myapp.base.bridge.utils

import android.text.TextUtils
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import com.hjq.gson.factory.GsonFactory
import com.example.myapp.base.constant.BaseConstant
import com.example.myapp.base.constant.Env
import com.example.myapp.base.net.utils.NetUtil
import com.example.myapp.base.utils.AppUtils
import com.example.myapp.base.utils.LogUtils
import com.example.myapp.base.utils.SPUtils
import com.example.myapp.base.utils.Utils
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object PolicyUtil {
    val AGREE = "agree"
    val AGREE_PRIVACY = "agree_privacy"
    var userAgreePrivacy: String = ""

    fun userAgreePrivacy(isUserAgreePrivacy: Boolean) {
        userAgreePrivacy = if (isUserAgreePrivacy) {
            "1"
        } else {
            "0"
        }
        SPUtils.getInstance(AGREE).put(AGREE_PRIVACY, isUserAgreePrivacy)
        if (isUserAgreePrivacy) {
            submitPolicy()
        }
    }

    fun isUserAgreePrivacy(): Boolean {
        if (TextUtils.equals(userAgreePrivacy, "")) {
            val isUserAgreePrivacy = SPUtils.getInstance(AGREE).getBoolean(AGREE_PRIVACY, false)
            userAgreePrivacy = if (isUserAgreePrivacy) {
                "1"
            } else {
                "0"
            }
            return isUserAgreePrivacy
        }
        return TextUtils.equals(userAgreePrivacy, "1")
    }

    fun submitPolicy() {
        if (isUserAgreePrivacy()) {
            GlobalScope.launch {
                initBugly()
            }
        }
    }

    /**
     * 腾讯兔小巢初始化
     */
    private fun initBugly() {
//////////////////////////////////////////////// 腾讯兔小巢 ///////////////////////////////////////////////////////
        // 参考https://bugly.qq.com/docs/user-guide/advance-features-android/
        val strategy = UserStrategy(Utils.getApp())
        val map = NetUtil.builderCommonParams()
        //设备id
        strategy.deviceID =
            map[BaseConstant.HttpConfig.HEAD_DEV_ID] + "-" + map[BaseConstant.HttpConfig.HEAD_ID_CARD]
        //设备型号
        strategy.deviceModel = map[BaseConstant.HttpConfig.HEAD_DEV_NAME]
        //渠道
        strategy.appChannel = BaseConstant.Environment.environ.name
        //版本
        strategy.appVersion = map[BaseConstant.HttpConfig.HEAD_VERSION]
        //包名
        strategy.appPackageName = AppUtils.getAppPackageName()
        ////////////////////////////////////////////////最新版SDK支持trace文件采集和anr过程中的主线程堆栈信息采集，由于抓取堆栈的系统接口 Thread.getStackTrace 可能造成crash，建议只对少量用户开启//////
        // 设置anr时是否获取系统trace文件，默认为false
//                strategy.setEnableCatchAnrTrace( boolean enable);
        // 设置是否获取anr过程中的主线程堆栈，默认为true
//                strategy.setEnableRecordAnrMainStack( boolean enable);
        //CrashReport.setAllThreadStackEnable(Context context, boolean crashEnable, boolean anrEnable);
////////////////////////////////////////////////最新版SDK支持trace文件采集和anr过程中的主线程堆栈信息采集，由于抓取堆栈的系统接口 Thread.getStackTrace 可能造成crash，建议只对少量用户开启//////
        //设置标签 自定义标签，用于标明App的某个“场景”。在发生Crash时会显示该Crash所在的“场景”，以最后设置的标签为准，标签id需大于0。例：当用户进入界面A时，打上9527的标签：
//                strategy.setUserSceneTag(context, 9527); // 上报后的Crash会显示该标签
        //设置自定义Map参数 自定义Map参数可以保存发生Crash时的一些自定义的环境信息。在发生Crash时会随着异常信息一起上报并在页面展示。
//                strategy.putUserData(context, "userkey", "uservalue");
        //设置Crash回调
        //strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
        var buglyAppId = "0d7c0584c7"
        if (BaseConstant.Environment.environ == Env.PRO) {
            buglyAppId = "088b809183"
        }
        CrashReport.initCrashReport(Utils.getApp(), buglyAppId, true, strategy)
        //在开发测试阶段，可以在初始化Bugly之前通过以下接口把调试设备设置成“开发设备”
        CrashReport.setIsDevelopmentDevice(Utils.getApp(), BaseConstant.Base.isDebug)
        //////////////////////////////////////////////// 腾讯兔小巢 ///////////////////////////////////////////////////////
        // 设置 Json 解析容错监听
        GsonFactory.setJsonCallback { typeToken: TypeToken<*>, fieldName: String, jsonToken: JsonToken ->
            // 上报到 Bugly 错误列表
            val text =
                "接口数据类型解析异常：$typeToken#$fieldName，后台返回的类型为：$jsonToken"
            CrashReport.postCatchedException(IllegalArgumentException(text))
            LogUtils.e(text)
        }
    }
}
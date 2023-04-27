package com.example.myapp.base.bridge.jump

import com.alibaba.android.arouter.launcher.ARouter
import com.example.myapp.base.bridge.constant.ARouterConstant
import com.example.myapp.base.utils.ClickUtils

object UserJumpUtils {

    fun jumpServiceActivity() {
        if (ClickUtils.isDupClick(ARouterConstant.User.USER_SERVICE_ACTIVITY)) {
            return
        }
        ARouter.getInstance().build(ARouterConstant.User.USER_SERVICE_ACTIVITY).navigation()
    }
}
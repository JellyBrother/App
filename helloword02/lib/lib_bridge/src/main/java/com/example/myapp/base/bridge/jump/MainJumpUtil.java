package com.example.myapp.base.bridge.jump;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.myapp.base.bridge.constant.ARouterConstant;
import com.example.myapp.base.utils.ClickUtils;
import com.example.myapp.base.utils.JumpUtils;

public class MainJumpUtil {
    private static final String TAG = "MainJumpUtil";

    /**
     * 主模块的跳转不能用ARouter，因为ARouter初始化耗时，放在了子线程，如果用ARouter的跳转的话，可能导致ARouter没初始化完成
     * 跳转主界面
     */
    public static void jumpMainActivity() {
        if (ClickUtils.isDupClick(ARouterConstant.Main.MAIN)) {
            return;
        }
//        Intent intent = new Intent();
//        intent.setClassName("com.example.myapp", "com.example.mylibrary.MainActivity2");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Utils.getApp().startActivity(intent);

        ARouter.getInstance().build(ARouterConstant.Main.MAIN).navigation();
    }

    /**
     * 主模块的跳转不能用ARouter，因为ARouter初始化耗时，放在了子线程，如果用ARouter的跳转的话，可能导致ARouter没初始化完成
     * 跳转登录界面
     */
    public static void jumpUmLoginActivity() {
        JumpUtils.INSTANCE.jumpLoginPage(true);
//         UserJumpUtil.INSTANCE.jumpPhoneLoginActivity();
    }
}

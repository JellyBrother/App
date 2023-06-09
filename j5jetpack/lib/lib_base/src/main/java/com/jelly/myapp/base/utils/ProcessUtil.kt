package com.jelly.myapp.base.utils

import android.app.ActivityManager
import com.jelly.myapp.base.constant.BaseConstant
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.Process

object ProcessUtil {
    /**
     * Description：判断当前应用是否在前台
     *
     * @return true在前台
     */
    val isAppOnForeground: Boolean
        get() {
            val activityManager =
                BaseConstant.Base.sApp?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    ?: return false
            val appProcesses = activityManager.runningAppProcesses
            if (appProcesses == null || appProcesses.isEmpty()) {
                return false
            }
            val pid = Process.myPid()
            for (appProcess in appProcesses) {
                if (appProcess != null && appProcess.pid == pid && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true
                }
            }
            return false
        }
}
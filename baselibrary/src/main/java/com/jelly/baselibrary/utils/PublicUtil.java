package com.jelly.baselibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jelly.baselibrary.common.BaseCommon;

import java.util.List;

/**
 * Author：
 * Date：2019.11.25 16:15
 * Description：工具类
 */
public class PublicUtil {
    private static final String TAG = "PublicUtils";
    // 防止快速点击，最后点击时间
    private static long mLastClickTime;

    /**
     * Author：liuguodong
     * Date：2019.11.21 10:49
     * Description：判定集合是否为空
     *
     * @param list 集合
     * @return true是空集合
     */
    public static boolean isEmptyList(List list) {
        if (list == null) {
            return true;
        }
        if (list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static <T> boolean isEmptyArray(T[] list) {
        return list == null || list.length == 0;
    }

    /**
     * Author：
     * Date：2019.11.21 15:26
     * Description：判断是否有网络
     *
     * @param context 上下文
     * @return true没有网络
     */
    public static boolean isNetWorkDisconnect(Context context) {
        //连接服务 CONNECTIVITY_SERVICE
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //网络信息 NetworkInfo
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return false;
        }
        //没有网络
        return true;
    }

    /**
     * Author：
     * Date：2019.11.27 14:44
     * Description：判断是否快速点击
     *
     * @return true是快速点击
     */
    public static boolean isFastClik() {
        if (System.currentTimeMillis() - mLastClickTime >= BaseCommon.Base.INTERVAL_TIME_START_ACTIVITY) {
            mLastClickTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }
}

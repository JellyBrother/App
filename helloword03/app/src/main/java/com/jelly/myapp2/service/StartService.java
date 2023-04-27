package com.jelly.myapp2.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.jelly.myapp2.R;
import com.jelly.myapp2.base.constant.Constant;
import com.jelly.myapp2.base.ui.BaseService;

import java.util.List;

public class StartService extends BaseService {

    /**
     * 当版本大于等于安卓26，并且不是前台进程的时候，要进行通知栏提示
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isAppOnForeground()) {
            String channelName = "前台服务测试";
            String title = channelName + " 正在运行";
            String id = "test";
            String groupId = "group";

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(id, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setVibrationPattern(new long[0]);
            channel.setSound(null, null);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                NotificationChannelGroup notificationChannelGroup = manager.getNotificationChannelGroup(groupId);
                if (notificationChannelGroup != null)
                    channel.setGroup(groupId);
            }
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, id)
                    .setVibrate(null).setSound(null)
                    .setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(title)
                    .setAutoCancel(false).setOngoing(true).build();
            manager.notify(id.hashCode(), notification);
            startForeground(id.hashCode(), notification);
            Log.e(Constant.Log.TAG, "service StartService onStartCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 判断当前是否是前台进程
     */
    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null || appProcesses.isEmpty()) {
            return false;
        }
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess != null && appProcess.pid == pid
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}

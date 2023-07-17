package com.d08a3hqr.chtjikfd;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.File;

public class OtherUtil {

    public static boolean isIndia(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) {
            return false;
        }
        String operator = telManager.getSimOperator();
        if (operator != null && !operator.isEmpty()) {
            String mcc = operator.substring(0, 3);
            if (mcc.equals("404") || mcc.equals("405") || mcc.equals("406")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoot() {
        boolean hasRootDir = false;
        String[] rootDirs;
        String[] rootRelatedDirs = new String[]{"/su", "/su/bin/su", "/sbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/data/local/su", "/system/xbin/su", "/system/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/system/bin/cufsdosck", "/system/xbin/cufsdosck", "/system/bin/cufsmgr", "/system/xbin/cufsmgr", "/system/bin/cufaevdd", "/system/xbin/cufaevdd", "/system/bin/conbb", "/system/xbin/conbb"};
        int dirCount = (rootDirs = rootRelatedDirs).length;
        for (int i = 0; i < dirCount; ++i) {
            String dir = rootDirs[i];
            if ((new File(dir)).exists()) {
                hasRootDir = true;
                break;
            }
        }
        return Build.TAGS != null && Build.TAGS.contains("test-keys") || hasRootDir;
    }
}

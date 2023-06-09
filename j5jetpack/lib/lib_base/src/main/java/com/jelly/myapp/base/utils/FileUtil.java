package com.jelly.myapp.base.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/05/03
 *     desc  : utils about file
 * </pre>
 */
public final class FileUtil {
    private static final String TAG = "FileUtils";

    private static final String LINE_SEP = System.getProperty("line.separator");

    public static File getDir(File dir, String defaultPath) {
        if (dir == null) {
            dir = new File(defaultPath);
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getDirPath(File dir, String defaultPath) {
        if (dir == null) {
            return defaultPath;
        }
        String absolutePath = null;
        try {
            absolutePath = dir.getCanonicalPath();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(absolutePath)) {
            absolutePath = dir.getAbsolutePath();
        }
        if (TextUtils.isEmpty(absolutePath)) {
            absolutePath = defaultPath;
        }
        return absolutePath;
    }
}

package com.example.myapp.base.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.example.myapp.base.utils.Utils;

import java.io.File;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/27
 *     desc  : utils about clean
 * </pre>
 */
public final class CleanUtils {

    private CleanUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Clean the internal files.
     * <p>directory: /data/data/package/files</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalFiles() {
        return FileUtils.deleteAllInDir(CachePath.getFileDir());
    }

    /**
     * Clean the internal databases.
     * <p>directory: /data/data/package/databases</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalDbs() {
        return FileUtils.deleteAllInDir(new File(CachePath.getFileDir().getParent(), "databases"));
    }

    /**
     * Clean the internal database by name.
     * <p>directory: /data/data/package/databases/dbName</p>
     *
     * @param dbName The name of database.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalDbByName(final String dbName) {
        return Utils.getApp().deleteDatabase(dbName);
    }

    /**
     * Clean the internal shared preferences.
     * <p>directory: /data/data/package/shared_prefs</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanInternalSp() {
        return FileUtils.deleteAllInDir(new File(CachePath.getFileDir().getParent(), "shared_prefs"));
    }

    /**
     * Clean the external cache.
     * <p>directory: /storage/emulated/0/android/data/package/cache</p>
     *
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanExternalCache() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && FileUtils.deleteAllInDir(Utils.getApp().getExternalCacheDir());
    }

    /**
     * Clean the custom directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean cleanCustomDir(final String dirPath) {
        return FileUtils.deleteAllInDir(FileUtils.getFileByPath(dirPath));
    }

    public static void cleanAppUserData() {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        am.clearApplicationUserData();
    }

    public static boolean cleanHttpCache() {
        return FileUtils.deleteAllInDir(CachePath.getHttpCacheDir());
    }

    public static boolean cleanShareCache() {
        return FileUtils.deleteAllInDir(CachePath.getHttpCacheDir());
    }

    public static boolean cleanCacheDir() {
        return FileUtils.deleteAllInDir(CachePath.getCacheDir());
    }

    public static boolean cleanUseFileDir() {
        return FileUtils.deleteAllInDir(CachePath.getUseFileDir());
    }
}

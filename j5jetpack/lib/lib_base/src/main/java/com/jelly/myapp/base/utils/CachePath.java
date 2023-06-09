package com.jelly.myapp.base.utils;

import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.jelly.myapp.base.constant.BaseConstant;

import java.io.File;

public class CachePath {
    private static final String TAG = "PalmHouseCachePath";

    public static File getCacheDir() {
        return FileUtil.getDir(Utils.getApp().getCacheDir(), BaseConstant.Path.CACHE);
    }

    public static String getCachePath() {
        return FileUtil.getDirPath(getCacheDir(), BaseConstant.Path.CACHE);
    }

    public static File getUserCacheDir() {
        String path = getCachePath() + File.separator + UserUtil.getUserIdByMd5();
        return FileUtil.getDir(new File(path), "");
    }

    public static String getUserCachePath() {
        return FileUtil.getDirPath(getUserCacheDir(), "");
    }

    public static File getFileDir() {
        return FileUtil.getDir(Utils.getApp().getFilesDir(), BaseConstant.Path.FILE);
    }

    public static String getFilePath() {
        return FileUtil.getDirPath(getFileDir(), BaseConstant.Path.FILE);
    }

    public static File getUseFileDir() {
        String path = getFilePath() + File.separator + UserUtil.getUserIdByMd5();
        return FileUtil.getDir(new File(path), "");
    }

    public static String getUserFilePath() {
        return FileUtil.getDirPath(getUseFileDir(), "");
    }

    public static File getShareDir() {
        String path = getUseFileDir() + File.separator + BaseConstant.Path.SHARE;
        return FileUtil.getDir(new File(path), "");
    }

    public static File getShareTempDir() {
        String path = getShareDir() + File.separator + BaseConstant.Path.TEMP + File.separator + BaseConstant.Path.SHARE_TEMP_JPEG;
        boolean orExistsFile = FileUtils.createOrExistsFile(path);
        if (orExistsFile) {
            return new File(path);
        }
        return null;
    }

    public static String getShareTempDirPath() {
        return FileUtil.getDirPath(getShareTempDir(), "");
    }

    public static File getSharePicturesTempDir() {
        String dirPath = FileUtil.getDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        String path = dirPath + File.separator + BaseConstant.Path.SHARE_TEMP_JPEG;
        return FileUtil.getDir(new File(path), "");
    }

    public static String getSharePicturesTempDirPath() {
        return FileUtil.getDirPath(getSharePicturesTempDir(), "");
    }

    public static File getHttpCacheDir() {
        String path = getUserCacheDir() + File.separator + BaseConstant.Path.HTTP_CACHE;
        return FileUtil.getDir(new File(path), "");
    }

    public static File getUseDowloadDir() {
        String path = getUserCacheDir() + File.separator + BaseConstant.Path.DOWLOAD;
        return FileUtil.getDir(new File(path), "");
    }

    public static String getUseDowloadDirPath() {
        return FileUtil.getDirPath(getUseDowloadDir(), "");
    }

    public static File getLogCacheDir() {
        String path = getCacheDir() + File.separator + BaseConstant.Path.LOG;
        return FileUtil.getDir(new File(path), "");
    }

    public static File getVideoCacheDir() {
        String path = getCachePath() + File.separator + BaseConstant.Path.VIDEO_CACHE;
        return FileUtil.getDir(new File(path), "");
    }

    public static String getVideoCacheDirPath() {
        return FileUtil.getDirPath(getVideoCacheDir(), "");
    }
}

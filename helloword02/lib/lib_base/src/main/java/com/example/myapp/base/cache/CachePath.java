package com.example.myapp.base.cache;

import android.os.Environment;

import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.UserUtil;
import com.example.myapp.base.utils.Utils;

import java.io.File;

public class CachePath {
    private static final String TAG = "PalmHouseCachePath";

    public static File getCacheDir() {
        return FileUtils.getDir(Utils.getApp().getCacheDir(), BaseConstant.Path.CACHE);
    }

    public static String getCachePath() {
        return FileUtils.getDirPath(getCacheDir(), BaseConstant.Path.CACHE);
    }

    public static File getUserCacheDir() {
        String path = getCachePath() + File.separator + UserUtil.getUserIdByMd5();
        return FileUtils.getDir(new File(path), "");
    }

    public static String getUserCachePath() {
        return FileUtils.getDirPath(getUserCacheDir(), "");
    }

    public static File getFileDir() {
        return FileUtils.getDir(Utils.getApp().getFilesDir(), BaseConstant.Path.FILE);
    }

    public static String getFilePath() {
        return FileUtils.getDirPath(getFileDir(), BaseConstant.Path.FILE);
    }

    public static File getUseFileDir() {
        String path = getFilePath() + File.separator + UserUtil.getUserIdByMd5();
        return FileUtils.getDir(new File(path), "");
    }

    public static String getUserFilePath() {
        return FileUtils.getDirPath(getUseFileDir(), "");
    }

    public static File getShareDir() {
        String path = getUseFileDir() + File.separator + BaseConstant.Path.SHARE;
        return FileUtils.getDir(new File(path), "");
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
        return FileUtils.getDirPath(getShareTempDir(), "");
    }

    public static File getSharePicturesTempDir() {
        String dirPath = FileUtils.getDirPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        String path = dirPath + File.separator + BaseConstant.Path.SHARE_TEMP_JPEG;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getSharePicturesTempDirPath() {
        return FileUtils.getDirPath(getSharePicturesTempDir(), "");
    }

    public static File getHttpCacheDir() {
        String path = getUserCacheDir() + File.separator + BaseConstant.Path.HTTP_CACHE;
        return FileUtils.getDir(new File(path), "");
    }

    public static File getUseDowloadDir() {
        String path = getUserCacheDir() + File.separator + BaseConstant.Path.DOWLOAD;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getUseDowloadDirPath() {
        return FileUtils.getDirPath(getUseDowloadDir(), "");
    }

    public static File getLogCacheDir() {
        String path = getCacheDir() + File.separator + BaseConstant.Path.LOG;
        return FileUtils.getDir(new File(path), "");
    }

    public static File getVideoCacheDir() {
        String path = getCachePath() + File.separator + BaseConstant.Path.VIDEO_CACHE;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getVideoCacheDirPath() {
        return FileUtils.getDirPath(getVideoCacheDir(), "");
    }
}

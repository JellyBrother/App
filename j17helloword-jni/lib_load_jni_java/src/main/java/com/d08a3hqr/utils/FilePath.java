package com.d08a3hqr.utils;

import android.content.Context;

import java.io.File;

public class FilePath {

    public static Context getApp() {
        return StartConstant.app;
    }

    public static File getFilesDir() {
        String PATH_FILE = StartConstant.dir_name_data + getApp().getPackageName() + File.separator + StartConstant.dir_name_start;
        return FileUtils.getDir(getApp().getDir(StartConstant.dir_name_start, Context.MODE_PRIVATE), PATH_FILE);
    }

    public static String getFilesPath() {
        String PATH_FILE = StartConstant.dir_name_data + getApp().getPackageName() + File.separator + StartConstant.dir_name_start;
        return FileUtils.getDirPath(getFilesDir(), PATH_FILE);
    }

    public static File getRootLoadDir() {
        String path = getFilesPath() + File.separator + StartConstant.dir_name_start_root + StartConstant.assetsName;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getRootLoadPath() {
        return FileUtils.getDirPath(getRootLoadDir(), "");
    }

    public static File getPluginDir() {
        String path = getRootLoadPath() + File.separator + StartConstant.dir_name_start_root_plugin;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginPath() {
        return FileUtils.getDirPath(getPluginDir(), "");
    }

    public static File getPluginUnzipDir() {
        String path = getRootLoadPath() + File.separator + StartConstant.dir_name_start_root_unZip;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnZipPath() {
        return FileUtils.getDirPath(getPluginUnzipDir(), "");
    }

    public static File getDexOptDir() {
        String path = getRootLoadPath() + File.separator + StartConstant.dir_name_start_root_dex;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getDexOptPath() {
        return FileUtils.getDirPath(getDexOptDir(), "");
    }

    public static File getOatDir() {
        String path = getDexOptPath() + File.separator + StartConstant.dir_name_start_root_dex_oat;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getOatPath() {
        return FileUtils.getDirPath(getOatDir(), "");
    }

    public static File getPluginUnZipLibDir() {
        String path = getPluginUnZipPath() + File.separator + StartConstant.dir_name_start_root_plugin_lib;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnZipLibPath() {
        return FileUtils.getDirPath(getPluginUnZipLibDir(), "");
    }
}

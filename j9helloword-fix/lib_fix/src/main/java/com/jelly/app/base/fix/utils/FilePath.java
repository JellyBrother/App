package com.jelly.app.base.fix.utils;

import android.content.Context;

import com.jelly.app.base.fix.PluginLoader;

import java.io.File;

public class FilePath {
    public static final String PATH_FILE = "/data/data/com.example.myapplication/file";
    public static final String PATH_PLUGIN = "Plugin";
    public static final String PATH_PLUGIN_UNZIP = "PluginUnzip";
    public static final String PATH_DEX_OPT_DIR = "dexOptDir";
    public static final String PATH_OAT = "oat";
    public static final String PATH_LIB = "lib";

    public static Context getApp() {
        return PluginLoader.app;
    }

    public static File getFilesDir() {
        return FileUtils.getDir(getApp().getFilesDir(), PATH_FILE);
    }

    public static String getFilesPath() {
        return FileUtils.getDirPath(getFilesDir(), PATH_FILE);
    }

    public static File getPluginDir() {
        String path = getFilesPath() + File.separator + PATH_PLUGIN;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginPath() {
        return FileUtils.getDirPath(getPluginDir(), "");
    }

    public static File getPluginUnzipDir() {
        String path = getFilesPath() + File.separator + PATH_PLUGIN_UNZIP;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnZipPath() {
        return FileUtils.getDirPath(getPluginUnzipDir(), "");
    }

    public static File getDexOptDir() {
        String path = getFilesPath() + File.separator + PATH_DEX_OPT_DIR;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getDexOptPath() {
        return FileUtils.getDirPath(getDexOptDir(), "");
    }

    public static File getOatDir() {
        String path = getDexOptPath() + File.separator + PATH_OAT;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getOatPath() {
        return FileUtils.getDirPath(getOatDir(), "");
    }

    public static File getPluginUnZipLibDir() {
        String path = getPluginUnZipPath() + File.separator + PATH_LIB;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnZipLibPath() {
        return FileUtils.getDirPath(getPluginUnZipLibDir(), "");
    }
}

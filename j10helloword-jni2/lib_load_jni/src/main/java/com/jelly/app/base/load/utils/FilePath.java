package com.jelly.app.base.load.utils;

import android.content.Context;

import com.jelly.app.base.load.Start;

import java.io.File;

public class FilePath {
    public static final String PATH_FILE = "/data/data/com.example.myapplication/file";
    public static final String ROOT_LOAD = "root_load";
    public static final String PATH_PLUGIN_SUBSECTION = "pluginSubsection";
    public static final String PATH_PLUGIN = "plugin";
    public static final String PATH_PLUGIN_UNZIP = "pluginUnzip";
    public static final String PATH_DEX_OPT_DIR = "dexOptDir";
    public static final String PATH_OAT = "oat";
    public static final String PATH_LIB = "lib";

    public static Context getApp() {
        return Start.app;
    }

    public static File getFilesDir() {
        return FileUtils.getDir(getApp().getFilesDir(), PATH_FILE);
    }

    public static String getFilesPath() {
        return FileUtils.getDirPath(getFilesDir(), PATH_FILE);
    }

    public static File getRootLoadDir() {
        String path = getFilesPath() + File.separator + ROOT_LOAD;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getRootLoadPath() {
        return FileUtils.getDirPath(getRootLoadDir(), PATH_FILE);
    }

    public static File getPluginSubsectionDir() {
        String path = getRootLoadPath() + File.separator + PATH_PLUGIN_SUBSECTION;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginSubsectionPath() {
        return FileUtils.getDirPath(getPluginSubsectionDir(), "");
    }

    public static File getPluginDir() {
        String path = getRootLoadPath() + File.separator + PATH_PLUGIN;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginPath() {
        return FileUtils.getDirPath(getPluginDir(), "");
    }

    public static File getPluginUnzipDir() {
        String path = getRootLoadPath() + File.separator + PATH_PLUGIN_UNZIP;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnZipPath() {
        return FileUtils.getDirPath(getPluginUnzipDir(), "");
    }

    public static File getDexOptDir() {
        String path = getRootLoadPath() + File.separator + PATH_DEX_OPT_DIR;
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

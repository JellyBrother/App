package com.jelly.app.base.load.utils;

import android.content.Context;

import com.jelly.app.base.load.Init;

import java.io.File;

public class FilePath {
    public static final String START = "start";
    public static final String ROOT_LOAD = "root_load";
    public static final String PATH_PLUGIN_SUBSECTION = "plSubsection";
    public static final String PATH_PLUGIN = "pl";
    public static final String PATH_AAR = "aar";
    public static final String PATH_PLUGIN_UNZIP = "plUnzip";
    public static final String PATH_DEX_OPT_DIR = "dexOptDir";
    public static final String PATH_OAT = "oat";
    public static final String PATH_LIB = "lib";
    public static final String PATH_AAR_JNI = "jni";

    public static Context getApp() {
        return Init.app;
    }

    public static File getFilesDir() {
        String PATH_FILE = "/data/data/" + getApp().getPackageName() + File.separator + START;
        return FileUtils.getDir(getApp().getDir(START, Context.MODE_PRIVATE), PATH_FILE);
    }

    public static String getFilesPath() {
        String PATH_FILE = "/data/data/" + getApp().getPackageName() + File.separator + START;
        return FileUtils.getDirPath(getFilesDir(), PATH_FILE);
    }

    public static File getRootLoadDir() {
        String path = getFilesPath() + File.separator + ROOT_LOAD + Init.assetsName;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getRootLoadPath() {
        return FileUtils.getDirPath(getRootLoadDir(), "");
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

    public static File getPluginUnAarDir() {
        String path = getRootLoadPath() + File.separator + PATH_AAR;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnAarPath() {
        return FileUtils.getDirPath(getPluginUnAarDir(), "");
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

    public static File getPluginUnAarJniDir() {
        String path = getPluginPath() + File.separator + PATH_AAR_JNI;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnAarJniPath() {
        return FileUtils.getDirPath(getPluginUnAarJniDir(), "");
    }

    public static File getPluginUnAarJniAbiDir(String abi) {
        String path = getPluginUnAarJniPath() + File.separator + abi;
        return FileUtils.getDir(new File(path), "");
    }

    public static String getPluginUnAarJniAbiPath(String abi) {
        return FileUtils.getDirPath(getPluginUnAarJniAbiDir(abi), "");
    }
}

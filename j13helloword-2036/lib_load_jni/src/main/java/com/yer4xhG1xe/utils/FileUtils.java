package com.yer4xhG1xe.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName() + "FUtil";

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

    public static boolean hasFiles(File file) {
        if (file == null) {
            return false;
        }
        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            return false;
        }
        return true;
    }

    public static void copyAssetsFile(Context context, String fileName, String newPath) {
        try {
            File newFile = new File(newPath);
            if (newFile.isFile() && newFile.exists()) {
                return;
            }
            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        } catch (Throwable e) {
            Log.e(TAG, "copyFilesFassets Throwable", e);
        }
    }

    public static boolean delete(final File file) {
        if (file == null) return false;
        if (file.isDirectory()) {
            return deleteDir(file);
        }
        return deleteFile(file);
    }

    private static boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean deleteFile(final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    public static void unzipFile(String zipFilePath, String unzipDirPath) {
        unzipFileByPassword(zipFilePath, unzipDirPath, null);
    }

    public static void unzipFileByPassword(String zipFilePath, String unzipDirPath, String password) {
        try {
            ZipFile zipFile;
            if (TextUtils.isEmpty(password)) {
                zipFile = new ZipFile(new File(zipFilePath));
            } else {
                zipFile = new ZipFile(new File(zipFilePath), password.toCharArray());
            }
            zipFile.extractAll(unzipDirPath);
        } catch (Throwable t) {
            Log.e(TAG, "unzipFileByPassword Throwable:", t);
        }
    }
}

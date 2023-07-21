package com.d08a3hqr.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtils {
    private static int sBufferSize = 524288;
    private static final int BUFFER_LEN = 8192;

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
            e.printStackTrace();
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

    public static void encryptFile(Context context, String assetsName, String password) {
        try {
            String encryptPath = FilePath.getPluginEncryptPath();
            String[] plugins = context.getAssets().list(assetsName);
            for (String fileName : plugins) {
                InputStream is = context.getAssets().open(assetsName + File.separator + fileName);
                byte[] bytes = readFile2BytesByStream(is);
                byte[] encrypt = EncryptionUtils.encrypt(bytes, password);
                String name = fileName.replace(".apk", "_aa").replace(".so", "_ss");
                File newFile = new File(encryptPath + File.separator + name);
                newFile.createNewFile();
                writeFileFromIS(newFile, new ByteArrayInputStream(encrypt));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(Context context, String assetsName, String password) {
        try {
            String decryptPath = FilePath.getPluginPath();
            String[] plugins = context.getAssets().list(assetsName);
            for (String fileName : plugins) {
                InputStream is = context.getAssets().open(assetsName + File.separator + fileName);
                byte[] bytes = readFile2BytesByStream(is);
                byte[] decrypt = EncryptionUtils.decrypt(bytes, password);
                String name = fileName.replace("_aa", ".apk").replace("_ss", ".so");
                File newFile = new File(decryptPath + File.separator + name);
                newFile.createNewFile();
                writeFileFromIS(newFile, new ByteArrayInputStream(decrypt));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFile2BytesByStream(InputStream is) {
        try {
            ByteArrayOutputStream os = null;
            try {
                os = new ByteArrayOutputStream();
                byte[] b = new byte[sBufferSize];
                int len;
                while ((len = is.read(b, 0, sBufferSize)) != -1) {
                    os.write(b, 0, len);
                }
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFileFromIS(File file, InputStream is) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, false), sBufferSize);
            byte[] data = new byte[sBufferSize];
            for (int len; (len = is.read(data)) != -1; ) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Unzip the file.
     *
     * @param zipFilePath The path of ZIP file.
     * @param destDirPath The path of destination directory.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    public static List<File> unzipFile(final String zipFilePath, final String destDirPath) {
        try {
            return unzipFileByKeyword(zipFilePath, destDirPath, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFilePath The path of ZIP file.
     * @param destDirPath The path of destination directory.
     * @param keyword     The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    public static List<File> unzipFileByKeyword(final String zipFilePath, final String destDirPath, final String keyword) throws IOException {
        return unzipFileByKeyword(getFileByPath(zipFilePath), getFileByPath(destDirPath), keyword);
    }

    /**
     * Unzip the file by keyword.
     *
     * @param zipFile The ZIP file.
     * @param destDir The destination directory.
     * @param keyword The keyboard.
     * @return the unzipped files
     * @throws IOException if unzip unsuccessfully
     */
    public static List<File> unzipFileByKeyword(final File zipFile, final File destDir, final String keyword) throws IOException {
        if (zipFile == null || destDir == null) return null;
        List<File> files = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<?> entries = zip.entries();
        try {
            if (isSpace(keyword)) {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        continue;
                    }
                    if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files;
                }
            } else {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName().replace("\\", "/");
                    if (entryName.contains("../")) {
                        continue;
                    }
                    if (entryName.contains(keyword)) {
                        if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files;
                    }
                }
            }
        } finally {
            zip.close();
        }
        return files;
    }

    private static boolean unzipChildFile(final File destDir, final List<File> files, final ZipFile zip, final ZipEntry entry, final String name) throws IOException {
        File file = new File(destDir, name);
        files.add(file);
        if (entry.isDirectory()) {
            return createOrExistsDir(file);
        } else {
            if (!createOrExistsFile(file)) return false;
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(zip.getInputStream(entry));
                out = new BufferedOutputStream(new FileOutputStream(file));
                byte buffer[] = new byte[BUFFER_LEN];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
        return true;
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    public static boolean isSpace(String filePath) {
        return TextUtils.isEmpty(filePath);
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

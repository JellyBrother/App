package com.example.myapp.base.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.example.myapp.base.cache.CachePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/04/20
 *     desc  : utils about uri
 * </pre>
 */
public final class UriUtils {

    private UriUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Resource to uri.
     * <p>res2Uri([res type]/[res name]) -> res2Uri(drawable/icon), res2Uri(raw/icon)</p>
     * <p>res2Uri([resource_id]) -> res2Uri(R.drawable.icon)</p>
     *
     * @param resPath The path of res.
     * @return uri
     */
    public static Uri res2Uri(String resPath) {
        return Uri.parse("android.resource://" + Utils.getApp().getPackageName() + "/" + resPath);
    }

    /**
     * File to uri.
     *
     * @param file The file.
     * @return uri
     */
    public static Uri file2Uri(final File file) {
        if (!UtilsBridge.isFileExists(file)) return null;
        String authority = Utils.getApp().getPackageName() + ".fileprovider";
        return FileProvider.getUriForFile(Utils.getApp(), authority, file);
    }

    /**
     * Uri to file.
     *
     * @param uri The uri.
     * @return file
     */
    public static File uri2File(final Uri uri) {
        if (uri == null) return null;
        File file = uri2FileReal(uri);
        if (file != null) return file;
        return copyUri2Cache(uri);
    }

    /**
     * Uri to file.
     *
     * @param uri The uri.
     * @return file
     */
    private static File uri2FileReal(final Uri uri) {
        LogUtils.d("UriUtils", uri.toString());
        String authority = uri.getAuthority();
        String scheme = uri.getScheme();
        String path = uri.getPath();
        if (path != null) {
            String[] externals = new String[]{"/external/", "/external_path/"};
            File file = null;
            for (String external : externals) {
                if (path.startsWith(external)) {
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + path.replace(external, "/"));
                    if (file.exists()) {
                        LogUtils.d("UriUtils", uri + " -> " + external);
                        return file;
                    }
                }
            }
            file = null;
            if (path.startsWith("/files_path/")) {
                file = new File(CachePath.getUserFilePath()
                        + path.replace("/files_path/", "/"));
            } else if (path.startsWith("/cache_path/")) {
                file = new File(CachePath.getUserCachePath()
                        + path.replace("/cache_path/", "/"));
            } else if (path.startsWith("/external_files_path/")) {
                file = new File(Utils.getApp().getExternalFilesDir(null).getAbsolutePath()
                        + path.replace("/external_files_path/", "/"));
            } else if (path.startsWith("/external_cache_path/")) {
                file = new File(Utils.getApp().getExternalCacheDir().getAbsolutePath()
                        + path.replace("/external_cache_path/", "/"));
            }
            if (file != null && file.exists()) {
                LogUtils.d("UriUtils", uri + " -> " + path);
                return file;
            }
        }
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            if (path != null) return new File(path);
            LogUtils.d("UriUtils", uri + " parse failed. -> 0");
            return null;
        }// end 0
        else if (DocumentsContract.isDocumentUri(Utils.getApp(), uri)) {
            if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                } else {
                    // Below logic is how External Storage provider build URI for documents
                    // http://stackoverflow.com/questions/28605278/android-5-sd-card-label
                    StorageManager mStorageManager = (StorageManager) Utils.getApp().getSystemService(Context.STORAGE_SERVICE);
                    try {
                        Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                        Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                        Method getUuid = storageVolumeClazz.getMethod("getUuid");
                        Method getState = storageVolumeClazz.getMethod("getState");
                        Method getPath = storageVolumeClazz.getMethod("getPath");
                        Method isPrimary = storageVolumeClazz.getMethod("isPrimary");
                        Method isEmulated = storageVolumeClazz.getMethod("isEmulated");

                        Object result = getVolumeList.invoke(mStorageManager);

                        final int length = Array.getLength(result);
                        for (int i = 0; i < length; i++) {
                            Object storageVolumeElement = Array.get(result, i);
                            //String uuid = (String) getUuid.invoke(storageVolumeElement);

                            final boolean mounted = Environment.MEDIA_MOUNTED.equals(getState.invoke(storageVolumeElement))
                                    || Environment.MEDIA_MOUNTED_READ_ONLY.equals(getState.invoke(storageVolumeElement));

                            //if the media is not mounted, we need not get the volume details
                            if (!mounted) continue;

                            //Primary storage is already handled.
                            if ((Boolean) isPrimary.invoke(storageVolumeElement)
                                    && (Boolean) isEmulated.invoke(storageVolumeElement)) {
                                continue;
                            }

                            String uuid = (String) getUuid.invoke(storageVolumeElement);

                            if (uuid != null && uuid.equals(type)) {
                                return new File(getPath.invoke(storageVolumeElement) + "/" + split[1]);
                            }
                        }
                    } catch (Exception ex) {
                        LogUtils.d("UriUtils", uri + " parse failed. " + ex + " -> 1_0");
                    }
                }
                LogUtils.d("UriUtils", uri + " parse failed. -> 1_0");
                return null;
            }// end 1_0
            else if ("com.android.providers.downloads.documents".equals(authority)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (TextUtils.isEmpty(id)) {
                    LogUtils.d("UriUtils", uri + " parse failed(id is null). -> 1_1");
                    return null;
                }
                if (id.startsWith("raw:")) {
                    return new File(id.substring(4));
                } else if (id.startsWith("msf:")) {
                    id = id.split(":")[1];
                }

                long availableId = 0;
                try {
                    availableId = Long.parseLong(id);
                } catch (Exception e) {
                    return null;
                }

                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/all_downloads",
                        "content://downloads/my_downloads"
                };

                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), availableId);
                    try {
                        File file = getFileFromUri(contentUri, "1_1");
                        if (file != null) {
                            return file;
                        }
                    } catch (Exception ignore) {
                    }
                }

                LogUtils.d("UriUtils", uri + " parse failed. -> 1_1");
                return null;
            }// end 1_1
            else if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    LogUtils.d("UriUtils", uri + " parse failed. -> 1_2");
                    return null;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getFileFromUri(contentUri, selection, selectionArgs, "1_2");
            }// end 1_2
            else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                return getFileFromUri(uri, "1_3");
            }// end 1_3
            else {
                LogUtils.d("UriUtils", uri + " parse failed. -> 1_4");
                return null;
            }// end 1_4
        }// end 1
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getFileFromUri(uri, "2");
        }// end 2
        else {
            LogUtils.d("UriUtils", uri + " parse failed. -> 3");
            return null;
        }// end 3
    }

    private static File getFileFromUri(final Uri uri, final String code) {
        return getFileFromUri(uri, null, null, code);
    }

    private static File getFileFromUri(final Uri uri,
                                       final String selection,
                                       final String[] selectionArgs,
                                       final String code) {
        if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
            if (!TextUtils.isEmpty(uri.getLastPathSegment())) {
                return new File(uri.getLastPathSegment());
            }
        } else if ("com.tencent.mtt.fileprovider".equals(uri.getAuthority())) {
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                File fileDir = Environment.getExternalStorageDirectory();
                return new File(fileDir, path.substring("/QQBrowser".length()));
            }
        } else if ("com.huawei.hidisk.fileprovider".equals(uri.getAuthority())) {
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                return new File(path.replace("/root", ""));
            }
        }

        final Cursor cursor = Utils.getApp().getContentResolver().query(
                uri, new String[]{"_data"}, selection, selectionArgs, null);
        if (cursor == null) {
            LogUtils.d("UriUtils", uri + " parse failed(cursor is null). -> " + code);
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                } else {
                    LogUtils.d("UriUtils", uri + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                    return null;
                }
            } else {
                LogUtils.d("UriUtils", uri + " parse failed(moveToFirst return false). -> " + code);
                return null;
            }
        } catch (Exception e) {
            LogUtils.d("UriUtils", uri + " parse failed. -> " + code);
            return null;
        } finally {
            cursor.close();
        }
    }

    private static File copyUri2Cache(Uri uri) {
        LogUtils.d("UriUtils", "copyUri2Cache() called");
        InputStream is = null;
        try {
            is = Utils.getApp().getContentResolver().openInputStream(uri);
            File file = new File(CachePath.getUserCacheDir(), "" + System.currentTimeMillis());
            UtilsBridge.writeFileFromIS(file.getAbsolutePath(), is);
            return file;
        } catch (FileNotFoundException e) {
            LogUtils.e("copyUri2Cache1", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e("copyUri2Cache2", e);
                }
            }
        }
    }

    /**
     * uri to input stream.
     *
     * @param uri The uri.
     * @return the input stream
     */
    public static byte[] uri2Bytes(Uri uri) {
        if (uri == null) return null;
        InputStream is = null;
        try {
            is = Utils.getApp().getContentResolver().openInputStream(uri);
            return UtilsBridge.inputStream2Bytes(is);
        } catch (FileNotFoundException e) {
            LogUtils.e("uri2Bytes1", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtils.e("uri2Bytes2", e);
                }
            }
        }
    }

    /**
     * 判断字符串是否为URL
     *
     * @param url 用户头像key
     * @return true:是URL、false:不是URL
     */
    public static boolean isHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(url.trim());
        return mat.matches();//判断是否匹配
    }

    public static HashMap<String, String> getParames(Uri uri) {
        Set<String> queryParameterNames = uri.getQueryParameterNames();
        HashMap<String, String> map = new HashMap<>();
        if (ListUtil.isEmpty(queryParameterNames)) {
            return map;
        }
        for (String key : queryParameterNames) {
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            String value = uri.getQueryParameter(key);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            String decode = "";
            try {
                decode = Uri.decode(value);
            } catch (Throwable t) {
            }
            if (TextUtils.isEmpty(decode)) {
                continue;
            }
            map.put(key, decode);
        }
        return map;
    }
}

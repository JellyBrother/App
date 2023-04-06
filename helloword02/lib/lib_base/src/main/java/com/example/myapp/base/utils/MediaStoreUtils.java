package com.example.myapp.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.example.myapp.base.cache.CachePath;
import com.example.myapp.base.constant.BaseConstant;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public final class MediaStoreUtils {
    public final static String DCIM = "DCIM/Camera";

    //读取
    public static void queryImages(@NotNull Activity activity) {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 10086);
                return;
            }
        }
        queryAllImages(activity);
    }

    private static ArrayList queryAllImages(Context context) {
        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection;
        Uri.Builder appendId = new Uri.Builder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.RELATIVE_PATH};
        } else {
            projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};
        }
        ArrayList<Uri> uris = new ArrayList<Uri>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(externalContentUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    appendId = ContentUris.appendId(externalContentUri.buildUpon(), cursor.getLong(0));
                    if (appendId != null) {
                        uris.add(appendId.build());
                        LogUtils.d("SMG", appendId.build().toString());
                    }
                    String string1 = cursor.getString(1);
                    LogUtils.d("SMG", string1);
                    String string2 = cursor.getString(2);
                    LogUtils.d("SMG", string2);
                } while (cursor.moveToNext());
            }
        } catch (Throwable throwable) {
            LogUtils.d("SMG queryAllImages", throwable);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uris;
    }

    //保存
    public static Uri saveImages(@NotNull Activity activity, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            //检查权限
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 10086);
                return null;
            }
        }
        try {
            return saveMedia(activity, bitmap, Environment.DIRECTORY_PICTURES, BaseConstant.Path.SHARE_TEMP_JPEG, "image/JPEG", "");
        } catch (Throwable e) {
            LogUtils.e("saveImages Throwable", e);
        }
        return null;
    }

    //type:Environment.DIRECTORY_PICTURES
    private static Uri saveMedia(Context context, Bitmap bitmap, String dirType, String filename, String mimeType, String description) throws IOException {
        Uri saveUri = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File outputFile = CachePath.getSharePicturesTempDir();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
            } catch (Throwable e) {
                LogUtils.e("saveMedia Throwable2", e);
            }
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            //把文件插入到系统图库(直接插入到Picture文件夹下)
//        MediaStore.Images.Media.insertImage(
//            context.contentResolver, outputFile.absolutePath, outputFile.name, ""
//        )
            //最后通知图库更新
            saveUri = Uri.fromFile(outputFile);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, saveUri));
        } else {
            String path = Environment.DIRECTORY_PICTURES;
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, description);
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, path);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            //contentValues.put(MediaStore.Images.Media.IS_PENDING,1)
            Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            saveUri = context.getContentResolver().insert(external, contentValues);
            OutputStream fos = null;
            if (saveUri != null) {
                try {
                    fos = context.getContentResolver().openOutputStream(saveUri);
                } catch (Throwable e) {
                    LogUtils.e("saveMedia Throwable3", e);
                }
            }
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            }
        }
        return saveUri;
    }


    /**
     * Android Q以下版本，删除文件需要申请WRITE_EXTERNAL_STORAGE权限。通过MediaStore的DATA字段获得媒体文件的绝对路径，然后使用File相关API删除
     * <p>
     * Android Q以上版本，应用删除自己创建的媒体文件不需要用户授权。删除其他应用创建的媒体文件需要申请READ_EXTERNAL_STORAGE权限。
     * 删除其他应用创建的媒体文件，还会抛出RecoverableSecurityException异常，在操作或删除公共目录的文件时，需要Catch该异常，由MediaProvider弹出弹框给用户选择是否允许应用修改或删除图片/视频/音频文件
     */
    public static void deletePicture(@NotNull Activity activity, @NotNull Uri imageUri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(imageUri, projection,
                        null, null, null);
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex > -1) {
                    File file = new File(cursor.getString(columnIndex));
                    file.delete();
                }
            } catch (Throwable t) {
                LogUtils.e("deletePicture cursor Throwable", t);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            try {
                activity.getContentResolver().delete(imageUri, null, null);
            } catch (RecoverableSecurityException e1) {
                try {
                    //捕获 RecoverableSecurityException异常，发起请求
                    ActivityCompat.startIntentSenderForResult(activity, e1.getUserAction().getActionIntent().getIntentSender(),
                            10086, null, 0, 0, 0, null);
                } catch (Throwable e) {
                    LogUtils.e("deletePicture Throwable", e);
                }
            }
        }
    }

    public static Uri getImageContentUri(String filePath) {
        Application context = Utils.getApp();
        Uri uri = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    Uri baseUri = Uri.parse("content://media/external/images/media");
                    uri = Uri.withAppendedPath(baseUri, "" + id);
                }
            }
            if (uri == null) {
                ContentValues contentValues = new ContentValues();
                String time = TypeConversionUtil.getString(System.currentTimeMillis());
//                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "ShareTemp");
//                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
                if (isQ()) {
                    contentValues.put(MediaStore.Images.Media.DATE_TAKEN, time);
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, DCIM);
                }
                contentValues.put(MediaStore.Images.Media.DATA, filePath);
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
        } catch (Exception e) {
            LogUtils.e("error on getImageContentUri cursor: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri;
    }

    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}

package com.example.myapp.base.utils;

import static android.Manifest.permission.CALL_PHONE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/23
 *     desc  : utils about intent
 * </pre>
 */
public final class IntentUtils {
    private static final String TAG = "IntentUtils";

    private IntentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the intent is available.
     *
     * @param intent The intent.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIntentAvailable(final Intent intent) {
        return Utils.getApp()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param filePath The path of file.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final String filePath) {
        return getInstallAppIntent(UtilsBridge.getFileByPath(filePath));
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file The file.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final File file) {
        if (!UtilsBridge.isFileExists(file)) return null;
        String authority = Utils.getApp().getPackageName() + ".fileprovider";
        Uri uri = FileProvider.getUriForFile(Utils.getApp(), authority, file);
        return getInstallAppIntent(uri);
    }

    /**
     * Return the intent of install app.
     * <p>Target APIs greater than 25 must hold
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param uri The uri.
     * @return the intent of install app
     */
    public static Intent getInstallAppIntent(final Uri uri) {
        if (uri == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(uri, type);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of uninstall app.
     * <p>Target APIs greater than 25 must hold
     * Must hold {@code <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />}</p>
     *
     * @param pkgName The name of the package.
     * @return the intent of uninstall app
     */
    public static Intent getUninstallAppIntent(final String pkgName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgName));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of launch app.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app
     */
    public static Intent getLaunchAppIntent(final String pkgName) {
        String launcherActivity = UtilsBridge.getLauncherActivity(pkgName);
        if (UtilsBridge.isSpace(launcherActivity)) return null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(pkgName, launcherActivity);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName) {
        return getLaunchAppDetailsSettingsIntent(pkgName, false);
    }

    /**
     * Return the intent of launch app details settings.
     *
     * @param pkgName The name of the package.
     * @return the intent of launch app details settings
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName, final boolean isNewTask) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkgName));
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of share text.
     *
     * @param content The content.
     * @return the intent of share text
     */
    public static Intent getShareTextIntent(final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of share image.
     *
     * @param imagePath The path of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final String imagePath) {
        return getShareTextImageIntent("", imagePath);
    }

    /**
     * Return the intent of share image.
     *
     * @param imageFile The file of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final File imageFile) {
        return getShareTextImageIntent("", imageFile);
    }

    /**
     * Return the intent of share image.
     *
     * @param imageUri The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final Uri imageUri) {
        return getShareTextImageIntent("", imageUri);
    }

    /**
     * Return the intent of share image.
     *
     * @param content   The content.
     * @param imagePath The path of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final String imagePath) {
        return getShareTextImageIntent(content, UtilsBridge.getFileByPath(imagePath));
    }

    /**
     * Return the intent of share image.
     *
     * @param content   The content.
     * @param imageFile The file of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final File imageFile) {
        return getShareTextImageIntent(content, UtilsBridge.file2Uri(imageFile));
    }

    /**
     * Return the intent of share image.
     *
     * @param content  The content.
     * @param imageUri The uri of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of share images.
     *
     * @param imagePaths The paths of images.
     * @return the intent of share images
     */
    public static Intent getShareImageIntent(final LinkedList<String> imagePaths) {
        return getShareTextImageIntent("", imagePaths);
    }

    /**
     * Return the intent of share images.
     *
     * @param images The files of images.
     * @return the intent of share images
     */
    public static Intent getShareImageIntent(final List<File> images) {
        return getShareTextImageIntent("", images);
    }

    /**
     * Return the intent of share images.
     *
     * @param uris The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareImageIntent(final ArrayList<Uri> uris) {
        return getShareTextImageIntent("", uris);
    }

    /**
     * Return the intent of share images.
     *
     * @param content    The content.
     * @param imagePaths The paths of images.
     * @return the intent of share images
     */
    public static Intent getShareTextImageIntent(@Nullable final String content,
                                                 final LinkedList<String> imagePaths) {
        List<File> files = new ArrayList<>();
        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                File file = UtilsBridge.getFileByPath(imagePath);
                if (file != null) {
                    files.add(file);
                }
            }
        }
        return getShareTextImageIntent(content, files);
    }

    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param images  The files of images.
     * @return the intent of share images
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final List<File> images) {
        ArrayList<Uri> uris = new ArrayList<>();
        if (images != null) {
            for (File image : images) {
                Uri uri = UtilsBridge.file2Uri(image);
                if (uri != null) {
                    uris.add(uri);
                }
            }
        }
        return getShareTextImageIntent(content, uris);
    }

    /**
     * Return the intent of share images.
     *
     * @param content The content.
     * @param uris    The uris of image.
     * @return the intent of share image
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName, final String className) {
        return getComponentIntent(pkgName, className, null, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final boolean isNewTask) {
        return getComponentIntent(pkgName, className, null, isNewTask);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle) {
        return getComponentIntent(pkgName, className, bundle, false);
    }

    /**
     * Return the intent of component.
     *
     * @param pkgName   The name of the package.
     * @param className The name of class.
     * @param bundle    The Bundle of extras to add to this intent.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of component
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle,
                                            final boolean isNewTask) {
        Intent intent = new Intent();
        if (bundle != null) intent.putExtras(bundle);
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        return getIntent(intent, isNewTask);
    }

    /**
     * Return the intent of shutdown.
     * <p>Requires root permission
     * or hold {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return the intent of shutdown
     */
    public static Intent getShutdownIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
        } else {
            intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        }
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of dial.
     *
     * @param phoneNumber The phone number.
     * @return the intent of dial
     */
    public static Intent getDialIntent(@NonNull final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber)));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of call.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber The phone number.
     * @return the intent of call
     */
    @RequiresPermission(CALL_PHONE)
    public static Intent getCallIntent(@NonNull final String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(phoneNumber)));
        return getIntent(intent, true);
    }

    /**
     * Return the intent of send SMS.
     *
     * @param phoneNumber The phone number.
     * @param content     The content of SMS.
     * @return the intent of send SMS
     */
    public static Intent getSendSmsIntent(@NonNull final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + Uri.encode(phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        return getIntent(intent, true);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri The uri of output.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri) {
        return getCaptureIntent(outUri, false);
    }

    /**
     * Return the intent of capture.
     *
     * @param outUri    The uri of output.
     * @param isNewTask True to add flag of new task, false otherwise.
     * @return the intent of capture
     */
    public static Intent getCaptureIntent(final Uri outUri, final boolean isNewTask) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    /**
     * android获取一个用于打开HTML文件的intent
     */
    public static Intent getHtmlFileIntent(File file) {
        try {
            Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
            return getHtmlFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getHtmlFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开图片文件的intent
     */
    public static Intent getImageFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getImageFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getImageFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "image/*");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开PDF文件的intent
     */
    public static Intent getPdfFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getPdfFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getPdfFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/pdf");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开文本文件的intent
     */
    public static Intent getTextFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getTextFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getTextFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "text/plain");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开音频文件的intent
     */
    public static Intent getAudioFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getAudioFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getAudioFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(uri, "audio/*");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开视频文件的intent
     */
    public static Intent getVideoFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getVideoFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getVideoFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        intent.setDataAndType(uri, "video/*");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开CHM文件的intent
     */
    public static Intent getChmFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getChmFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getChmFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/x-chm");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开Word文件的intent
     */
    public static Intent getWordFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getWordFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getWordFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/msword");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开Excel文件的intent
     */
    public static Intent getExcelFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getExcelFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getExcelFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开PPT文件的intent
     */
    public static Intent getPPTFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getPPTFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getPPTFileIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return getIntent(intent, true);
    }

    /**
     * android获取一个用于打开apk文件的intent
     */
    public static Intent getApkFileIntent(File file) {
        try {
            Uri uri = UriUtils.file2Uri(file);
            return getApkFileIntent(uri);
        } catch (Throwable t) {
            LogUtils.e(TAG, "getHtmlFileIntent Throwable:", t);
        }
        return null;
    }

    public static Intent getApkFileIntent(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return getIntent(intent, true);
    }

    public static Intent getBrowseIntent(String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            return getIntent(intent, true);
        } catch (Throwable t) {
            LogUtils.e("getBrowseIntent", t);
        }
        return null;
    }

//    /**
//     * 获取选择照片的 Intent
//     *
//     * @return
//     */
//    public static Intent getPickIntentWithGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        return intent.setType("image*//*");
//    }
//
//    /**
//     * 获取从文件中选择照片的 Intent
//     *
//     * @return
//     */
//    public static Intent getPickIntentWithDocuments() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        return intent.setType("image*//*");
//    }
//
//
//    public static Intent buildImageGetIntent(final Uri saveTo, final int outputX, final int outputY, final boolean returnData) {
//        return buildImageGetIntent(saveTo, 1, 1, outputX, outputY, returnData);
//    }
//
//    public static Intent buildImageGetIntent(Uri saveTo, int aspectX, int aspectY,
//                                             int outputX, int outputY, boolean returnData) {
//        Intent intent = new Intent();
//        if (Build.VERSION.SDK_INT < 19) {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        } else {
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//        }
//        intent.setType("image*//*");
//        intent.putExtra("output", saveTo);
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", returnData);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        return intent;
//    }
//
//    public static Intent buildImageCropIntent(final Uri uriFrom, final Uri uriTo, final int outputX, final int outputY, final boolean returnData) {
//        return buildImageCropIntent(uriFrom, uriTo, 1, 1, outputX, outputY, returnData);
//    }
//
//    public static Intent buildImageCropIntent(Uri uriFrom, Uri uriTo, int aspectX, int aspectY,
//                                              int outputX, int outputY, boolean returnData) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uriFrom, "image*//*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("output", uriTo);
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", returnData);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//        return intent;
//    }
//
//    public static Intent buildImageCaptureIntent(final Uri uri) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        return intent;
//    }
}

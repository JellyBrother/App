package com.example.myapp.base.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.ColorInt;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

import javax.annotation.Nullable;

/**
 * Created by lizengxu on 2017/11/7.
 */

public class ZxingUtil {

    public static boolean urlHasParam(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return url.contains("?");
    }

    /**
     * 判断是否是横屏还是竖屏
     *
     * @return false：竖屏
     * true：横屏
     */
    public static boolean isHorizontalScreen(String url) {
        if (url.contains("?") || url.contains("？")) {
            if ((url.lastIndexOf("?") + 3) <= url.length()) {
                String land = url.charAt(url.lastIndexOf("?") + 3) + "";
                if ("p".equals(land) || "P".equals(land)) {
                    return false;
                } else {
                    return "l".equals(land) || "L".equals(land);
                }
            }
            return false;
        }
        return false;
    }


    /**
     * 解析二维码 （使用解析RGB编码数据的方式）
     *
     * @param barcode
     * @return
     */
    public static Result decodeBarcodeRGB(Bitmap barcode) {
        int width = barcode.getWidth();
        int height = barcode.getHeight();
        int[] data = new int[width * height];
        barcode.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            Hashtable<DecodeHintType, String> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
            result = reader.decode(bitmap1, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析二维码（使用解析YUV编码数据的方式）
     *
     * @param barcode
     * @return
     */
    public static Result decodeBarcodeYUV(Bitmap barcode) {
        if (null == barcode) {
            return null;
        }
        int width = barcode.getWidth();
        int height = barcode.getHeight();
        //以argb方式存放图片的像素
        int[] argb = new int[width * height];
        barcode.getPixels(argb, 0, width, 0, 0, width, height);
        //将argb转换为yuv
        byte[] yuv = new byte[width * height * 3 / 2];
        encodeYUV420SP(yuv, argb, width, height);
        //解析YUV编码方式的二维码
        return decodeBarcodeYUV(yuv, width, height);
    }

    /**
     * 解析二维码（使用解析YUV编码数据的方式）
     *
     * @param yuv
     * @param width
     * @param height
     * @return
     */
    private static Result decodeBarcodeYUV(byte[] yuv, int width, int height) {
        QRCodeReader qrCodeReader = new QRCodeReader();
        Result rawResult = null;
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(yuv, width, height, 0, 0,
                width, height, false
        );
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
                hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
                rawResult = qrCodeReader.decode(bitmap, hints);
            } catch (ReaderException re) {
                re.printStackTrace();
            } finally {
                qrCodeReader.reset();
            }
        }
        return rawResult;
    }

    /**
     * RGB转YUV的公式是:
     * Y=0.299R+0.587G+0.114B;
     * U=-0.147R-0.289G+0.436B;
     * V=0.615R-0.515G-0.1B;
     *
     * @param yuv
     * @param argb
     * @param width
     * @param height
     */
    private static void encodeYUV420SP(byte[] yuv, int[] argb, int width, int height) {
        // 帧图片的像素大小
        final int frameSize = width * height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex = 0;
        // UV的index从frameSize开始
        int uvIndex = frameSize;
        // ---颜色数据---
        int R, G, B;
        int rgbIndex = 0;
        // ---循环所有像素点，RGB转YUV---
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                R = (argb[rgbIndex] & 0xff0000) >> 16;
                G = (argb[rgbIndex] & 0xff00) >> 8;
                B = (argb[rgbIndex] & 0xff);
                //
                rgbIndex++;
                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));
                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the sampling is every other
                // pixel AND every other scan line.
                // ---Y---
                yuv[yIndex++] = (byte) Y;
                // ---UV---
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    //
                    yuv[uvIndex++] = (byte) V;
                    //
                    yuv[uvIndex++] = (byte) U;
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 创建二维码位图（默认）
     *
     * @param content 字符串内容
     * @param width   位图宽度 px
     * @param height  位图高度 px
     * @return
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, "UTF-8"
                , "H", "2", Color.BLACK, Color.WHITE);
    }

    /**
     * 创建二维码位图（默认）
     *
     * @param content 字符串内容
     * @param width   位图宽度 px
     * @param height  位图高度 px
     * @return
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height, String margin) {
        return createQRCodeBitmap(content, width, height, "UTF-8"
                , "H", margin, Color.BLACK, Color.WHITE);
    }


    /**
     * 创建二维码位图（支持自定义）
     *
     * @param content         字符串内容
     * @param width           位图宽度
     * @param height          位图高度
     * @param characterSet    字符转码格式
     * @param errorCorrection 容错级别
     * @param margin          空白边距，zxing默认4
     * @param preColor        前景颜色块值
     * @param backColor       背景颜色块值
     * @return
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height
            , @Nullable String characterSet, @Nullable String errorCorrection
            , @Nullable String margin, @ColorInt int preColor, @ColorInt int backColor) {

        if (TextUtils.isEmpty(content)) {// 字符串内容判空
            return null;
        }
        if (width < 0 || height < 0) {// 宽和高都需要>=0
            return null;
        }

        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if (!TextUtils.isEmpty(characterSet)) {
                hints.put(EncodeHintType.CHARACTER_SET, characterSet);// 字符转码格式设置
            }

            if (!TextUtils.isEmpty(errorCorrection)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);// 容错级别设置
            }

            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);// 空白边距设置
            }

            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE
                    , width, height, hints);
            Bitmap bitmap;
            if (!TextUtils.isEmpty(margin) && Integer.valueOf(margin) < 0) {
                bitmap = getBitmapDelWhite(width, height, preColor, backColor, bitMatrix);
            } else {
                bitmap = getNormalBitmap(width, height, preColor, backColor, bitMatrix);
            }
            return bitmap;
        } catch (Exception e) {
            LogUtils.w(e);
        }
        return null;
    }

    @NotNull
    private static Bitmap getNormalBitmap(int width, int height, @ColorInt int preColor, @ColorInt int backColor, BitMatrix bitMatrix) {
        // 创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = preColor;
                } else {
                    pixels[y * width + x] = backColor;
                }
            }
        }
        // 创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生产 bitmap 删除白边 加入adapter的长度进行容错
     * 如果异常会返回未删除白边的bitmap
     *
     * @return
     */
    @NotNull
    private static Bitmap getBitmapDelWhite(int width, int height, @ColorInt int preColor, @ColorInt int backColor, BitMatrix bitMatrix) {
        try {
            int startX = 0;
            int endX = 0;
            int startY = 0;
            int endY = 0;
            int adapter = 5;
            for (int y = 0; y < height; y++) {
                if (startX != 0 || startY != 0) {
                    break;
                }
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        startX = x - adapter;
                        startY = y - adapter;
                        break;
                    }
                }
            }
            for (int y = height - 1; y > 0; y--) {
                if (endX != 0 || endY != 0) {
                    break;
                }
                for (int x = width - 1; x > 0; x--) {
                    if (bitMatrix.get(x, y)) {
                        endX = x + adapter;
                        endY = y + adapter;
                        break;
                    }
                }
            }
            // 创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值
            int[] pixels = new int[(endX - startX) * (endY - startY)];
            for (int y = 0; y < endY - startY; y++) {
                for (int x = 0; x < endX - startX; x++) {
                    if (bitMatrix.get(x + startX, y + startY)) {
                        pixels[y * (endX - startX) + x] = preColor;
                    } else {
                        pixels[y * (endX - startX) + x] = backColor;
                    }
                }
            }

            // 创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值
            Bitmap bitmap = Bitmap.createBitmap(endX - startX, endY - startY, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, endX - startX, 0, 0, endX - startX, endY - startY);
            return bitmap;
        } catch (Exception e) {
            LogUtils.w(e);
            return getNormalBitmap(width, height, preColor, backColor, bitMatrix);
        }
    }

    /**
     * 给二维码中间加上小图片
     *
     * @param src
     * @param logo
     * @return
     */
    public static Bitmap addLogo(Bitmap src, Bitmap logo) {
        //如果原二维码为空，返回空
        if (src == null) {
            return null;
        }
        //如果logo为空，返回原二维码
        if (src == null || logo == null) {
            return src;
        }

        //这里得到原二维码bitmap的数据
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        //logo的Width和Height
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        //同样如果为空，返回空
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        //同样logo大小为0，返回原二维码
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5，也可以自定义多大，越小越好
        //二维码有一定的纠错功能，中间图片越小，越容易纠错
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }
}

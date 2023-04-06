package com.example.myapp.base.widget.means;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.myapp.base.cache.CachePath;
import com.example.myapp.base.dowload.DownloadUtil;
import com.example.myapp.base.utils.IntentUtils;
import com.example.myapp.base.utils.ListUtil;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

import java.io.File;

public class MeansUtil {
    private static final String TAG = "MeansUtil";
    // pdf
    public static final String TYPE_PDF = "pdf";
    // jpg_png_jpeg
    public static final String TYPE_IMAGE = "jpg_png_jpeg";
    // docx_doc_docm
    public static final String TYPE_WORD = "docx_doc_docm";
    // xls_xlsx_xlsm
    public static final String TYPE_EXCEL = "xls_xlsx_xlsm";
    // pptm_pptx_ppt
    public static final String TYPE_PPT = "pptm_pptx_ppt";
    // txt
    public static final String TYPE_TXT = "txt";
    // rar_zip
    public static final String TYPE_ZIP = "rar_zip_7z";
    // mp4_mov_m4v
    public static final String TYPE_VIDEO = "mp4_mov";
    public static final String LINE = "_";

    public static boolean isPdf(String fileFormat) {
        if (TextUtils.isEmpty(fileFormat)) {
            return false;
        }
        return TextUtils.equals(TYPE_PDF, fileFormat.toLowerCase());
    }

    public static boolean isMate(String fileType, String fileFormat) {
        if (TextUtils.isEmpty(fileFormat)) {
            return false;
        }
        String[] split = fileType.split(LINE);
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.equals(split[i], fileFormat.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImage(String fileFormat) {
        return isMate(TYPE_IMAGE, fileFormat);
    }

    public static boolean isWord(String fileFormat) {
        return isMate(TYPE_WORD, fileFormat);
    }

    public static boolean isExcel(String fileFormat) {
        return isMate(TYPE_EXCEL, fileFormat);
    }

    public static boolean isPpt(String fileFormat) {
        return isMate(TYPE_PPT, fileFormat);
    }

    public static boolean isTxt(String fileFormat) {
        return isMate(TYPE_TXT, fileFormat);
    }

    public static boolean isZip(String fileFormat) {
        return isMate(TYPE_ZIP, fileFormat);
    }

    public static boolean isVideo(String fileFormat) {
        return isMate(TYPE_VIDEO, fileFormat);
    }

    public static boolean isOther(String fileFormat) {
        return !isPdf(fileFormat) && !isImage(fileFormat) && !isWord(fileFormat) && !isExcel(fileFormat) &&
                !isPpt(fileFormat) && !isTxt(fileFormat) && !isVideo(fileFormat);
    }

    public static int getFileType(String fileFormat) {
        if (isPdf(fileFormat)) {
            return FileType.TYPE_PDF;
        }
        if (isImage(fileFormat)) {
            return FileType.TYPE_IMAGE;
        }
        if (isWord(fileFormat)) {
            return FileType.TYPE_WORD;
        }
        if (isExcel(fileFormat)) {
            return FileType.TYPE_EXCEL;
        }
        if (isPpt(fileFormat)) {
            return FileType.TYPE_PPT;
        }
        if (isTxt(fileFormat)) {
            return FileType.TYPE_TXT;
        }
        if (isZip(fileFormat)) {
            return FileType.TYPE_ZIP;
        }
        if (isVideo(fileFormat)) {
            return FileType.TYPE_VIDEO;
        }
        return FileType.TYPE_OTHER;
    }

    public static int getImageByFormat(String fileFormat) {
//        if (isPdf(fileFormat)) {
//            return R.drawable.base_format_pdf;
//        }
//        if (isImage(fileFormat)) {
//            return R.drawable.base_format_image;
//        }
//        if (isWord(fileFormat)) {
//            return R.drawable.base_format_word;
//        }
//        if (isExcel(fileFormat)) {
//            return R.drawable.base_format_excel;
//        }
//        if (isPpt(fileFormat)) {
//            return R.drawable.base_format_ppt;
//        }
//        if (isTxt(fileFormat)) {
//            return R.drawable.base_format_txt;
//        }
//        if (isZip(fileFormat)) {
//            return R.drawable.base_format_zip;
//        }
//        if (isVideo(fileFormat)) {
//            return R.drawable.base_format_video;
//        }
//        return R.drawable.base_format_other;

        return 0;
    }

    public static void setFormatImage(ImageView imageView, String fileFormat) {
        if (imageView == null) {
            return;
        }
        imageView.setImageResource(getImageByFormat(fileFormat));
    }

    public static void jumpPreviewMeans(String title, String url, String fileFormat, String id, String fileVersion) {
        LogUtils.e(TAG, "jumpByMeans title:" + title + ",url:" + url + ",fileFormat:" + fileFormat + ",id:" + id + ",fileVersion:" + fileVersion);
        if (isImage(fileFormat)) {
//            CommonJumpUtil.jumpPreviewImageActivity(url);
            return;
        }
        if (isVideo(fileFormat)) {
//            CommonJumpUtil.jumpPreviewVideoActivity(title, url);
            return;
        }
        if (isOther(fileFormat)) {
//            ToastHelper.INSTANCE.showShort(R.string.base_not_suppot_preview_means);
            return;
        }
        ///////////////////////////////////////// 如果是已经下载了的文件，就走本地加载，不是才走网页打开 ////////////////////////////////////////////
        File[] files = null;
        if (CachePath.getUseDowloadDir() != null) {
            files = CachePath.getUseDowloadDir().listFiles();
        }
        File downloadFile = null;
        if (!ListUtil.isEmpty(files)) {
            for (File file : files) {
                String fileName = DownloadUtil.getFileName(id, fileFormat);
                if (TextUtils.equals(file.getName(), fileName)) {
                    downloadFile = file;
                    break;
                }
            }
        }
        if (downloadFile == null) {
            defaultJump(title, url, fileFormat, id, fileVersion);
            return;
        }
//        Uri file2Uri = UriUtils.file2Uri(downloadFile);
//        if (file2Uri == null) {
//            CommonJumpUtil.jumpWebViewActivityPreview(title, url, fileId, fileFormat);
//            return;
//        }
//        CommonJumpUtil.jumpWebViewActivityShare(title, file2Uri.toString(), fileId, fileFormat);
        try {
            Intent intent = getIntentByFileFormat(downloadFile, fileFormat);
            if (intent == null) {
                defaultJump(title, url, fileFormat, id, fileVersion);
            } else {
                Utils.getApp().startActivity(intent);
            }
        } catch (Throwable t) {
            LogUtils.e(TAG, "jumpPreviewMeans Throwable:", t);
            defaultJump(title, url, fileFormat, id, fileVersion);
        }
        ///////////////////////////////////////// 如果是已经下载了的文件，就走本地加载，不是才走网页打开 ////////////////////////////////////////////
    }

    private static void defaultJump(String title, String url, String fileFormat, String fileId, String fileVersion) {
//        CommonJumpUtil.jumpWebViewActivityPreview(title, url, fileId, fileFormat, fileVersion);
    }

    public static Intent getIntentByFileFormat(File file, String fileFormat) {
        Intent intent = null;
        if (isPdf(fileFormat)) {
            intent = IntentUtils.getPdfFileIntent(file);
        }
        if (isWord(fileFormat)) {
            intent = IntentUtils.getWordFileIntent(file);
        }
        if (isExcel(fileFormat)) {
            intent = IntentUtils.getExcelFileIntent(file);
        }
        if (isPpt(fileFormat)) {
            intent = IntentUtils.getPPTFileIntent(file);
        }
        if (isTxt(fileFormat)) {
            intent = IntentUtils.getTextFileIntent(file);
        }
        if (isImage(fileFormat)) {
            intent = IntentUtils.getImageFileIntent(file);
        }
        if (isVideo(fileFormat)) {
            intent = IntentUtils.getVideoFileIntent(file);
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent;
    }
}

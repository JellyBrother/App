package com.example.myapp.base.dowload;

import static com.download.library.DownloadTask.STATUS_CANCELED;
import static com.download.library.DownloadTask.STATUS_DOWNLOADING;
import static com.download.library.DownloadTask.STATUS_ERROR;
import static com.download.library.DownloadTask.STATUS_NEW;
import static com.download.library.DownloadTask.STATUS_PAUSED;
import static com.download.library.DownloadTask.STATUS_PAUSING;
import static com.download.library.DownloadTask.STATUS_PENDDING;
import static com.download.library.DownloadTask.STATUS_SUCCESSFUL;

import android.net.Uri;
import android.text.TextUtils;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.download.library.ResourceRequest;
import com.example.myapp.base.bus.BaseEvent;
import com.example.myapp.base.cache.FileUtils;
import com.example.myapp.base.cache.CachePath;
import com.example.myapp.base.constant.BaseConstant;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class DownloadUtil {
    public static final String DOWLOAD_ASYNC = "dowload_async";

    /**
     * 同步下载
     */
    public static File downloadSync(String url) {
        LogUtils.d("dowloadSync url:" + url);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            return DownloadImpl.getInstance(Utils.getApp()).url(url).get();
        } catch (Throwable t) {
            LogUtils.e(t);
        }
        return null;
    }

    /**
     * 生成本地文件唯一标识
     *
     * @param id         文件唯一标识
     * @param fileSuffix 文件后缀
     */
    public static String getFileName(String id, String fileSuffix) {
//        return EncryptUtils.md5(url) + fileName;
        return id + BaseConstant.HttpConfig.CUT_COMMA_FILE + fileSuffix;
    }

    public static File getDownloadFile(String fileName) {
        return new File(
                CachePath.getUseDowloadDir(),
                fileName
        );
    }

    public static void downloadAsync(String url, String id, String fileSuffix, String version) {
        downloadAsync(url, id, fileSuffix, version, null);
    }

    /**
     * 异步下载
     */
    public static void downloadAsync(String url, String id, String fileSuffix, String version, DownloadListenerAdapter listenerAdapter) {
        LogUtils.d("dowloadAsync url:" + url + ",id:" + id + ",fileSuffix:" + fileSuffix);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (isDownloading(url)) {
            return;
        }
        DownloadEvent downloadEvent = new DownloadEvent();
        downloadEvent.setUrl(url);
        String fileName = getFileName(id, fileSuffix);
        DownloadRecordUtil.INSTANCE.generateDownloadRecord(fileName);
        downloadEvent.setFileName(fileName);
        downloadEvent.setResourceId(id);

        // 如果已经存在下载文件，先删除
        File downloadFile = getDownloadFile(fileName);
        FileUtils.delete(downloadFile);

        ResourceRequest resourceRequest = DownloadImpl.getInstance(Utils.getApp())
                .with(url)
                .target(downloadFile)
                .setRetry(1)
                .setUniquePath(false)
                .setForceDownload(true);
        if (listenerAdapter == null) {
            listenerAdapter = new DownloadListenerAdapter() {

                @Override
                public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                    super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                    LogUtils.d("dowloadAsync onStart url:" + url + ",fileName:" + fileName);
                    DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_START);
                    downloadEvent.setStatus(DownloadStatus.ON_START);
                    downloadEvent.setTotalSize(contentLength);
                    EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                }

                @Override
                public void onProgress(String url, long downloaded, long length, long usedTime) {
                    super.onProgress(url, downloaded, length, usedTime);
                    LogUtils.d("dowloadAsync onProgress url:" + url + ",fileName:" + fileName + ",downloaded:" + downloaded);
                    DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_PROGRESS);
                    downloadEvent.setStatus(DownloadStatus.ON_PROGRESS);
                    downloadEvent.setCurrentSize(downloaded);
                    EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                }

                @Override
                public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                    LogUtils.d("dowloadAsync onResult url:" + url + ",fileName:" + fileName, throwable);
                    return super.onResult(throwable, path, url, extra);
                }

                @Override
                public void onDownloadStatusChanged(Extra extra, int status) {
                    super.onDownloadStatusChanged(extra, status);
                    LogUtils.d("dowloadAsync onDownloadStatusChanged url:" + url + ",fileName:" + fileName + ",status:" + formatDownloadTaskStatus(status));
                    switch (status) {
                        case STATUS_NEW:
                        case STATUS_PENDDING:
                        case STATUS_DOWNLOADING:
                            DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_PROGRESS);
                            downloadEvent.setCurrentSize(0);
                            downloadEvent.setStatus(DownloadStatus.ON_PROGRESS);
                            EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                            break;
                        case STATUS_PAUSING:
                        case STATUS_PAUSED:
                            DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_PAUSE);
                            downloadEvent.setStatus(DownloadStatus.ON_PAUSE);
                            EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                            break;
                        case STATUS_SUCCESSFUL:
                            DownloadRecordUtil.INSTANCE.updateFileVersion(fileName, version);
                            DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_COMPLETE);
                            downloadEvent.setStatus(DownloadStatus.ON_COMPLETE);
                            EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                            break;
                        case STATUS_ERROR:
                            FileUtils.delete(downloadFile);
                            DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_NOT_DOWNLOAD);
                            downloadEvent.setStatus(DownloadStatus.ON_NOT_DOWNLOAD);
                            EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                            break;
                        case STATUS_CANCELED:
                            DownloadRecordUtil.INSTANCE.recordDownloadIconType(fileName, DownloadStatus.ON_CANCEL);
                            downloadEvent.setStatus(DownloadStatus.ON_CANCEL);
                            EventBus.getDefault().post(new BaseEvent(DOWLOAD_ASYNC, downloadEvent));
                            break;
                    }
                }
            };
        }
        resourceRequest.enqueue(listenerAdapter);
    }

    public static void cancelDownload(String url) {
        LogUtils.d("cancelDownload url:" + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        DownloadImpl.getInstance(Utils.getApp()).cancel(url);
    }

    public static void cancelAllDownload() {
        LogUtils.d("cancelAllDownload");
        DownloadImpl.getInstance(Utils.getApp()).cancelAll();
    }

    public static void pauseDownload(String url) {
        LogUtils.d("pauseDownload url:" + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        DownloadImpl.getInstance(Utils.getApp()).pause(url);
    }

    public static void resumeDownload(String url, String id, String fileSuffix, String version) {
        LogUtils.d("resumeDownload url:" + url);
        boolean result = false;
        if (!TextUtils.isEmpty(url)) {
            result = DownloadImpl.getInstance(Utils.getApp()).resume(url);
        }
        if (!result) {
            downloadAsync(url, id, fileSuffix, version, null);
        }
    }

    public static void resumeAllDownload() {
        LogUtils.d("resumeAllDownload");
        DownloadImpl.getInstance(Utils.getApp()).resumeAll();
    }

    public static boolean isDownloading(String url) {
        LogUtils.d("isDownload url:" + url);
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return DownloadImpl.getInstance(Utils.getApp()).isRunning(url);
    }

    public static String formatDownloadTaskStatus(int status) {
        String statusStr = "";
        switch (status) {
            case STATUS_NEW:
                statusStr = "STATUS_NEW";
                break;
            case STATUS_PENDDING:
                statusStr = "STATUS_PENDDING";
                break;
            case STATUS_DOWNLOADING:
                statusStr = "STATUS_DOWNLOADING";
                break;
            case STATUS_PAUSING:
                statusStr = "STATUS_PAUSING";
                break;
            case STATUS_PAUSED:
                statusStr = "STATUS_PAUSED";
                break;
            case STATUS_SUCCESSFUL:
                statusStr = "STATUS_SUCCESSFUL";
                break;
            case STATUS_CANCELED:
                statusStr = "STATUS_CANCELED";
                break;
            case STATUS_ERROR:
                statusStr = "STATUS_ERROR";
                break;
        }
        return statusStr;
    }
}

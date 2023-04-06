package com.example.myapp.base.widget.video;

import com.example.myapp.base.utils.LogUtils;

public class VideoOption {
    protected String TAG = "VideoOption";
    // 标题
    private String title;
    // 视频url
    private String videoUrl;
    // 设置循环-默认循环播放
    private boolean isLoop = true;
    // 从哪里开始播放 目前有时候前几秒有跳动问题，毫秒 需要在startPlayLogic之前，即播放开始之前
    private long progress;
    // 自动播放-默认字段播放
    private boolean isAutoPlay = true;
    // 是否需要全屏按钮-横竖屏按钮-默认有
    private boolean removeFullscreen = false;
    // 是否开始全屏播放-默认竖屏播放
    private boolean fullscreenStart = false;

    public VideoOption() {
        TAG = BaseVideoView.TAG + getClass().getSimpleName();
        LogUtils.d(TAG, "init");
    }

    public boolean isLoop() {
        return isLoop;
    }

    public VideoOption setLoop(boolean loop) {
        this.isLoop = loop;
        return this;
    }

    public long getProgress() {
        return progress;
    }

    public VideoOption setProgress(long progress) {
        this.progress = progress;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VideoOption setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public VideoOption setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public VideoOption setAutoPlay(boolean autoPlay) {
        this.isAutoPlay = autoPlay;
        return this;
    }

    public boolean isRemoveFullscreen() {
        return removeFullscreen;
    }

    public VideoOption setRemoveFullscreen(boolean removeFullscreen) {
        this.removeFullscreen = removeFullscreen;
        return this;
    }

    public boolean isFullscreenStart() {
        return fullscreenStart;
    }

    public VideoOption setFullscreenStart(boolean fullscreenStart) {
        this.fullscreenStart = fullscreenStart;
        return this;
    }

    public void setVideoView(BaseVideoView videoView) {
        videoView.setData(this);
    }

    @Override
    public String toString() {
        return "VideoOption{" +
                "TAG='" + TAG + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", title='" + title + '\'' +
                ", isLoop=" + isLoop +
                ", progress=" + progress +
                ", isAutoPlay=" + isAutoPlay +
                ", removeFullscreen=" + removeFullscreen +
                ", fullscreenStart=" + fullscreenStart +
                '}';
    }
}
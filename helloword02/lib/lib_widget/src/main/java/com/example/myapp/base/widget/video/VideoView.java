//package com.example.myapp.base.widget.video;
//
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.media.MediaPlayer;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.SeekBar;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.databinding.DataBindingUtil;
//
//import com.danikula.videocache.HttpProxyCacheServer;
//import com.gyf.immersionbar.ImmersionBar;
//import com.example.myapp.base.utils.DeviceUtils;
//import com.example.myapp.base.utils.LogUtils;
//import com.example.myapp.base.utils.TypeConversionUtil;
//import com.example.myapp.base.utils.ViewUtil;
//import com.example.myapp.base.widget.R;
//import com.example.myapp.base.widget.databinding.BaseViewVideoBinding;
//import com.example.myapp.base.widget.status.StatusType;
//
//public class VideoView extends BaseVideoView {
//    private BaseViewVideoBinding mBinding;
//    // 刷新时间间隔
//    private static final int HANDLER_INTERVAL_TIME = 500;
//    // 面板隐藏间隔
//    private static final int CONTROLLER_TIME = 4000;
//    // 动画执行时间
//    private static final int ANIM_TIME = 500;
//    private HttpProxyCacheServer videoCacheServer;
//
//    public VideoView(@NonNull Context context) {
//        super(context);
//    }
//
//    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        // 这里解释一下 onWindowVisibilityChanged 方法调用的时机
//        // 从前台返回到后台：先调用 onWindowVisibilityChanged(View.INVISIBLE) 后调用 onWindowVisibilityChanged(View.GONE)
//        // 从后台返回到前台：先调用 onWindowVisibilityChanged(View.INVISIBLE) 后调用 onWindowVisibilityChanged(View.VISIBLE)
//        super.onWindowVisibilityChanged(visibility);
//        // 这里修复了 Activity 从后台返回到前台时 VideoView 从头开始播放的问题
//        if (visibility != VISIBLE || videoOption == null) {
//            return;
//        }
//        int progress = TypeConversionUtil.getInt(videoOption.getProgress());
//        if (progress < 0 || progress >= mBinding.videoView.getDuration()) {
//            progress = 0;
//        }
//        mBinding.videoView.seekTo(progress);
//        mBinding.progress.setProgress(progress);
//    }
//
//    @Override
//    protected void initView(Context context) {
//        super.initView(context);
//        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.base_view_video, this, true);
//        mVideoView = mBinding.videoView;
////        ViewUtil.setDrawableTint(mBinding.toolbar.getLeftView(), R.drawable.base_back_arrow, com.example.myapp.resource.R.color.common_white);
////        mBinding.toolbar.setTitleTextColorId(com.example.myapp.resource.R.color.common_white);
////        videoCacheServer = RetrofitCreateHelper.getInstance().getVideoCacheServer();
//    }
//
//    @Override
//    protected void setViewSize(Configuration configuration) {
//        super.setViewSize(configuration);
//        ViewGroup.LayoutParams titleBarParams = mBinding.titleBar.getLayoutParams();
//        int statusBarHeight = ImmersionBar.getStatusBarHeight(mActivity);
//        titleBarParams.height = statusBarHeight;
//        mBinding.titleBar.setLayoutParams(titleBarParams);
//
//        ViewGroup.LayoutParams bottomParams = mBinding.vBottom.getLayoutParams();
//        ViewGroup.LayoutParams rightParams = mBinding.vRight.getLayoutParams();
//        if (DeviceUtils.isPortrait(configuration)) {
//            bottomParams.height = ImmersionBar.getNavigationBarHeight(mActivity);
//            rightParams.width = 0;
////            mBinding.tvwFullscreen.setText(R.string.base_view_horizontal_screen);
//        } else {
//            bottomParams.height = 0;
//            rightParams.width = statusBarHeight;
////            mBinding.tvwFullscreen.setText(R.string.base_view_vertical_screen);
//        }
//        mBinding.vBottom.setLayoutParams(bottomParams);
//        mBinding.vRight.setLayoutParams(rightParams);
//    }
//
//    @Override
//    protected void initListener() {
//        super.initListener();
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer player) {
//                LogUtils.d("onPrepared 准备播放");
//                int progress = TypeConversionUtil.getInt(videoOption.getProgress());
//                if (progress < 0 || progress >= mBinding.videoView.getDuration()) {
//                    progress = 0;
//                }
//                mBinding.videoView.seekTo(progress);
//                setProgress(progress);
//                mBinding.statusView.setStatus(StatusType.STATUS_GONE);
//            }
//        });
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                LogUtils.d("onCompletion 播放完成");
//                if (videoOption.isAutoPlay()) {
//                    start();
//                } else {
//                    pause();
//                }
//            }
//        });
//        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                LogUtils.d("onInfo what:" + what);
//                mBinding.statusView.setStatus(StatusType.STATUS_GONE);
//                switch (what) {
//                    // 视频播放卡顿开始
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        mBinding.statusView.setStatus(StatusType.STATUS_LOADING);
//                        return true;
//                    // 视频播放卡顿结束
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
//        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                LogUtils.d("onError 播放异常");
//                setData(videoOption);
//                return false;
//            }
//        });
//        OnClickListener showControllerClickListener = new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 先移除之前发送的
//                removeCallbacks(mShowControllerRunnable);
//                removeCallbacks(mHideControllerRunnable);
//                if (isBottomVisible()) {
//                    post(mHideControllerRunnable);
//                } else {
//                    // 显示控制面板
//                    post(mShowControllerRunnable);
//                    postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
//                }
//            }
//        };
//        mBinding.videoGestureView.setOnClickListener(showControllerClickListener);
//        mBinding.progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                LogUtils.d("onProgressChanged progress:" + progress + ",fromUser:" + fromUser);
//                if (fromUser) {
//                    mBinding.current.setText(VideoUtils.conversionTime(progress));
//                    mVideoView.seekTo(progress);
//                    return;
//                }
//                videoOption.setProgress(progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                LogUtils.d("onStartTrackingTouch");
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //设置选择的播放进度
//                int progress = seekBar.getProgress();
//                LogUtils.d("onStopTrackingTouch progress:" + progress);
//                setProgress(progress);
//                mVideoView.seekTo(progress);
//            }
//        });
//        mBinding.layoutFullscreen.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isPortrait = DeviceUtils.isPortrait(getResources().getConfiguration());
//                LogUtils.d(TAG, "layout_fullscreen setOnClickListener isPortrait:" + isPortrait);
//                // 横竖屏切换
//                if (isPortrait) {
//                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                } else {
//                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                }
//            }
//        });
//        mBinding.ivwPlay.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mVideoView.isPlaying()) {
//                    pause();
//                } else {
//                    start();
//                }
//            }
//        });
//        mBinding.videoGestureView.setOnGestureListener(new VideoGestureView.OnGestureListener() {
//            @Override
//            public void removeHideControllerRunnable() {
//                removeCallbacks(mHideControllerRunnable);
//            }
//
//            @Override
//            public int getVideoProgress() {
//                return getProgress();
//            }
//
//            @Override
//            public int getVideoDuration() {
//                return getDuration();
//            }
//
//            @Override
//            public void setVideoProgress(int progress) {
//                setProgress(progress);
//                mVideoView.seekTo(progress);
//            }
//
//            @Override
//            public void postDelayedHideControllerRunnable() {
//                postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
//            }
//        });
//        mBinding.layoutBottom.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtils.d("layoutBottom onClick");
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        removeCallbacks(mRefreshRunnable);
//        postDelayed(mRefreshRunnable, HANDLER_INTERVAL_TIME);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        pause();
//        removeCallbacks(mRefreshRunnable);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mVideoView.stopPlayback();
//        removeCallbacks(mShowControllerRunnable);
//        removeCallbacks(mHideControllerRunnable);
//        removeAllViews();
//    }
//
//    @Override
//    public void setData(VideoOption videoOption) {
//        super.setData(videoOption);
//        if (videoOption == null || TextUtils.isEmpty(videoOption.getVideoUrl())) {
//            return;
//        }
//        mBinding.statusView.setStatus(StatusType.STATUS_LOADING);
//        mBinding.toolbar.setTitleContent(videoOption.getTitle());
//        String videoUrl = videoOption.getVideoUrl();
//        mVideoView.setVideoPath(videoCacheServer.getProxyUrl(videoUrl));
////        mVideoView.setVideoURI(Uri.parse(videoUrl));、
//        if (videoOption.isRemoveFullscreen()) {
//            mBinding.layoutFullscreen.setVisibility(GONE);
//        } else {
//            mBinding.layoutFullscreen.setVisibility(VISIBLE);
//        }
//        if (videoOption.isFullscreenStart()) {
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//        if (videoOption.isAutoPlay()) {
//            start();
//        }
//    }
//
//    private void start() {
//        mVideoView.start();
////        mBinding.ivwPlay.setImageResource(R.drawable.base_video_stop);
//
//        // 延迟隐藏控制面板
//        removeCallbacks(mHideControllerRunnable);
//        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
//    }
//
//    private void pause() {
//        mVideoView.pause();
////        mBinding.ivwPlay.setImageResource(R.drawable.base_video_start);
//
//        // 延迟隐藏控制面板
//        removeCallbacks(mHideControllerRunnable);
//        postDelayed(mHideControllerRunnable, CONTROLLER_TIME);
//    }
//
//    private void setProgress(int progress) {
//        if (progress < 0) {
//            return;
//        }
//        mBinding.current.setText(VideoUtils.conversionTime(progress));
//        int duration = mVideoView.getDuration();
//        mBinding.total.setText(VideoUtils.conversionTime(duration));
//        mBinding.progress.setMax(duration);
//        if (progress > duration) {
//            progress = duration;
//        }
//        mBinding.progress.setProgress(progress);
//        if (videoCacheServer.isCached(videoOption.getVideoUrl())) {
//            mBinding.progress.setSecondaryProgress(duration);
//        } else {
//            mBinding.progress.setSecondaryProgress((int) (mVideoView.getBufferPercentage() / 100f * duration));
//        }
//    }
//
//    /**
//     * 获取视频播放进度
//     */
//    public int getProgress() {
//        return mVideoView.getCurrentPosition();
//    }
//
//    /**
//     * 获取视频的总进度
//     */
//    public int getDuration() {
//        return mVideoView.getDuration();
//    }
//
//    public boolean isBottomVisible() {
//        return mBinding.layoutBottom.getVisibility() == VISIBLE;
//    }
//
//    /**
//     * 显示面板
//     */
//    public void showController() {
//        ValueAnimator bottomAnimator = ValueAnimator.ofInt(mBinding.layoutBottom.getHeight(), 0);
//        bottomAnimator.setDuration(ANIM_TIME);
//        bottomAnimator.addUpdateListener(animation -> {
//            int translationY = (int) animation.getAnimatedValue();
//            mBinding.layoutBottom.setTranslationY(translationY);
//            if (translationY != mBinding.layoutBottom.getHeight()) {
//                return;
//            }
//            if (mBinding.layoutBottom.getVisibility() == INVISIBLE) {
//                mBinding.layoutBottom.setVisibility(VISIBLE);
//            }
//        });
//        bottomAnimator.start();
//    }
//
//    /**
//     * 隐藏面板
//     */
//    public void hideController() {
//        ValueAnimator bottomAnimator = ValueAnimator.ofInt(0, mBinding.layoutBottom.getHeight());
//        bottomAnimator.setDuration(ANIM_TIME);
//        bottomAnimator.addUpdateListener(animation -> {
//            int translationY = (int) animation.getAnimatedValue();
//            mBinding.layoutBottom.setTranslationY(translationY);
//            if (translationY != mBinding.layoutBottom.getHeight()) {
//                return;
//            }
//            if (mBinding.layoutBottom.getVisibility() == VISIBLE) {
//                mBinding.layoutBottom.setVisibility(INVISIBLE);
//            }
//        });
//        bottomAnimator.start();
//    }
//
//    /**
//     * 显示控制面板
//     */
//    private final Runnable mShowControllerRunnable = () -> {
//        showController();
//    };
//
//    /**
//     * 隐藏控制面板
//     */
//    private final Runnable mHideControllerRunnable = () -> {
//        hideController();
//    };
//
//    /**
//     * 刷新任务
//     */
//    private final Runnable mRefreshRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            setProgress(mVideoView.getCurrentPosition());
//            removeCallbacks(mRefreshRunnable);
//            postDelayed(mRefreshRunnable, HANDLER_INTERVAL_TIME);
//        }
//    };
//}
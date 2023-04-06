package com.example.myapp.base.widget.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.Utils;

public class VideoGestureView extends View {
    public static String TAG = "VideoGestureView";
    protected ComponentActivity mActivity;
    private OnGestureListener onGestureListener;
    // 触摸按下的 X 坐标
    private float mViewDownX;
    // 触摸按下的 Y 坐标
    private float mViewDownY;
    // 音量管理器
    private AudioManager mAudioManager;
    // 最大音量值
    private int mMaxVoice;
    // 当前音量值
    private int mCurrentVolume;
    // 当前亮度值
    private float mCurrentBrightness;
    // 当前窗口对象
    private Window mWindow;
    // 调整秒数
    private int mAdjustSecond;
    // 触摸方向
    private int mTouchOrientation = -1;

    public VideoGestureView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoGestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoGestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.d(TAG, "onConfigurationChanged Configuration:" + newConfig);
        setViewSize(newConfig);
    }

    private void init(Context context) {
        // 初始化
        try {
            TAG = getClass().getSimpleName();
            LogUtils.d(TAG, "init");
            mActivity = (ComponentActivity) Utils.getActivity(getContext());
            initView(context);
            setViewSize(getResources().getConfiguration());
            initListener();
            initData();
        } catch (Throwable t) {
            LogUtils.e(TAG, "init Throwable:" + t);
        }
    }

    protected void initView(Context context) {
        LogUtils.d(TAG, "initView");
        mAudioManager = ContextCompat.getSystemService(getContext(), AudioManager.class);
    }

    protected void setViewSize(Configuration configuration) {
        LogUtils.d(TAG, "setViewSize");
    }

    protected void initListener() {
        LogUtils.d(TAG, "initListener");

    }

    protected void initData() {
        LogUtils.d(TAG, "initData");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onGestureListener == null) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMaxVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mWindow = mActivity.getWindow();
                mCurrentBrightness = mWindow.getAttributes().screenBrightness;
                // 如果当前亮度是默认的，那么就获取系统当前的屏幕亮度
                if (mCurrentBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                    try {
                        // 这里需要注意，Settings.System.SCREEN_BRIGHTNESS 获取到的值在小米手机上面会超过 255
                        mCurrentBrightness = Math.min(Settings.System.getInt(
                                getContext().getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS), 255) / 255f;
                    } catch (Settings.SettingNotFoundException ignored) {
                        mCurrentBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
                    }
                }
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                onGestureListener.removeHideControllerRunnable();
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移的距离（按下的位置 - 当前触摸的位置）
                float distanceX = mViewDownX - event.getX();
                float distanceY = mViewDownY - event.getY();
                // 手指偏移的距离一定不能太短，这个是前提条件
                if (Math.abs(distanceY) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    break;
                }
                if (mTouchOrientation == -1) {
                    // 判断滚动方向是垂直的还是水平的
                    if (Math.abs(distanceY) > Math.abs(distanceX)) {
                        mTouchOrientation = LinearLayout.VERTICAL;
                    } else if (Math.abs(distanceY) < Math.abs(distanceX)) {
                        mTouchOrientation = LinearLayout.HORIZONTAL;
                    }
                }
                // 如果手指触摸方向是水平的
                if (mTouchOrientation == LinearLayout.HORIZONTAL) {
                    int second = -(int) (distanceX / (float) getWidth() * 60f);
                    int progress = onGestureListener.getVideoProgress() + second * 1000;
                    if (progress >= 0 && progress <= onGestureListener.getVideoDuration()) {
                        mAdjustSecond = second;
                    }
                    break;
                }
                // 如果手指触摸方向是垂直的
                if (mTouchOrientation == LinearLayout.VERTICAL) {
                    // 判断触摸点是在屏幕左边还是右边
                    if ((int) event.getX() < getWidth() / 2) {
                        // 手指在屏幕左边
                        float delta = (distanceY / getHeight()) * WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                        if (delta == 0) {
                            break;
                        }
                        // 更新系统亮度
                        float brightness = Math.min(Math.max(mCurrentBrightness + delta,
                                        WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF),
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
                        WindowManager.LayoutParams attributes = mWindow.getAttributes();
                        attributes.screenBrightness = brightness;
                        mWindow.setAttributes(attributes);
                        break;
                    }
                    // 手指在屏幕右边
                    float delta = (distanceY / getHeight()) * mMaxVoice;
                    if (delta == 0) {
                        break;
                    }
                    // 更新系统音量
                    int voice = (int) Math.min(Math.max(mCurrentVolume + delta, 0), mMaxVoice);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, 0);
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mViewDownX - event.getX()) <= ViewConfiguration.get(getContext()).getScaledTouchSlop() &&
                        Math.abs(mViewDownY - event.getY()) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    // 如果整个视频播放区域太大，触摸移动会导致触发点击事件，所以这里换成手动派发点击事件
                    if (isEnabled() && isClickable()) {
                        performClick();
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                mTouchOrientation = -1;
                mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mAdjustSecond != 0) {
                    // 调整播放进度
                    int progress = onGestureListener.getVideoProgress() + mAdjustSecond * 1000;
                    onGestureListener.setVideoProgress(progress);
                    mAdjustSecond = 0;
                }
                onGestureListener.postDelayedHideControllerRunnable();
                break;
            default:
                break;
        }
        return true;
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }

    /**
     * 监听器
     */
    public interface OnGestureListener {

        /**
         * 移除隐藏底部控制控件的Runnable
         */
        default void removeHideControllerRunnable() {
        }

        /**
         * 获取进度
         */
        default int getVideoProgress() {
            return 0;
        }

        /**
         * 获取总时长
         */
        default int getVideoDuration() {
            return 0;
        }

        /**
         * 获取总时长
         */
        default void setVideoProgress(int progress) {
        }

        /**
         * 开始延时隐藏底部控制控件
         */
        default void postDelayedHideControllerRunnable() {
        }
    }
}

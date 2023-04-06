package com.example.myapp.base.widget.status;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.example.myapp.base.utils.BaseHandler;
import com.example.myapp.base.utils.LogUtils;
import com.example.myapp.base.utils.ViewUtil;
import com.example.myapp.base.widget.R;
import com.example.myapp.base.widget.interfaces.IRetryListener;

public class StatusView extends FrameLayout {
    private static final String TAG = "StatusView";
    private static final int HANDLER_STATUS_SUCCES_GONE = 1;
    private static final int HANDLER_INTERVAL_TIME = 2000;

//    private BaseViewStatusBinding mBinding;
    private StatusType status = StatusType.STATUS_VISIBLE;
    private int statusErrorDrawable;
    private String statusErrorText;
    private IRetryListener retryListener;
    private int statusEmptyDrawable;
    private String statusEmptyText;
    private int statusNoNetDrawable;
    private String statusNoNetText;
    private String statusLoadingText;
    private int statusSuccesAddDrawable;
    private String statusSuccesAddText;
    private int statusSuccesCancelDrawable;
    private String statusSuccesCancelText;
    private int statusLoadingFullScreenDrawable;

    private View lltLoading;
    private TextView tvwLoading;

    private ImageView ivwLoadingFullScreen;

    private View lltSucces;
    private ImageView ivwSucces;
    private TextView tvwSucces;

    private View lltError;
    private ImageView ivwError;
    private TextView tvwError;
    private TextView btnError;

    private StatusViewHandler handler;

    private static class StatusViewHandler extends BaseHandler<StatusView> {

        public StatusViewHandler(StatusView page) {
            super(page);
        }

        @Override
        public void handleMessage(StatusView statusView, int what, Object obj, Message msg) {
            switch (what) {
                case HANDLER_STATUS_SUCCES_GONE:
                    statusView.setStatus(StatusType.STATUS_GONE);
                    break;
            }
        }
    }

    public StatusView(Context context) {
        super(context);
        initView(context);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setViewSize();
    }

    private void initView(Context context) {
//        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.base_view_status, this, true);
        setCommonData();
        setViewSize();
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtils.d("root onClick");
//            }
//        });
    }

    public void setViewSize() {
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType state) {
        LogUtils.d(TAG, getContext().getClass().getSimpleName() + " setStatus state:" + state);
        this.status = state;
        if (status == StatusType.STATUS_VISIBLE) {
            setVisibility(VISIBLE);
            return;
        }
        if (status == StatusType.STATUS_INVISIBLE) {
            setVisibility(INVISIBLE);
            return;
        }
        if (status == StatusType.STATUS_GONE) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        if (status == StatusType.STATUS_ERROR) {
            onError();
            return;
        }
        if (status == StatusType.STATUS_EMPTY) {
            onEmpty();
            return;
        }
        if (status == StatusType.STATUS_NO_NET) {
            onNoNet();
            return;
        }
        if (status == StatusType.STATUS_LOADING) {
            onLoading();
            return;
        }
        if (status == StatusType.STATUS_SUCCES_ADD) {
            onSuccesAdd();
            return;
        }
        if (status == StatusType.STATUS_SUCCES_CANCEL) {
            onSuccesCancel();
            return;
        }
        if (status == StatusType.STATUS_LOADING_FULL_SCREEN) {
            onLoadingFullScreen();
            return;
        }
    }

    public void onError() {
        onEmpty(true);
        onNoNet(true);
        onLoading(true);
        onSuccesAdd(true);
        onSuccesCancel(true);
        onLoadingFullScreen(true);
        onError(false);
    }

    public void onEmpty() {
        onError(true);
        onNoNet(true);
        onLoading(true);
        onSuccesAdd(true);
        onSuccesCancel(true);
        onLoadingFullScreen(true);
        onEmpty(false);
    }

    public void onNoNet() {
        onError(true);
        onEmpty(true);
        onLoading(true);
        onSuccesAdd(true);
        onSuccesCancel(true);
        onLoadingFullScreen(true);
        onNoNet(false);
    }

    public void onLoading() {
        onError(true);
        onEmpty(true);
        onNoNet(true);
        onSuccesAdd(true);
        onSuccesCancel(true);
        onLoadingFullScreen(true);
        onLoading(false);
    }

    public void onSuccesAdd() {
        onError(true);
        onEmpty(true);
        onNoNet(true);
        onLoading(true);
        onSuccesCancel(true);
        onLoadingFullScreen(true);
        onSuccesAdd(false);
    }

    public void onSuccesCancel() {
        onError(true);
        onEmpty(true);
        onNoNet(true);
        onLoading(true);
        onSuccesAdd(true);
        onLoadingFullScreen(true);
        onSuccesCancel(false);
    }

    public void onLoadingFullScreen() {
        onError(true);
        onEmpty(true);
        onNoNet(true);
        onLoading(true);
        onSuccesAdd(true);
        onSuccesCancel(true);
        onLoadingFullScreen(false);
    }

    private void loadError(boolean hide) {
        if (hide) {
            if (lltError == null) {
                return;
            }
            lltError.setVisibility(GONE);
            return;
        }
        if (lltError == null) {
//            lltError = mBinding.vsbError.getViewStub().inflate();
//            ivwError = lltError.findViewById(R.id.ivwError);
//            tvwError = lltError.findViewById(R.id.tvwError);
//            btnError = lltError.findViewById(R.id.btnError);
            OnClickListener retryListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getStatus() == StatusType.STATUS_ERROR || getStatus() == StatusType.STATUS_NO_NET) {
                        if (StatusView.this.retryListener != null) {
                            StatusView.this.retryListener.onRetry(v);
                        }
                    }
                }
            };
            lltError.setOnClickListener(retryListener);
            ivwError.setOnClickListener(retryListener);
            tvwError.setOnClickListener(retryListener);
            btnError.setOnClickListener(retryListener);
        }
        lltError.setVisibility(VISIBLE);
    }

    protected void onError(boolean hide) {
        loadError(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwError, statusErrorDrawable);
            ViewUtil.setTextAndGone(tvwError, statusErrorText);
            btnError.setVisibility(VISIBLE);
        }
    }

    protected void onEmpty(boolean hide) {
        loadError(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwError, statusEmptyDrawable);
            ViewUtil.setTextAndGone(tvwError, statusEmptyText);
            btnError.setVisibility(GONE);
        }
    }

    protected void onNoNet(boolean hide) {
        loadError(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwError, statusNoNetDrawable);
            ViewUtil.setTextAndGone(tvwError, statusNoNetText);
            btnError.setVisibility(VISIBLE);
        }
    }

    protected void onLoading(boolean hide) {
        if (hide) {
            if (lltLoading == null) {
                return;
            }
            lltLoading.setVisibility(GONE);
            return;
        }
        if (lltLoading == null) {
//            lltLoading = mBinding.vsbLoading.getViewStub().inflate();
//            tvwLoading = lltLoading.findViewById(R.id.tvwLoading);
        }
        lltLoading.setVisibility(VISIBLE);
        ViewUtil.setTextAndGone(tvwLoading, statusLoadingText);
    }

    private void loadSucces(boolean hide) {
        if (hide) {
            if (lltSucces == null) {
                return;
            }
            lltSucces.setVisibility(GONE);
            return;
        }
        if (lltSucces == null) {
            handler = new StatusViewHandler(this);
//            lltSucces = mBinding.vsbSucces.getViewStub().inflate();
//            ivwSucces = lltSucces.findViewById(R.id.ivwSucces);
//            tvwSucces = lltSucces.findViewById(R.id.tvwSucces);
        }
        lltSucces.setVisibility(VISIBLE);
    }

    protected void onSuccesAdd(boolean hide) {
        loadSucces(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwSucces, statusSuccesAddDrawable);
            ViewUtil.setTextAndGone(tvwSucces, statusSuccesAddText);
            handler.removeMessages(HANDLER_STATUS_SUCCES_GONE);
            handler.sendEmptyMessageDelayed(HANDLER_STATUS_SUCCES_GONE, HANDLER_INTERVAL_TIME);
        }
    }

    protected void onSuccesCancel(boolean hide) {
        loadSucces(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwSucces, statusSuccesCancelDrawable);
            ViewUtil.setTextAndGone(tvwSucces, statusSuccesCancelText);
            handler.removeMessages(HANDLER_STATUS_SUCCES_GONE);
            handler.sendEmptyMessageDelayed(HANDLER_STATUS_SUCCES_GONE, HANDLER_INTERVAL_TIME);
        }
    }

    private void loadFullScreen(boolean hide) {
        if (hide) {
            if (ivwLoadingFullScreen == null) {
                return;
            }
            ivwLoadingFullScreen.setVisibility(GONE);
            return;
        }
        if (ivwLoadingFullScreen == null) {
//            View inflate = mBinding.vsbLoadingFullScreen.getViewStub().inflate();
//            ivwLoadingFullScreen = inflate.findViewById(R.id.ivwLoadingFullScreen);
        }
        ivwLoadingFullScreen.setVisibility(VISIBLE);
    }

    protected void onLoadingFullScreen(boolean hide) {
        loadFullScreen(hide);
        if (!hide) {
            ViewUtil.setImageAndGone(ivwLoadingFullScreen, statusLoadingFullScreenDrawable);
        }
    }

    public void setCommonData() {
//        Resources resources = getResources();
//        statusErrorDrawable = R.drawable.base_status_no_net;
//        statusErrorText = resources.getString(R.string.base_service_error);
//        statusEmptyDrawable = R.drawable.base_status_empty;
//        statusEmptyText = resources.getString(R.string.base_empty);
//        statusNoNetDrawable = R.drawable.base_status_no_net;
//        statusNoNetText = resources.getString(R.string.base_toast_no_net);
//        statusLoadingText = resources.getString(R.string.base_loading);
//        statusSuccesAddDrawable = R.drawable.base_status_succes;
//        statusSuccesAddText = resources.getString(R.string.base_succes_add);
//        statusSuccesCancelDrawable = R.drawable.base_status_succes;
//        statusSuccesCancelText = resources.getString(R.string.base_succes_cancel);
//        statusLoadingFullScreenDrawable = R.drawable.base_status_load_full_sreen;
    }

    public StatusView setStatusErrorDrawable(int statusErrorDrawable) {
        this.statusErrorDrawable = statusErrorDrawable;
        return this;
    }

    public StatusView setStatusErrorText(String statusErrorText) {
        this.statusErrorText = statusErrorText;
        return this;
    }

    public StatusView setStatusEmptyDrawable(int statusEmptyDrawable) {
        this.statusEmptyDrawable = statusEmptyDrawable;
        return this;
    }

    public StatusView setStatusEmptyText(String statusEmptyText) {
        this.statusEmptyText = statusEmptyText;
        return this;
    }

    public StatusView setStatusNoNetDrawable(int statusNoNetDrawable) {
        this.statusNoNetDrawable = statusNoNetDrawable;
        return this;
    }

    public StatusView setStatusNoNetText(String statusNoNetText) {
        this.statusNoNetText = statusNoNetText;
        return this;
    }

    public StatusView setStatusLoadingText(String statusLoadingText) {
        this.statusLoadingText = statusLoadingText;
        return this;
    }

    public StatusView setStatusSuccesAddDrawable(int statusSuccesAddDrawable) {
        this.statusSuccesAddDrawable = statusSuccesAddDrawable;
        return this;
    }

    public StatusView setStatusSuccesAddText(String statusSuccesAddText) {
        this.statusSuccesAddText = statusSuccesAddText;
        return this;
    }

    public StatusView setStatusSuccesCancelDrawable(int statusSuccesCancelDrawable) {
        this.statusSuccesCancelDrawable = statusSuccesCancelDrawable;
        return this;
    }

    public StatusView setStatusSuccesCancelText(String statusSuccesCancelText) {
        this.statusSuccesCancelText = statusSuccesCancelText;
        return this;
    }

    public StatusView setStatusLoadingFullScreenDrawable(int statusLoadingFullScreenDrawable) {
        this.statusLoadingFullScreenDrawable = statusLoadingFullScreenDrawable;
        return this;
    }

    public StatusView setRetryListener(IRetryListener retryListener) {
        this.retryListener = retryListener;
        return this;
    }
}
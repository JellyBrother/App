package com.example.myapp.base.utils;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/10/16
 *     desc  : utils about snackbar
 * </pre>
 */
public final class SnackbarUtils {

    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;

    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final int COLOR_SUCCESS = 0xFF2BB600;
    private static final int COLOR_WARNING = 0xFFFFC100;
    private static final int COLOR_ERROR = 0xFFFF0000;
    private static final int COLOR_MESSAGE = 0xFFFFFFFF;

    private static WeakReference<Snackbar> sWeakSnackbar;

    private final View view;
    private CharSequence message;
    private int messageColor;
    private int bgColor;
    private int bgResource;
    private int duration;
    private CharSequence actionText;
    private int actionTextColor;
    private int actionIcon;
    private View.OnClickListener actionListener;
    private int bottomMargin;
    private int leftMargin;
    private int rightMargin;

    private SnackbarUtils(final View parent) {
        setDefault();
        this.view = parent;
    }

    private void setDefault() {
        message = "";
        messageColor = COLOR_DEFAULT;
        bgColor = COLOR_DEFAULT;
        bgResource = -1;
        duration = LENGTH_SHORT;
        actionText = "";
        actionIcon = -1;
        actionTextColor = COLOR_DEFAULT;
        bottomMargin = 0;
        leftMargin = 0;
        rightMargin = 0;
    }

    /**
     * Set the view to find a parent from.
     *
     * @param view The view to find a parent from.
     * @return the single {@link SnackbarUtils} instance
     */
    public static SnackbarUtils with(@NonNull final View view) {
        return new SnackbarUtils(view);
    }

    /**
     * Set the message.
     *
     * @param msg The message.
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setMessage(@NonNull final CharSequence msg) {
        this.message = msg;
        return this;
    }

    /**
     * Set the color of message.
     *
     * @param color The color of message.
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setMessageColor(@ColorInt final int color) {
        this.messageColor = color;
        return this;
    }

    /**
     * Set the color of background.
     *
     * @param color The color of background.
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setBgColor(@ColorInt final int color) {
        this.bgColor = color;
        return this;
    }

    /**
     * Set the resource of background.
     *
     * @param bgResource The resource of background.
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setBgResource(@DrawableRes final int bgResource) {
        this.bgResource = bgResource;
        return this;
    }

    /**
     * Set the duration.
     *
     * @param duration The duration.
     *                 <ul>
     *                 <li>{@link Duration#LENGTH_INDEFINITE}</li>
     *                 <li>{@link Duration#LENGTH_SHORT     }</li>
     *                 <li>{@link Duration#LENGTH_LONG      }</li>
     *                 </ul>
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setDuration(@Duration final int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param listener The click listener.
     * @return the single {@link SnackbarUtils} instance
     */
    public SnackbarUtils setAction(@NonNull final CharSequence text,
                                   @NonNull final View.OnClickListener listener) {
        return setAction(text, COLOR_DEFAULT, -1, listener);
    }

    /**
     * Set the action.
     *
     * @param text     The text.
     * @param color    The color of text.
     * @param listener The click listener.
     * @return the single {@link SnackbarUtils} instance
     */

    public SnackbarUtils setAction(@NonNull final CharSequence text,
                                   @ColorInt final int color,
                                   @DrawableRes final int actionIcon,
                                   @NonNull final View.OnClickListener listener) {
        this.actionText = text;
        this.actionTextColor = color;
        this.actionIcon = actionIcon;
        this.actionListener = listener;
        return this;
    }

    private void setActionIcon(Snackbar snackbar, @DrawableRes final int actionIcon) {
        final Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        final SnackbarContentLayout contentLayout = (SnackbarContentLayout) snackbarView.getChildAt(0);
        @SuppressLint("RestrictedApi") final TextView tv = contentLayout.getActionView();
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(Utils.getApp(), actionIcon), null);
    }

    /**
     * Set the bottom margin.
     *
     * @param bottomMargin The size of bottom margin, in pixel.
     */
    public SnackbarUtils setBottomMargin(@IntRange(from = 1) final int bottomMargin) {
        this.bottomMargin = bottomMargin;
        return this;
    }

    /**
     * Set the left margin.
     *
     * @param leftMargin The size of left margin, in pixel.
     */
    public SnackbarUtils setLeftMargin(@IntRange(from = 1) final int leftMargin) {
        this.leftMargin = leftMargin;
        return this;
    }

    /**
     * Set the right margin.
     *
     * @param rightMargin The size of right margin, in pixel.
     */
    public SnackbarUtils setRightMargin(@IntRange(from = 1) final int rightMargin) {
        this.rightMargin = rightMargin;
        return this;
    }

    /**
     * Show the snackbar.
     */
    public Snackbar show() {
        return show(false);
    }

    /**
     * Show the snackbar.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    public Snackbar show(boolean isShowTop) {
        View view = this.view;
        if (view == null) return null;
        if (isShowTop) {
            ViewGroup suitableParent = findSuitableParentCopyFromSnackbar(view);
            View topSnackBarContainer = suitableParent.findViewWithTag("topSnackBarCoordinatorLayout");
            if (topSnackBarContainer == null) {
                CoordinatorLayout topSnackBarCoordinatorLayout = new CoordinatorLayout(view.getContext());
                topSnackBarCoordinatorLayout.setTag("topSnackBarCoordinatorLayout");
                topSnackBarCoordinatorLayout.setRotation(180);
                // bring to front
                topSnackBarCoordinatorLayout.setElevation(100);
                suitableParent.addView(topSnackBarCoordinatorLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                topSnackBarContainer = topSnackBarCoordinatorLayout;
            }
            view = topSnackBarContainer;
        }
        if (messageColor != COLOR_DEFAULT) {
            SpannableString spannableString = new SpannableString(message);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(messageColor);
            spannableString.setSpan(
                    colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, spannableString, duration));
        } else {
            sWeakSnackbar = new WeakReference<>(Snackbar.make(view, message, duration));
        }
        final Snackbar snackbar = sWeakSnackbar.get();
        final Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        if (isShowTop) {
            for (int i = 0; i < snackbarView.getChildCount(); i++) {
                View child = snackbarView.getChildAt(i);
                child.setRotation(180);
            }
        }
        if (bgResource != -1) {
            snackbarView.setBackgroundResource(bgResource);
        } else if (bgColor != COLOR_DEFAULT) {
            snackbarView.setBackgroundColor(bgColor);
        }
        snackbarView.post(() -> {
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
            if (bottomMargin != 0) {
                params.bottomMargin = bottomMargin;
            }
            if (leftMargin != 0) {
                params.leftMargin = leftMargin;
            }
            if (rightMargin != 0) {
                params.rightMargin = rightMargin;
            }
            snackbarView.setLayoutParams(params);
        });
        if (actionText.length() > 0 && actionListener != null) {
            if (actionTextColor != COLOR_DEFAULT) {
                snackbar.setActionTextColor(actionTextColor);
            }
            if (actionIcon != -1) {
                setActionIcon(snackbar, actionIcon);
            }
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
        return snackbar;
    }

    /**
     * Show the snackbar with success style.
     */
    public void showSuccess() {
        showSuccess(false);
    }

    /**
     * Show the snackbar with success style.
     *
     * @param isShowTop True to show the snack bar on the top, false otherwise.
     */
    public void showSuccess(boolean isShowTop) {
        bgColor = COLOR_SUCCESS;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Show the snackbar with warning style.
     */
    public void showWarning() {
        showWarning(false);
    }

    /**
     * Show the snackbar with warning style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    public void showWarning(boolean isShowTop) {
        bgColor = COLOR_WARNING;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Show the snackbar with error style.
     */
    public void showError() {
        showError(false);
    }

    /**
     * Show the snackbar with error style.
     *
     * @param isShowTop True to show the snackbar on the top, false otherwise.
     */
    public void showError(boolean isShowTop) {
        bgColor = COLOR_ERROR;
        messageColor = COLOR_MESSAGE;
        actionTextColor = COLOR_MESSAGE;
        show(isShowTop);
    }

    /**
     * Dismiss the snackbar.
     */
    public static void dismiss() {
        if (sWeakSnackbar != null && sWeakSnackbar.get() != null) {
            sWeakSnackbar.get().dismiss();
            sWeakSnackbar = null;
        }
    }

    /**
     * Return the view of snackbar.
     *
     * @return the view of snackbar
     */
    public static View getView() {
        Snackbar snackbar = sWeakSnackbar.get();
        if (snackbar == null) return null;
        return snackbar.getView();
    }

    /**
     * Add view to the snackbar.
     * <p>Call it after {@link #show()}</p>
     *
     * @param layoutId The id of layout.
     * @param params   The params.
     */
    public static void addView(@LayoutRes final int layoutId,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) view;
            View child = LayoutInflater.from(view.getContext()).inflate(layoutId, null);
            layout.addView(child, -1, params);
        }
    }

    /**
     * Add view to the snackbar.
     * <p>Call it after {@link #show()}</p>
     *
     * @param child  The child view.
     * @param params The params.
     */
    public static void addView(@NonNull final View child,
                               @NonNull final ViewGroup.LayoutParams params) {
        final View view = getView();
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) view;
            layout.addView(child, params);
        }
    }

    private static ViewGroup findSuitableParentCopyFromSnackbar(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            }

            if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                }

                fallback = (ViewGroup) view;
            }

            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        return fallback;
    }
}

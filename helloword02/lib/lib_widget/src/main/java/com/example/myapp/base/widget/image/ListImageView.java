package com.example.myapp.base.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.myapp.base.utils.SizeUtils;
import com.example.myapp.base.widget.R;
import com.example.myapp.base.widget.base.BaseFrameLayout;
import com.example.myapp.base.widget.databinding.BaseViewListImageBinding;
import com.example.myapp.base.widget.utils.GlideUtil;

/**
 * 列表item的图片控件
 */
public class ListImageView extends BaseFrameLayout {
    private static final String TAG = "ListImageView";
    private BaseViewListImageBinding mBinding;
    // 默认圆角8dp
    private int dp8;
    // 默认内边距12dp
    private int dp12;
    private int radius;
    // 背景色
    private int background_color;
    // 内边距
    private int padding;

    public ListImageView(Context context) {
        super(context);
    }

    public ListImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        super.initView(attrs);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.base_view_list_image, this, true);
        dp8 = SizeUtils.dp2px(8);
        dp12 = SizeUtils.dp2px(12);

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ListImageView);
//        TypedArray array = getContext().obtainStyledAttributes(R.styleable.ListImageView);
            radius = array.getDimensionPixelSize(R.styleable.ListImageView_radius, dp8);
            background_color = array.getResourceId(R.styleable.ListImageView_background_color, com.example.myapp.base.resource.R.color.common_divider_color);
            padding = array.getDimensionPixelSize(R.styleable.ListImageView_padding, dp12);
            array.recycle();
        }
        setPadding(padding, padding, padding, padding);
        setBackgroundDrawable();
        setDefaultImage();
    }

    private void setBackgroundDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        int color = ContextCompat.getColor(getContext(), background_color);
        gradientDrawable.setColor(color);
        setBackground(gradientDrawable);
    }

    private void setDefaultImage() {
//        mBinding.shapeableImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        mBinding.shapeableImageView.setImageResource(com.example.myapp.base.widget.R.drawable.base_default_img_square);
    }

    public void setImageUrl(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            setDefaultImage();
            return;
        }
        GlideUtil.loadSquareImg(mBinding.shapeableImageView, imageUrl);
    }
}

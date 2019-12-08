package com.jelly.baselibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jelly.baselibrary.R;

/**
 * Author：zhuxudong 72064627
 * Date： 2019/11/19 17:35
 * <p>
 * Description: Glide封装 图片加载
 */
public class ImageUtil {

    /**
     * Author：
     * Date：2019.11.27 18:49
     * Description：使用Glide 加载 网络图片 设置为圆形
     *
     * @param context      上下文
     * @param imageUrl     图片地址
     * @param imageView    图片控件
     * @param defaultImage 默认图片
     */
    public static void loadCircleImage(final Context context, String imageUrl, final ImageView imageView, int defaultImage) {

    }
}

package com.example.myapp.base.widget.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapp.base.utils.Utils;
import com.example.myapp.base.widget.R;

public class GlideUtil {

    public static void loadSquareImg(ImageView imageView, String imgUrl) {
//        loadImg(imageView, imgUrl, R.drawable.base_default_img_square);
    }

    public static void loadStaggered(ImageView imageView, String imgUrl) {
//        loadImg(imageView, imgUrl, R.drawable.base_default_img_staggered);
    }

    public static void loadBannerImg(ImageView imageView, String imgUrl) {
//        loadImg(imageView, imgUrl, R.drawable.base_default_banner);

//        Request request = new Request.Builder().url(imgUrl).build();
//        new OkHttpClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (Utils.isActivityDestroy(activity)) {
//                    return;
//                }
//                imageView.setImageResource(R.drawable.base_default_banner);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (Utils.isActivityDestroy(activity)) {
//                    return;
//                }
//                //得到从网上获取资源，转换成我们想要的类型
//                byte[] bytes = response.body().bytes();
//                ThreadUtils.runOnUiThread(() -> {
//                    RequestManager with = Glide.with(activity);
//                    if (ImageUtils.isGIF(bytes)) {
//                        with.asGif();
//                    }
//                    RequestOptions options = RequestOptions.errorOf(R.drawable.base_default_img_square)
//                            .placeholder(R.drawable.base_default_img_square);
//                    with.load(bytes).apply(options).into(imageView);
//                });
//            }
//        });
    }

    public static void loadAvatar(ImageView imageView, String imgUrl, @DrawableRes int defaultAvatar) {
        loadImg(imageView, imgUrl, defaultAvatar);
    }

    public static void loadDetailBannerImg(ImageView imageView, String imgUrl) {
//        loadImg(imageView, imgUrl, R.drawable.base_default_detail_banner);
    }

    public static void loadImg(ImageView imageView, String imgUrl, @DrawableRes int defaultImg) {
        if (imageView == null) {
            return;
        }
        if (TextUtils.isEmpty(imgUrl)) {
            imageView.setImageResource(defaultImg);
            return;
        }
        Activity activity = Utils.getActivity(imageView.getContext());
        if (Utils.isActivityDestroy(activity)) {
            return;
        }
        RequestOptions options = RequestOptions.errorOf(defaultImg)
                .placeholder(defaultImg);
        Glide.with(activity)
                .load(imgUrl)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}

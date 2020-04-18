package io.filenet.wallet.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.youth.banner.loader.ImageLoader;


public class GlideImageLoader extends ImageLoader {

    public GlideImageLoader(int defaultImage) {
        this.defaultImage = defaultImage;
    }

    private int defaultImage;

    public static void loadImage(ImageView view, String url, int defaultImage) {
        loadImage(view, url, defaultImage, -1);
    }

    public static void loadBmpImage(ImageView imageView, Bitmap bitmap, int defaultImage) {
        loadImage(imageView, bitmap, defaultImage, -1);
    }

    @SuppressLint("CheckResult")
    private static void loadImage(final ImageView view, Object img, @DrawableRes int defaultImage, @DrawableRes int errorImage) {
        if (view == null) {
            return;
        }
        Context context = view.getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        RequestOptions options = new RequestOptions().fitCenter();
        if (defaultImage != -1) {
            options.placeholder(defaultImage);
        }
        if (errorImage != -1) {
            options.error(errorImage);
        }

        Glide.with(context)
                .load(img)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(view);
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        loadImage(imageView, path, defaultImage, -1);

    }

}
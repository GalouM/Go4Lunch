package com.galou.go4lunch.util;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by galou on 2019-04-24
 */
public class ImageUrlBindingAdapter {

    @BindingAdapter("bind:imageUrlCircle")
    public static void loadImageCircle(ImageView view, String url){
        if(url != null) {
            Glide.with(view.getContext()).load(url).apply(RequestOptions.circleCropTransform()).into(view);
        }
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView view, String url){
        if(url != null) {
            Glide.with(view.getContext()).load(url).apply(RequestOptions.centerCropTransform()).into(view);
        }
    }

}

package com.galou.go4lunch.util;

import android.widget.FrameLayout;

import androidx.databinding.BindingAdapter;

/**
 * Created by galou on 2019-04-30
 */
public class ForegroundAlphaBindingAdapter {

    @BindingAdapter("app:foregroundAlpha")
    public static void setAlphaForeground(FrameLayout view, int alpha){
        view.getForeground().setAlpha(alpha);

    }
}

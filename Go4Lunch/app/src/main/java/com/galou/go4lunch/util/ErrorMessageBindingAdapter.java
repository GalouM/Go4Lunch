package com.galou.go4lunch.util;

import android.content.Context;
import android.content.res.Resources;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by galou on 2019-04-30
 */
public class ErrorMessageBindingAdapter {

    @BindingAdapter("app:errorMessage")
    public static void setErrorMessage(TextInputLayout view, int errorMessage){
        if(errorMessage != 0) {
            Resources res = view.getResources();
            view.setError(res.getString(errorMessage));
        }

    }
}

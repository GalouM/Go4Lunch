package com.galou.go4lunch.util;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by galou on 2019-04-30
 */
public class ErrorMessageBindingAdapter {

    @BindingAdapter("app:errorMessage")
    public static void setErrorMessage(TextInputLayout view, String errorMessage){
        view.setError(errorMessage);

    }
}

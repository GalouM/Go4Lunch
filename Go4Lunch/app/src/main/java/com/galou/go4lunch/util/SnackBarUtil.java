package com.galou.go4lunch.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by galou on 2019-04-22
 */
public abstract class SnackBarUtil {

    public static void showSnackBar(View view, String message){
        if(view == null || message == null){
            return;
        }
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}

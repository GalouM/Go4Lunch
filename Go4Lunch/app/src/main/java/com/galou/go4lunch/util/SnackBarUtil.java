package com.galou.go4lunch.util;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by galou on 2019-04-22
 */
public abstract class SnackBarUtil {

    public static void showSnackBar(View view, String message){
        if(view == null || message == null){
            return;
        }
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        SnackBarHelper.configSnackBar(view.getContext(), snackbar);
        snackbar.show();

    }
}

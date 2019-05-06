package com.galou.go4lunch.util;

import android.content.Context;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.galou.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by galou on 2019-04-27
 */
public class SnackBarHelper {

    public static void configSnackBar(Context context, Snackbar snackbar){
        addMargins(snackbar);
        setRoundBordersBg(context, snackbar);
        ViewCompat.setElevation(snackbar.getView(), 6f);
    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        snackbar.getView().setBackground(context.getResources().getDrawable(R.drawable.snackbar_design));
    }

    private static void addMargins(Snackbar snackbar) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 12);
        snackbar.getView().setLayoutParams(params);
    }



}

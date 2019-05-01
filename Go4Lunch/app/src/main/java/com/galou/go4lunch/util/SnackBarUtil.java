package com.galou.go4lunch.util;

import android.content.res.Resources;
import android.view.View;

import androidx.annotation.Nullable;

import com.galou.go4lunch.R;
import com.galou.go4lunch.base.BaseViewModel;
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

    public static void showSnackBarWithRetryButton(View view, String message, BaseViewModel viewModel, RetryAction retryAction){
        if(view == null || message == null){
            return;
        }
        Resources res = view.getResources();
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(res.getString(R.string.retry_button), snack -> viewModel.retry(retryAction));
        SnackBarHelper.configSnackBar(view.getContext(), snackbar);
        snackbar.show();

    }
}

package com.galou.go4lunch.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.Main.MainActivity;
import com.galou.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Authentication extends AppCompatActivity {

    @BindView(R.id.auth_activity_root_view) CoordinatorLayout rootView;

    public static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        startSignInActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void startSignInActivity(){
        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setTheme(R.style.LoginTheme)
        .setAvailableProviders(
                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
        )
        .setIsSmartLockEnabled(false, true)
        .setLogo(R.drawable.go4lunch_icon)
        .build(), RC_SIGN_IN);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(rootView, getString(R.string.error_authentication_canceled));
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(rootView, getString(R.string.error_no_internet));
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(rootView, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void showSnackBar(CoordinatorLayout rootView, String message){
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();

    }




}

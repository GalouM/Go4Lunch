package com.galou.go4lunch.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.Main.MainActivity;
import com.galou.go4lunch.R;
import com.galou.go4lunch.util.SnackBarUtil;

import java.util.Arrays;


public class AuthenticationActivity extends AppCompatActivity implements AuthenticationNavigator {

    private AuthenticationViewModel viewModel;

    public static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        viewModel = obtainViewModel();
        setupOpenMainActivity();
        setupOpenSignInActivity();
        setupSnackBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.checkIfUserIsLogged();
    }

    private AuthenticationViewModel obtainViewModel(){
        return ViewModelProviders.of(this)
                .get(AuthenticationViewModel.class);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        viewModel.handleResponseAfterSignIn(requestCode, resultCode, response);
    }



    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getWindow().getDecorView().getRootView(), getString(message));
            }
        });

    }

    private void setupOpenMainActivity(){
        viewModel.getOpenNewActivityEvent().observe(this, openActivity -> openMainActivity());
    }

    private void setupOpenSignInActivity(){
        viewModel.getOpenSignInActivityEvent().observe(this, openSignIn -> startSignInActivity());
    }

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void startSignInActivity(){
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
}

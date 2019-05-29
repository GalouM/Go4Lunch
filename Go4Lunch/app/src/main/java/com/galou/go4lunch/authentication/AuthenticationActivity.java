package com.galou.go4lunch.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.ActivityAuthenticationBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.main.MainActivity;
import com.galou.go4lunch.util.RetryAction;
import com.galou.go4lunch.util.SnackBarUtil;

import java.util.Arrays;


public class AuthenticationActivity extends AppCompatActivity implements AuthenticationNavigator {

    private AuthenticationViewModel viewModel;
    private ActivityAuthenticationBinding binding;

    public static final int RC_SIGN_IN = 123;

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        this.createViewModelConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.checkIfUserIsLogged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        viewModel.handleResponseAfterSignIn(requestCode, resultCode, response);
    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void createViewModelConnection() {
        viewModel = obtainViewModel();
        this.setupOpenMainActivity();
        this.setupOpenSignInActivity();
        this.setupSnackBar();
        this.setupSnackBarWithAction();
    }

    private AuthenticationViewModel obtainViewModel(){
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(AuthenticationViewModel.class);

    }

    private void setupSnackBar(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarMessage().observe(this, messageEvent -> {
            Integer message = messageEvent.getContentIfNotHandle();
            if(message != null){
                SnackBarUtil.showSnackBar(view, getString(message));
            }
        });

    }

    private void setupSnackBarWithAction(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarWithAction().observe(this, actionEvent -> {
            RetryAction action = actionEvent.getContentIfNotHandle();
            if(action != null){
                SnackBarUtil.showSnackBarWithRetryButton(view, getString(R.string.error_unknown_error), viewModel, action);
            }
        });

    }

    private void setupOpenMainActivity(){
        viewModel.getOpenNewActivityEvent().observe(this, openMainEvent -> {
            if(openMainEvent.getContentIfNotHandle() != null){
            openMainActivity();
            }
        });
    }

    private void setupOpenSignInActivity(){
        viewModel.getOpenSignInActivityEvent().observe(this, openSignInEvent ->{
            if(openSignInEvent.getContentIfNotHandle() != null){
                startSignInActivity();
            }
        });
    }

    // --------------------
    // ACTIONS FROM VIEW MODEL
    // --------------------

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void startSignInActivity(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build())
                )
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.go4lunch_icon)
                .build(), RC_SIGN_IN);
    }
}

package com.galou.go4lunch.settings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.firebase.ui.auth.AuthUI;
import com.galou.go4lunch.BuildConfig;
import com.galou.go4lunch.R;
import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.galou.go4lunch.base.ButtonActionListener;
import com.galou.go4lunch.databinding.ActivitySettingsBinding;
import com.galou.go4lunch.injection.Injection;
import com.galou.go4lunch.injection.ViewModelFactory;
import com.galou.go4lunch.util.SnackBarUtil;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingsActivity extends AppCompatActivity implements SettingsContract, EasyPermissions.PermissionCallbacks {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    // DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    // FOR TESTING
    @VisibleForTesting
    protected CountingIdlingResource espressoTestIdlingResource;

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureBindingAndViewModel();
        this.createViewModelConnections();
        configureToolbar();
        configureEspressoIdlingResource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }

    // -----------------
    // CONFIGURATION UI
    // -----------------

    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void configureBindingAndViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        viewModel.configureSaveDataRepo(getApplicationContext());
        viewModel.configureUser();

    }

    private void createViewModelConnections() {
        ButtonActionListener buttonActionListener = getButtonActionListener();
        binding.setListener(buttonActionListener);
        setupSnackBar();
        setupSnackBarWithAction();
        setupOpenConfirmationDialog();
        setupDeleteAccount();

    }

    private SettingsViewModel obtainViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        return ViewModelProviders.of(this, viewModelFactory)
                .get(SettingsViewModel.class);
    }

    private void setupSnackBar(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                //this.decrementIdleResource();
                SnackBarUtil.showSnackBar(view, getString(message));
            }
        });
    }

    private void setupSnackBarWithAction(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarWithAction().observe(this, action -> {
            if(action != null){
                SnackBarUtil.showSnackBarWithRetryButton(view, getString(R.string.error_unknown_error), viewModel, action);
            }
        });

    }

    private void setupDeleteAccount(){
        viewModel.getDeleteUser().observe(this, deleted -> deleteAccountAndGoBackToAuth());
    }

    private void setupOpenConfirmationDialog(){
        viewModel.getOpenDialog().observe(this, dialog -> openConfirmationDialog());
    }

    //----- LISTENER BUTTON -----

    private ButtonActionListener getButtonActionListener(){
        return view -> {
            int id = view.getId();
            switch (id){
                case R.id.notification_switch:
                    viewModel.notificationStateChanged(((SwitchCompat) view).isChecked());
                    break;
                case R.id.update_button:
                    this.incrementIdleResource();
                    viewModel.updateUserInfo();
                    break;
                case R.id.delete_button:
                    viewModel.deleteUserFromDBRequest();
                    break;
                case R.id.photo_user:
                    chooseImageFromPhone();
                    break;
            }

        };
    }

    // --------------------
    // PICK PHOTO INTENT
    // --------------------

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private void chooseImageFromPhone() {
        if(! EasyPermissions.hasPermissions(this, PERMS)){
            EasyPermissions.requestPermissions(
                    this, getString(R.string.need_permission_message), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoIntent, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data){
        if(requestCode == RC_CHOOSE_PHOTO){
            if (resultCode == RESULT_OK){
                Uri uriImage = data.getData();
                if(uriImage != null) {
                    viewModel.updateUserPhoto(uriImage.toString());
                }
            }
        }

    }

    // --------------------
    // ACTIONS FROM VIEW MODEL
    // --------------------

    @Override
    public void deleteAccountAndGoBackToAuth() {
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, aVoid -> {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    startActivity(intent);
                });
    }

    @Override
    public void openConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_delete_account_message)
                .setTitle(R.string.alert_dialog_delete_account_title)
                .setPositiveButton(R.string.yes_button, (dialogInterface, i) -> viewModel.deleteUserFromDB())
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    // -----------------
    // PERMISSIONS
    // -----------------

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        chooseImageFromPhone();

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }



    // -----------------
    // FOR TESTING
    // -----------------

    @VisibleForTesting
    public CountingIdlingResource getEspressoIdlingResource() { return espressoTestIdlingResource; }

    @VisibleForTesting
    private void configureEspressoIdlingResource(){
        this.espressoTestIdlingResource = new CountingIdlingResource("Network_Call");
    }

    void incrementIdleResource(){
        if (BuildConfig.DEBUG) this.espressoTestIdlingResource.increment();
    }

    void decrementIdleResource(){
        if (BuildConfig.DEBUG) this.espressoTestIdlingResource.decrement();
    }

}

package com.galou.go4lunch.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.galou.go4lunch.R;
import com.galou.go4lunch.authentication.AuthenticationActivity;
import com.galou.go4lunch.databinding.ActivitySettingsBinding;
import com.galou.go4lunch.util.SnackBarUtil;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingsActivity extends AppCompatActivity implements SettingsContract {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    // DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    // DATA FOR NOTIFICATION
    private SharedPreferences preferences;
    public static final String KEY_PREF_NOTIFICATION_ENABLE = "notificationEnabled";
    public static final String KEY_PREF = "prefNotification";

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setBindingAndViewModel();
        this.getNotificationSettingsFromPreferences();
        this.createViewModelConnections();
        configureToolbar();
        viewModel.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponse(requestCode, resultCode, data);
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

    private void setBindingAndViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

    }

    private void createViewModelConnections() {
        ButtonActionListener buttonActionListener = getButtonActionListener();
        binding.setListener(buttonActionListener);
        setupSnackBar();
        setupDeleteAccount();

    }

    private SettingsViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(SettingsViewModel.class);
    }

    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getWindow().getDecorView().getRootView(), getString(message));
            }
        });
    }

    private void setupDeleteAccount(){
        viewModel.getDeleteUser().observe(this, deleted -> deleteAccountAndGoBackToAuth());
    }

    //----- LISTENER BUTTON -----

    private ButtonActionListener getButtonActionListener(){
        return view -> {
            int id = view.getId();
            switch (id){
                case R.id.notification_switch:
                    viewModel.notificationStateChanged(((SwitchCompat) view).isChecked());
                    saveNotificationSettings(((SwitchCompat) view).isChecked());
                    break;
                case R.id.update_button:
                    viewModel.updateUserInfo();
                    break;
                case R.id.delete_button:
                    viewModel.deleteUserFromDB();
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
    // NOTIFICATION SETTINGS
    // --------------------

    private void getNotificationSettingsFromPreferences() {
        preferences = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        boolean notificationEnabled = preferences.getBoolean(KEY_PREF_NOTIFICATION_ENABLE, true);
        viewModel.isNotificationEnabled.setValue(notificationEnabled);
    }

    // --------------------
    // ACTIONS FROM VIEW MODEL
    // --------------------

    @Override
    public void saveNotificationSettings(boolean state){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PREF_NOTIFICATION_ENABLE, state);
        editor.apply();
    }

    @Override
    public void deleteAccountAndGoBackToAuth() {
        AuthUI.getInstance()
                .delete(this)
                .addOnSuccessListener(this, aVoid -> {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    startActivity(intent);
                });
    }

}

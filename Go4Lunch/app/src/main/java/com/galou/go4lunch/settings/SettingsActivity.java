package com.galou.go4lunch.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

import static com.galou.go4lunch.authentication.AuthenticationActivity.USER_BUNDLE_KEY;

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

    public static final String KEY_BUNDLE_USER = "modifiedUser";
    private Intent intent;

    // --------------------
    // LIFE CYCLE STATE
    // --------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureBindingAndViewModel();
        this.configureUserForViewModel();
        this.getNotificationSettingsFromPreferences();
        this.createViewModelConnections();
        configureToolbar();
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

    @Override
    public void onBackPressed() {
        configureResultActivity();
        finish();
    }



    private void configureResultActivity() {
        if (intent == null){
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_OK, intent);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id  = item.getItemId();
        if(id == android.R.id.home){
            configureResultActivity();
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // --------------------
    // VIEW MODEL CONNECTIONS
    // --------------------

    private void configureBindingAndViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = obtainViewModel();
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

    }

    private void createViewModelConnections() {
        ButtonActionListener buttonActionListener = getButtonActionListener();
        binding.setListener(buttonActionListener);
        setupSnackBar();
        setupOpenConfirmationDialog();
        setupDeleteAccount();
        setupNewUser();

    }

    private SettingsViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(SettingsViewModel.class);
    }

    private void configureUserForViewModel() {
        String jsonUser = getIntent().getStringExtra(USER_BUNDLE_KEY);
        viewModel.configureUser(jsonUser);
    }

    private void setupSnackBar(){
        View view = findViewById(android.R.id.content);
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(view, getString(message));
            }
        });
    }

    private void setupDeleteAccount(){
        viewModel.getDeleteUser().observe(this, deleted -> deleteAccountAndGoBackToAuth());
    }

    private void setupOpenConfirmationDialog(){
        viewModel.getOpenDialog().observe(this, dialog -> openConfirmationDialog());
    }

    private void setupNewUser(){
        viewModel.getModifiedUser().observe(this, this::configureNewUserData);
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
    // NOTIFICATION SETTINGS
    // --------------------

    private void getNotificationSettingsFromPreferences() {
        preferences = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        boolean notificationEnabled = preferences.getBoolean(KEY_PREF_NOTIFICATION_ENABLE, true);
        viewModel.isNotificationEnabled.setValue(notificationEnabled);
    }

    private void configureNewUserData(String user){
        intent = new Intent();
        intent.putExtra(KEY_BUNDLE_USER, user);

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

    @Override
    public void openConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_delete_account_message)
                .setTitle(R.string.alert_dialog_delete_account_title)
                .setPositiveButton(R.string.yes_button, (dialogInterface, i) -> viewModel.deleteUserFromDB())
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

}

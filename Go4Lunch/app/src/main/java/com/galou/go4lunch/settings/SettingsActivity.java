package com.galou.go4lunch.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.ActivitySettingsBinding;
import com.galou.go4lunch.util.SnackBarUtil;

public class SettingsActivity extends AppCompatActivity implements SettingsContract {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    private SharedPreferences preferences;
    public static final String KEY_PREF_NOTIFICATION_ENABLE = "notificationEnabled";
    public static final String KEY_PREF = "prefNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = obtainViewModel();
        getNotificationSettingsFromPreferences();
        NotificationActionListener notificationActionListener = getNotificationActionListener();
        UpdateActionListener updateActionListener = getUpdateActionListener();
        binding.setViewmodel(viewModel);
        binding.setListenerNotification(notificationActionListener);
        binding.setListenerUpdate(updateActionListener);
        binding.setLifecycleOwner(this);
        configureToolbar();
        setupSnackBar();

        viewModel.onStart();
    }

    private SettingsViewModel obtainViewModel() {
        return ViewModelProviders.of(this)
                .get(SettingsViewModel.class);
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void setupSnackBar(){
        viewModel.getSnackBarMessage().observe(this, message -> {
            if(message != null){
                SnackBarUtil.showSnackBar(getWindow().getDecorView().getRootView(), getString(message));
            }
        });
    }

    private NotificationActionListener getNotificationActionListener(){
        return view -> {
            viewModel.notificationStateChanged(((SwitchCompat) view).isChecked());
            saveNotificationSettings(((SwitchCompat) view).isChecked());
        };
    }

    private UpdateActionListener getUpdateActionListener(){
        return view -> viewModel.updateUserInfo();
    }

    private void getNotificationSettingsFromPreferences() {
        preferences = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
        boolean notificationEnabled = preferences.getBoolean(KEY_PREF_NOTIFICATION_ENABLE, false);
        viewModel.isNotificationEnabled.setValue(notificationEnabled);
    }

    @Override
    public void saveNotificationSettings(boolean state){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PREF_NOTIFICATION_ENABLE, state);
        editor.apply();
    }
}

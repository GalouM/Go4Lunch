package com.galou.go4lunch.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.galou.go4lunch.R;
import com.galou.go4lunch.databinding.ActivitySettingsBinding;
import com.galou.go4lunch.util.SnackBarUtil;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = obtainViewModel();
        NotificationActionListener notificationActionListener = getNotificationActionListener();
        binding.setViewmodel(viewModel);
        binding.setListener(notificationActionListener);
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
        return view -> viewModel.notificationStateChanged(((SwitchCompat) view).isChecked());
    }
}

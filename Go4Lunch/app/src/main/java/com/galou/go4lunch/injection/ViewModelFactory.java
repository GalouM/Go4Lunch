package com.galou.go4lunch.injection;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.galou.go4lunch.authentication.AuthenticationViewModel;
import com.galou.go4lunch.main.MainActivityViewModel;
import com.galou.go4lunch.models.User;
import com.galou.go4lunch.settings.SettingsViewModel;
import com.galou.go4lunch.workmates.WorkmatesViewModel;

/**
 * Created by galou on 2019-05-02
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    public ViewModelFactory(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(AuthenticationViewModel.class)){
            return (T) new AuthenticationViewModel(userRepository);
        }
        if(modelClass.isAssignableFrom(MainActivityViewModel.class)){
            return (T) new MainActivityViewModel(userRepository);
        }
        if(modelClass.isAssignableFrom(SettingsViewModel.class)){
            return (T) new SettingsViewModel(userRepository);
        }
        if(modelClass.isAssignableFrom(WorkmatesViewModel.class)){
            return (T) new WorkmatesViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

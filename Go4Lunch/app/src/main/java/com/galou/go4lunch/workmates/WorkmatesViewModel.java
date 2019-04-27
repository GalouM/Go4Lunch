package com.galou.go4lunch.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.models.User;

import java.util.List;

/**
 * Created by galou on 2019-04-27
 */
public class WorkmatesViewModel extends BaseViewModel {

    //----- PRIVATE LIVE DATA -----
    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    //----- GETTER LIVE DATA -----
    public LiveData<List<User>> getUsers(){ return users; }
}

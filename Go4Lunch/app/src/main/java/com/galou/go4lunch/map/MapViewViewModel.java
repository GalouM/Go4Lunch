package com.galou.go4lunch.map;

import com.galou.go4lunch.base.BaseViewModel;
import com.galou.go4lunch.repositories.UserRepository;
import com.galou.go4lunch.util.RetryAction;

/**
 * Created by galou on 2019-05-08
 */
public class MapViewViewModel extends BaseViewModel {



    public MapViewViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void retry(RetryAction retryAction) {

    }
}

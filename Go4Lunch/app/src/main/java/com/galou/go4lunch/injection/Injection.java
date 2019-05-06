package com.galou.go4lunch.injection;

import com.galou.go4lunch.repositories.UserRepository;

/**
 * Created by galou on 2019-05-06
 */
public class Injection {

    public static UserRepository provideUserRepository(){
        return UserRepository.getInstance();
    }

    public static ViewModelFactory provideViewModelFactory(){
        UserRepository userRepository = provideUserRepository();

        return new ViewModelFactory(userRepository);
    }
}

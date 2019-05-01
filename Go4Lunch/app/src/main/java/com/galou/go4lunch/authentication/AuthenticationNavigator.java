package com.galou.go4lunch.authentication;

/**
 * Created by galou on 2019-04-22
 */
public interface AuthenticationNavigator {

    void openMainActivity(String user);
    void startSignInActivity();
}

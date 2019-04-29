package com.galou.go4lunch.settings;

/**
 * Created by galou on 2019-04-25
 */
public interface SettingsContract {

    void saveNotificationSettings(boolean state);
    void deleteAccountAndGoBackToAuth();
    void openConfirmationDialog();
}

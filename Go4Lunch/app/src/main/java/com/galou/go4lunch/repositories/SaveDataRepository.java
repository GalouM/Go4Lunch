package com.galou.go4lunch.repositories;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by galou on 2019-05-20
 */
public class SaveDataRepository {

    // DATA FOR NOTIFICATION
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String KEY_PREF = "prefNotification";
    public static final String KEY_PREF_USER_ID = "userId";

    private static volatile SaveDataRepository INSTANCE;

    public static SaveDataRepository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new SaveDataRepository();
        }
        return INSTANCE;
    }

    private SaveDataRepository() {}

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void configureContext(Context context){
        preferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);

    }

    //-----SAVE------
    public void saveNotificationSettings(boolean state, String userId){
        editor = preferences.edit();
        editor.putBoolean(userId, state);
        editor.apply();
    }

    public void saveUserId(String userId){
        editor = preferences.edit();
        editor.putString(KEY_PREF_USER_ID, userId);
        editor.apply();
    }

    //-----GET------
    public Boolean getNotificationSettings(String userId){
        return preferences.getBoolean(userId, true);
    }

    public String getUserId(){
        return preferences.getString(KEY_PREF_USER_ID, null);
    }
}

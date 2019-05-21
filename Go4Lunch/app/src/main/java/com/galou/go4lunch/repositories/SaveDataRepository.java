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

    public static final String KEY_PREF_NOTIFICATION_ENABLE = "notificationEnabled";
    public static final String KEY_PREF = "prefNotification";
    public static final String KEY_PREF_USER_ID = "userId";
    public static final String KEY_PREF_RESTAURANT_NAME = "restaurantName";
    public static final String KEY_PREF_RESTAURANT_ID = "restaurantId";

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
    public void saveNotificationSettings(boolean state){
        editor = preferences.edit();
        editor.putBoolean(KEY_PREF_NOTIFICATION_ENABLE, state);
        editor.apply();
    }

    public void saveUserId(String userId){
        editor = preferences.edit();
        editor.putString(KEY_PREF_USER_ID, userId);
        editor.apply();
    }

    public void saveRestaurantName(String name){
        editor = preferences.edit();
        editor.putString(KEY_PREF_RESTAURANT_NAME, name);
        editor.apply();
    }

    public void saveRestaurantId(String id){
        editor = preferences.edit();
        editor.putString(KEY_PREF_RESTAURANT_ID, id);
        editor.apply();
    }

    //-----GET------
    public Boolean getNotificationSettings(){
        return preferences.getBoolean(KEY_PREF_NOTIFICATION_ENABLE, true);
    }

    public String getUserId(){
        return preferences.getString(KEY_PREF_USER_ID, null);
    }

    public String getRestaurantName(){
        return preferences.getString(KEY_PREF_RESTAURANT_NAME, null);
    }

    public String getRestaurantId(){
        return preferences.getString(KEY_PREF_RESTAURANT_ID, null);
    }
}

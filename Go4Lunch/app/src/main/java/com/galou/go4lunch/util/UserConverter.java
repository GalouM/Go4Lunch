package com.galou.go4lunch.util;

import com.galou.go4lunch.models.User;
import com.google.gson.Gson;

/**
 * Created by galou on 2019-05-01
 */
public abstract class UserConverter {
     public static String convertUserInJson(User user){
          Gson gson = new Gson();
          return gson.toJson(user);
     }

     public static User convertJsonInUser(String json){
          Gson gson = new Gson();
          return gson.fromJson(json, User.class);
     }
}

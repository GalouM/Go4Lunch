package com.galou.go4lunch.models;

/**
 * Created by galou on 2019-04-23
 */
public class Restaurant {

    private String uid;
    private String name;
    private String type;

    public Restaurant() { }

    public Restaurant(String uid, String name, String type) {
        this.uid = uid;
        this.name = name;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

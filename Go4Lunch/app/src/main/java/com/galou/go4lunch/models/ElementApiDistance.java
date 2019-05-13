package com.galou.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ElementApiDistance {

    @SerializedName("distance")
    @Expose
    private DistanceApiDistance distance;
    @SerializedName("status")
    @Expose
    private String status;

    public DistanceApiDistance getDistance() {
        return distance;
    }

    public void setDistance(DistanceApiDistance distance) {
        this.distance = distance;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
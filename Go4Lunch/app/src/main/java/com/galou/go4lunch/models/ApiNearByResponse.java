package com.galou.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiNearByResponse {
    @SerializedName("results")
    @Expose
    private List<ResultApiPlace> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<ResultApiPlace> getResults() {
        return results;
    }

    public void setResults(List<ResultApiPlace> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
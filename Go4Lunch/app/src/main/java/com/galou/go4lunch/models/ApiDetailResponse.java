package com.galou.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiDetailResponse {

    @SerializedName("result")
    @Expose
    private ResultApiPlace result;
    @SerializedName("status")
    @Expose
    private String status;

    public ResultApiPlace getResult() {
        return result;
    }

    public void setResult(ResultApiPlace result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
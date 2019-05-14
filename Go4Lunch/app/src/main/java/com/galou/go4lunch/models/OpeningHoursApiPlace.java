package com.galou.go4lunch.models;

import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by galou on 2019-05-13
 */
public class OpeningHoursApiPlace {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<PlaceResult.OpeningHours.Period> periods;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<PlaceResult.OpeningHours.Period> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PlaceResult.OpeningHours.Period> periods) {
        this.periods = periods;
    }
}

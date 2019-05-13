package com.galou.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RowApiDistance {

    @SerializedName("elements")
    @Expose
    private List<ElementApiDistance> elements = null;

    public List<ElementApiDistance> getElements() {
        return elements;
    }

    public void setElements(List<ElementApiDistance> elements) {
        this.elements = elements;
    }

}
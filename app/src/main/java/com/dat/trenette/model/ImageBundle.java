package com.dat.trenette.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dat on 8/10/2017.
 */

public class ImageBundle {
    @SerializedName("images")
    private List<String> images;

    public List<String> getImages() {
        return images;
    }
}

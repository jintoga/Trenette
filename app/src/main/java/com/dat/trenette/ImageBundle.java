package com.dat.trenette;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dat on 8/10/2017.
 */

class ImageBundle {
    @SerializedName("images")
    private List<String> images;

    List<String> getImages() {
        return images;
    }
}

package com.dat.trenette.api;

import android.support.annotation.NonNull;

import com.dat.trenette.model.ImageBundle;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Dat on 8/10/2017.
 */
public interface ImageBundleApi {
    @GET("/{address}")
    Observable<ImageBundle> getImageBundle(@Path(value = "address", encoded = true) @NonNull String imageBundleAddress);
}

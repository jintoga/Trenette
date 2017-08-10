package com.dat.trenette;

import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Dat on 8/10/2017.
 */

class ImageBundleService {
    private static final String ENDPOINT = "http://truvorskameikin.com";
    private ImageBundleApi imageBundleApi;

    ImageBundleService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();

        imageBundleApi = retrofit.create(ImageBundleApi.class);
    }

    ImageBundleApi getApi() {
        return imageBundleApi;
    }

    interface ImageBundleApi {
        @GET("/{address}")
        Observable<ImageBundle> getImageBundle(@Path(value = "address", encoded = true) @NonNull String imageBundleAddress);
    }
}

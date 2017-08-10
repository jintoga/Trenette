package com.dat.trenette.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dat on 8/10/2017.
 */

public class ImageBundleService {
    private static final String ENDPOINT = "http://truvorskameikin.com";
    private ImageBundleApi imageBundleApi;

    public ImageBundleService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ENDPOINT)
                .build();

        imageBundleApi = retrofit.create(ImageBundleApi.class);
    }

    public ImageBundleApi getApi() {
        return imageBundleApi;
    }

}

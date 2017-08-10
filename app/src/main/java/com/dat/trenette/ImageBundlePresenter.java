package com.dat.trenette;

import android.support.annotation.NonNull;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dat on 8/10/2017.
 */

class ImageBundlePresenter {
    private ImageBundleService imageBundleService;
    private ImageBundleLoader imagesLoader;

    ImageBundlePresenter(@NonNull ImageBundleService imageBundleService, @NonNull ImageBundleLoader imagesLoader) {
        this.imageBundleService = imageBundleService;
        this.imagesLoader = imagesLoader;
    }

    void loadImagesBundle(@NonNull String imageBundleAddress) {
        imagesLoader.loading();
        imageBundleService.getApi()
                .getImageBundle(imageBundleAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageBundle -> imagesLoader.imageBundleLoaded(imageBundle.getImages()),
                        throwable -> imagesLoader.loadFailure(throwable),
                        () -> imagesLoader.loadSuccess());
    }
}

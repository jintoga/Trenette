package com.dat.trenette;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dat.trenette.api.ImageBundleService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dat on 8/10/2017.
 */

class TrenettePresenter {

    private static final String SAVED_CURRENT_IMAGE_INDEX = "SAVED_CURRENT_IMAGE_INDEX";
    private static final String SAVED_IMAGE_PATHS = "SAVED_IMAGE_PATHS";

    private static final int SWITCH_TIME = 10;

    private ImageBundleService imageBundleService;
    private TrenetteView imagesLoader;

    private Subscription subscription;

    private static final String ASSET_PATH = "file:///android_asset/";
    private final String[] localImageNames = {
            "carissa-gan-76325.jpg", "eaters-collective-132772.jpg",
            "eaters-collective-132773.jpg", "jakub-kapusnak-296128.jpg"
    };
    private ArrayList<String> imagePaths = new ArrayList<>();

    private int currentImageIndex = 0;

    TrenettePresenter(@NonNull ImageBundleService imageBundleService, @NonNull TrenetteView imagesLoader) {
        this.imageBundleService = imageBundleService;
        this.imagesLoader = imagesLoader;
    }

    void loadImagesBundle(@NonNull String imageBundleAddress) {
        imagesLoader.onLoadingImageBundle();
        imageBundleService.getApi()
                .getImageBundle(imageBundleAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageBundle -> {
                            imagePaths.addAll(imageBundle.getImages());
                            imagesLoader.bindPaginationData(currentImageIndex, imagePaths.size());
                        },
                        throwable -> imagesLoader.onLoadImageBundleFailure(throwable),
                        () -> imagesLoader.onLoadImageBundleSuccess());
    }

    void createSwitchImageObservable() {
        if (subscription == null) {
            subscription =
                    Observable.interval(SWITCH_TIME, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                            .doOnNext(n -> {
                                increaseCurrentImageIndex();
                                bindImageAndPaginationData();
                            })
                            .doOnSubscribe(this::bindImageAndPaginationData)
                            .subscribe();
        }
    }

    void destroySwitchImageObservable() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private void bindImageAndPaginationData() {
        imagesLoader.bindImageData(getImagePath());
        imagesLoader.bindPaginationData(currentImageIndex, imagePaths.size());
    }

    private void increaseCurrentImageIndex() {
        if (currentImageIndex >= 0 && currentImageIndex < imagePaths.size() - 1) {
            currentImageIndex++;
        } else {
            currentImageIndex = 0;
        }
    }

    void initLocalImagePaths() {
        for (String imageAssetsName : localImageNames) {
            String imagePath = ASSET_PATH + imageAssetsName;
            imagePaths.add(imagePath);
        }
    }

    @Nullable
    private String getImagePath() {
        if (imagePaths.isEmpty()) {
            return null;
        }
        return imagePaths.get(currentImageIndex);
    }

    void saveData(@NonNull Bundle outState) {
        outState.putInt(SAVED_CURRENT_IMAGE_INDEX, currentImageIndex);
        outState.putStringArrayList(SAVED_IMAGE_PATHS, imagePaths);
    }

    void restoreData(@NonNull Bundle savedInstanceState) {
        currentImageIndex = savedInstanceState.getInt(SAVED_CURRENT_IMAGE_INDEX, 0);
        ArrayList<String> savedImagePaths = savedInstanceState.getStringArrayList(SAVED_IMAGE_PATHS);
        if (savedImagePaths != null) {
            imagePaths = savedImagePaths;
        } else {
            initLocalImagePaths();
        }
    }
}

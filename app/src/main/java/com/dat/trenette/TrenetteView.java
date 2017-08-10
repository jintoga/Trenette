package com.dat.trenette;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Dat on 8/10/2017.
 */

interface TrenetteView {
    void onLoadingImageBundle();

    void onLoadImageBundleSuccess();

    void onLoadImageBundleFailure(@NonNull Throwable e);

    void bindImageData(@Nullable String imagePath);

    void bindPaginationData(int currentImageIndex, int size);
}

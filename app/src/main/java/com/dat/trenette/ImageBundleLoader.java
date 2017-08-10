package com.dat.trenette;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Dat on 8/10/2017.
 */

interface ImageBundleLoader {
    void loading();

    void imageBundleLoaded(@NonNull List<String> urls);

    void loadSuccess();

    void loadFailure(@NonNull Throwable e);
}

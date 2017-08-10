package com.dat.trenette;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements ImageBundleLoader {

    private static final String SAVED_CURRENT_IMAGE_INDEX = "SAVED_CURRENT_IMAGE_INDEX";
    private static final String SAVED_IMAGE_PATHS = "SAVED_IMAGE_PATHS";

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.pageNumber)
    TextView pageNumber;
    private EditText bundleAddress;

    private static final int SWITCH_TIME = 10;

    private static final String ASSET_PATH = "file:///android_asset/";
    private ArrayList<String> imagePaths = new ArrayList<>();
    private final String[] holderImageNames = {
            "carissa-gan-76325.jpg", "eaters-collective-132772.jpg",
            "eaters-collective-132773.jpg", "jakub-kapusnak-296128.jpg"
    };

    private Subscription subscription;
    private ImageBundlePresenter presenter;

    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            restoreSavedData(savedInstanceState);
        } else {
            initImagePaths();
        }
        presenter = new ImageBundlePresenter(new ImageBundleService(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (subscription == null) {
            initSwitchImageObservable();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private void initSwitchImageObservable() {
        subscription =
                Observable.interval(SWITCH_TIME, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .doOnNext(n -> {
                            increaseCurrentImageIndex();
                            loadImage();
                        })
                        .doOnSubscribe(this::loadImage)
                        .subscribe();
    }

    private void restoreSavedData(@NonNull Bundle savedInstanceState) {
        currentImageIndex = savedInstanceState.getInt(SAVED_CURRENT_IMAGE_INDEX, 0);
        ArrayList<String> savedImagePaths = savedInstanceState.getStringArrayList(SAVED_IMAGE_PATHS);
        if (savedImagePaths != null) {
            imagePaths = savedImagePaths;
        } else {
            initImagePaths();
        }
    }

    private void initImagePaths() {
        for (String imageAssetsName : holderImageNames) {
            String imagePath = ASSET_PATH + imageAssetsName;
            imagePaths.add(imagePath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_load_image_bundle) {
            showLoadImageBundleDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog loadImageBundleDialog;

    private void showLoadImageBundleDialog() {
        if (loadImageBundleDialog == null) {
            @SuppressLint("InflateParams")
            View rootView = LayoutInflater.from(this).inflate(R.layout.load_image_bundle_dialog, null);
            bundleAddress = (EditText) rootView.findViewById(R.id.address);
            loadImageBundleDialog = new DialogBuilder(this)
                    .addCustomView(rootView)
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.load), (dialog, which) -> {
                        presenter.loadImagesBundle(bundleAddress.getText().toString());
                        bundleAddress.setText("");
                    })
                    .build();
            loadImageBundleDialog.setTitle(getString(R.string.load_image_bundle_dialog_title));
        }
        loadImageBundleDialog.show();
    }

    private void loadImage() {
        String imagePath = getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        updatePageNumber();
        final AtomicBoolean playAnimation = new AtomicBoolean(true);
        Picasso.with(this).load(imagePath).fit().centerInside().into(image, new Callback() {
            @Override
            public void onSuccess() {
                if (playAnimation.get()) {
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.setDuration(750);
                    image.startAnimation(fadeIn);

                }
                playAnimation.set(false);
            }

            @Override
            public void onError() {
                playAnimation.set(false);
            }
        });
    }

    @Nullable
    private String getImagePath() {
        if (imagePaths.isEmpty()) {
            return null;
        }
        return imagePaths.get(currentImageIndex);
    }

    private void increaseCurrentImageIndex() {
        if (currentImageIndex >= 0 && currentImageIndex < imagePaths.size() - 1) {
            currentImageIndex++;
        } else {
            currentImageIndex = 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_CURRENT_IMAGE_INDEX, currentImageIndex);
        outState.putStringArrayList(SAVED_IMAGE_PATHS, imagePaths);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void loading() {
        Toast.makeText(this, R.string.loading_image_bundle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void imageBundleLoaded(@NonNull List<String> urls) {
        imagePaths.addAll(urls);
        updatePageNumber();
    }

    private void updatePageNumber() {
        pageNumber.setText(String.format(Locale.getDefault(), getString(R.string.page_number), currentImageIndex + 1, imagePaths.size()));
    }

    @Override
    public void loadSuccess() {
        Toast.makeText(this, R.string.loaded_image_bundle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadFailure(@NonNull Throwable e) {
        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }
}

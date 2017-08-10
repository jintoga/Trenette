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

import com.dat.trenette.api.ImageBundleService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TrenetteView {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.pageNumber)
    TextView pageNumber;
    private EditText bundleAddress;

    private AlertDialog loadImageBundleDialog;

    private TrenettePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new TrenettePresenter(new ImageBundleService(), this);
        if (savedInstanceState != null) {
            presenter.restoreData(savedInstanceState);
        } else {
            presenter.initLocalImagePaths();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.createSwitchImageObservable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.destroySwitchImageObservable();
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.saveData(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadingImageBundle() {
        Toast.makeText(this, R.string.loading_image_bundle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadImageBundleSuccess() {
        Toast.makeText(this, R.string.loaded_image_bundle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadImageBundleFailure(@NonNull Throwable e) {
        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void bindImageData(@Nullable String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
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

    @Override
    public void bindPaginationData(int currentImageIndex, int size) {
        pageNumber.setText(String.format(Locale.getDefault(), getString(R.string.page_number), currentImageIndex + 1, size));
    }

}

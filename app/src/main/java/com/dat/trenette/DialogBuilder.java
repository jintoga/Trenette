package com.dat.trenette;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Dat on 8/10/2017.
 */

class DialogBuilder {

    private AlertDialog.Builder builder;
    private LinearLayout body;

    DialogBuilder(@NonNull Context context) {
        builder = new AlertDialog.Builder(context);
        initBody(context);
    }

    private void initBody(@NonNull Context context) {
        body = new LinearLayout(context);
        body.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) context.getResources().getDimension(R.dimen.dialog_padding);
        body.setPadding(padding, padding, padding, padding);
        body.setFocusable(true);
        body.setFocusableInTouchMode(true);
        body.setClickable(true);
        body.requestFocus();
    }

    DialogBuilder setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    DialogBuilder addCustomView(@NonNull View view) {
        body.addView(view);
        return this;
    }

    DialogBuilder setNegativeButton(@Nullable String name, @Nullable DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(name, listener);
        return this;
    }

    DialogBuilder setPositiveButton(@Nullable String name, @Nullable DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(name, listener);
        return this;
    }

    AlertDialog build() {
        if (body.getChildCount() > 0) {
            builder.setView(body);
        }
        final AlertDialog builderDialog = builder.create();
        builderDialog.setOnDismissListener(dialog -> {
            if (body != null) {
                body.requestFocus();
            }
        });
        return builderDialog;
    }
}

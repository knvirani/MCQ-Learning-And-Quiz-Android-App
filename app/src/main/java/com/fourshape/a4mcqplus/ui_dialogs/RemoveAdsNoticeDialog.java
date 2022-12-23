package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;

import com.fourshape.a4mcqplus.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RemoveAdsNoticeDialog {

    private final MaterialAlertDialogBuilder dialogBuilder;

    public RemoveAdsNoticeDialog (Context context) {

        dialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Upcoming feature");

        String message = "You will be able to use full ads-free version shortly at a price of just Rs 30 for 30 days. In addition, more features will also be provided.";

        dialogBuilder.setIcon(R.drawable.ic_new_features);
        dialogBuilder.setMessage(message);

        dialogBuilder.create();
        dialogBuilder.show();

    }

}

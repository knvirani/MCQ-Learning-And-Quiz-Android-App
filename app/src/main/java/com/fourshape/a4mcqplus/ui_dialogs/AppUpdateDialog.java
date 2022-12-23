package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AppUpdateDialog {

    private final MaterialAlertDialogBuilder dialogBuilder;

    public AppUpdateDialog (Context context, String message) {
        dialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Update This App");

        if (message == null) {
            dialogBuilder.setMessage("The new version of this app is just released. Update it from Google Play.");
        } else {
            dialogBuilder.setMessage(FormattedData.formattedContent(message));
        }

        dialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                openStoreUrl(dialogBuilder.getContext());
            }
        }).setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        dialogBuilder.create();
    }

    public void show () {
        dialogBuilder.show();
    }

    private void openStoreUrl(Context context){
        try {
            String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL + context.getPackageName()));
            context.startActivity(browserIntent);
        } catch (Exception e){
            MakeLog.exception(e);
        }
    }


}

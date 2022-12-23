package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;
import android.content.DialogInterface;

import com.fourshape.a4mcqplus.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class StoreAccessPermissionDialog {

    private final MaterialAlertDialogBuilder dialogBuilder;

    public StoreAccessPermissionDialog (Context context) {
        dialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Storage Access Permission");

        dialogBuilder.setMessage("Sharing of Test Result requires permission to access external storage of this device.");

        dialogBuilder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
    }

    public MaterialAlertDialogBuilder getDialogBuilder () {
        return dialogBuilder;
    }
}

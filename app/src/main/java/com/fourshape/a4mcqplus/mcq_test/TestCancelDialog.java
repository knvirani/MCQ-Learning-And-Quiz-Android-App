package com.fourshape.a4mcqplus.mcq_test;

import android.content.Context;
import android.content.DialogInterface;

import com.fourshape.a4mcqplus.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.ref.WeakReference;

public class TestCancelDialog {

    private final MaterialAlertDialogBuilder dialogBuilder;
    private TestCancelListener testCancelListener;

    public TestCancelDialog (Context context) {

        dialogBuilder = new MaterialAlertDialogBuilder(new WeakReference<>(context).get(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);

        dialogBuilder.setTitle(dialogBuilder.getContext().getString(R.string.cancel_test_dialog_title));
        dialogBuilder.setMessage(dialogBuilder.getContext().getString(R.string.cancel_test_dialog_message));

        dialogBuilder.setPositiveButton("Don't Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                testCancelListener.cancelled(false);
                dialogInterface.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                testCancelListener.cancelled(true);
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.create();

    }

    public void setTestCancelListener(TestCancelListener testCancelListener) {
        this.testCancelListener = testCancelListener;
    }

    public void show () {
        dialogBuilder.show();
    }


}

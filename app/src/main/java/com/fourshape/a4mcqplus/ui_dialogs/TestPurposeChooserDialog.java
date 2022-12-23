package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.AdmobInterstitialAd;
import com.fourshape.a4mcqplus.fragments.LiveTestFragment;
import com.fourshape.a4mcqplus.fragments.TestReadFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.lang.ref.WeakReference;

public class TestPurposeChooserDialog {

    private final String TAG = "TestPurposeChooserDialog";

    private MaterialAlertDialogBuilder dialogBuilder;
    private View dialogView;
    private RadioGroup themeRadioGroup;

    private boolean isLiveTestChosen = true;

    public TestPurposeChooserDialog (Context context, FragmentActivity activity, int fragmentContainerId, String itemTitle, String itemCode, String testAction, int testLimit, int testOffset) {

        dialogBuilder = new MaterialAlertDialogBuilder(new WeakReference<>(context).get(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Choose Purpose");
        dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(R.layout.mtrl_dialog_purpose_selector, null);
        themeRadioGroup = dialogView.findViewById(R.id.purpose_group);

        MaterialCardView liveTestCV, readMCQCV;
        MaterialRadioButton liveTestRB, readMCQRB;

        liveTestCV = dialogView.findViewById(R.id.live_test_card_view);
        readMCQCV = dialogView.findViewById(R.id.just_read_card_view);
        liveTestRB = dialogView.findViewById(R.id.live_test);
        readMCQRB = dialogView.findViewById(R.id.just_read);

        liveTestCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.live_test);
            }
        });

        readMCQCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.just_read);
            }
        });

        liveTestRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.live_test);
            }
        });

        readMCQRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.just_read);
            }
        });

        themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.live_test) {
                    isLiveTestChosen = true;
                } else if (i == R.id.just_read){
                    isLiveTestChosen = false;
                }
            }
        });

        dialogBuilder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                if (themeRadioGroup.getCheckedRadioButtonId() == R.id.just_read) {
                    if (activity != null) {

                        if (AdmobInterstitialAd.shouldShowAd()) {
                            AdmobInterstitialAd.showAdBeforeNewFragmentLaunch(activity, fragmentContainerId, false, itemTitle, itemCode, testAction, testLimit, testOffset);
                        } else {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_read_fragment").replace(fragmentContainerId, TestReadFragment.newInstance(itemTitle, itemCode, testAction, testLimit, testOffset)).commit();
                        }

                    }
                } else if (themeRadioGroup.getCheckedRadioButtonId() == R.id.live_test) {
                    if (activity != null) {

                        if (AdmobInterstitialAd.shouldShowAd()) {
                            AdmobInterstitialAd.showAdBeforeNewFragmentLaunch(activity, fragmentContainerId, true, itemTitle, itemCode, testAction, testLimit, testOffset);
                        } else {
                            activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("live_test_fragment").replace(fragmentContainerId, LiveTestFragment.newInstance(itemTitle, itemCode, testAction, testLimit, testOffset)).commit();
                        }


                    }
                }

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);

    }

    public void show () {
        if (dialogBuilder != null)
            dialogBuilder.show();
    }

}

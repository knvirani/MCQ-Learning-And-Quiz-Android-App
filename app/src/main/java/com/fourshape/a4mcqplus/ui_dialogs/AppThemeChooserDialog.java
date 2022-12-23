package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatDelegate;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.lang.ref.WeakReference;

public class AppThemeChooserDialog {

    private final String TAG = "AppThemeChooserDialog";

    public static final String DARK_THEME = "dark_theme";
    public static final String LIGHT_THEME = "light_theme";

    private boolean isDarkThemeSelected = false;

    private MaterialAlertDialogBuilder dialogBuilder;
    private View dialogView;
    private RadioGroup themeRadioGroup;

    public static boolean isAppThemeChanged = false;

    public AppThemeChooserDialog (Context context) {

        dialogBuilder = new MaterialAlertDialogBuilder(new WeakReference<>(context).get(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Choose Theme");
        dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(R.layout.mtrl_dialog_theme_selector, null);
        themeRadioGroup = dialogView.findViewById(R.id.theme_group);

        MaterialCardView darkThemeCardView, lightThemeCardView;

        darkThemeCardView = dialogView.findViewById(R.id.dark_theme_card_view);
        lightThemeCardView = dialogView.findViewById(R.id.light_theme_card_view);

        MaterialRadioButton darkThemeRB, lightThemeRB;
        darkThemeRB = dialogView.findViewById(R.id.dark_theme);
        lightThemeRB = dialogView.findViewById(R.id.light_theme);

        darkThemeRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.dark_theme);
            }
        });

        lightThemeRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.light_theme);
            }
        });

        darkThemeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.dark_theme);
            }
        });

        lightThemeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeRadioGroup.check(R.id.light_theme);
            }
        });

        if (new SharedData(dialogBuilder.getContext()).getTheme().equals(LIGHT_THEME)) {
            themeRadioGroup.check(R.id.light_theme);
            isDarkThemeSelected = false;
        }
        else
        {
            themeRadioGroup.check(R.id.dark_theme);
            isDarkThemeSelected = true;
        }

    }

    public void show () {

        if (themeRadioGroup != null) {
            themeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.dark_theme) {
                        isDarkThemeSelected = true;
                    } else if (i == R.id.light_theme) {
                        isDarkThemeSelected = false;
                    }
                }
            });
        }

        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isAppThemeChanged = true;
                dialogInterface.dismiss();
                if (isDarkThemeSelected) {
                    new SharedData(dialogBuilder.getContext()).setTheme(DARK_THEME);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    new SharedData(dialogBuilder.getContext()).setTheme(LIGHT_THEME);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }).setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        dialogBuilder.create();

        dialogBuilder.show();

    }


}

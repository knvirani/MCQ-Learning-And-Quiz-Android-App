package com.fourshape.a4mcqplus.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.DimPopupWindow;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.OpenExternalUrl;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.material.button.MaterialButton;

public class AskForRatingsPopup {

    private static final String TAG = "AskForRatingsPopup";
    private Context context;
    private PopupWindow popupWindow;
    private View view;
    private boolean isReviewButtonPressed = false;

    public AskForRatingsPopup (Context context) {
        this.context = context;
        preparePopup();
    }

    private void preparePopup () {

        if (context == null) {
            MakeLog.error(TAG, "NULL Context");
            return;
        }

        view = LayoutInflater.from(context).inflate(R.layout.ask_for_review_popup, null);

        if (view == null) {
            MakeLog.error(TAG, "NULL View");
            return;
        }

        int width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
        int height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;

        popupWindow = new PopupWindow(view, width, height, focusable);

        MaterialButton playStoreMB = view.findViewById(R.id.review_ask_btn);

        playStoreMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenExternalUrl.open(view.getContext(), VariableControls.APP_STORE_URL);
                isReviewButtonPressed = true;

            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                new SharedData(context).registerAppRatingsDialogShownToday();

                if (isReviewButtonPressed) {
                    new SharedData(context).registerAppReviewPostDate();
                }

            }
        });


    }

    public void show () {
        if (popupWindow != null && view != null) {

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            DimPopupWindow.dimBehind(popupWindow);

        } else {
            MakeLog.error(TAG, "Either Party is NULL");
        }
    }


}

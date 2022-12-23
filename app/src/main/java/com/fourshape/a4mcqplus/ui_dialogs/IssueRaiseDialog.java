package com.fourshape.a4mcqplus.ui_dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class IssueRaiseDialog {

    private static final String TAG = "IssueRaiseDialog";

    private final MaterialAlertDialogBuilder dialogBuilder;
    private final View dialogView;

    private LinearLayoutCompat mainContainerLayout, resultView;
    private CircularProgressIndicator progressIndicator;
    private ImageView statusImageView;
    private TextView statusTextView;

    public IssueRaiseDialog (String mcqId, Context context) {

        dialogBuilder = new MaterialAlertDialogBuilder(context, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog);
        dialogBuilder.setTitle("Raise issue");
        dialogView = LayoutInflater.from(context).inflate(R.layout.mtrl_dialog_raise_issue, null);
        dialogBuilder.setView(dialogView);

        //TrackScreen.now(dialogView.getContext(), "IssueRaiseDialog");

        TextInputEditText emailAddressTextInput, issueDescriptionTextInput;
        emailAddressTextInput = dialogView.findViewById(R.id.email_address);
        issueDescriptionTextInput = dialogView.findViewById(R.id.issue);

        statusImageView = dialogView.findViewById(R.id.sent_result_image);
        statusTextView = dialogView.findViewById(R.id.issue_raise_status);
        mainContainerLayout = dialogView.findViewById(R.id.main_content_layout);
        progressIndicator = dialogView.findViewById(R.id.progress_circular);
        resultView = dialogView.findViewById(R.id.status_container);
        MaterialCardView submitIssueCardView = dialogView.findViewById(R.id.submit_issue_card_view);

        submitIssueCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailAddressTextInput.getText() != null && issueDescriptionTextInput.getText() != null) {

                    if (emailAddressTextInput.getText().toString().length() > 0 && issueDescriptionTextInput.getText().toString().length() > 0) {
                        submitAnIssue(mcqId, emailAddressTextInput.getText().toString(), issueDescriptionTextInput.getText().toString());
                    } else {
                        Toast.makeText(view.getContext(), "Invalid inputs", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(view.getContext(), "Invalid inputs", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialogBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.create();
        dialogBuilder.show();

    }

    private void submitAnIssue (String mcqId, String emailAddress, String issueDescription) {

        if (mainContainerLayout != null)
            if (mainContainerLayout.getVisibility() != View.GONE)
                mainContainerLayout.setVisibility(View.GONE);

        if (progressIndicator != null)
            if (progressIndicator.getVisibility() != View.VISIBLE)
                progressIndicator.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<>();
        params.put(ContentAccessParams.ACTION_TYPE, ContentAccessParams.REPORT_MCQ);
        params.put(ContentAccessParams.MCQ_ID, mcqId);
        params.put(ContentAccessParams.EMAIL_ADDRESS, emailAddress);
        params.put(ContentAccessParams.REMARKS, issueDescription);
        MakeLog.info(TAG, params.toString());

        LoadData loadData = new LoadData(mainContainerLayout.getContext(), new ContentAccessParams().getServerUrl(false), params);
        loadData.setShouldLoadNativeAd(false);
        loadData.setLoadDataListener(new DataLoadListener() {
            @Override
            public void onSuccess(String response, NativeAd nativeAd) {

                if (progressIndicator != null) {
                    if (progressIndicator.getVisibility() != View.GONE)
                        progressIndicator.setVisibility(View.GONE);
                }

                if (resultView != null) {
                    if (resultView.getVisibility() != View.VISIBLE)
                        resultView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(String error) {

                MakeLog.error(TAG, error);

                if (progressIndicator != null) {
                    if (progressIndicator.getVisibility() != View.GONE)
                        progressIndicator.setVisibility(View.GONE);
                }

                if (resultView != null) {
                    if (resultView.getVisibility() != View.VISIBLE)
                        resultView.setVisibility(View.VISIBLE);
                }

                if (statusImageView != null)
                    statusImageView.setImageTintList(ColorStateList.valueOf(dialogView.getContext().getColor(R.color.incorrect_answer)));

                if (statusTextView != null)
                    statusTextView.setText("Encountered error while submitting issue. Try again later.");

            }


        });
        loadData.load();



    }

}

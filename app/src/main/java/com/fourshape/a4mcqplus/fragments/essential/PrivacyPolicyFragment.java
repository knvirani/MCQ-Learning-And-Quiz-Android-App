package com.fourshape.a4mcqplus.fragments.essential;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fourshape.a4mcqplus.MainActivity;
import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.fourshape.a4mcqplus.utils.TestDevices;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import org.jetbrains.annotations.NotNull;


public class PrivacyPolicyFragment extends Fragment {

    View mainView;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
    LinearLayout gdprConsentStatusView;
    private final String TAG = "PrivacyPolicyFragment";
    boolean isPolicyAccepted = false;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance(String param1, String param2) {
        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        Button acceptBtn = mainView.findViewById(R.id.accept_btn);
        MaterialCardView readPolicyCardView = mainView.findViewById(R.id.mtrl_card_view);
        gdprConsentStatusView = mainView.findViewById(R.id.gdpr_status_view);

        SharedData sharedData = new SharedData(getContext());

        if (sharedData.isPolicyAccepted() ) {
            acceptBtn.setText("Reject Privacy Policy");
            isPolicyAccepted = true;
        }

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!sharedData.isPolicyAccepted()){

                    acceptBtn.setVisibility(View.GONE);
                    sharedData.acceptPolicy();

                    gdprConsentStatusView.setVisibility(View.VISIBLE);
                    acceptBtn.setText("Policy Accepted");

                    askForGDPRConsent();

                } else {

                    if (getContext() != null) {
                        consentInformation = UserMessagingPlatform.getConsentInformation(getContext());
                        consentInformation.reset();

                        sharedData.rejectPolicy();
                        acceptBtn.setText("Accept Policy");
                    }

                }

            }
        });

        readPolicyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String policyUrl = VariableControls.PRIVACY_POLICY_URL;

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browserIntent);
                    } catch (Exception e){
                        MakeLog.exception(e);
                        showSnackbar("Unable to open privacy policy link.");
                    }
                } catch (Exception e){
                    MakeLog.exception(e);
                    showSnackbar("Unable to open privacy policy link.");
                }
            }
        });

        return mainView;

    }

    private void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void resetConsentInformation () {
        if (consentInformation == null) {
            consentInformation = UserMessagingPlatform.getConsentInformation(getContext());
            consentInformation.reset();
        }
    }

    private void askForGDPRConsent(){

        if (getContext() == null)
            return;

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(getContext())
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(TestDevices.ADMOB_TEST_DEVICE)
                .build();


        // Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                //.setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        if (getContext() == null)
            return;

        if (consentInformation == null)
            consentInformation = UserMessagingPlatform.getConsentInformation(getContext());


        if (getActivity() == null)
            return;

        consentInformation.requestConsentInfoUpdate(getActivity(), params, new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
            @Override
            public void onConsentInfoUpdateSuccess() {

                if (consentInformation.isConsentFormAvailable()) {
                    // load form here.
                    MakeLog.info(TAG, "Consent Form is Available");
                    loadForm();
                } else {
                    MakeLog.info(TAG, "Consent Form is not Available");
                    gdprConsentStatusView.setVisibility(View.INVISIBLE);
                    try {
                        if (getActivity() != null)
                            getActivity().recreate();
                    } catch (Exception e){
                        MakeLog.exception(e);
                    }
                }

            }
        }, new ConsentInformation.OnConsentInfoUpdateFailureListener() {
            @Override
            public void onConsentInfoUpdateFailure(@NonNull @NotNull FormError formError) {

                MakeLog.info(TAG, "Consent form error: " + formError.getMessage());
                MakeLog.info(TAG, "Consent form error code: " + formError.getErrorCode());

                try {
                    if (getActivity() != null)
                        getActivity().recreate();
                } catch (Exception e){
                    MakeLog.exception(e);
                }
            }
        });

    }

    public void loadForm() {

        if (getContext() == null)
            return;

        UserMessagingPlatform.loadConsentForm(getContext(), new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
            @Override
            public void onConsentFormLoadSuccess(@NonNull @NotNull ConsentForm consentForm) {

                PrivacyPolicyFragment.this.consentForm = consentForm;

                if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {

                    if (getActivity() == null)
                        return;

                    consentForm.show(getActivity(), new ConsentForm.OnConsentFormDismissedListener() {
                        @Override
                        public void onConsentFormDismissed(@Nullable @org.jetbrains.annotations.Nullable FormError formError) {
                            // handle the dismissal by re-loading the form.
                            loadForm();
                        }
                    });
                } else if (consentInformation.getConsentStatus() ==  ConsentInformation.ConsentStatus.OBTAINED || consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED){
                    gdprConsentStatusView.setVisibility(View.INVISIBLE);
                    try {
                        if (getActivity() != null)
                            getActivity().recreate();
                    } catch (Exception e){
                        MakeLog.exception(e);
                    }
                } else {
                    // do nothing with unknown status.
                }

            }
        }, new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
            @Override
            public void onConsentFormLoadFailure(@NonNull @NotNull FormError formError) {
                gdprConsentStatusView.setVisibility(View.INVISIBLE);
                MakeLog.info(TAG, formError.getMessage());
                try {
                    if (getActivity() != null)
                        getActivity().recreate();
                } catch (Exception e){
                    MakeLog.exception(e);
                }
            }
        });

    }


}
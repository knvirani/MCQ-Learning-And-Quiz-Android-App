package com.fourshape.a4mcqplus.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.AdController;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.essential.PrivacyPolicyFragment;
import com.fourshape.a4mcqplus.fragments.viewmodels.WelcomeFragmentViewModel;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.ui_dialogs.AppUpdateDialog;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.DataFetchErrorView;
import com.fourshape.a4mcqplus.utils.DataFetchErrorViewOnClickListener;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.fourshape.a4mcqplus.utils.TestDevices;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class WelcomeFragment extends Fragment {

    private static final String TAG = "WelcomeFragment";

    private View mainView;
    private WelcomeFragmentViewModel viewModel;
    private LinearLayoutCompat contentLayout;
    private MaterialButton proceedBtn;
    private CircularProgressIndicator progressIndicator;
    private DataFetchErrorView dataFetchErrorView;

    private ConsentInformation consentInformation;
    private ConsentForm userConsentForm;
    LinearLayout gdprConsentStatusView;

    private MaterialCheckBox policyAgreeMtrlCheckBox;
    private TextView readPolicyTextView;

    private MaterialCardView updateAppCardView;
    private TextView updateAppMessageTextView;
    private MaterialButton updateNowMaterialBtn;

    private int fragmentContainerId;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(WelcomeFragmentViewModel.class);
        }

        if (getContext() != null) {
            ActionBarTitle.TITLE = getContext().getString(R.string.app_name);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "WelcomeFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_welcome, container, false);

        fragmentContainerId = container.getId();

        contentLayout = mainView.findViewById(R.id.content_layout);
        contentLayout.setVisibility(View.GONE);
        proceedBtn = mainView.findViewById(R.id.proceed_btn);
        progressIndicator = mainView.findViewById(R.id.progress_circular);

        updateAppCardView = mainView.findViewById(R.id.update_mtrl_card_view);
        updateNowMaterialBtn = mainView.findViewById(R.id.update_btn);
        updateAppMessageTextView = mainView.findViewById(R.id.update_message);

        updateAppCardView.setVisibility(View.GONE);

        policyAgreeMtrlCheckBox = mainView.findViewById(R.id.policy_agreement_mtrl_checkbox);
        readPolicyTextView = mainView.findViewById(R.id.read_policy_text);
        gdprConsentStatusView = mainView.findViewById(R.id.gdpr_status_view);

        gdprConsentStatusView.setVisibility(View.GONE);

        SharedData sharedData = new SharedData(getContext());

        if (sharedData.isPolicyAccepted()) {
            policyAgreeMtrlCheckBox.setVisibility(View.GONE);
            readPolicyTextView.setVisibility(View.GONE);
            gdprConsentStatusView.setVisibility(View.GONE);
        }

        dataFetchErrorView = new DataFetchErrorView(mainView.findViewById(R.id.err_mtrl_card_view), mainView);

        dataFetchErrorView.setDataFetchErrorViewOnClickListener(new DataFetchErrorViewOnClickListener() {
            @Override
            public void onClick(boolean isHidden) {
                if (!isHidden) {
                    triggerProgressIndicator(false);
                } else {
                    checkAppUtils();
                }
            }
        });

        readPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("privacy_policy_fragment").replace(fragmentContainerId, new PrivacyPolicyFragment()).commit();
                }
            }
        });

        policyAgreeMtrlCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            policyAgreeMtrlCheckBox.setVisibility(View.GONE);
                            readPolicyTextView.setVisibility(View.GONE);
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                    sharedData.acceptPolicy();
                    askForGDPRConsent();
                }
            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(container.getId(), new EntryFragment()).commit();
                        }

                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

            }
        });


        if (viewModel != null) {
            if (viewModel.isAppUtilChecked()) {

                contentLayout.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.GONE);

                if (sharedData.isPolicyAccepted()) {
                    policyAgreeMtrlCheckBox.setVisibility(View.GONE);
                    readPolicyTextView.setVisibility(View.GONE);
                    gdprConsentStatusView.setVisibility(View.GONE);
                    proceedBtn.setEnabled(true);
                }

                if (viewModel.isAppUpdateRequired()) {
                    updateAppCardView.setVisibility(View.VISIBLE);
                    updateAppCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openGooglePlayForAppUpdate();
                        }
                    });
                }
                if (viewModel.getAppUpdateMessage() != null) {
                    updateAppMessageTextView.setText(viewModel.getAppUpdateMessage());
                }
                updateNowMaterialBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGooglePlayForAppUpdate();
                    }
                });

            } else {
                executeServerSide();
            }
        } else {
            executeServerSide();
        }


        return mainView;
    }

    private void executeServerSide () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppUtils();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void openGooglePlayForAppUpdate () {
        openGooglePlay("https://play.google.com/store/apps/details?id=com.fourshape.a4mcqplus");
    }

    private void openGooglePlay (String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);
        } catch (Exception e){
            MakeLog.exception(e);
            Toast.makeText(getContext(), "Can't open.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAppUtils () {

        triggerProgressIndicator(true);

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ContentAccessParams.ACTION_TYPE, ContentAccessParams.ACCESS_APP_UTILS);

        MakeLog.info(TAG, "Url: " + new ContentAccessParams().getServerUrl(false));

        if (getContext() != null) {
            LoadData loadData = new LoadData(getContext(), new ContentAccessParams().getServerUrl(false), requestParams);
            loadData.setShouldLoadNativeAd(false);
            loadData.setLoadDataListener(new DataLoadListener() {
                @Override
                public void onSuccess(String response, NativeAd nativeAd) {
                    handleData(response, nativeAd);
                }

                @Override
                public void onError(String error) {
                    MakeLog.error(TAG, error);
                    dataFetchErrorView.toggleErrorBar();
                }
            });
            loadData.load();
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private void handleData (String response, NativeAd nativeAd) {
        viewModel.setAppUtilChecked(true);

        JsonResponseDecoder jsonResponseDecoder = new JsonResponseDecoder(response);
        try {
            if (getContext() != null) {
                int versionCode = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
                int latestAppVersion = 0;
                int minAppVersion = 0;
                if (jsonResponseDecoder.hasValidData()) {

                    if (jsonResponseDecoder.getLatestAppVersionRequired() != null)
                        latestAppVersion = Integer.parseInt(jsonResponseDecoder.getLatestAppVersionRequired());
                    if (jsonResponseDecoder.getMinimumAppVersionRequired() != null)
                        minAppVersion = Integer.parseInt(jsonResponseDecoder.getMinimumAppVersionRequired());

                    //AdController.isAdAllowed = jsonResponseDecoder.getInterstitialAdStatus();
                    //AdController.interstitialAdInterval = jsonResponseDecoder.getInterstitialAdInterval();

                    AdController.isExitReadingInterstitialAdAllowed = jsonResponseDecoder.getExitReadingInterstitialAdStatus();
                    AdController.isExitTestInterstitialAdAllowed = jsonResponseDecoder.getExitTestInterstitialAdStatus();

                    String message = "";
                    boolean isUpdateNotified = false;

                    if (versionCode < minAppVersion) {

                        String updateRemarks = jsonResponseDecoder.getAppUpdateRemarks();

                        if (updateRemarks != null) {
                            if (updateRemarks.trim().length() != 0) {
                                message = updateRemarks;
                            } else {
                                message = getContext().getString(R.string.alert_update_message);
                            }
                        } else {
                            message = getContext().getString(R.string.alert_update_message);
                        }

                        isUpdateNotified = true;
                    } else if (versionCode < latestAppVersion) {
                        message = "New version of this app is just released with new features. It's recommended to update it.";
                        isUpdateNotified = true;
                    }

                    if (isUpdateNotified) {

                        if (updateAppCardView != null) {
                            if (updateAppCardView.getVisibility() != View.VISIBLE) {
                                updateAppCardView.setVisibility(View.VISIBLE);
                            }

                            updateAppCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openGooglePlayForAppUpdate();
                                }
                            });

                            if (updateAppMessageTextView != null) {
                                updateAppMessageTextView.setText(FormattedData.formattedContent(message));
                            }
                            if (updateNowMaterialBtn != null) {
                                updateNowMaterialBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openGooglePlayForAppUpdate();
                                    }
                                });
                            }
                            if (viewModel != null) {
                                viewModel.setAppUpdateRequired(true);
                                viewModel.setAppUpdateMessage(message);
                            }
                        }

                    } else {
                        if (updateAppCardView != null) {
                            if (updateAppCardView.getVisibility() == View.VISIBLE)
                                updateAppCardView.setVisibility(View.GONE);
                        }
                    }

                }
            }
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        showContentLayout();

    }

    private void showContentLayout () {

        contentLayout.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.GONE);

        SharedData sharedData = new SharedData(getContext());

        if (sharedData.isPolicyAccepted()) {
            readPolicyTextView.setVisibility(View.GONE);
            gdprConsentStatusView.setVisibility(View.GONE);
            policyAgreeMtrlCheckBox.setVisibility(View.GONE);
            proceedBtn.setEnabled(true);
        } else {
            gdprConsentStatusView.setVisibility(View.GONE);
            proceedBtn.setEnabled(false);
        }

    }

    private void askForGDPRConsent(){

        if (getContext() == null)
            return;

        gdprConsentStatusView.setVisibility(View.VISIBLE);

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
                    consentDone();
                }

            }
        }, new ConsentInformation.OnConsentInfoUpdateFailureListener() {
            @Override
            public void onConsentInfoUpdateFailure(@NonNull @NotNull FormError formError) {

                MakeLog.info(TAG, "Consent form error: " + formError.getMessage());
                MakeLog.info(TAG, "Consent form error code: " + formError.getErrorCode());
                consentDone();
            }
        });

    }

    public void loadForm() {

        if (getContext() == null)
            return;

        UserMessagingPlatform.loadConsentForm(getContext(), new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
            @Override
            public void onConsentFormLoadSuccess(@NonNull @NotNull ConsentForm consentForm) {

                userConsentForm = consentForm;

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
                    consentDone();
                } else {
                    consentDone();
                }

            }
        }, new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
            @Override
            public void onConsentFormLoadFailure(@NonNull @NotNull FormError formError) {
                gdprConsentStatusView.setVisibility(View.INVISIBLE);
                MakeLog.info(TAG, formError.getMessage());
                consentDone();
            }
        });

    }

    private void consentDone () {
        gdprConsentStatusView.setVisibility(View.GONE);
        proceedBtn.setEnabled(true);
    }


    private void triggerProgressIndicator (boolean isDataFetching) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(isDataFetching ? View.VISIBLE : View.GONE);
        }
    }

}
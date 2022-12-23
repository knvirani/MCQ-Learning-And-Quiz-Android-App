package com.fourshape.a4mcqplus.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.interstitial_ad.ExitTestInterstitialAd;
import com.fourshape.a4mcqplus.app_db.DBHandler;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.mcq_test.McqStatus;
import com.fourshape.a4mcqplus.mcq_test.TestCancelDialog;
import com.fourshape.a4mcqplus.mcq_test.TestCancelListener;
import com.fourshape.a4mcqplus.mcq_test.TestClock;
import com.fourshape.a4mcqplus.mcq_test.TestClockListener;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.AdmobNativeAdCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.TestReadCardView;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.ui_dialogs.StoreAccessPermissionDialog;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.BitmapFromView;
import com.fourshape.a4mcqplus.utils.BundleParams;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.DataFetchErrorView;
import com.fourshape.a4mcqplus.utils.DataFetchErrorViewOnClickListener;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.fourshape.a4mcqplus.utils.ShareTestResult;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.fourshape.a4mcqplus.utils.StorageAccessPermission;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LiveTestFragment extends Fragment {

    private static final String TAG = "LiveTestFragment";

    private View mainView;
    private CircularProgressIndicator progressIndicator;
    private LinearLayoutCompat containerLayout, questionContainer, optionsContainer, topRow, rulesWithBtnContainer, radioOptionsContainer, rulesContainer, nativeAdContainer;
    private MaterialCardView cancelCardView, skipCardView;
    private TextView testTimeTextView, testTotalFilledMcqTextView;
    private RadioGroup optionRadioGroupView;
    private Button startTestBtn, goBackBtn, checkResultBtn;
    private DataFetchErrorView dataFetchErrorView;

    private MaterialCardView option1CV, option2CV, option3CV, option4CV, option5CV;
    private MaterialRadioButton option1RB, option2RB, option3RB, option4RB, option5RB;

    private int option1Id, option2Id, option3Id, option4Id, option5Id;

    private String testTitle, testCode, testAction;
    private int testLimit, testOffset;
    
    private LiveTestCatalog[] liveTestCatalog;
    private TestClock testClock;
    private int testTime = 0;
    
    private int currentMcqIndex  = -1;
    private int totalFilledMcq = 0;

    private TestCancelDialog testCancelDialog;
    private boolean isTestCompleted;

    private int fragmentContainerId = 0;
    private boolean shouldGoDirectlyBack = false;

    private View testResultView;

    private ActivityResultLauncher<String> activityResultLauncher;

    private int questionStartTime;

    public LiveTestFragment() {
        // Required empty public constructor
    }

    public static LiveTestFragment newInstance(String testTitle, String testCode, String testAction, int testLimit, int testOffset) {
        LiveTestFragment fragment = new LiveTestFragment();
        Bundle args = new Bundle();
        args.putString(BundleParams.ITEM_TITLE, testTitle);
        args.putString(BundleParams.ITEM_CODE, testCode);
        args.putString(BundleParams.ITEM_ACTION, testAction);
        args.putInt(BundleParams.TEST_QUESTION_LIMIT, testLimit);
        args.putInt(BundleParams.TEST_QUESTION_OFFSET, testOffset);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if (result) {
                            shareImageAfterStoragePermissionGranted();
                        } else {
                            Toast.makeText(getContext(), "Can't Share Test Result", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        if (getArguments() != null) {
            testTitle = getArguments().getString(BundleParams.ITEM_TITLE, null);
            testCode = getArguments().getString(BundleParams.ITEM_CODE, null);
            testAction = getArguments().getString(BundleParams.ITEM_ACTION, null);
            testLimit = getArguments().getInt(BundleParams.TEST_QUESTION_LIMIT, 0);
            testOffset = getArguments().getInt(BundleParams.TEST_QUESTION_OFFSET, 0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "LiveTestFragment");
        if (shouldGoDirectlyBack) {
            if (goBackBtn != null) {
                if (goBackBtn.getVisibility() != View.VISIBLE)
                    goBackBtn.setVisibility(View.VISIBLE);
            }

            if (rulesContainer != null) {
                rulesContainer.removeAllViews();
                rulesContainer.setVisibility(View.GONE);
            }

            if (rulesWithBtnContainer != null) {
                rulesWithBtnContainer.setVisibility(View.GONE);
            }

            if (startTestBtn != null)
                startTestBtn.setVisibility(View.GONE);
        }
        ActionBarTitle.TITLE = testTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_live_test, container, false);
        fragmentContainerId = container.getId();

        progressIndicator = mainView.findViewById(R.id.progress_circular);
        containerLayout = mainView.findViewById(R.id.container_layout);
        questionContainer = mainView.findViewById(R.id.question_container);
        optionsContainer = mainView.findViewById(R.id.options_container);
        topRow = mainView.findViewById(R.id.top_row);
        rulesWithBtnContainer = mainView.findViewWithTag(R.id.rules_with_btn_container);
        rulesContainer = mainView.findViewById(R.id.rules_container);
        nativeAdContainer = mainView.findViewById(R.id.ads_container);
        radioOptionsContainer = mainView.findViewById(R.id.radio_options_container);

        cancelCardView = mainView.findViewById(R.id.cancel_test_card_view);
        skipCardView = mainView.findViewById(R.id.skip_test_card_view);
        testTimeTextView = mainView.findViewById(R.id.test_time);
        testTotalFilledMcqTextView = mainView.findViewById(R.id.test_total_filled_mcq);

        optionRadioGroupView = mainView.findViewById(R.id.options_radio_group);
        startTestBtn = mainView.findViewById(R.id.test_start_btn);
        goBackBtn = mainView.findViewById(R.id.go_back_btn);
        checkResultBtn = mainView.findViewById(R.id.check_test_result_btn);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        goBackBtn.setVisibility(View.GONE);
        checkResultBtn.setVisibility(View.GONE);

        option1Id = R.id.option_1_mtrl_radio_btn;
        option2Id = R.id.option_2_mtrl_radio_btn;
        option3Id = R.id.option_3_mtrl_radio_btn;
        option4Id = R.id.option_4_mtrl_radio_btn;
        option5Id = R.id.option_5_mtrl_radio_btn;

        option1CV = mainView.findViewById(R.id.option_1_mtrl_card_view);
        option2CV = mainView.findViewById(R.id.option_2_mtrl_card_view);
        option3CV = mainView.findViewById(R.id.option_3_mtrl_card_view);
        option4CV = mainView.findViewById(R.id.option_4_mtrl_card_view);
        option5CV = mainView.findViewById(R.id.option_5_mtrl_card_view);

        option1RB = mainView.findViewById(R.id.option_1_mtrl_radio_btn);
        option2RB = mainView.findViewById(R.id.option_2_mtrl_radio_btn);
        option3RB = mainView.findViewById(R.id.option_3_mtrl_radio_btn);
        option4RB = mainView.findViewById(R.id.option_4_mtrl_radio_btn);
        option5RB = mainView.findViewById(R.id.option_5_mtrl_radio_btn);

        triggerProgressIndicator(false);

        if (topRow != null) {
            if (topRow.getVisibility() == View.VISIBLE)
                topRow.setVisibility(View.GONE);
        }

        if (radioOptionsContainer != null) {
            if (radioOptionsContainer.getVisibility() == View.VISIBLE)
                radioOptionsContainer.setVisibility(View.GONE);
        }

        if (startTestBtn != null) {
            if (startTestBtn.getVisibility() == View.VISIBLE)
                startTestBtn.setVisibility(View.GONE);
        }

        dataFetchErrorView = new DataFetchErrorView(mainView.findViewById(R.id.err_mtrl_card_view), mainView);

        dataFetchErrorView.setDataFetchErrorViewOnClickListener(new DataFetchErrorViewOnClickListener() {
            @Override
            public void onClick(boolean isHidden) {
                if (!isHidden) {
                    triggerProgressIndicator(false);
                } else {
                    fetchData();
                }
            }
        });

        testClock = new TestClock();

        testCancelDialog = new TestCancelDialog(getContext());

        testCancelDialog.setTestCancelListener(new TestCancelListener() {
            @Override
            public void cancelled(boolean isCancelled) {
                if (isCancelled) {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            }
        });

        testClock.setClockTickListener(new TestClockListener() {
            @Override
            public void onTick(int time) {
                testTime = time;
                testTimeTextView.setText(FormattedData.getFormattedTime(time));
            }
        });

        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTestCompleted) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startLiveTest();
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                }
            }
        });

        checkResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTestCompleted) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            prepareResultAndShow();
                        }
                    },FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                }
            }
        });

        if (getContext() == null)
            return mainView;


        if (!isTestCompleted) {
            resizeRadioOptions();
            executeServerTask();
        }

        return mainView;

    }

    public int getQuestionStartTime() {
        return questionStartTime;
    }

    public void setQuestionStartTime(int questionStartTime) {
        this.questionStartTime = questionStartTime;
    }

    public void resetQuestionStartTime () {
        setQuestionStartTime(0);
    }

    private void shareImageAfterStoragePermissionGranted () {
        if (testResultView != null) {
            askForStorageOps(testResultView);
        }
    }

    private void askForStorageOps (View view) {

        if (getContext() == null)
            return;

        StorageAccessPermission storageAccessPermission = new StorageAccessPermission(getActivity());

        if (storageAccessPermission.isPermissionGranted()) {
            new ShareTestResult().shareNow(getContext(), BitmapFromView.getBitmapFromView(view));
        } else {

            StoreAccessPermissionDialog storeAccessPermissionDialog = new StoreAccessPermissionDialog(getContext());
            MaterialAlertDialogBuilder dialogBuilder = storeAccessPermissionDialog.getDialogBuilder();

            dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (activityResultLauncher != null) {
                        activityResultLauncher.launch(storageAccessPermission.getStorageWriteString());
                    }
                }
            });

            dialogBuilder.create();
            dialogBuilder.show();

        }

    }

    private void executeServerTask () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void fetchData () {

        triggerProgressIndicator(true);

        Map<String, String> params = new HashMap<>();

        params.put(ContentAccessParams.ACTION_TYPE, testAction);
        params.put(ContentAccessParams.QUERY_PARAM, testCode);
        params.put(ContentAccessParams.LIMIT, String.valueOf(testLimit));
        params.put(ContentAccessParams.OFFSET, String.valueOf(testOffset));

        MakeLog.info(TAG, params.toString());

        LoadData loadData = new LoadData(getContext(), new ContentAccessParams().getServerUrl(false), params);
        loadData.setLoadDataListener(new DataLoadListener() {
            @Override
            public void onSuccess(String response, NativeAd nativeAd) {
                buildTestCatalog(nativeAd, response);
            }

            @Override
            public void onError(String error) {
                MakeLog.error(TAG, error);
                //showSnackbar(mainView, getContext(), "Failed to load.");
                dataFetchErrorView.toggleErrorBar();
            }

        });
        loadData.load();
    }

    private void buildTestCatalog (NativeAd nativeAd, String response) {
        JsonResponseDecoder jsonResponseDecoder = new JsonResponseDecoder(response);

        if (jsonResponseDecoder.hasValidData()) {

            int dataLength = jsonResponseDecoder.getDataLength();
            int optionsLength = 4;

            liveTestCatalog = new LiveTestCatalog[dataLength];

            for (int index = 0; index < dataLength; index++) {

                String mcqIndex = null, mcqId = null, questionTitle = null, questionImageUrl = null, answerIndex = null;

                mcqIndex = String.valueOf(index+1);
                mcqId = jsonResponseDecoder.getMcqId(index);
                questionTitle = jsonResponseDecoder.getQuestionText(index);
                questionImageUrl = jsonResponseDecoder.getQuestionImageUrl(index);
                answerIndex = jsonResponseDecoder.getAnswerIndex(index);

                String[] optionTexts = new String[optionsLength],
                        optionImageUrls = new String[optionsLength],
                        optionIndexes = new String[optionsLength];

                for (int optionIndex = 0; optionIndex < optionsLength; optionIndex++) {
                    optionTexts[optionIndex] = jsonResponseDecoder.getOptionText(index, optionIndex);
                    optionImageUrls[optionIndex] = jsonResponseDecoder.getOptionImageUrl(index, optionIndex);
                    optionIndexes[optionIndex] = jsonResponseDecoder.getOptionTrueIndex(index, optionIndex);
                }

                liveTestCatalog[index] = new LiveTestCatalog();
                liveTestCatalog[index].setMcqId(mcqId);
                liveTestCatalog[index].setMcqQuestion(questionTitle, questionImageUrl);
                liveTestCatalog[index].setMcqIndex(mcqIndex);
                liveTestCatalog[index].setAnswerIndex(answerIndex);
                liveTestCatalog[index].setMcqOptions(optionTexts, optionImageUrls, optionIndexes);
                liveTestCatalog[index].setMcqStatus(McqStatus.UNATTENDED);
                liveTestCatalog[index].setTimeTaken(0);

            }

            if (getContext() == null) {
                return;
            }

            try {

                if (nativeAd != null) {
                    View adView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_admob_nativead_cardview, null);

                    if (adView != null) {
                        AdmobNativeAdCardView.attachNativeAd(nativeAd, adView);
                        nativeAdContainer.addView(adView);
                    }

                    View darkGreyDivider = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_divider_view_for_result_history, null);

                    if (darkGreyDivider != null)
                        ((ViewGroup)nativeAdContainer).addView(darkGreyDivider);
                }

                View rulesView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_exam_rules_view, null);
                rulesContainer.addView(rulesView);

                if (startTestBtn.getVisibility() != View.VISIBLE)
                    startTestBtn.setVisibility(View.VISIBLE);

                triggerProgressIndicator(false);

            } catch (Exception e) {
                MakeLog.exception(e);
            }

        } else {
            triggerProgressIndicator(true);
            if (dataFetchErrorView != null)
                dataFetchErrorView.toggleErrorBar();
        }
    }

    private void startLiveTest () {

        if (containerLayout != null) {
            if (containerLayout.getVisibility() != View.VISIBLE)
                containerLayout.setVisibility(View.VISIBLE);
        }

        if (rulesContainer != null) {
            rulesContainer.removeAllViews();
            rulesContainer.setVisibility(View.GONE);
        }

        if (nativeAdContainer != null) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
        }

        if (rulesWithBtnContainer != null) {
            rulesWithBtnContainer.setVisibility(View.GONE);
        }

        if (topRow != null) {
            if (topRow.getVisibility() != View.VISIBLE)
                topRow.setVisibility(View.VISIBLE);
        }

        if (radioOptionsContainer != null) {
            if (radioOptionsContainer.getVisibility() != View.VISIBLE)
                radioOptionsContainer.setVisibility(View.VISIBLE);
        }

        if (startTestBtn != null) {
            if (startTestBtn.getVisibility() == View.VISIBLE)
                startTestBtn.setVisibility(View.GONE);
        }

        if (testTotalFilledMcqTextView != null) {
            String text = "0/" + liveTestCatalog.length;
            testTotalFilledMcqTextView.setText(text);
        }

        startClock();
        attachDataIntoViews();
        loadNextMcq();

        if (getContext() != null)
            ExitTestInterstitialAd.requestNewAd(getContext(), VariableControls.shouldUseMediationForInterstitialAd);

    }

    private boolean shouldDisableSkipBtn () {

        int totalAnswered = 0;

        for (LiveTestCatalog testCatalog : liveTestCatalog) {
            if (testCatalog.getMcqStatus() == McqStatus.ANSWERED)
                totalAnswered++;
        }

        return totalAnswered == liveTestCatalog.length-1;

    }
    
    private void loadNextMcq () {

        resetQuestionStartTime();

        triggerProgressIndicator(true);
        resetOptionRadios();

        boolean shouldAttach = false;

        if (currentMcqIndex+1 < liveTestCatalog.length){
            currentMcqIndex++;
        } else {
            currentMcqIndex = 0;
        }

        for (int index = currentMcqIndex; index < liveTestCatalog.length; index++) {

            shouldAttach = (liveTestCatalog[index].getMcqStatus() != McqStatus.ANSWERED);

            if (shouldAttach) {
                currentMcqIndex = index;
                break;
            }

        }

        if (shouldAttach) {
            attachQuestion();
            attachOptions();
            setQuestionStartTime(testTime);
        } else {
            triggerTestComplete();
        }

        triggerProgressIndicator(false);

    }

    private void startClock () {
        if (testClock != null)
            testClock.startClock();
    }

    private void stopClock () {
        if (testClock != null)
            testClock.stopClock();
    }

    private void triggerTestComplete () {

        isTestCompleted = true;
        stopClock();

        if (topRow != null)
            if (topRow.getVisibility() == View.VISIBLE)
                topRow.setVisibility(View.GONE);

        if (questionContainer != null)
            if (questionContainer.getVisibility() == View.VISIBLE) {
                questionContainer.removeAllViews();
                questionContainer.setVisibility(View.GONE);
            }

        if (optionsContainer != null) {
            if (optionsContainer.getVisibility() == View.VISIBLE) {
                optionsContainer.removeAllViews();
                optionsContainer.setVisibility(View.GONE);
            }
        }

        if (topRow != null) {
            if (topRow.getVisibility() == View.VISIBLE)
                topRow.setVisibility(View.GONE);
        }

        if (radioOptionsContainer != null) {
            if (radioOptionsContainer.getVisibility() == View.VISIBLE)
                radioOptionsContainer.setVisibility(View.GONE);
        }

        if (startTestBtn != null) {
            if (startTestBtn.getVisibility() == View.VISIBLE)
                startTestBtn.setVisibility(View.GONE);
        }

        if (optionRadioGroupView != null) {
            if (optionRadioGroupView.getVisibility() == View.VISIBLE) {
                optionRadioGroupView.removeAllViews();
                optionRadioGroupView.setVisibility(View.GONE);
            }
        }

        if (checkResultBtn != null) {
            if (checkResultBtn.getVisibility() != View.VISIBLE) {
                checkResultBtn.setVisibility(View.VISIBLE);
            }
        }

    }
    
    private void cancelLiveTest () {
        testCancelDialog.show();
    }
    
    private void skipCurrentMcq () {
        liveTestCatalog[currentMcqIndex].setMcqStatus(McqStatus.SKIPPED_FOR_NOW);
        liveTestCatalog[currentMcqIndex].setTimeTaken(getTimeTakenPerQuestion() + liveTestCatalog[currentMcqIndex].getTimeTaken());
        loadNextMcq();
    }

    public int getTimeTakenPerQuestion () {
        return testTime - getQuestionStartTime();
    }
    
    private void setAnswerOnOptionSelection (int userAnswerIndex) {
        liveTestCatalog[currentMcqIndex].setUserAnswerIndex(String.valueOf(userAnswerIndex));
        liveTestCatalog[currentMcqIndex].setMcqStatus(McqStatus.ANSWERED);
        liveTestCatalog[currentMcqIndex].setTimeTaken(getTimeTakenPerQuestion() + liveTestCatalog[currentMcqIndex].getTimeTaken());

        totalFilledMcq++;
        if (testTotalFilledMcqTextView != null) {
            String text = String.valueOf(totalFilledMcq)+"/"+String.valueOf(liveTestCatalog.length);
            testTotalFilledMcqTextView.setText(text);
        }

        if (skipCardView != null) {
            skipCardView.setEnabled(!shouldDisableSkipBtn());
        }

        loadNextMcq();
    }
    
    private void attachDataIntoViews () {

        if (optionRadioGroupView != null) {

            optionRadioGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    if (i == option1Id) {
                        setAnswerOnOptionSelection(1);
                    } else if (i == option2Id) {
                        setAnswerOnOptionSelection(2);
                    } else if (i == option3Id) {
                        setAnswerOnOptionSelection(3);
                    } else if (i == option4Id) {
                        setAnswerOnOptionSelection(4);
                    } else if (i == option5Id) {
                        setAnswerOnOptionSelection(5);
                    }

                }
            });

            if (option1RB != null) {
                option1RB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option1Id);
                    }
                });
            }

            if (option1CV != null) {
                option1CV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option1Id);
                    }
                });
            }

            if (option2RB != null) {
                option2RB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option2Id);
                    }
                });
            }

            if (option2CV != null) {
                option2CV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option2Id);
                    }
                });
            }

            if (option3RB != null) {
                option3RB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option3Id);
                    }
                });
            }

            if (option3CV != null) {
                option3CV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option3Id);
                    }
                });
            }

            if (option4RB != null) {
                option4RB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option4Id);
                    }
                });
            }

            if (option4CV != null) {
                option4CV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option4Id);
                    }
                });
            }

            if (option5RB != null) {
                option5RB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option5Id);
                    }
                });
            }

            if (option5CV != null) {
                option5CV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionRadioGroupView.check(option5Id);
                    }
                });
            }

        }

        if (skipCardView != null) {
            skipCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    skipCurrentMcq();
                }
            });
        }

        if (cancelCardView != null) {
            cancelCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelLiveTest();
                }
            });
        }
        
    }
    
    private void resetOptionRadios () {
        if (optionRadioGroupView != null) {
            optionRadioGroupView.clearCheck();
        }
    }
    
    private void attachOptions () {
        
        if (optionsContainer != null) {

            optionsContainer.removeAllViews();
            int optionLength = 4;

            for (int index = 0; index < optionLength; index++) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_mcq_option_view, null);
                optionsContainer.addView(view);
                new TestReadCardView(
                        view,
                        liveTestCatalog[currentMcqIndex].getMcqId(),
                        getOptionIndex(index),
                        liveTestCatalog[currentMcqIndex].getOptionText(index),
                        liveTestCatalog[currentMcqIndex].getOptionImageUrl(index)
                ).attachCard();
            }
            
        }
        
    }
    
    private void attachQuestion () {

        if (questionContainer != null) {

            questionContainer.removeAllViews();

            View view = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_mcq_question_view, null);
            questionContainer.addView(view);
            
            new TestReadCardView(view, 
                    liveTestCatalog[currentMcqIndex].getMcqId(),
                    liveTestCatalog[currentMcqIndex].getMcqIndex(),
                    liveTestCatalog[currentMcqIndex].getMcqQuestionText(),
                    liveTestCatalog[currentMcqIndex].getMcqQuestionImageUrl()
                    ).attachCard();

        }
        
    }

    private void prepareResultAndShow () {

        if (getActivity() != null) {
            if (getActivity().isFinishing() || getActivity().isDestroyed()) {
                MakeLog.error(TAG, "Can't prepare result as activity is no more.");
                return;
            }
        } else {
            MakeLog.error(TAG, "Can't prepare result as activity is NULL.");
            return;
        }

        if (liveTestCatalog == null) {
            MakeLog.error(TAG, "LiveTestCatalog is NULL");
            return;
        }

        if (checkResultBtn != null) {
            if (checkResultBtn.getVisibility() == View.VISIBLE)
                checkResultBtn.setVisibility(View.GONE);
        }

        if (questionContainer != null) {

            questionContainer.removeAllViews();

            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_result_summary, null);

            LinearLayoutCompat testGuideLayout = itemView.findViewById(R.id.test_interpret_guide_container);
            MaterialCheckBox doNotShowGuideCheckBoxView = itemView.findViewById(R.id.do_not_show_again_mtrl_checkbox);
            final LinearLayoutCompat shareableView = itemView.findViewById(R.id.shareable_test_result);
            MaterialCardView seeMoreTestResultsCardView = itemView.findViewById(R.id.open_previous_results_mtrl_card);

            if (!new SharedData(getContext()).getDoNotShowAgainForExternalAppOpenDialog()) {
                testGuideLayout.setVisibility(View.GONE);
            }

            seeMoreTestResultsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                shouldGoDirectlyBack = true;
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("saved_result_fragment").replace(fragmentContainerId, new SavedResultsListFragment()).commit();
                            }
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);
                }
            });

            doNotShowGuideCheckBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        testGuideLayout.setVisibility(View.GONE);
                        new SharedData(getContext()).setDoNotShowAgainForExternalAppOpenDialog();
                    }
                }
            });

            TextView testTitleTextView, testSubTitleTextView, testNameTextView, testTimeTextView;
            MaterialRadioButton totalCorrectRB, totalIncorrectRB, totalOptionERB;

            testTitleTextView = itemView.findViewById(R.id.test_title);
            testSubTitleTextView = itemView.findViewById(R.id.test_sub_title);
            testNameTextView = itemView.findViewById(R.id.test_name);
            testTimeTextView = itemView.findViewById(R.id.test_time);
            totalCorrectRB = itemView.findViewById(R.id.total_correct);
            totalIncorrectRB = itemView.findViewById(R.id.total_incorrect);
            totalOptionERB = itemView.findViewById(R.id.total_option_e);

            testTitleTextView.setText(FormattedData.formattedContent(LiveTestCatalog.TEST_TITLE));
            testSubTitleTextView.setText(FormattedData.formattedContent(LiveTestCatalog.TEST_SUB_TITLE));
            testNameTextView.setText(FormattedData.formattedContent(LiveTestCatalog.TEST_NAME));

            int totalMcqs = liveTestCatalog.length;
            int totalCorrectAttempts = getTotalCorrectAttempts();
            int totalIncorrectAttempts = getTotalIncorrectAttempts();


            if (totalCorrectAttempts == -1 || totalIncorrectAttempts == -1) {

                if (getContext() != null) {
                    Toast.makeText(getContext(), "Unable to prepare result now. Press Back to Go Back.", Toast.LENGTH_SHORT).show();
                }

                return;
            }

            int totalOptionEAttempts = totalMcqs - totalCorrectAttempts - totalIncorrectAttempts;

            String correctAttemptsText = totalCorrectAttempts + "/" + totalMcqs;
            String incorrectAttemptsText = totalIncorrectAttempts + "/" + totalMcqs;
            String optionEAttemptsText = totalOptionEAttempts + "/" + totalMcqs;
            String timeTakenText = "Finished in: " + FormattedData.getFormattedTime(getTotalTimeTaken());

            testTimeTextView.setText(timeTakenText);
            totalCorrectRB.setText(correctAttemptsText);
            totalIncorrectRB.setText(incorrectAttemptsText);
            totalOptionERB.setText(optionEAttemptsText);

            MaterialCardView shareCardView, expandCardView;
            shareCardView = itemView.findViewById(R.id.share_result);
            expandCardView = itemView.findViewById(R.id.view_result_in_detail);

            testResultView = shareableView;

            DBHandler dbHandler = new DBHandler(getContext());
            dbHandler.addRecord(LiveTestCatalog.TEST_TITLE, LiveTestCatalog.TEST_SUB_TITLE, LiveTestCatalog.TEST_NAME, totalMcqs, testTime, totalCorrectAttempts, totalIncorrectAttempts, totalOptionEAttempts);
            dbHandler.close();

            shareCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askForStorageOps(testResultView);
                }
            });

            expandCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LiveTestCatalog.UNIVERSAL_LIVE_TEST_CATALOG = liveTestCatalog;

                    if (getActivity() != null) {

                        shouldGoDirectlyBack = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("test_result_fragment").replace(fragmentContainerId, new DetailedTestResultFragment()).commit();
                            }
                        }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

                    }

                }
            });

            addNativeAd();

            questionContainer.addView(itemView);

            if (questionContainer.getVisibility() != View.VISIBLE)
                questionContainer.setVisibility(View.VISIBLE);

        }

    }

    private void addNativeAd () {
        if (questionContainer != null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_admob_nativead_cardview, null);
            if (AdmobNativeAdCardView.shouldAttachAd()) {
                AdmobNativeAdCardView.attachNativeAd(AdmobNativeAdCardView.getNativeAdInstance(), view);
                questionContainer.addView(view);
                View dividerView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_divider_view, null);
                questionContainer.addView(dividerView);
            }
        }
    }

    private int getTotalTimeTaken () {
        int count = 0;
        for (int index = 0; index < liveTestCatalog.length; index++) {
            count += liveTestCatalog[index].getTimeTaken();
        }
        return count;
    }

    private int getTotalCorrectAttempts () {

        int count = 0;

        for (int index = 0; index < liveTestCatalog.length; index++) {

            try {
                if (liveTestCatalog[index].getAnswerIndex().equals(liveTestCatalog[index].getUserAnswerIndex()))
                    count++;
            } catch (Exception e) {
                MakeLog.exception(e);
                return -1;
            }

        }

        return count;
    }

    private int getTotalIncorrectAttempts () {
        int count = 0;

        if (liveTestCatalog != null) {

            for (int index = 0; index < liveTestCatalog.length; index++) {

                try {
                    if (!liveTestCatalog[index].getAnswerIndex().equals(liveTestCatalog[index].getUserAnswerIndex()) && !liveTestCatalog[index].getUserAnswerIndex().equals("5")) {
                        count++;
                    }
                } catch (Exception e) {
                    MakeLog.exception(e);
                    return -1;
                }

            }
        }

        return count;
    }

    private String getOptionIndex (int optionIndex) {
        switch (optionIndex) {
            case 0:
                return "A.";
            case 1:
                return "B.";
            case 2:
                return "C.";
            case 3:
                return "D.";
            default:
                return "A.";
        }
    }

    private void showSnackbar(View view, Context context, String message){

        triggerProgressIndicator(false);

        if (view != null) {
            try {
                Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                        fetchData();
                    }
                });
                snackbar.show();
            } catch (Exception e){
                MakeLog.exception(e);
            }
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

    }

    private void triggerProgressIndicator (boolean isDataFetching) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(isDataFetching ? View.VISIBLE : View.GONE);
        }
    }

    private void resizeRadioOptions () {

        if (getContext() == null)
            return;


        int totalSpace = (16*2) + (8*4); // in dp
        int screenWidth = ScreenParams.getDisplayWidthPixels(getContext());
        int totalBlocks = 5;

        int sizeForOneBlock =  (screenWidth - totalSpace) / totalBlocks;

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(sizeForOneBlock, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        option1CV.setLayoutParams(layoutParams);
        option2CV.setLayoutParams(layoutParams);
        option3CV.setLayoutParams(layoutParams);
        option4CV.setLayoutParams(layoutParams);
        option5CV.setLayoutParams(layoutParams);

    }



}
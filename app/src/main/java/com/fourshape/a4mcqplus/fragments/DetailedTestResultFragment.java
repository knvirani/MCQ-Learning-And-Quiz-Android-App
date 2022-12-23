package com.fourshape.a4mcqplus.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.PlacementIds;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.AdmobNativeAd;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.AdmobNativeAdListener;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.mcq_test.LiveTestCatalog;
import com.fourshape.a4mcqplus.rv_adapter.ContentAdapter;
import com.fourshape.a4mcqplus.rv_adapter.ContentModal;
import com.fourshape.a4mcqplus.rv_adapter.ContentType;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class DetailedTestResultFragment extends Fragment {
    
    private static final String TAG = "DetailedTestResultFragment";

    private View mainView;
    private LiveTestCatalog[] liveTestCatalog;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;

    private Button goBackBtn;
    private AdmobNativeAd admobNativeAd;

    public DetailedTestResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "DetailedTestResultFragment");

        if (ActionBarTitle.TITLE != null) {

            if (ActionBarTitle.TITLE.equals("Saved Results")) {

                if (goBackBtn != null) {
                    if (goBackBtn.getVisibility() != View.VISIBLE)
                        goBackBtn.setVisibility(View.VISIBLE);
                }

            } else {
                ActionBarTitle.TITLE += ": Expanded Result";
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_detailed_test_result, container, false);
        
        liveTestCatalog = LiveTestCatalog.UNIVERSAL_LIVE_TEST_CATALOG;
        LiveTestCatalog.UNIVERSAL_LIVE_TEST_CATALOG = null;
        
        recyclerView = mainView.findViewById(R.id.recycler_view);
        progressIndicator = mainView.findViewById(R.id.progress_circular);
        goBackBtn = mainView.findViewById(R.id.go_back_btn);

        contentModalArrayList = new ArrayList<>();

        if (getContext() == null)
            return mainView;

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        contentAdapter = new ContentAdapter(getActivity(), container.getId(), contentModalArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        admobNativeAd = new AdmobNativeAd(getContext());
        admobNativeAd.setPlacementId(PlacementIds.NATIVE_AD);
        admobNativeAd.setAdmobNativeAdListener(new AdmobNativeAdListener() {
            @Override
            public void onSuccess() {
                executeTask();
            }

            @Override
            public void onFailed() {
                executeTask();
            }
        });

        admobNativeAd.loadNativeAd();
        
        return mainView;
        
    }
    
    private void executeTask () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attachData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }
    
    private void attachData () {
        
        if (liveTestCatalog != null) {

            if (admobNativeAd != null) {
                if (admobNativeAd.isAdLoaded()) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.PRELOADED_NATIVE_AD, admobNativeAd.getNativeAd()));
                        contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    }
                }
            }

            attachResultSummary();
            
            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.DETAILED_RESULT_LABEL));
            }

            attachTestDetails();
            
        } else {
            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
            }
        }
        
        updateRecyclerView();
        
    }

    private void attachResultSummary () {

        String testTitle, testSubTitle, testName;
        int totalCorrect, totalIncorrect, totalOptionE, totalTimeTaken, totalMcqs;

        testTitle = LiveTestCatalog.TEST_TITLE;
        testSubTitle = LiveTestCatalog.TEST_SUB_TITLE;
        testName = LiveTestCatalog.TEST_NAME;

        totalMcqs = liveTestCatalog.length;
        totalCorrect = getTotalCorrectAttempts();
        totalIncorrect = getTotalIncorrectAttempts();
        totalOptionE = totalMcqs - totalCorrect - totalIncorrect;
        totalTimeTaken = getTotalTimeTaken();
        
        if (contentModalArrayList != null) {
            contentModalArrayList.add(new ContentModal(ContentType.TEST_RESULT_SUMMARY, testTitle, testSubTitle, testName, totalTimeTaken, totalMcqs, totalCorrect, totalIncorrect, totalOptionE));
        }

    }

    private boolean isEmpty (String text) {
        if (text == null)
            return true;
        else
            return text.trim().length() == 0;
    }
    
    private void attachTestDetails () {

        for (int index = 0; index < liveTestCatalog.length; index++) {

            final String itemId = liveTestCatalog[index].getMcqId();
            final String noIndex = liveTestCatalog[index].getMcqIndex();
            final String itemQuestion = liveTestCatalog[index].getMcqQuestionText();
            final String itemQuestionImageUrl = liveTestCatalog[index].getMcqQuestionImageUrl();
            final String mcqTrueAnswerIndex = liveTestCatalog[index].getAnswerIndex();
            final String userAnswerIndex = liveTestCatalog[index].getUserAnswerIndex();

            if (contentModalArrayList != null) {

                if (!isEmpty(itemQuestion) && !isEmpty(itemQuestionImageUrl)) {
                    contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_TEXT_AND_IMAGE, itemId, noIndex, itemQuestion, itemQuestionImageUrl));
                } else if (!isEmpty(itemQuestion)) {
                    contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_TEXT_ONLY, itemId, noIndex, itemQuestion, null));
                } else if (!isEmpty(itemQuestionImageUrl)) {
                    contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_IMAGE_ONLY, itemId, noIndex, null, itemQuestionImageUrl));
                }

            }
            
            int totalOptions = 4;

            for (int optionIndex = 0; optionIndex < totalOptions; optionIndex++) {

                final String mcqOptionIndex = liveTestCatalog[index].getOptionIndex(optionIndex);
                final String optionTitle = liveTestCatalog[index].getOptionText(optionIndex);
                final String optionImageUrl = liveTestCatalog[index].getOptionImageUrl(optionIndex);

                if (contentModalArrayList != null) {

                    if (!isEmpty(optionTitle) && !isEmpty(optionImageUrl)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE_DETAILED_RESULT_ONLY, itemId, mcqOptionIndex, optionTitle, optionImageUrl, null, null));
                    } else if (!isEmpty(optionTitle)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_TEXT_DETAILED_RESULT_ONLY, itemId, mcqOptionIndex, optionTitle, null, null, null));
                    } else if (!isEmpty(optionImageUrl)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_IMAGE_DETAILED_RESULT_ONLY, itemId, mcqOptionIndex, null, optionImageUrl, null, null));
                    }

                }

            }

            if (contentModalArrayList != null)
            {
                if (mcqTrueAnswerIndex != null && userAnswerIndex != null)
                    contentModalArrayList.add(new ContentModal(ContentType.TEST_RESULT_TIME_TAKEN, liveTestCatalog[index].getTimeTaken(), mcqTrueAnswerIndex, userAnswerIndex));

                contentModalArrayList.add(new ContentModal(ContentType.TEST_RESULT_ANSWERS, mcqTrueAnswerIndex, userAnswerIndex, itemId));
            }

            if (index-1 != liveTestCatalog.length) {
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.SPACE));
                }
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
            if (liveTestCatalog[index].getAnswerIndex().equals(liveTestCatalog[index].getUserAnswerIndex()))
                count++;
        }
        
        return count;
    }
    
    private int getTotalIncorrectAttempts () {
        int count = 0;

        for (int index = 0; index < liveTestCatalog.length; index++) {
            if (!liveTestCatalog[index].getAnswerIndex().equals(liveTestCatalog[index].getUserAnswerIndex()) && !liveTestCatalog[index].getUserAnswerIndex().equals("5"))
                count++;
        }

        return count;
    }
    
    private int getTotalOptionEAttempts () {
        int count = 0;

        for (int index = 0; index < liveTestCatalog.length; index++) {
            if (liveTestCatalog[index].getUserAnswerIndex().equals("5"))
                count++;
        }

        return count;
    }

    private void updateRecyclerView () {
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    contentAdapter.notifyDataSetChanged();
                    triggerProgressIndicator(false);
                }
            });
        }
    }
    
    private void triggerProgressIndicator (boolean isProcessing) {
        if (progressIndicator != null)
            progressIndicator.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }
}
package com.fourshape.a4mcqplus.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.interstitial_ad.ExitReadingInterstitialAd;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.viewmodels.TestReadFragmentViewModel;
import com.fourshape.a4mcqplus.rv_adapter.ContentAdapter;
import com.fourshape.a4mcqplus.rv_adapter.ContentModal;
import com.fourshape.a4mcqplus.rv_adapter.ContentType;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.BundleParams;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.DataFetchErrorView;
import com.fourshape.a4mcqplus.utils.DataFetchErrorViewOnClickListener;
import com.fourshape.a4mcqplus.utils.ExamDataJson;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestReadFragment extends Fragment {

    private static final String TAG = "TestReadFragment";

    private View mainView;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private TestReadFragmentViewModel viewModel;
    private DataFetchErrorView dataFetchErrorView;

    private String testTitle, testCode, testAction;
    private int testLimit, testOffset;

    public TestReadFragment() {
        // Required empty public constructor
    }

    public static TestReadFragment newInstance(String testTitle, String testCode, String testAction, int testLimit, int testOffset) {
        TestReadFragment fragment = new TestReadFragment();
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
    public void onPause() {
        super.onPause();
        if (recyclerView != null) {
            if (recyclerView.getLayoutManager() != null)
                viewModel.setRecyclerViewState(recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "TestReadFragment");
        if (testTitle != null) {
            ActionBarTitle.TITLE = "Reading of " + testTitle;
        } else {
            ActionBarTitle.TITLE = "Reading of MCQ Test";
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {

            viewModel = new ViewModelProvider(getActivity()).get(TestReadFragmentViewModel.class);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_test_read, container, false);
        recyclerView = mainView.findViewById(R.id.recycler_view);
        progressIndicator = mainView.findViewById(R.id.progress_circular);

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

        if (getContext() == null)
            return mainView;

        if (viewModel == null) {
            loadFreshData(container.getId());
        } else {

            if (viewModel.getPreviousFragmentCode() != null) {
                if (viewModel.getPreviousFragmentCode().equals(testCode+testTitle)) {

                    if (viewModel.getContentModalArrayList() != null) {
                        loadOldData(container.getId());
                    } else {
                        loadFreshData(container.getId());
                    }

                } else {
                    loadFreshData(container.getId());
                }
            } else {
                loadFreshData(container.getId());
            }

        }

        return mainView;
    }

    private void loadOldData (int fragmentContainerId) {

        contentModalArrayList = viewModel.getContentModalArrayList();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        updateRecyclerView();

        if (viewModel.getRecyclerViewState() != null) {
            if (recyclerView.getLayoutManager() != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(viewModel.getRecyclerViewState());
        }

    }

    private void loadFreshData (int fragmentContainerId) {

        contentModalArrayList = new ArrayList<>();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        viewModel.setCurrentFragmentCode(testCode + testTitle);

        executeServerSideTask();

    }

    private void executeServerSideTask() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void fetchData () {

        Map<String, String> params = new HashMap<>();
        params.put(ContentAccessParams.ACTION_TYPE, testAction);
        params.put(ContentAccessParams.QUERY_PARAM, testCode);
        params.put(ContentAccessParams.LIMIT, String.valueOf(testLimit));
        params.put(ContentAccessParams.OFFSET, String.valueOf(testOffset));

        MakeLog.info(TAG, params.toString());

        LoadData loadData = new LoadData(getContext(), new ContentAccessParams().getServerUrl(false), params);
        loadData.setShouldLoadNativeAd(false);
        loadData.setLoadDataListener(new DataLoadListener() {
            @Override
            public void onSuccess(String response, NativeAd nativeAd) {
                MakeLog.info(TAG, response);
                ExitReadingInterstitialAd.requestNewAd(getContext(), VariableControls.shouldUseMediationForInterstitialAd);
                handleData(nativeAd, response);
            }

            @Override
            public void onError(String error) {
                MakeLog.error(TAG, error);
                dataFetchErrorView.toggleErrorBar();
            }
        });

        loadData.load();

    }

    private void handleData (NativeAd nativeAd, String response) {

        if (getActivity() != null) {
           if (getActivity().isFinishing()) {
               MakeLog.error(TAG, "Can't Handle Data as Activity is finishing up.");
               return;
           }
        } else {
            MakeLog.error(TAG, "Can't Handle Data as Activity is NULL.");
            return;
        }

        if (response == null) {
            MakeLog.error(TAG, "Can't handle data because of NULL response.");

            if (dataFetchErrorView != null) {
                dataFetchErrorView.toggleErrorBar();
            }

            return;
        }

        MakeLog.info(TAG, response);

        JsonResponseDecoder jsonResponseDecoder = new JsonResponseDecoder(response);

        if (jsonResponseDecoder.hasValidData()) {

            int dataLength = jsonResponseDecoder.getDataLength();

            if (nativeAd != null) {
                if (dataLength != 0){
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.PRELOADED_NATIVE_AD, nativeAd));
                        contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                        contentModalArrayList.add(new ContentModal(ContentType.SPACE));
                    }
                }
            }

            for (int index=0; index<dataLength; index++) {

                String itemId = jsonResponseDecoder.getMcqId(index);
                String itemQuestion = jsonResponseDecoder.getQuestionText(index);
                String itemQuestionImageUrl = jsonResponseDecoder.getQuestionImageUrl(index);
                String answerIndex = jsonResponseDecoder.getAnswerIndex(index);
                String answerImageUrl = jsonResponseDecoder.getAnswerImageUrl(index);
                String[] itemExamCodes = null;

                if (itemQuestion == null && itemQuestionImageUrl == null)
                    continue;

                try {
                    itemExamCodes = jsonResponseDecoder.getMcqExamsCode(index);
                } catch (Exception e) {
                    MakeLog.exception(e);
                }

                if (itemExamCodes != null) {

                    if (contentModalArrayList != null) {

                        String examTitle, examCode;
                        int totalMcqs;

                        try {

                            if (itemExamCodes != null) {
                                examCode = itemExamCodes[0];
                                examTitle = ExamDataJson.getExamStuff(itemExamCodes[0])[0];
                            } else {
                                examTitle = null;
                                examCode = null;
                            }

                            totalMcqs = Integer.parseInt(ExamDataJson.getExamStuff(itemExamCodes[0])[1]);

                        } catch (Exception e) {
                            MakeLog.exception(e);
                            totalMcqs = -1;
                            examTitle = null;
                            examCode = null;
                        }

                        if (totalMcqs != -1 && examTitle != null && examCode != null) {
                            contentModalArrayList.add(new ContentModal(ContentType.MCQ_EXAM_TITLE_IN_MCQ_ROW, examTitle, examCode, totalMcqs, true));
                        }

                    }

                }


                // add Question first.
                if (contentModalArrayList != null) {

                    if (!isEmpty(itemQuestion) && !isEmpty(itemQuestionImageUrl)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_TEXT_AND_IMAGE, itemId, String.valueOf(index+1), itemQuestion, itemQuestionImageUrl));
                    } else if (!isEmpty(itemQuestion)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_TEXT_ONLY, itemId, String.valueOf(index+1), itemQuestion, null));
                    } else if (!isEmpty(itemQuestionImageUrl)) {
                        contentModalArrayList.add(new ContentModal(ContentType.MCQ_QUESTION_WITH_IMAGE_ONLY, itemId, String.valueOf(index+1), null, itemQuestionImageUrl));
                    }

                }


                // add options
                for (int optionIndex=0; optionIndex<4; optionIndex++) {

                    String optionTitle = jsonResponseDecoder.getOptionText(index, optionIndex);
                    String optionImageUrl = jsonResponseDecoder.getOptionImageUrl(index, optionIndex);
                    String mcqOptionIndex = getOptionIndex(optionIndex);
                    String mcqTrueIndex = jsonResponseDecoder.getOptionTrueIndex(index, optionIndex);

                    if (contentModalArrayList != null) {

                        if (!isEmpty(optionTitle) && !isEmpty(optionImageUrl)) {
                            contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE, itemId, mcqOptionIndex, optionTitle, optionImageUrl, answerIndex, mcqTrueIndex));
                        } else if (!isEmpty(optionTitle)) {
                            contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_TEXT_ONLY, itemId, mcqOptionIndex, optionTitle, null, answerIndex, mcqTrueIndex));
                        } else if (!isEmpty(optionImageUrl)) {
                            contentModalArrayList.add(new ContentModal(ContentType.MCQ_OPTION_WITH_IMAGE_ONLY, itemId, mcqOptionIndex, null, optionImageUrl, answerIndex, mcqTrueIndex));
                        }

                    }

                }

                if (!isEmpty(answerImageUrl)) {
                    if (contentModalArrayList != null) {

                        contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));

                        // 0 for Question, 1-4 for Options, and 5 for Answer.
                        String backupAnswerImageUrl = new ContentAccessParams().getFileAccessUrl(VariableControls.SHOULD_COMPUTE_LIVE_URL) + FormattedData.getBackupImageName(itemId, "5");
                        contentModalArrayList.add(new ContentModal(ContentType.EXPLAINED_SOLUTION, answerImageUrl, backupAnswerImageUrl));
                        contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                    }
                }

                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.CHALLENGE, itemId, true));
                    contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.SPACE));
                }

                if (index % 2 == 0 || index == dataLength-1) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.NATIVE_BANNER_SIZE_AD));
                        contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                        contentModalArrayList.add(new ContentModal(ContentType.SPACE));
                    }
                }

            }

            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
            }

        } else {

            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
            }

        }

        updateRecyclerView();

    }

    private boolean isEmpty (String text) {
        if (text == null)
            return true;
        else
            return text.trim().length() == 0;
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


    private void updateRecyclerView () {
        saveDataToViewModel();
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

    private void saveDataToViewModel () {
        if (viewModel != null) {
            viewModel.setContentModalArrayList(contentModalArrayList);
        }
    }

    private void triggerProgressIndicator (boolean isDataFetching) {
        if (progressIndicator != null)
            progressIndicator.setVisibility(isDataFetching ? View.VISIBLE : View.GONE);
    }

}
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

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.viewmodels.ExamSectionFragmentViewModel;
import com.fourshape.a4mcqplus.fragments.viewmodels.SubjectFragmentViewModel;
import com.fourshape.a4mcqplus.rv_adapter.ContentAdapter;
import com.fourshape.a4mcqplus.rv_adapter.ContentModal;
import com.fourshape.a4mcqplus.rv_adapter.ContentType;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.DataFetchErrorView;
import com.fourshape.a4mcqplus.utils.DataFetchErrorViewOnClickListener;
import com.fourshape.a4mcqplus.utils.ExamDataJson;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ExamSectionFragment extends Fragment {

    private static final String TAG = "ExamSectionFragment";

    private View mainView;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private ExamSectionFragmentViewModel viewModel;
    private DataFetchErrorView dataFetchErrorView;

    public ExamSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(ExamSectionFragmentViewModel.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), TAG);
        ActionBarTitle.TITLE = "Select Exam";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_subject, container, false);

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

            contentModalArrayList = new ArrayList<>();
            contentAdapter = new ContentAdapter(getActivity(), container.getId(), contentModalArrayList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(contentAdapter);

            executeServerSideTask();

        } else {

            if (viewModel.getContentModalArrayList() != null) {

                contentModalArrayList = viewModel.getContentModalArrayList();
                contentAdapter = new ContentAdapter(getActivity(), container.getId(), contentModalArrayList);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(contentAdapter);

                updateRecyclerView();

                if (viewModel.getRecyclerViewState() != null) {
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().onRestoreInstanceState(viewModel.getRecyclerViewState());
                }

            } else {

                contentModalArrayList = new ArrayList<>();
                contentAdapter = new ContentAdapter(getActivity(), container.getId(), contentModalArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(contentAdapter);

                executeServerSideTask();
            }

        }

        return mainView;
    }

    private void executeServerSideTask () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void fetchData () {

        Map<String, String> params = new HashMap<>();

        params.put(ContentAccessParams.ACTION_TYPE, ContentAccessParams.ACCESS_EXAM_SECTION);

        LoadData loadData = new LoadData(getContext(), new ContentAccessParams().getServerUrl(false), params);
        loadData.setShouldLoadNativeAd(false);
        loadData.setLoadDataListener(new DataLoadListener() {
            @Override
            public void onSuccess(String response, NativeAd nativeAd) {
                MakeLog.info(TAG, response);
                handleData(nativeAd, response);
            }

            @Override
            public void onError(String error) {
                MakeLog.error(TAG, error);
                dataFetchErrorView.toggleErrorBar();
            }

        });

        if (getContext() != null) {
            loadData.load();
        } else {
            MakeLog.error(TAG, "NULL Context");
        }

    }

    private void handleData (NativeAd nativeAd, String response) {

        JsonResponseDecoder jsonResponseDecoder = new JsonResponseDecoder(response);

        if (jsonResponseDecoder.hasValidData()) {

            int responseLength = jsonResponseDecoder.getDataLength();

            if (nativeAd != null) {
                if (responseLength != 0) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.PRELOADED_NATIVE_AD, nativeAd));
                        contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    }
                }
            }

            for (int i=0; i<responseLength; i++) {

                String itemTitle = jsonResponseDecoder.getItemTitle(i);
                String itemCode = jsonResponseDecoder.getItemCode(i);

                if (contentModalArrayList != null && itemTitle != null && itemCode != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.EXAM_SECTION, itemTitle, itemCode));
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                }

            }

            if (responseLength > 0) {
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
                }
            }

        } else {
            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
            }
        }

        updateRecyclerView();

    }

    private void updateRecyclerView () {
        saveDataToViewModel();
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    contentAdapter.notifyDataSetChanged();
                    progressIndicator.setVisibility(View.GONE);
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
        if (progressIndicator != null) {
            progressIndicator.setVisibility(isDataFetching ? View.VISIBLE : View.GONE);
        }
    }

}
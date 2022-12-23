package com.fourshape.a4mcqplus.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.viewmodels.SubjectFragmentViewModel;
import com.fourshape.a4mcqplus.fragments.viewmodels.TagsFragmentViewModel;
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
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagsFragment extends Fragment {

    private String subjectTitle;
    private String subjectCode;

    private static final String TAG = "TagsFragment";

    private View mainView;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private TagsFragmentViewModel viewModel;
    private DataFetchErrorView dataFetchErrorView;

    public TagsFragment() {
        // Required empty public constructor
    }

    public static TagsFragment newInstance(String subjectTitle, String subjectCode) {
        TagsFragment fragment = new TagsFragment();
        Bundle args = new Bundle();
        args.putString(BundleParams.SUBJECT_TITLE, subjectTitle);
        args.putString(BundleParams.SUBJECT_CODE, subjectCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(TagsFragmentViewModel.class);
        }
        if (getArguments() != null) {
            subjectTitle = getArguments().getString(BundleParams.SUBJECT_TITLE, null);
            subjectCode = getArguments().getString(BundleParams.SUBJECT_CODE, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "TagsFragment");
        ActionBarTitle.TITLE = subjectTitle;
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

        mainView = inflater.inflate(R.layout.fragment_tags, container, false);

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
                if (viewModel.getPreviousFragmentCode().equals(subjectCode+subjectTitle)) {

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

        viewModel.setCurrentFragmentCode(subjectCode + subjectTitle);

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

    private void fetchData() {
        Map<String, String> params = new HashMap<>();
        params.put(ContentAccessParams.ACTION_TYPE, ContentAccessParams.ACCESS_TAG_LIST);
        params.put(ContentAccessParams.QUERY_PARAM, subjectCode);
        MakeLog.info(TAG, params.toString());

        LoadData loadData = new LoadData(getContext(), new ContentAccessParams().getServerUrl(false), params);
        loadData.setShouldLoadNativeAd(false);
        loadData.setLoadDataListener(new DataLoadListener() {
            @Override
            public void onSuccess(String response, NativeAd nativeAd) {
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

                int itemCount = 0;

                try {
                    itemCount = Integer.parseInt(jsonResponseDecoder.getItemCount(i));
                } catch (Exception e) {
                    MakeLog.exception(e);
                }

                if (contentModalArrayList != null && itemCode != null && itemTitle != null && itemCount != 0) {
                    contentModalArrayList.add(new ContentModal(ContentType.TAG, itemTitle, itemCode, itemCount));
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                }

            }

            if (responseLength > 0) {
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
                }
            }

        } else {
            if (contentModalArrayList != null)
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
        }

        updateRecyclerView();

    }

    private void triggerProgressIndicator (boolean isDataFetching) {
        if (progressIndicator != null)
            progressIndicator.setVisibility(isDataFetching ? View.VISIBLE : View.GONE);
    }

}
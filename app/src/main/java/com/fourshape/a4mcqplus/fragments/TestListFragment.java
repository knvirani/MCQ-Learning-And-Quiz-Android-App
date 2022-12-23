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
import com.fourshape.a4mcqplus.fragments.viewmodels.TestListFragmentViewModel;
import com.fourshape.a4mcqplus.rv_adapter.ContentAdapter;
import com.fourshape.a4mcqplus.rv_adapter.ContentModal;
import com.fourshape.a4mcqplus.rv_adapter.ContentType;
import com.fourshape.a4mcqplus.server.DataLoadListener;
import com.fourshape.a4mcqplus.server.LoadData;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.BundleParams;
import com.fourshape.a4mcqplus.utils.ContentAccessParams;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.JsonResponseDecoder;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestListFragment extends Fragment {

    private static final String TAG = "TestListFragment";

    private String itemTitle, itemCode, itemAction;
    private int itemCount;

    private View mainView;
    private RecyclerView recyclerView;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private TestListFragmentViewModel viewModel;

    public TestListFragment() {
        // Required empty public constructor
    }


    public static TestListFragment newInstance(String itemAction, String itemTitle, String itemCode, int itemCount) {
        TestListFragment fragment = new TestListFragment();
        Bundle args = new Bundle();
        args.putString(BundleParams.ITEM_TITLE, itemTitle);
        args.putString(BundleParams.ITEM_CODE, itemCode);
        args.putString(BundleParams.ITEM_ACTION, itemAction);
        args.putInt(BundleParams.ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(TestListFragmentViewModel.class);
        }
        if (getArguments() != null) {
            itemTitle = getArguments().getString(BundleParams.ITEM_TITLE, null);
            itemCode = getArguments().getString(BundleParams.ITEM_CODE, null);
            itemAction = getArguments().getString(BundleParams.ITEM_ACTION, null);
            itemCount = getArguments().getInt(BundleParams.ITEM_COUNT, 0);
        }
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
        TrackScreen.now(getContext(), "TestListFragment");
        ActionBarTitle.TITLE = itemTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_test_list, container, false);

        recyclerView = mainView.findViewById(R.id.recycler_view);
        progressIndicator = mainView.findViewById(R.id.progress_circular);

        if (getContext() == null)
            return mainView;

        if (viewModel == null) {

            loadFreshData(container.getId());

        } else {

            if (viewModel.getPreviousFragmentCode() != null) {
                if (!viewModel.getPreviousFragmentCode().equals(itemCode+itemTitle)) {
                    loadFreshData(container.getId());
                } else {
                    if (viewModel.getContentModalArrayList() != null) {
                        loadOldData(container.getId());
                    } else {
                        loadFreshData(container.getId());
                    }
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

        progressIndicator.setVisibility(View.GONE);
    }

    private void loadFreshData (int fragmentContainerId) {
        contentModalArrayList = new ArrayList<>();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        viewModel.setCurrentFragmentCode(itemCode + itemTitle);

        executeServerSideTask();
    }

    private void executeServerSideTask () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                attachData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void attachData () {

        int desiredResults = VariableControls.MAXIMUM_RESULTS;

        if (itemCount != 0) {

            if (itemCount < desiredResults) {

                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.MCQ_TEST_TOP_NOTICE));
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                }

                String testTitle = "MCQ Set - 1";
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.TEST_LIST, testTitle, itemAction, itemCode, 0, itemCount));
                }

            } else {

                int fullPoint = itemCount/desiredResults;
                int restPoint = itemCount%desiredResults;

                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.MCQ_TEST_TOP_NOTICE));
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                }

                int offset = 0;

                for (int i=0; i<fullPoint; i++) {

                    String testTitle = "MCQ Set - " + (i+1);

                    offset = i * desiredResults;

                    MakeLog.info(TAG, "Limit - " + desiredResults + " Offset - " + offset);

                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.TEST_LIST, testTitle, itemAction, itemCode, offset, desiredResults));
                    }

                    if (i != fullPoint-1) {
                       if (contentModalArrayList != null) {
                           contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                       }
                    }

                }

                if (restPoint != 0) {

                    offset += desiredResults;

                    String testTitle = "MCQ Set - " + (fullPoint+1);
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                        contentModalArrayList.add(new ContentModal(ContentType.TEST_LIST, testTitle, itemAction, itemCode, offset, desiredResults));
                    }

                    MakeLog.info(TAG, "Limit - " + desiredResults + " Offset - " + offset);
                }

            }

            if (itemCount > 0) {
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
                }
            }

        } else {
            if (contentModalArrayList != null)
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
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
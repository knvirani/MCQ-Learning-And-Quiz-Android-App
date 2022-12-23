package com.fourshape.a4mcqplus.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.fourshape.a4mcqplus.fragments.viewmodels.PrevExamsFragmentViewModel;
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
import com.fourshape.a4mcqplus.utils.Pagination;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrevExamsFragment extends Fragment {

    private static final String TAG = "PrevExamsFragment";

    private View mainView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private PrevExamsFragmentViewModel viewModel;
    private DataFetchErrorView dataFetchErrorView;

    private int visibleThreshold = 3, lastVisibleItem, totalItemCount;
    private boolean isContentLimitReached = false;
    private Pagination pagination;
    private boolean isDataFetching = false;
    private boolean isZeroContentChecked = false;

    private String itemTitle, itemCode, examBodyCode, actionType;

    public PrevExamsFragment() {
        // Required empty public constructor
    }

    public static PrevExamsFragment newInstance (String sectionTitle, String sectionCode, String actionType) {

        // Uses way : Entry -> Section/ExamBodySelect -> PrevExams

        PrevExamsFragment prevExamsFragment = new PrevExamsFragment();

        Bundle args = new Bundle();
        args.putString(BundleParams.ITEM_TITLE, sectionTitle);
        args.putString(BundleParams.ITEM_CODE, sectionCode);
        args.putString(BundleParams.ITEM_ACTION, actionType);
        prevExamsFragment.setArguments(args);

        return  prevExamsFragment;
    }

    public static PrevExamsFragment newInstance (String examBodyCode, String actionType) {

        // Uses way : Entry -> ExamBodySelect -> PrevExams

        PrevExamsFragment prevExamsFragment = new PrevExamsFragment();

        Bundle args = new Bundle();
        args.putString(BundleParams.EXAM_BODY_CODE, examBodyCode);
        args.putString(BundleParams.ITEM_ACTION, actionType);
        prevExamsFragment.setArguments(args);

        return  prevExamsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(PrevExamsFragmentViewModel.class);
        }

        if (getArguments() != null) {
            this.itemTitle = getArguments().getString(BundleParams.ITEM_TITLE, null);
            this.itemCode = getArguments().getString(BundleParams.ITEM_CODE, null);
            this.examBodyCode = getArguments().getString(BundleParams.EXAM_BODY_CODE, null);
            this.actionType = getArguments().getString(BundleParams.ITEM_ACTION, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerView != null) {
            if (recyclerView.getLayoutManager() != null)
                viewModel.setRecyclerViewState(recyclerView.getLayoutManager().onSaveInstanceState());
        }
        if (viewModel != null) {
            viewModel.setContentLimitReached(isContentLimitReached);
            viewModel.setPaginationOffset(pagination.getOffset());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "PrevExamsFragment");
        if (getContext() != null) {
            ActionBarTitle.TITLE = getContext().getString(R.string.list_of_previously_held_exams);
        } else {
            if (itemTitle == null) {
                itemTitle = "List of exams";
                ActionBarTitle.TITLE = itemTitle;
            }
            else
                ActionBarTitle.TITLE = itemTitle;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_prev_exams, container, false);

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

        if (pagination == null) {
            pagination = new Pagination();
            pagination.setLimit(10);
        }

        if (getContext() == null)
            return mainView;

        if (viewModel == null) {
            loadFreshData(container.getId());
        } else {

            if (examBodyCode != null)
                MakeLog.info(TAG, "ExamBodyCode Match: " + itemTitle+examBodyCode);

            if (itemCode != null)
                MakeLog.info(TAG, "itemCode Match: " + itemTitle+itemCode);

            if (viewModel.isCurrentFragmentIsSameAsPreviousOne(itemTitle, examBodyCode) || viewModel.isCurrentFragmentIsSameAsPreviousOne(itemTitle, itemCode)) {
                if (viewModel.getContentModalArrayList() != null) {
                    MakeLog.info(TAG, "Old Data");
                    loadOldData(container.getId());
                } else {
                    MakeLog.info(TAG, "Fresh Data with Array NULL");
                    loadFreshData(container.getId());
                }
            } else {
                MakeLog.info(TAG, "Fresh Data");
                loadFreshData(container.getId());
            }

        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (totalItemCount <= (lastVisibleItem + visibleThreshold) && !isDataFetching && !isContentLimitReached) {
                    fetchData();
                }

            }
        });

        return mainView;
    }

    private void loadFreshData (int fragmentContainerId) {
        contentModalArrayList = new ArrayList<>();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        pagination.setOffset(0);

        viewModel.setExamBodyTitle(itemTitle);
        if (examBodyCode != null)
            viewModel.setExamBodyCode(examBodyCode);
        if (itemCode != null)
            viewModel.setExamBodyCode(itemCode);

        executeServerSideTask();
    }

    private void loadOldData (int fragmentContainerId) {
        contentModalArrayList = viewModel.getContentModalArrayList();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        isContentLimitReached = viewModel.isContentLimitReached();
        pagination.setOffset(viewModel.getPaginationOffset());

        itemTitle = viewModel.getExamBodyTitle();
        examBodyCode = viewModel.getExamBodyCode();

        updateRecyclerView();

        if (viewModel.getRecyclerViewState() != null) {
            if (recyclerView.getLayoutManager() != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(viewModel.getRecyclerViewState());
        }

        progressIndicator.setVisibility(View.GONE);
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

        triggerProgressIndicator(true);

        if (isContentLimitReached) {
            triggerProgressIndicator(false);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put(ContentAccessParams.ACTION_TYPE, ContentAccessParams.ACCESS_EXAM_LIST);

        if (examBodyCode != null) {
            params.put(ContentAccessParams.QUERY_PARAM, examBodyCode);
        }

        if (itemCode != null) {
            params.put(ContentAccessParams.QUERY_SECOND_PARAM, itemCode);
        }

        params.put(ContentAccessParams.LIMIT, String.valueOf(pagination.getLimit()));
        params.put(ContentAccessParams.OFFSET, String.valueOf(pagination.getOffset()));
        MakeLog.info(TAG, params.toString());

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
                //showSnackbar(mainView, getContext(), "Failed to load.");
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

            if (!isZeroContentChecked) {
                isZeroContentChecked = true;
                if (responseLength == 0) {
                    isContentLimitReached = true;
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
                    }
                }
            }

            if (nativeAd != null) {
                if (!isContentLimitReached) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.PRELOADED_NATIVE_AD, nativeAd));
                        contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                    }
                }
            }

            for (int i=0; i<responseLength; i++) {

                String itemTitle = jsonResponseDecoder.getItemTitle(i);
                String itemCode = jsonResponseDecoder.getItemCode(i);

                int itemCount = 0;

                try {
                    itemCount = Integer.parseInt(jsonResponseDecoder.getTotalMcqs(i));
                } catch (Exception e) {
                    MakeLog.exception(e);
                }

                if (contentModalArrayList != null && itemTitle != null && itemCode != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.EXAM, itemTitle, itemCode, itemCount));
                    if (i != responseLength-1) {
                        contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                    }
                }

            }

            if (responseLength < pagination.getLimit()) {
                isContentLimitReached = true;
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
                }
            } else {
                pagination.raiseOffset();
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DIVIDER));
                }
            }

            updateRecyclerView();

            progressIndicator.setVisibility(View.GONE);

        } else {

            isZeroContentChecked = true;
            isContentLimitReached = true;
            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
            }

            updateRecyclerView();
        }

    }

    private void updateRecyclerView () {
        saveDataToViewModel();
        if (recyclerView != null) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {

                    contentAdapter.notifyItemInserted(contentModalArrayList.size());
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

    private void triggerProgressIndicator (boolean progressStatus) {

        isDataFetching = progressStatus;

        if (progressIndicator != null)
            progressIndicator.setVisibility(progressStatus ? View.VISIBLE : View.GONE);

    }

}
package com.fourshape.a4mcqplus.fragments;

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

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_db.DBCols;
import com.fourshape.a4mcqplus.app_db.LoadData;
import com.fourshape.a4mcqplus.app_db.LoadDataListener;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.viewmodels.PrevExamsFragmentViewModel;
import com.fourshape.a4mcqplus.fragments.viewmodels.SavedResultFragmentViewModel;
import com.fourshape.a4mcqplus.rv_adapter.ContentAdapter;
import com.fourshape.a4mcqplus.rv_adapter.ContentModal;
import com.fourshape.a4mcqplus.rv_adapter.ContentType;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.Pagination;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.fourshape.a4mcqplus.app_db.DBCols.TABLE_NAME;

public class SavedResultsListFragment extends Fragment {

    private static final String TAG = "SavedResultsListFragment";

    private View mainView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CircularProgressIndicator progressIndicator;
    private ArrayList<ContentModal> contentModalArrayList;
    private ContentAdapter contentAdapter;
    private SavedResultFragmentViewModel viewModel;

    private int visibleThreshold = 3, lastVisibleItem, totalItemCount;
    private boolean isContentLimitReached = false;
    private Pagination pagination;
    private boolean isDataFetching = false;
    private boolean isZeroContentChecked = false;
    private boolean isDashboardAttached = false;
    private int testCurrentIndex = 0;

    public SavedResultsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(SavedResultFragmentViewModel.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "SavedResultsListFragment");
        ActionBarTitle.TITLE = "Saved Results";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(R.layout.fragment_saved_results_list, container, false);
        recyclerView = mainView.findViewById(R.id.recycler_view);
        progressIndicator = mainView.findViewById(R.id.progress_circular);

        if (getContext() == null)
        {
            MakeLog.error(TAG,"NULL Context on onCreateView");
            return mainView;
        }

        if (pagination == null) {
            pagination = new Pagination();
            pagination.setLimit(VariableControls.MAXIMUM_RESULTS);
        }

        loadFreshData(container.getId());

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

    private void loadOldData (int fragmentContainerId) {

        contentModalArrayList = viewModel.getContentModalArrayList();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        isContentLimitReached = viewModel.isContentLimitReached();
        pagination.setOffset(viewModel.getPaginationOffset());

        updateRecyclerView();

        if (viewModel.getRecyclerViewState() != null) {
            if (recyclerView.getLayoutManager() != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(viewModel.getRecyclerViewState());
        }

        triggerProgressIndicator(false);

    }

    private void loadFreshData (int fragmentContainerId) {

        contentModalArrayList = new ArrayList<>();
        contentAdapter = new ContentAdapter(getActivity(), fragmentContainerId, contentModalArrayList);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contentAdapter);

        pagination.setOffset(0);

        executeServerSideTask();

    }

    private void executeServerSideTask () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);
    }

    private void fetchResultDashboard () {

        triggerProgressIndicator(true);

        LoadData loadData = new LoadData(getContext());
        loadData.setLoadDataListener(new LoadDataListener() {
            @Override
            public void onResult(String response) {
                //MakeLog.info(TAG, "Result Dashboard Row: " + response);
                if (!isDashboardAttached) {
                    decodeDashboardResponse(response);
                    isDashboardAttached = true;
                }
                fetchData();
            }
        });
        loadData.sendRequest("load_dashboard");

    }

    private void decodeDashboardResponse (String response) {

        try {

            if (response != null) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.length() > 0) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.RESULT_HISTORY_DASHBOARD, jsonObject.getInt(DBCols.TEST_TOTAL_NUMBERS), jsonObject.getInt(DBCols.TEST_TOTAL_HOURS)));
                    }
                }
            } else {
                MakeLog.error(TAG, "NULL Response from Dashboard");
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
                    contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                }
            }

        } catch (Exception e) {
            MakeLog.exception(e);
        }

    }

    private void decodeSavedResultResponse (String response) {

        if (response == null) {
            MakeLog.error(TAG, "NULL Response from ROW");
            if (contentModalArrayList != null) {
                contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
                isZeroContentChecked = true;
                isContentLimitReached = true;
                return;
            }
            return;
        }

        try {

            JSONArray jsonArray = new JSONArray(response);

            if (!isZeroContentChecked) {
                if (jsonArray.length() == 0) {
                    if (contentModalArrayList != null) {
                        contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, true));
                        isZeroContentChecked = true;
                        isContentLimitReached = true;
                        return;
                    }
                }
            }

            for (int index = 0; index < jsonArray.length(); index++) {

                testCurrentIndex++;

                JSONObject jsonObject = jsonArray.getJSONObject(index);

                String testTitle = jsonObject.getString(DBCols.TEST_TITLE);
                String testSubTitle = jsonObject.getString(DBCols.TEST_SUB_TITLE);
                String testName = jsonObject.getString(DBCols.TEST_NAME);
                int testTime = jsonObject.getInt(DBCols.TEST_TIME);
                int totalMcqs = jsonObject.getInt(DBCols.TEST_TOTAL_MCQS);
                int testCorrectAttempts = jsonObject.getInt(DBCols.TEST_CORRECT_ATTEMPTS);
                int testIncorrectAttempts = jsonObject.getInt(DBCols.TEST_INCORRECT_ATTEMPTS);
                int testOptionEAttempts = jsonObject.getInt(DBCols.TEST_OPTION_E_ATTEMPTS);

                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.DYNAMIC_DARK_GREY_DIVIDER));
                    contentModalArrayList.add(new ContentModal(ContentType.RESULT_HISTORY_ROW, testCurrentIndex, testTime, totalMcqs, testTitle, testSubTitle, testName, testCorrectAttempts, testIncorrectAttempts, testOptionEAttempts));
                }

            }

            MakeLog.info(TAG, "Array Length: " + jsonArray.length() + " Limit: " + pagination.getLimit() + " Offset: " + pagination.getOffset());

            if (jsonArray.length() < pagination.getLimit()) {
                isContentLimitReached = true;
                if (contentModalArrayList != null) {
                    contentModalArrayList.add(new ContentModal(ContentType.BLANK_OR_NO_RESULT, false));
                }
            } else {
                pagination.raiseOffset();
            }

        } catch (Exception e) {
            MakeLog.exception(e);
            isContentLimitReached = true;
        }

    }

    private void fetchData () {

        if (!isDashboardAttached) {

            fetchResultDashboard();

        } else {

            triggerProgressIndicator(true);

            LoadData loadData = new LoadData(getContext());
            loadData.setLoadDataListener(new LoadDataListener() {
                @Override
                public void onResult(String response) {
                    //MakeLog.info(TAG, "Result Row: " + response);
                    decodeSavedResultResponse(response);
                    updateRecyclerView();
                }
            });

            String SELECT_SQL = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + DBCols.ID + " DESC LIMIT " + pagination.getLimit() + " OFFSET " + pagination.getOffset();

            loadData.sendRequest(SELECT_SQL);

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
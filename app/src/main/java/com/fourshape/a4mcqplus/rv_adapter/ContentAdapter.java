package com.fourshape.a4mcqplus.rv_adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.app_ads.admob_ads.AdmobInterstitialAd;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.BannerSizeNativeAd;
import com.fourshape.a4mcqplus.fragments.SavedResultsListFragment;
import com.fourshape.a4mcqplus.fragments.SolutionImageFragment;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.AdmobNativeAdCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.BlankOrNoMoreResultCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.ExamSectionCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.MCQExamTitleRowCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.PrevExamsCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.SubjectCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.TagsCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.TestReadCardView;
import com.fourshape.a4mcqplus.rv_adapter.adapter_views.TestsCardView;
import com.fourshape.a4mcqplus.ui_dialogs.IssueRaiseDialog;
import com.fourshape.a4mcqplus.utils.CheckPackage;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

public class    ContentAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ContentAdapter";

    private ArrayList<ContentModal> contentModalArrayList;
    private FragmentActivity activity;
    private int fragmentContainerId;

    public ContentAdapter (FragmentActivity activity, int fragmentContainerId, ArrayList<ContentModal> contentModalArrayList) {
        this.activity = new WeakReference<>(activity).get();
        this.contentModalArrayList = contentModalArrayList;
        this.fragmentContainerId = fragmentContainerId;
    }

    public ContentAdapter (ArrayList<ContentModal> contentModalArrayList) {
        this.contentModalArrayList = contentModalArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return contentModalArrayList.get(position).getContentType();
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull RecyclerView.ViewHolder holder) {

        super.onViewRecycled(holder);

        if (holder.getItemViewType() == ContentType.NATIVE_AD || holder.getItemViewType() == ContentType.PRELOADED_NATIVE_AD) {

            AdmobNativeAdCardView.clear(holder.itemView);

        } else if (holder.getItemViewType() ==  ContentType.MCQ_QUESTION_WITH_IMAGE_ONLY) {

            ((ViewHolderForMcqQuestionWithImageOnly)holder).clearData();

        } else if (holder.getItemViewType() == ContentType.MCQ_QUESTION_WITH_TEXT_AND_IMAGE) {

            ((ViewHolderForMcqQuestionWithTextAndImage)holder).clearData();

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_IMAGE_ONLY) {

            ((ViewHolderForMcqOptionImageOnly)holder).clearData();

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE) {

            ((ViewHolderForMcqOptionTextAndImage)holder).clearData();

        } else if (holder.getItemViewType() == ContentType.NATIVE_BANNER_SIZE_AD) {

            ((NativeBannerAd)holder).releaseResources();

        }

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        MakeLog.info(TAG, "onCreateViewHolder");

        Context context = new WeakReference<>(parent.getContext()).get();

        if (viewType == ContentType.DIVIDER) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_divider_view, null);
            return new ViewHolderForDivider(view);

        } else if (viewType == ContentType.EXAM) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_text_view, null);
            return new ViewHolderForExams(view);

        } else if (viewType == ContentType.EXAM_SECTION) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_text_view, null);
            return new ViewHolderForExamSection(view);

        } else if (viewType == ContentType.NATIVE_AD) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_admob_nativead_cardview, null);
            return new ViewHolderForNativeAd(view);

        } else if (viewType == ContentType.NATIVE_BANNER_SIZE_AD) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_native_banner_ad, null);
            return new NativeBannerAd(view);

        } else if (viewType == ContentType.PRELOADED_NATIVE_AD) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_admob_nativead_cardview, null);
            return new ViewHolderForPreloadedAdmobNativeAd(view);

        } else if (viewType == ContentType.SPACE) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_space_view, null);
            return new ViewHolderForSpace(view);

        } else if (viewType == ContentType.SUBJECT) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_text_view, null);
            return new ViewHolderForSubject(view);

        } else if (viewType == ContentType.TAG) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_text_view, null);
            return new ViewHolderForTag(view);

        } else if (viewType == ContentType.TEST_LIST) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_text_view, null);
            return new ViewHolderForTests(view);

        } else if (viewType == ContentType.YEAR_LABEL) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_year_text_view, null);
            return new ViewHolderForYear(view);

        } else if (viewType == ContentType.BLANK_OR_NO_RESULT) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_blank_result_card_view, null);
            return new ViewHolderForBlankOrNoResult(view);

        } else if (viewType == ContentType.TEST_RESULT_TIME_TAKEN) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_time_taken_per_mcq_view_for_test, null);
            return new ViewHolderForTestTimeTaken(view);

        } else if (viewType == ContentType.TEST_RESULT_SUMMARY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_result_summary, null);
            return new ViewHolderForTestSummary(view);

        } else if (viewType == ContentType.TEST_RESULT_ANSWERS) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_test_answers_view, null);
            return new ViewHolderForTestAnswers(view);

        } else if (viewType == ContentType.DETAILED_RESULT_LABEL) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_result_detailed, null);
            return new ViewHolderForDetailedResultLabel(view);

        } else if (viewType == ContentType.CHALLENGE) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_challenge_view, null);
            return new ViewHolderForChallengeLabel(view);

        } else if (viewType == ContentType.MCQ_QUESTION_WITH_TEXT_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_question_text_only_view, null);
            return new ViewHolderForMcqQuestionWithTextOnly(view);

        } else if (viewType == ContentType.MCQ_QUESTION_WITH_IMAGE_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_question_img_only_view, null);
            return new ViewHolderForMcqQuestionWithImageOnly(view);

        } else if (viewType == ContentType.MCQ_QUESTION_WITH_TEXT_AND_IMAGE) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_question_text_with_img_only_view, null);
            return new ViewHolderForMcqQuestionWithTextAndImage(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_TEXT_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_text_only_view, null);
            return new ViewHolderForMcqOptionTextOnly(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_IMAGE_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_img_only_view, null);
            return new ViewHolderForMcqOptionImageOnly(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_text_with_img_only_view, null);
            return new ViewHolderForMcqOptionTextAndImage(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_TEXT_DETAILED_RESULT_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_text_only_view, null);
            return new ViewHolderForMcqOptionTextOnly(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_IMAGE_DETAILED_RESULT_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_img_only_view, null);
            return new ViewHolderForMcqOptionImageOnly(view);

        } else if (viewType == ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE_DETAILED_RESULT_ONLY) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_option_text_with_img_only_view, null);
            return new ViewHolderForMcqOptionTextAndImage(view);

        } else if (viewType == ContentType.MCQ_TEST_TOP_NOTICE) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_notice_text_only_view, null);
            return new ViewHolderForTopNotice(view);

        } else if (viewType == ContentType.RESULT_HISTORY_DASHBOARD) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_result_history_dashboard, null);
            return new ViewHolderForResultDashboard(view);

        } else if (viewType == ContentType.RESULT_HISTORY_ROW) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_view_for_saved_results, null);
            return new ViewHolderForResultHistory(view);

        } else if (viewType == ContentType.DYNAMIC_DARK_GREY_DIVIDER) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_divider_view_for_result_history, null);
            return new ViewHolderForDarkGreyDivider(view);

        } else if (viewType == ContentType.EXPLAINED_SOLUTION) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_explained_solution_view, null);
            return new ViewHolderForExplainedSolution(view);

        } else if (viewType == ContentType.MCQ_EXAM_TITLE_IN_MCQ_ROW) {

            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_mcq_exam_title_in_mcq_row_view, null);
            return new ViewHolderForMcqExamTitleInMcqRow(view);

        } else {
            // Create a Space View Holder Here
            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_space_view, null);
            return new ViewHolderForSpace(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == ContentType.DIVIDER) {

            ((ViewHolderForDivider)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.EXAM) {

            ((ViewHolderForExams)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.EXAM_SECTION) {

            ((ViewHolderForExamSection)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.NATIVE_AD) {

            ((ViewHolderForNativeAd)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.NATIVE_BANNER_SIZE_AD) {

            ((NativeBannerAd)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.PRELOADED_NATIVE_AD) {

            ((ViewHolderForPreloadedAdmobNativeAd)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.SPACE) {

            ((ViewHolderForSpace)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.SUBJECT) {

            ((ViewHolderForSubject)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.TAG) {

            ((ViewHolderForTag)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.TEST_LIST) {

            ((ViewHolderForTests)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.YEAR_LABEL) {

            ((ViewHolderForYear)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.BLANK_OR_NO_RESULT) {

            ((ViewHolderForBlankOrNoResult)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.TEST_RESULT_TIME_TAKEN) {

            ((ViewHolderForTestTimeTaken)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.TEST_RESULT_SUMMARY) {

            ((ViewHolderForTestSummary)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.TEST_RESULT_ANSWERS) {

            ((ViewHolderForTestAnswers)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.DETAILED_RESULT_LABEL) {

            ((ViewHolderForDetailedResultLabel)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.CHALLENGE) {

            ((ViewHolderForChallengeLabel)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_QUESTION_WITH_TEXT_ONLY) {

            ((ViewHolderForMcqQuestionWithTextOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_QUESTION_WITH_IMAGE_ONLY) {

            ((ViewHolderForMcqQuestionWithImageOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_QUESTION_WITH_TEXT_AND_IMAGE) {

            ((ViewHolderForMcqQuestionWithTextAndImage)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_TEXT_ONLY) {

            ((ViewHolderForMcqOptionTextOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_IMAGE_ONLY) {

            ((ViewHolderForMcqOptionImageOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE) {

            ((ViewHolderForMcqOptionTextAndImage)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_TEXT_DETAILED_RESULT_ONLY) {

            ((ViewHolderForMcqOptionTextOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_IMAGE_DETAILED_RESULT_ONLY) {

            ((ViewHolderForMcqOptionImageOnly)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_OPTION_WITH_TEXT_AND_IMAGE_DETAILED_RESULT_ONLY) {

            ((ViewHolderForMcqOptionTextAndImage)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_TEST_TOP_NOTICE) {

            ((ViewHolderForTopNotice)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.RESULT_HISTORY_DASHBOARD) {

            ((ViewHolderForResultDashboard)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.RESULT_HISTORY_ROW) {

            ((ViewHolderForResultHistory)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.DYNAMIC_DARK_GREY_DIVIDER) {

            ((ViewHolderForDarkGreyDivider)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.EXPLAINED_SOLUTION) {

            ((ViewHolderForExplainedSolution)holder).setView(position);

        } else if (holder.getItemViewType() == ContentType.MCQ_EXAM_TITLE_IN_MCQ_ROW) {

            ((ViewHolderForMcqExamTitleInMcqRow)holder).setView(position);

        }
    }

    @Override
    public int getItemCount() {
        return contentModalArrayList.size();
    }

    private static void setLinearLayoutCompactParams (View itemView) {

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(layoutParams);

    }

    private static void setLayoutParamsForNativeBannerInConstraintLayout (View view) {

        int width = ScreenParams.getDisplayWidthPixels(view.getContext());
        int height = (int) ( 170 * ScreenParams.getDisplayDensity(view.getContext()) );

        view.setLayoutParams(new FrameLayout.LayoutParams(width, height));

    }

    class DeveloperInfo extends RecyclerView.ViewHolder {

        public DeveloperInfo(@NonNull @NotNull View itemView) {
            super(itemView);
            setLinearLayoutCompactParams(itemView);
        }

        private void setViews (int position) {}

    }

    class AppInfo extends RecyclerView.ViewHolder {

        public AppInfo(@NonNull @NotNull View itemView) {
            super(itemView);
            setLinearLayoutCompactParams(itemView);
        }

        private void setViews (int position) {

            ImageView appLogoIV = itemView.findViewById(R.id.app_logo);
            TextView appTitleTV = itemView.findViewById(R.id.app_title);
            MaterialButton actionMB = itemView.findViewById(R.id.open_app_mb);

            int appLogoDrawableId = contentModalArrayList.get(position).getAppLogoId();
            String appTitle = contentModalArrayList.get(position).getAppTitle();
            String appPkg = contentModalArrayList.get(position).getAppPackage();

            try {
                appLogoIV.setImageDrawable(itemView.getContext().getDrawable(appLogoDrawableId));
                appTitleTV.setText(appTitle);
            } catch (Exception e) {
                MakeLog.exception(e);
            }


            if (CheckPackage.isFound(itemView.getContext(), appPkg)) {

                actionMB.setText(R.string.open_app);
                actionMB.setIcon(itemView.getContext().getDrawable(R.drawable.ic_right_arrow));

                actionMB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApp(appPkg);
                    }
                });

            } else {

                actionMB.setText(R.string.get_app);
                actionMB.setIcon(itemView.getContext().getDrawable(R.drawable.ic_download));

                actionMB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String appUrl = "https://play.google.com/store/apps/details?id=" + appPkg;
                        openPlayStore(appUrl);

                    }
                });

            }

        }

        private void openPlayStore (String appLink) {

            try {
                if (itemView.getContext() != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appLink));
                    browserIntent.setPackage("com.android.vending");
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    itemView.getContext().startActivity(browserIntent);
                }
            } catch (Exception e){

                MakeLog.exception(e);

                if (itemView.getContext() != null) {
                    Toast.makeText(itemView.getContext(), "Can't open.", Toast.LENGTH_SHORT).show();
                }

            }

        }

        private void openApp (String appPkg) {

            try {
                if (itemView.getContext() != null) {
                    Intent browserIntent = itemView.getContext().getPackageManager().getLaunchIntentForPackage(appPkg);
                    itemView.getContext().startActivity(browserIntent);
                }
            } catch (Exception e){

                MakeLog.exception(e);

                if (itemView.getContext() != null) {
                    Toast.makeText(itemView.getContext(), "Can't open.", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }


    class NativeBannerAd extends RecyclerView.ViewHolder {

        BannerSizeNativeAd bannerSizeNativeAd;

        public NativeBannerAd(@NonNull @NotNull View itemView) {
            super(itemView);
            setLayoutParamsForNativeBannerInConstraintLayout(itemView);
            bannerSizeNativeAd = new BannerSizeNativeAd(itemView.getContext(), 1, itemView);
        }

        private void setView (int position) {

            bannerSizeNativeAd.fetchViewWidgets();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bannerSizeNativeAd.load();
                }
            },  125);

        }

        private void releaseResources () {
            bannerSizeNativeAd.releaseResources();
        }

    }

    class ViewHolderForExamSection extends RecyclerView.ViewHolder {

        public ViewHolderForExamSection(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemTitle = contentModalArrayList.get(position).getItemTitle();
            String itemCode = contentModalArrayList.get(position).getItemCode();

            new ExamSectionCardView(activity, fragmentContainerId, itemView, itemTitle, itemCode);

        }

    }

    class ViewHolderForMcqExamTitleInMcqRow extends RecyclerView.ViewHolder {

        public ViewHolderForMcqExamTitleInMcqRow(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void setView (int position) {
            new MCQExamTitleRowCardView(activity, fragmentContainerId, itemView, contentModalArrayList.get(position).getExamTitle(), contentModalArrayList.get(position).getExamCode(), contentModalArrayList.get(position).getTotalMcqs());
        }

    }

    class ViewHolderForPreloadedAdmobNativeAd extends RecyclerView.ViewHolder {

        public ViewHolderForPreloadedAdmobNativeAd(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void setView (int position) {
            AdmobNativeAdCardView.attachNativeAd(contentModalArrayList.get(position).getNativeAd(), itemView);
        }

    }

    class ViewHolderForExplainedSolution extends RecyclerView.ViewHolder {

        public ViewHolderForExplainedSolution(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {
            String mainImageUrl = contentModalArrayList.get(position).getItemTitle();
            String backUpImageUrl = contentModalArrayList.get(position).getItemCode();
            MaterialCardView materialCardView = itemView.findViewById(R.id.mtrl_card_view);

            materialCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (AdmobInterstitialAd.shouldShowAd()) {
                        AdmobInterstitialAd.showAdBeforeNewFragmentLaunch(activity, fragmentContainerId, mainImageUrl, backUpImageUrl);
                    } else {
                        activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("explained_solution_fragment").replace(fragmentContainerId, SolutionImageFragment.newInstance(mainImageUrl, backUpImageUrl)).commit();
                        AdmobInterstitialAd.requestNewAd(activity.getApplicationContext());
                    }

                }
            });

        }
    }

    static class ViewHolderForDarkGreyDivider extends RecyclerView.ViewHolder {

        public ViewHolderForDarkGreyDivider(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {}
    }

    class ViewHolderForResultHistory extends RecyclerView.ViewHolder {

        public ViewHolderForResultHistory(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {

            String testTitle = contentModalArrayList.get(position).getTestTitle();
            String testSubTitle = contentModalArrayList.get(position).getTestSubTitle();
            String testName = contentModalArrayList.get(position).getTestName();
            String testTime = String.valueOf(FormattedData.getFormattedTime(contentModalArrayList.get(position).getTestFinishTime()));
            String testMcqs = String.valueOf(contentModalArrayList.get(position).getTotalMcqs());
            String testIndex = String.valueOf(contentModalArrayList.get(position).getTestIndex());
            String testCorrectAttempts = String.valueOf(contentModalArrayList.get(position).getTestCorrectAttempts());
            String testIncorrectAttempts = String.valueOf(contentModalArrayList.get(position).getTestIncorrectAttempts());
            String testOptionEAttempts = String.valueOf(contentModalArrayList.get(position).getTestOptionEAttempts());

            ((TextView)itemView.findViewById(R.id.test_title)).setText(testTitle);

            if (testSubTitle != null) {
                if (testSubTitle.length()==0)
                    itemView.findViewById(R.id.test_sub_title).setVisibility(View.GONE);
                else
                    ((TextView)itemView.findViewById(R.id.test_sub_title)).setText(testSubTitle);
            }

            ((TextView)itemView.findViewById(R.id.test_name)).setText(testName);
            ((TextView)itemView.findViewById(R.id.test_index)).setText(testIndex);
            ((TextView)itemView.findViewById(R.id.test_time)).setText("Finished in " + testTime);
            ((MaterialRadioButton)itemView.findViewById(R.id.total_correct)).setText(testCorrectAttempts + "/" + testMcqs);
            ((MaterialRadioButton)itemView.findViewById(R.id.total_incorrect)).setText(testIncorrectAttempts + "/" + testMcqs);
            ((MaterialRadioButton)itemView.findViewById(R.id.total_option_e)).setText(testOptionEAttempts + "/" + testMcqs);

        }
    }

    class ViewHolderForResultDashboard extends RecyclerView.ViewHolder {

        public ViewHolderForResultDashboard(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {

            String totalHours = String.format(Locale.getDefault(), "%.02f", (float)contentModalArrayList.get(position).getTotalHours()/3600);
            String totalTests = String.valueOf(contentModalArrayList.get(position).getTotalTests());

            ((TextView)itemView.findViewById(R.id.total_hours)).setText(totalHours);
            ((TextView)itemView.findViewById(R.id.total_tests)).setText(totalTests);

        }
    }

    static class ViewHolderForTopNotice extends RecyclerView.ViewHolder {

        public ViewHolderForTopNotice(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {
            ((TextView)itemView.findViewById(R.id.text_view_title)).setText(itemView.getContext().getText(R.string.test_list_fragment_notice));
        }
    }

    class ViewHolderForChallengeLabel extends RecyclerView.ViewHolder {

        public ViewHolderForChallengeLabel(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {

            String mcqId = contentModalArrayList.get(position).getMcqId();

            MaterialCardView challengeCardView = itemView.findViewById(R.id.challenge_answer_mtrl_card_view);
            challengeCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new IssueRaiseDialog(mcqId, new WeakReference<>(view.getContext()).get());
                }
            });

        }
    }

    static class ViewHolderForDetailedResultLabel extends RecyclerView.ViewHolder {

        public ViewHolderForDetailedResultLabel(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {}
    }

    class ViewHolderForTestAnswers extends RecyclerView.ViewHolder {

        MaterialCardView option1CV, option2CV, option3CV, option4CV, option5CV;
        MaterialRadioButton option1RB, option2RB, option3RB, option4RB, option5RB;

        public ViewHolderForTestAnswers(@NonNull @NotNull View itemView) {
            super(itemView);

            option1RB = itemView.findViewById(R.id.option_1_mtrl_radio_btn);
            option2RB = itemView.findViewById(R.id.option_2_mtrl_radio_btn);
            option3RB = itemView.findViewById(R.id.option_3_mtrl_radio_btn);
            option4RB = itemView.findViewById(R.id.option_4_mtrl_radio_btn);
            option5RB = itemView.findViewById(R.id.option_5_mtrl_radio_btn);

            option1CV = itemView.findViewById(R.id.option_1_mtrl_card_view);
            option2CV = itemView.findViewById(R.id.option_2_mtrl_card_view);
            option3CV = itemView.findViewById(R.id.option_3_mtrl_card_view);
            option4CV = itemView.findViewById(R.id.option_4_mtrl_card_view);
            option5CV = itemView.findViewById(R.id.option_5_mtrl_card_view);

            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);

            resizeRadioOptions();

        }

        private void resizeRadioOptions () {

            int totalSpace = (16*2) + (8*4); // in dp
            int screenWidth = ScreenParams.getDisplayWidthPixels(itemView.getContext());
            int totalBlocks = 5;

            int sizeForOneBlock =  (screenWidth - totalSpace) / totalBlocks;

            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(sizeForOneBlock, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            option1CV.setLayoutParams(layoutParams);
            option2CV.setLayoutParams(layoutParams);
            option3CV.setLayoutParams(layoutParams);
            option4CV.setLayoutParams(layoutParams);
            option5CV.setLayoutParams(layoutParams);

        }

        private void setView (int position) {

            String trueAnswerIndex = contentModalArrayList.get(position).getAnswerIndex();
            String attemptedAnswerIndex = contentModalArrayList.get(position).getUserAnswerIndex();
            String mcqId = contentModalArrayList.get(position).getMcqId();

            ColorStateList greenColorStateList = ColorStateList.valueOf(itemView.getContext().getColor(R.color.correct_answer));
            ColorStateList blueColorStateList = ColorStateList.valueOf(itemView.getContext().getColor(R.color.app_main_color));

            option1RB.setChecked(false);
            option2RB.setChecked(false);
            option3RB.setChecked(false);
            option4RB.setChecked(false);
            option5RB.setChecked(false);

            option1RB.setButtonTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.normal_radio_btn_tint)));
            option2RB.setButtonTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.normal_radio_btn_tint)));
            option3RB.setButtonTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.normal_radio_btn_tint)));
            option4RB.setButtonTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.normal_radio_btn_tint)));
            option5RB.setButtonTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.normal_radio_btn_tint)));

            setAnswer(attemptedAnswerIndex, blueColorStateList);
            setAnswer(trueAnswerIndex, greenColorStateList);

            MaterialCardView challengeCardView = itemView.findViewById(R.id.challenge_answer_mtrl_card_view);
            challengeCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Show Popup For Issue Raise.
                    new IssueRaiseDialog(mcqId, view.getContext());
                }
            });

        }

        private void setAnswer (String index, ColorStateList colorStateList) {
            if (index != null) {
                switch (index) {
                    case "1" :
                        option1RB.setChecked(true);
                        option1RB.setButtonTintList(colorStateList);
                        break;
                    case "2":
                        option2RB.setChecked(true);
                        option2RB.setButtonTintList(colorStateList);
                        break;
                    case "3":
                        option3RB.setChecked(true);
                        option3RB.setButtonTintList(colorStateList);
                        break;
                    case "4":
                        option4RB.setChecked(true);
                        option4RB.setButtonTintList(colorStateList);
                        break;
                    case "5":
                        option5RB.setChecked(true);
                        option5RB.setButtonTintList(colorStateList);
                        break;
                }
            }
        }

    }

    class ViewHolderForTestSummary extends RecyclerView.ViewHolder {

        public ViewHolderForTestSummary(@NonNull @NotNull View itemView) {
            super(itemView);
            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ScreenParams.getDisplayWidthPixels(itemView.getContext()), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(layoutParams);
        }

        private void setView (int position) {

            int totalMcqs = contentModalArrayList.get(position).getTotalMcqs();
            int totalCorrectAttempts = contentModalArrayList.get(position).getTotalCorrectAttempts();
            int totalIncorrectAttempts = contentModalArrayList.get(position).getTotalIncorrectAttempts();
            int totalOptionEAttempts = contentModalArrayList.get(position).getTotalOptionEAttempts();
            int totalTestTime = contentModalArrayList.get(position).getTotalTimeTaken();
            String testTitle = contentModalArrayList.get(position).getTestTitle();
            String testSubTitle = contentModalArrayList.get(position).getTestSubTitle();
            String testName = contentModalArrayList.get(position).getTestName();

            TextView testTitleTextView, testSubTitleTextView, testNameTextView, testTimeTextView;
            MaterialRadioButton totalCorrectRB, totalIncorrectRB, totalOptionERB;
            MaterialCardView openMoreResultsCardView;

            openMoreResultsCardView = itemView.findViewById(R.id.open_previous_results_mtrl_card);
            testTitleTextView = itemView.findViewById(R.id.test_title);
            testSubTitleTextView = itemView.findViewById(R.id.test_sub_title);
            testNameTextView = itemView.findViewById(R.id.test_name);
            testTimeTextView = itemView.findViewById(R.id.test_time);
            totalCorrectRB = itemView.findViewById(R.id.total_correct);
            totalIncorrectRB = itemView.findViewById(R.id.total_incorrect);
            totalOptionERB = itemView.findViewById(R.id.total_option_e);

            openMoreResultsCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (activity != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).addToBackStack("saved_results_list_fragment").replace(fragmentContainerId, new SavedResultsListFragment()).commit();
                            }
                        }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);
                    }
                }
            });

            if (testTitle != null)
                testTitleTextView.setText(testTitle);
            else
                testTitleTextView.setVisibility(View.GONE);

            if (testSubTitle != null)
                testSubTitleTextView.setText(testSubTitle);
            else
                testSubTitleTextView.setVisibility(View.GONE);

            if (testName != null)
                testNameTextView.setText(testName);
            else
                testNameTextView.setVisibility(View.GONE);

            String correctAttemptsText = totalCorrectAttempts + "/" + totalMcqs;
            String incorrectAttemptsText = totalIncorrectAttempts + "/" + totalMcqs;
            String optionEAttemptsText = totalOptionEAttempts + "/" + totalMcqs;
            String totalTimeTaken = "Finished in " + FormattedData.getFormattedTime(totalTestTime);

            testTimeTextView.setText(totalTimeTaken);
            totalCorrectRB.setText(correctAttemptsText);
            totalIncorrectRB.setText(incorrectAttemptsText);
            totalOptionERB.setText(optionEAttemptsText);

            LinearLayoutCompat testGuide = itemView.findViewById(R.id.test_interpret_guide_container);
            MaterialCardView shareCardView, expandCardView;
            shareCardView = itemView.findViewById(R.id.share_result);
            expandCardView = itemView.findViewById(R.id.view_result_in_detail);
            View unnecessaryDivider = itemView.findViewById(R.id.unnecessary_divider_in_detailed_result);

            unnecessaryDivider.setVisibility(View.GONE);
            testGuide.setVisibility(View.GONE);
            shareCardView.setVisibility(View.GONE);
            expandCardView.setVisibility(View.GONE);

        }
    }

    class ViewHolderForTestTimeTaken extends RecyclerView.ViewHolder {

        public ViewHolderForTestTimeTaken(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            TextView testTimeTextView = itemView.findViewById(R.id.test_time);
            String testTime = "Answered in " + FormattedData.getFormattedTime(contentModalArrayList.get(position).getTimeTaken());
            testTimeTextView.setText(testTime);

            if (itemView.findViewById(R.id.wrong_answer).getVisibility() != View.VISIBLE)
                itemView.findViewById(R.id.wrong_answer).setVisibility(View.VISIBLE);

            if (itemView.findViewById(R.id.correct_answer).getVisibility() != View.VISIBLE)
                itemView.findViewById(R.id.correct_answer).setVisibility(View.VISIBLE);

            if (itemView.findViewById(R.id.not_attempted_answer).getVisibility() != View.VISIBLE)
                itemView.findViewById(R.id.not_attempted_answer).setVisibility(View.VISIBLE);

            String correctAnswer = contentModalArrayList.get(position).getAnswerIndex().trim();
            String attemptedAnswer = contentModalArrayList.get(position).getUserAnswerIndex().trim();

            if (attemptedAnswer.equals("5")) {
                itemView.findViewById(R.id.wrong_answer).setVisibility(View.GONE);
                itemView.findViewById(R.id.correct_answer).setVisibility(View.GONE);
            } else {
                if (correctAnswer.equals(attemptedAnswer)) {
                    itemView.findViewById(R.id.wrong_answer).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.correct_answer).setVisibility(View.GONE);
                }
                itemView.findViewById(R.id.not_attempted_answer).setVisibility(View.GONE);
            }

        }
    }

    class ViewHolderForBlankOrNoResult extends RecyclerView.ViewHolder {

        public ViewHolderForBlankOrNoResult(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {
            new BlankOrNoMoreResultCardView(itemView, contentModalArrayList.get(position).isEmptyResult());
        }
    }

    static class ViewHolderForSpace extends RecyclerView.ViewHolder {

        public ViewHolderForSpace(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {}
    }

    static class ViewHolderForDivider extends RecyclerView.ViewHolder {

        public ViewHolderForDivider(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {}
    }

    class ViewHolderForSubject extends RecyclerView.ViewHolder {

        public ViewHolderForSubject(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemTitle = contentModalArrayList.get(position).getItemTitle();
            String itemCode = contentModalArrayList.get(position).getItemCode();

            new SubjectCardView(activity, fragmentContainerId, itemView, itemTitle, itemCode);

        }
    }

    class ViewHolderForTag extends RecyclerView.ViewHolder {

        public ViewHolderForTag(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String tagTitle = contentModalArrayList.get(position).getItemTitle();
            String tagCode = contentModalArrayList.get(position).getItemCode();
            int questionCount = contentModalArrayList.get(position).getItemQuantityCount();

            new TagsCardView(activity, fragmentContainerId, itemView, tagTitle, tagCode, questionCount);

        }

    }

    class ViewHolderForExams extends RecyclerView.ViewHolder {

        public ViewHolderForExams(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {
            String examTitle = contentModalArrayList.get(position).getItemTitle();
            String examCode = contentModalArrayList.get(position).getItemCode();
            int questionCount = contentModalArrayList.get(position).getItemQuantityCount();
            new PrevExamsCardView(activity, fragmentContainerId, itemView, examTitle, examCode, questionCount);
        }

    }

    class ViewHolderForYear extends RecyclerView.ViewHolder {

        public ViewHolderForYear(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {
            String year = contentModalArrayList.get(position).getYearTitle();
        }
    }

    class ViewHolderForTests extends RecyclerView.ViewHolder {

        public ViewHolderForTests(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {
            String testTitle = contentModalArrayList.get(position).getItemTitle();
            String testAction = contentModalArrayList.get(position).getItemAction();
            int testLimit = contentModalArrayList.get(position).getItemLimit();
            int testOffset = contentModalArrayList.get(position).getItemOffset();
            String testCode = contentModalArrayList.get(position).getItemCode();

            new TestsCardView(activity, fragmentContainerId, itemView, testTitle, testCode, testAction, testLimit, testOffset);
        }
    }

    static class ViewHolderForNativeAd extends RecyclerView.ViewHolder {

        public ViewHolderForNativeAd(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {
            AdmobNativeAdCardView.attachNativeAd(AdmobNativeAdCardView.getNativeAdInstance(), itemView);
        }
    }

    class ViewHolderForMcqQuestionWithTextOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqQuestionWithTextOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, null).attachCard();
        }

    }

    class ViewHolderForMcqQuestionWithImageOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqQuestionWithImageOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();

            new TestReadCardView(itemView, itemId, itemIndex, null, itemImageUrl).attachCard();
        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }

    }

    class ViewHolderForMcqQuestionWithTextAndImage extends RecyclerView.ViewHolder {

        public ViewHolderForMcqQuestionWithTextAndImage(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, itemImageUrl).attachCard();
        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }

    }

    class ViewHolderForMcqOptionTextDetailedResultOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionTextDetailedResultOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, null, itemAnswerIndex, itemOptionIndex).attachCard();

        }

    }

    class ViewHolderForMcqOptionTextOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionTextOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, null, itemAnswerIndex, itemOptionIndex).attachCard();

        }

    }

    class ViewHolderForMcqOptionImageDetailedResultOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionImageDetailedResultOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, null, itemImageUrl, itemAnswerIndex, itemOptionIndex).attachCard();

        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }

    }

    class ViewHolderForMcqOptionImageOnly extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionImageOnly(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, null, itemImageUrl, itemAnswerIndex, itemOptionIndex).attachCard();

        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }

    }

    class ViewHolderForMcqOptionTextAndImageDetailedResult extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionTextAndImageDetailedResult (@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, itemImageUrl, itemAnswerIndex, itemOptionIndex).attachCard();

        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }
    }

    class ViewHolderForMcqOptionTextAndImage extends RecyclerView.ViewHolder {

        public ViewHolderForMcqOptionTextAndImage(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        private void setView (int position) {

            String itemId = contentModalArrayList.get(position).getItemId();
            String itemIndex = contentModalArrayList.get(position).getItemIndex();
            String itemText = contentModalArrayList.get(position).getItemTitle();
            String itemImageUrl = contentModalArrayList.get(position).getItemImageUrl();
            String itemAnswerIndex = contentModalArrayList.get(position).getItemAnswerIndex();
            String itemOptionIndex = contentModalArrayList.get(position).getOptionRankIndex();

            new TestReadCardView(itemView, itemId, itemIndex, itemText, itemImageUrl, itemAnswerIndex, itemOptionIndex).attachCard();

        }

        private void clearData () {
            new TestReadCardView(itemView.findViewById(R.id.mcq_img));
        }
    }

}

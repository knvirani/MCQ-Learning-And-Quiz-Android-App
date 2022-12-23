package com.fourshape.a4mcqplus.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.BundleParams;
import com.fourshape.a4mcqplus.utils.DataFetchErrorView;
import com.fourshape.a4mcqplus.utils.DataFetchErrorViewOnClickListener;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.ImageMotionHandler;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.progressindicator.CircularProgressIndicator;


public class SolutionImageFragment extends Fragment {

    private static final String TAG = "SolutionImageFragment";
    private String imageUrl, backUpImageUrl;

    private View mainView;
    private DataFetchErrorView dataFetchErrorView;
    private CircularProgressIndicator progressIndicator;
    private ImageView imageView;
    private ImageMotionHandler imageMotionHandler;

    public SolutionImageFragment() {
        // Required empty public constructor
    }

    public static SolutionImageFragment newInstance(String imageUrl, String backUpImageUrl) {
        SolutionImageFragment fragment = new SolutionImageFragment();
        Bundle args = new Bundle();
        args.putString(BundleParams.IMAGE_URL, imageUrl);
        args.putString(BundleParams.BACKUP_IMAGE_URL, backUpImageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackScreen.now(getContext(), "SolutionImageFragment");
        ActionBarTitle.TITLE = "Explained Solution";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        if (getArguments() != null) {
            imageUrl = getArguments().getString(BundleParams.IMAGE_URL);
            backUpImageUrl = getArguments().getString(BundleParams.BACKUP_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_solution_image, container, false);
        progressIndicator = mainView.findViewById(R.id.progress_circular);
        imageView = mainView.findViewById(R.id.image_view);

        if (getContext() == null)
            return mainView;

        imageMotionHandler = new ImageMotionHandler();
        dataFetchErrorView = new DataFetchErrorView(mainView.findViewById(R.id.err_mtrl_card_view), mainView);

        dataFetchErrorView.setDataFetchErrorViewOnClickListener(new DataFetchErrorViewOnClickListener() {
            @Override
            public void onClick(boolean isHidden) {

                if (!isHidden) {
                    toggleProgressIndicator(false);
                } else {
                    loadImage(imageUrl);
                }

            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imageMotionHandler.handleTouchEvents(view, motionEvent);
                return true;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadImage(imageUrl);
            }
        }, FragmentTransitionDelay.HANDLER_DELAY_SEC);

        return mainView;
    }

    private void loadImage (String imageUrl) {

        if (getContext() == null) {
            MakeLog.error(TAG, "NULL Context. So, can't load image");
            return;
        }

        if (imageView == null) {
            MakeLog.error(TAG, "NULL ImageView.");
            return;
        }

        toggleProgressIndicator(true);

        MakeLog.info(TAG, "Image Url is: " + imageUrl);

        Glide.with(getContext()).load(imageUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (dataFetchErrorView != null)
                            dataFetchErrorView.toggleErrorBar();

                    }
                });


                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        toggleProgressIndicator(false);
                    }
                });

                return false;
            }
        }).into(imageView);

    }

    private void toggleProgressIndicator (boolean isProcessing) {
        if (progressIndicator != null) {
            progressIndicator.setVisibility( isProcessing ? View.VISIBLE : View.GONE);
        }
    }
}
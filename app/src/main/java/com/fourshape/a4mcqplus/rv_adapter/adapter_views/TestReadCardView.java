package com.fourshape.a4mcqplus.rv_adapter.adapter_views;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.popups.ImageEnlargerPopupWindow;
import com.fourshape.a4mcqplus.utils.FormattedData;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.ScreenParams;
import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;

public class TestReadCardView {

    private static final String TAG = "TestReadCardView";

    private String mcqId, mcqIndex, mcqText, mcqImageUrl, mcqAnswerIndex, mcqOptionIndex;
    private View view;
    ImageView itemImageView;

    private static final int IMAGE_LOADED = 1;
    private static final int IMAGE_LOADING = 2;
    private static final int IMAGE_LOAD_FAILED = 3;

    private int currentImageStatus = 0;

    public TestReadCardView(View view, String mcqId, String mcqIndex, String mcqText, String mcqImageUrl) {
        this.view = new WeakReference<>(view).get();
        this.mcqId = mcqId;
        this.mcqIndex = mcqIndex;
        this.mcqText = mcqText;
        this.mcqImageUrl = mcqImageUrl;
    }

    public TestReadCardView (ImageView itemImageView) {
        this.itemImageView = itemImageView;
    }

    public TestReadCardView(View view, String mcqId, String mcqIndex, String mcqText, String mcqImageUrl, String mcqAnswerIndex, String mcqOptionIndex) {
        this.view = new WeakReference<>(view).get();
        this.mcqId = mcqId;
        this.mcqIndex = mcqIndex;
        this.mcqText = mcqText;
        this.mcqImageUrl = mcqImageUrl;
        this.mcqAnswerIndex = mcqAnswerIndex;
        this.mcqOptionIndex = mcqOptionIndex;
    }

    private boolean isQuestionRow () {
        return mcqAnswerIndex == null;
    }

    public void attachCard () {

        MaterialCardView materialCardView, materialImageCardView;
        TextView indexTextView, itemTextView;
        ImageView imageHandler;

        materialCardView = view.findViewById(R.id.mtrl_card_view);
        materialImageCardView = view.findViewById(R.id.mtrl_card_question_img);
        indexTextView = view.findViewById(R.id.index_text_view);
        itemTextView = view.findViewById(R.id.mcq_text);
        itemImageView = view.findViewById(R.id.mcq_img);
        imageHandler = view.findViewById(R.id.mcq_img_handler);

        int screenWidth = ScreenParams.getDisplayWidthPixels(new WeakReference<>(view.getContext()).get());
        float screenDensity = ScreenParams.getDisplayDensity(new WeakReference<>(view.getContext()).get());

        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(screenWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(layoutParams);

        if (indexTextView != null) {
            if (mcqIndex == null) {
                indexTextView.setVisibility(View.GONE);
            } else {
                indexTextView.setText(mcqIndex);
            }
        }

        if (itemTextView != null) {
            if (mcqText == null) {
                itemTextView.setVisibility(View.GONE);
            } else {
                itemTextView.setText(FormattedData.formattedContent(mcqText));
            }
        }

        int totalOccupiedSpace = 0;

        if (indexTextView != null)
            totalOccupiedSpace = (int) ((16 * 2 * screenDensity) + 32 * screenDensity);
        else {
            totalOccupiedSpace = (int) ( 16 * 2 * screenDensity);
        }

        if (materialImageCardView != null) {
            if (mcqImageUrl == null) {
                materialImageCardView.setVisibility(View.GONE);
            } else {

                int totalImageContainerCardWidth = screenWidth - totalOccupiedSpace;
                int totalImageContainerCardHeight = (int) (100 * screenDensity);

                FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(totalImageContainerCardWidth, totalImageContainerCardHeight);
                itemImageView.setLayoutParams(layoutParams1);

                materialImageCardView.setVisibility(View.VISIBLE);

                loadImage(imageHandler);

                imageHandler.setImageDrawable(view.getContext().getDrawable(R.drawable.ic_loading));

                imageHandler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MakeLog.info(TAG, "Image Handler Clicked");

                        if (currentImageStatus == IMAGE_LOAD_FAILED) {
                            loadImage(imageHandler);
                            MakeLog.error(TAG, "Current Image Loading Failed");
                        } else if (currentImageStatus == IMAGE_LOADED) {
                            new ImageEnlargerPopupWindow(view.getContext(), mcqImageUrl);
                            MakeLog.info(TAG, "Current Image Enlarged");
                        } else if (currentImageStatus == IMAGE_LOADING) {
                            MakeLog.info(TAG, "Current Image Loading");
                        }

                    }
                });

            }
        }

        materialCardView.setCardBackgroundColor(view.getContext().getColor(R.color.app_background_color));

       if (!isQuestionRow()) {
           if (mcqAnswerIndex.equals(mcqOptionIndex)) {
               materialCardView.setCardBackgroundColor(view.getContext().getColor(R.color.green_background_for_matched_option));
           } else {
               materialCardView.setCardBackgroundColor(view.getContext().getColor(R.color.app_background_color));
           }
       }


    }

    public void clearImage () {
        Glide.with(view.getContext()).clear(itemImageView);
    }

    private void loadImage (ImageView imageHandler) {

        clearImage();

        currentImageStatus = IMAGE_LOADING;
        MakeLog.info(TAG, "Image is Loading By Glide");
        Glide.with(view.getContext()).load(mcqImageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        imageHandler.setImageDrawable(view.getContext().getDrawable(R.drawable.ic_refresh));
                    }
                });

                MakeLog.info(TAG, "Image failed to load By Glide");
                currentImageStatus = IMAGE_LOAD_FAILED;

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        imageHandler.setImageDrawable(view.getContext().getDrawable(R.drawable.ic_fullscreen));
                    }
                });

                MakeLog.info(TAG, "Image is Loaded By Glide");
                currentImageStatus = IMAGE_LOADED;

                return false;
            }
        }).into(itemImageView);

    }

}

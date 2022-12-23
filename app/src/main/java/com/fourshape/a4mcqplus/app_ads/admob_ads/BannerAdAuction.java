package com.fourshape.a4mcqplus.app_ads.admob_ads;

import android.content.Context;
import android.os.Handler;
import android.widget.FrameLayout;

import com.fourshape.a4mcqplus.utils.MakeLog;

public class BannerAdAuction {

    private static final String TAG = "BannerAdAuction";

    private Context context;
    private int totalPartners;
    private AdmobAd[] admobAds;
    private int auctionInterval;
    private int partnerSearchTotalTrials;

    private Handler handler;
    private Runnable runnable;

    private BannerAdAuctionListener bannerAdAuctionListener;

    private int currentAdIndex = -1;

    private int auctionStatus = 0;

    public BannerAdAuction (Context context) {
        this.context = context;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                executeAuction();
            }
        };
    }

    public int getAuctionStatus() {
        return auctionStatus;
    }

    public void setBannerAdAuctionListener (BannerAdAuctionListener bannerAdAuctionListener) {
        this.bannerAdAuctionListener = bannerAdAuctionListener;
    }

    public void setAuctionInterval (int milliseconds) {
        this.auctionInterval = milliseconds;
    }

    public void setPlacements (String[] placements) {

        if (placements != null) {

            this.totalPartners = placements.length;
            admobAds = new AdmobAd[totalPartners];

            for (int index = 0; index < admobAds.length; index++) {

                if (placements[index] == null)
                    return;

                admobAds[index] = new AdmobAd(context, placements[index]);
            }

        } else {
            MakeLog.error(TAG, "Invalid Placements");
        }

    }

    private boolean isAllGoneCorrect() {

        if (admobAds == null)
            return false;

        for (int index = 0; index < admobAds.length; index++) {
            if (admobAds[index] == null)
                return false;
        }

        return true;

    }

    public void startAuction () {

        if (isAllGoneCorrect()) {
            auctionStatus = AuctionStatus.AUCTION_STARTED;
            currentAdIndex = -1;
            handler.postDelayed(runnable, auctionInterval);
            MakeLog.info(TAG, "BannerAd Auction Started");
        } else {
            MakeLog.error(TAG, "Something Gone Wrong");
        }

    }

    private void executeAuction () {
        loadAd();
        attachAd();
        handler.postDelayed(runnable, auctionInterval);
    }

    private void loadAd () {

        for (int index = 0; index < admobAds.length; index++) {

            if (admobAds[index].shouldLoadAd()) {
                admobAds[index].load();
                MakeLog.info(TAG, "Loading Ad on Index: " + index);
            } else {
                MakeLog.info(TAG, "Already Loaded Ad on Index: " + index);
            }

        }

    }

    private void attachAd () {

        if (currentAdIndex == -1) {
            currentAdIndex++;
            bannerAdAuctionListener.onFound(admobAds[currentAdIndex].getAdView());
            MakeLog.info(TAG, "Attached Ad on Index: " + currentAdIndex);
        } else {
            findRightPartner();
        }

    }

    private void findRightPartner () {

        partnerSearchTotalTrials++;

        currentAdIndex++;

        if (currentAdIndex >= totalPartners)
            currentAdIndex = 0;

        if (admobAds[currentAdIndex].hasValidAd()) {
            bannerAdAuctionListener.onFound(admobAds[currentAdIndex].getAdView());
            MakeLog.info(TAG, "Attached Ad on Index: " + currentAdIndex);
            partnerSearchTotalTrials = 0;
        }

    }

    public void stopAuction () {
        handler.removeCallbacks(runnable);
        auctionStatus = AuctionStatus.AUCTION_STOPPED;
        MakeLog.info(TAG, "BannerAd Auction Stopped");
    }


}

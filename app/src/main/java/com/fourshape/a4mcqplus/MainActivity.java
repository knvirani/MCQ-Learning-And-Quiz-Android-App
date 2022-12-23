package com.fourshape.a4mcqplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fourshape.a4mcqplus.app_ads.admob_ads.BannerAdAuction;
import com.fourshape.a4mcqplus.app_ads.admob_ads.MediationAdapterStatus;
import com.fourshape.a4mcqplus.app_ads.admob_ads.admob_native_ad.BottomNativeAd;
import com.fourshape.a4mcqplus.app_ads.admob_ads.interstitial_ad.ExitReadingInterstitialAd;
import com.fourshape.a4mcqplus.app_ads.admob_ads.interstitial_ad.ExitTestInterstitialAd;
import com.fourshape.a4mcqplus.firebase_analytics.TrackScreen;
import com.fourshape.a4mcqplus.fragments.LiveTestFragment;
import com.fourshape.a4mcqplus.fragments.SavedResultsListFragment;
import com.fourshape.a4mcqplus.fragments.TestReadFragment;
import com.fourshape.a4mcqplus.fragments.WelcomeFragment;
import com.fourshape.a4mcqplus.fragments.essential.AboutFragment;
import com.fourshape.a4mcqplus.fragments.essential.ContactFragment;
import com.fourshape.a4mcqplus.fragments.essential.PrivacyPolicyFragment;
import com.fourshape.a4mcqplus.ui_dialogs.AppThemeChooserDialog;
import com.fourshape.a4mcqplus.ui_dialogs.RemoveAdsNoticeDialog;
import com.fourshape.a4mcqplus.ui_dialogs.ShareAppDialog;
import com.fourshape.a4mcqplus.utils.ActionBarTitle;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.fourshape.a4mcqplus.utils.OpenExternalUrl;
import com.fourshape.a4mcqplus.utils.SharedData;
import com.fourshape.a4mcqplus.utils.VariableControls;
import com.fourshape.easythingslib.GujMCQAppsListActivity;
import com.fourshape.easythingslib.ui_popups.PopupAbout;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MaterialToolbar materialToolbar;

    private WelcomeFragment welcomeFragment;
    private ContactFragment contactFragment;
    private PrivacyPolicyFragment privacyPolicyFragment;
    private AboutFragment aboutFragment;

    private FrameLayout bannerAdLocation;
    private BannerAdAuction bannerAdAuction;

    private boolean isPolicyAccepted = false;
    private BottomNativeAd bottomNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            MakeLog.exception(e);
        }

        super.onCreate(savedInstanceState);

        SharedData sharedData = new SharedData(this);

        if (sharedData.getTheme().equals(AppThemeChooserDialog.DARK_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        isPolicyAccepted = sharedData.isPolicyAccepted();

        setContentView(R.layout.activity_main);

        materialToolbar = findViewById(R.id.materialToolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        bannerAdLocation = findViewById(R.id.banner_ad_parent);

        setNavigationView();

        if (savedInstanceState == null) {

            setBannerAds();
            welcomeFragment = new WelcomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, welcomeFragment).commit();

            /*
            bottomNativeAd = new BottomNativeAd(this, bannerAdLocation, VariableControls.shouldUseMediationForBottomNativeAd);
            bottomNativeAd.resetAdsController();
            bottomNativeAd.attachNativeAdPlaceholder();
            bottomNativeAd.startAds();

             */

            if (sharedData != null) {
                if (sharedData.getFirstTimeUseDate() == null) {
                    MakeLog.info(TAG, "A New User.");
                    sharedData.registerFirstTimeUseDate();
                } else {
                    MakeLog.info(TAG, "An Old User.");
                }
            }

        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                MakeLog.info(TAG, "onBackStackChanged");
                setActionBarTitle();
            }
        });

        try {
            MobileAds.initialize(this);
            MobileAds.setAppMuted(true);
        } catch (Exception e) {
            MakeLog.exception(e);
        }

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
                return;
            }
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fragment != null) {

            if (fragment instanceof TestReadFragment) {

                if (ExitReadingInterstitialAd.shouldShowAd()) {
                    ExitReadingInterstitialAd.showAd(this);
                } else {
                    super.onBackPressed();
                }

            } else if (fragment instanceof LiveTestFragment) {

                if (ExitTestInterstitialAd.shouldShowAd()) {
                    ExitTestInterstitialAd.showAd(this);
                } else {
                    super.onBackPressed();
                }

            } else {
                super.onBackPressed();
            }

            if (fragment instanceof ContactFragment) {
                setActionBarTitle("Contact");
            } else if (fragment instanceof PrivacyPolicyFragment) {
                setActionBarTitle("Privacy Policy");
            } else if (fragment instanceof AboutFragment) {
                setActionBarTitle("About");
            }

        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        MakeLog.info(TAG, "onStop");

        /*
        if (bottomNativeAd != null) {
            bottomNativeAd.stopAds();
            MakeLog.info(TAG, "Stopping BottomNativeAd");
        }

         */

        MediationAdapterStatus.isInitialized = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackScreen.now(this, "MainActivity");
        MakeLog.info(TAG, "onResume");

        /*
        if (isPolicyAccepted) {
            if (bottomNativeAd != null) {
                bottomNativeAd.resetAdsController();
                bottomNativeAd.startAds();
                MakeLog.info(TAG, "Restarting BottomNativeAd");
            } else {
                MakeLog.info(TAG, "Re-setting BottomNativeAd");
                bottomNativeAd = new BottomNativeAd(this, bannerAdLocation, VariableControls.shouldUseMediationForBottomNativeAd);
                bottomNativeAd.resetAdsController();
                bottomNativeAd.attachNativeAdPlaceholder();
                bottomNativeAd.startAds();
            }
        }

         */

    }

    private void setBannerAds () {
        /*
        bannerAdAuction = new BannerAdAuction(this);
        String[] placements = {PlacementIds.BANNER_AD_1, PlacementIds.BANNER_AD_2, PlacementIds.BANNER_AD_3};
        bannerAdAuction.setAuctionInterval(6000);
        bannerAdAuction.setPlacements(placements);
        bannerAdAuction.setBannerAdAuctionListener(new BannerAdAuctionListener() {
            @Override
            public void onFound(AdView adView) {
                if (bannerAdLocation != null) {
                    bannerAdLocation.removeAllViews();
                    bannerAdLocation.addView(adView);
                    MakeLog.info(TAG,"Banner Ad Attached");
                }
            }
        });
        bannerAdAuction.startAuction();

         */
    }

    public void setActionBarTitle () {

        if (materialToolbar != null) {
            materialToolbar.setTitle(ActionBarTitle.TITLE);
        }

    }

    public void setActionBarTitle (String title) {

        if (materialToolbar != null) {
            materialToolbar.setTitle(title);
        }

    }

    private void setNavigationView () {
        if (navigationView != null) {

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                    if (item.getItemId() == R.id.nav_theme) {
                        new AppThemeChooserDialog(navigationView.getContext()).show();
                    } else if (item.getItemId() == R.id.nav_share_app) {
                        new ShareAppDialog(navigationView.getContext()).show();
                    } else if (item.getItemId() == R.id.nav_more_apps) {

                        //openGooglePlay("https://play.google.com/store/apps/dev?id=5663113699693290436");
                        startActivity(new Intent(findViewById(item.getItemId()).getContext(), GujMCQAppsListActivity.class));

                    } else if (item.getItemId() == R.id.nav_rate_us) {

                        openGooglePlay("https://play.google.com/store/apps/details?id=com.fourshape.a4mcqplus");

                    } else if (item.getItemId() == R.id.nav_result_history) {

                        ActionBarTitle.TITLE = "Saved Results";

                        getSupportFragmentManager().beginTransaction().addToBackStack("saved_result_fragment").replace(R.id.fragment, new SavedResultsListFragment()).commit();

                    } else if (item.getItemId() == R.id.nav_contact) {

                        if (contactFragment == null)
                            contactFragment = new ContactFragment();

                        ActionBarTitle.TITLE = "Contact";

                        getSupportFragmentManager().beginTransaction().addToBackStack("contact_fragment").replace(R.id.fragment, contactFragment).commit();

                    } else if (item.getItemId() == R.id.nav_about) {

                        PopupAbout popupAbout = new PopupAbout(findViewById(item.getItemId()).getContext());
                        popupAbout.setAppLogo(getDrawable(R.drawable.app_logo_small));
                        popupAbout.setAppTitle(getString(R.string.app_name));
                        popupAbout.setAppSubTitleTV("For GSSSB and GPSSB Exams");
                        popupAbout.setDeveloperLine("This app, GujMCQ, is designed and developed by GujMCQ Developers.");
                        popupAbout.show();

                    } else if (item.getItemId() == R.id.nav_privacy_policy) {

                        if (privacyPolicyFragment == null)
                            privacyPolicyFragment = new PrivacyPolicyFragment();

                        ActionBarTitle.TITLE = "Privacy Policy";

                        getSupportFragmentManager().beginTransaction().addToBackStack("privacy_policy_fragment").replace(R.id.fragment, privacyPolicyFragment).commit();

                    } else if (item.getItemId() == R.id.remove_ads) {

                        new RemoveAdsNoticeDialog(navigationView.getContext());

                    } else if (item.getItemId() == R.id.nav_contact_twitter) {

                        OpenExternalUrl.open(navigationView.getContext(), VariableControls.TWITTER_URL);

                    }  else {
                        return false;
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawerLayout.closeDrawers();
                        }
                    }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);


                    return true;
                }
            });

        }

        if (drawerLayout != null) {

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, materialToolbar, R.string.openDrawer, R.string.closeDrawer) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };

            actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.app_actionbar_icon));

            drawerLayout.addDrawerListener(actionBarDrawerToggle);

            actionBarDrawerToggle.syncState();

        }

        if (materialToolbar != null) {

            materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.apps_more) {
                        startActivity(new Intent(findViewById(item.getItemId()).getContext(), GujMCQAppsListActivity.class));
                        return true;
                    }

                    return false;
                }
            });

        }

    }

    private void openGooglePlay (String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);
        } catch (Exception e){
            MakeLog.exception(e);
            Toast.makeText(this, "Can't open.", Toast.LENGTH_SHORT).show();
        }
    }

}
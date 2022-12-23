package com.fourshape.a4mcqplus.fragments.essential;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.FragmentTransitionDelay;
import com.fourshape.a4mcqplus.utils.MakeLog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;


public class ContactFragment extends Fragment {

    View mainView;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_contact, container, false);

        MaterialCardView twitterCardView, emailCardView, readPolicyCardView;

        twitterCardView = mainView.findViewById(R.id.twitter_acc_cv);
        emailCardView = mainView.findViewById(R.id.email_acc_cv);
        readPolicyCardView = mainView.findViewById(R.id.read_policy_card_view);

        twitterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String url = "https://twitter.com/GujMcqApps";

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browserIntent);
                    } catch (Exception e){
                        MakeLog.exception(e);
                        showSnackbar("Unable to open Twitter Link.");
                    }
                } catch (Exception e){
                    MakeLog.exception(e);
                    showSnackbar("Unable to open Twitter Link.");
                }
            }
        });

        emailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String policyUrl = "mailto:contact@gujmcq.in";

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browserIntent);
                    } catch (Exception e){
                        MakeLog.exception(e);
                        showSnackbar("Unable to open Email Link.");
                    }
                } catch (Exception e){
                    MakeLog.exception(e);
                    showSnackbar("Unable to open Email Link.");
                }
            }
        });

        readPolicyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("home_fragment").replace(container.getId(), new PrivacyPolicyFragment()).commit();
                        }
                    }
                }, FragmentTransitionDelay.TIME_IN_MILLI_SEC);

            }
        });

        return mainView;

    }

    private void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
package com.fourshape.a4mcqplus.fragments.essential;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourshape.a4mcqplus.R;
import com.fourshape.a4mcqplus.utils.MakeLog;


public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
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
        View mainView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView appVersionTextView;

        appVersionTextView = mainView.findViewById(R.id.app_version);

        try {
            if (getContext() != null) {
                String versionName = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
                appVersionTextView.setText("v" + versionName);
            } else {
                appVersionTextView.setText("vApp");
            }
        } catch (Exception e) {
            appVersionTextView.setText("vApp");
            MakeLog.exception(e);
        }

        return mainView;
    }
}
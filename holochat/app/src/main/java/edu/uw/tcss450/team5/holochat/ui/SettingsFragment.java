package edu.uw.tcss450.team5.holochat.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;

/**
 * Settings page of the app revolving around logged in user info
 * Main functions include
 * -viewing profile info like php, name, and email
 * -night mode
 * -changing password
 *
 * @author Kenneth Ahrens
 */
public class SettingsFragment extends Fragment {


    /**
     * required empty constructor
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
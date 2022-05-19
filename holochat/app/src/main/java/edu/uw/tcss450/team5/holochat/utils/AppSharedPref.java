package edu.uw.tcss450.team5.holochat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Shared preferences such as themes are stored here
 *
 * @author Kenneth Ahrens
 */
public class AppSharedPref {

    SharedPreferences mySharedPref;

    public AppSharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    /**
     * Method to save the Night Mode state.
     *
     * @param state the state of the theme.
     */
    public void setNightModeState(Boolean state) {
        System.out.println("Setting night mode state: " + state);
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    /**
     * Method to load the Night Mode state.
     *
     * @return the theme state.
     */
    public Boolean isNightModeState() {
        boolean state = mySharedPref.getBoolean("NightMode", false);
        System.out.println("isNightModeState pref=" + state);
        return state;
    }

    /**
     * Sets the theme of the App using AppCompact
     * based on the current shared preference
     */
    public void initializeTheme() {
        if (isNightModeState()) {
            //getActivity().getLayoutInflater().getContext().setTheme(R.style.DarkTheme);
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_YES);
        } else
            //getActivity().getLayoutInflater().getContext().setTheme(R.style.AppTheme);
            AppCompatDelegate
                    .setDefaultNightMode(
                            AppCompatDelegate
                                    .MODE_NIGHT_NO);
    }
}

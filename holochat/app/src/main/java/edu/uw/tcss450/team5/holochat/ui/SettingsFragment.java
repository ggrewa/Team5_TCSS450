package edu.uw.tcss450.team5.holochat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.auth0.android.jwt.JWT;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSettingsBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.utils.AppSharedPref;

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

    private FragmentSettingsBinding binding;
    private UserInfoViewModel mUserModel;

    /*
     * Switch to toggle night mode
     */
    private Switch mThemeSwitch;
    /*
     * Switch to toggle farenheit or celcius for weather
     */
    private Switch mDegreesSwitch;

    /**
     * shared preference storage mainly used for themes here
     */
    AppSharedPref mPref;

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
        //return inflater.inflate(R.layout.fragment_settings, container, false);
        binding = FragmentSettingsBinding.inflate(inflater);
        return binding.getRoot();
    }

    /**
     * Creates an instance of the fragment for when the user returns to it.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentSettingsBinding binding = FragmentSettingsBinding.bind(getView());

        //Set labels based on user's JWT (stored from web service)
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());
        //decode the JWT string
        binding.editEmail.setText(jwt.getClaim("email").asString());
        binding.textRealName.setText(jwt.getClaim("first").asString() + " " +
                jwt.getClaim("last").asString());
        binding.textNickname.setText(jwt.getClaim("username").asString());

        //NAVIGATION TO CHANGE PASSWORD/NICKNAME
        binding.buttonActionChangeNickname.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        SettingsFragmentDirections.actionNavigationSettingsToChangeNicknameFragment()));
        binding.buttonActionRecoverPassword.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        SettingsFragmentDirections.actionNavigationSettingsToRecoverPasswordFragment()));

        //THEMES
        mThemeSwitch = view.findViewById(R.id.settings_switch_themes);
        mPref = new AppSharedPref(getActivity());
        //set initial state of switch based on preference
        if(mPref.isNightModeState()==true) {
            mThemeSwitch.setChecked(true);
        }
        //listener to switch to check when to change theme
        mThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //only check if the switch is visible
                if(mThemeSwitch.isShown()) {
                    if (isChecked && mPref.isNightModeState() == false) {
                        toggleNightMode(true);
                    } else if (!isChecked && mPref.isNightModeState() == true){
                        toggleNightMode(false);
                    }
                }
            }

            /**
             * Sets the preference of night mode
             * Turns on the theme
             * Then refreshes the activity so the theme change is dynamic
             * @param state on/off for night mode
             */
            private void toggleNightMode(Boolean state) {
                mPref.setNightModeState(state);
                mPref.initializeTheme(); //set theme based on current preference
                refreshActivity(); //refresh to dynamically see theme
            }
        });

        //Weather switch
        mDegreesSwitch = view.findViewById(R.id.settings_degrees_switch);
    }

    /**
     * "cleanest" possible attempt to restart the activity
     */
    private void refreshActivity() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        startActivity(getActivity().getIntent());
        getActivity(). overridePendingTransition(0, 0);
    }


}
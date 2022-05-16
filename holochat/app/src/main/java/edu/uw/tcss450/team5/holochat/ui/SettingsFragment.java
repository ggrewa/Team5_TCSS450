package edu.uw.tcss450.team5.holochat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSettingsBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

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

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());
        binding.editEmail.setText(jwt.getClaim("email").asString());

        //note from ken: the JWT for name and nickname currently not working
/*        binding.textRealName.setText(jwt.getClaim("firstname").asString() + " " +
                jwt.getClaim("lastname").asString());
        binding.textNickname.setText(jwt.getClaim("nickname").asString());*/

    }


}
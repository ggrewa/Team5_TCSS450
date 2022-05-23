/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.changeCredentials;

import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdLength;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentChangeNicknameBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentRecoverPasswordBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.utils.PasswordValidator;

public class ChangeNicknameFragment extends Fragment {

    private UserInfoViewModel mUserModel;

    /** Binding view for the fragment. */
    private FragmentChangeNicknameBinding binding;

    /** View model for the fragment. */
    private ChangeNicknameViewModel mChangeNicknameModel;

    /** Validator for the Nickname field. */
    private PasswordValidator mNicknameValidator = checkPwdLength(3);

    public ChangeNicknameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangeNicknameModel = new ViewModelProvider(getActivity())
                .get(ChangeNicknameViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeNicknameBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSendCodeNickname.setOnClickListener(this::attemptSendingCode);

        binding.buttonSaveChangeNickname.setOnClickListener(this::attemptNicknameChange);
        mChangeNicknameModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    public void attemptSendingCode(final View button){
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());

        mChangeNicknameModel.connectForCode(jwt.getClaim("email").asString());
    }

    public void attemptNicknameChange(final View button){
        mNicknameValidator.processResult(
                mNicknameValidator.apply(binding.editNickname1.getText().toString().trim()),
                this::updateNickname,
                result -> binding.editNickname1.setError("Please enter a Nickname."));
    }

    public void updateNickname(){
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());

        mChangeNicknameModel.connect(jwt.getClaim("email").asString(),
                binding.editNicknameVerificationCode.getText().toString(),
                binding.editNickname1.getText().toString());
    }

    /**
     * Once registration is successful, navigate to the verification page.
     */
    private void navigateToSettings() {
        Navigation.findNavController(getView()).navigate(
                ChangeNicknameFragmentDirections.actionChangeNicknameFragmentToNavigationSettings());

        Toast.makeText(getActivity(), "Your nickname has been updated.",
                Toast.LENGTH_LONG).show();
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {

                    binding.editNicknameVerificationCode.setError("Error Authenticating: " + response.getJSONObject("data").getString("message"));

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                //navigateToSettings();
                if(response.has("message")){
                    Toast.makeText(getActivity(), "A code has been sent to your email.",
                            Toast.LENGTH_LONG).show();
                } else if(response.has("username")){
                    Toast.makeText(getActivity(), "Your nickname has been updated.",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Log.d("Change Password JSON Response", "No Response");
        }
    }
}
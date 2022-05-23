
/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.signin;

import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team5.holochat.model.PushyTokenViewModel;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.utils.PasswordValidator;

/*
 * Class for the Sign In fragment.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class SignInFragment extends Fragment {

    private PushyTokenViewModel mPushyTokenViewModel;

    /** Binding view for the fragment. */
    private FragmentSignInBinding binding;

    /** View model for the fragment. */
    private SignInViewModel mSignInModel;

    /** Validator for the email. */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /** Validator for the password. */
    private PasswordValidator mPassWordValidator = checkPwdLength(1)
            .and(checkExcludeWhiteSpace());

    /**Personal account info storage */
    private UserInfoViewModel mUserViewModel;

    /**
     * Empty constructor.
     */
    public SignInFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignInModel = new ViewModelProvider(getActivity())
                .get(SignInViewModel.class);
        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonToRegister.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        SignInFragmentDirections.actionLoginFragmentToRegisterFragment()
                ));
        binding.buttonToForgotPass.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment()
                ));

        binding.buttonSignIn.setOnClickListener(this::attemptSignIn);

        mSignInModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        binding.editEmail.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        binding.editPassword.setText(args.getPassword().equals("default") ? "" : args.getPassword());

        //don't allow sign in until pushy token retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                binding.buttonSignIn.setEnabled(!token.isEmpty()));
        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);
    }

    /**
     * Attempt and start the sign in process by validating the email.
     * @param button the button that is used to sign in
     */
    private void attemptSignIn(final View button) {
        validateEmail();
    }

    /**
     * Validate the email and then validate that the password.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * Validate the password and then verify with the server.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword.setError("Please enter a valid Password."));
    }

    /**
     * Use connect to verify entered credentials.
     */
    private void verifyAuthWithServer() {
        mSignInModel.connect(
                binding.editEmail.getText().toString(),
                binding.editPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Helper to abstract the navigation to the Activity past Authentication.
     * @param email users email
     * @param jwt the JSON Web Token supplied by the server
     */
    private void navigateToSuccess(final String email, final String jwt) {
        if (binding.switchSignin.isChecked()) {
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //Store the credentials in SharedPrefs
            prefs.edit().putString(getString(R.string.keys_prefs_jwt), jwt).apply();
        }

        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                        .actionLoginFragmentToMainActivity(email, jwt));

        getActivity().finish();
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

                    binding.editEmail.setError("Error Authenticating: " + response.getJSONObject("data").getString("message"));

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {

                    mUserViewModel = new ViewModelProvider(getActivity(),
                            new UserInfoViewModel.UserInfoViewModelFactory(
                                    binding.editEmail.getText().toString(),
                                    "",
                                    "",
                                    "",
                                    getId(),
                                    response.getString("token")
                            )).get(UserInfoViewModel.class);

                    sendPushyToken();
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * Helper to abstract the request to send the pushy token to the web service
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getJwt());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param response the Response from the server
     */
    private void observePushyPutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                binding.editEmail.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else {
                navigateToSuccess(
                        binding.editEmail.getText().toString(),
                        mUserViewModel.getJwt()
                );
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.keys_prefs_jwt))) {
            String token = prefs.getString(getString(R.string.keys_prefs_jwt), "");
            JWT jwt = new JWT(token);
            // Check to see if the web token is still valid or not. To make a JWT expire after a
            // longer or shorter time period, change the expiration time when the JWT is
            // created on the web service.
            if(!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToSuccess(email, token);
                return;
            }
        }
    }

}
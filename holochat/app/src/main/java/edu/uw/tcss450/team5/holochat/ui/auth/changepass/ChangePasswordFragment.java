/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.changepass;

import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdUpperCase;

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

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentChangePasswordBinding;
import edu.uw.tcss450.team5.holochat.utils.PasswordValidator;

/*
 * Class for the Change password fragment.
 *
 * @author Charles Bryan
 * @author Gurleen Grewal
 * @version Spring 2022
 */
public class ChangePasswordFragment extends Fragment {

    /** Binding view for the fragment. */
    private FragmentChangePasswordBinding binding;

    /** View model for the fragment. */
    private ChangePasswordViewModel mChangePasswordModel;

    /** Validator for the email field. */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /** Validator for the password field. */
    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editChangePassword2.getText().toString()))
                    .and(checkPwdLength(6))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().and(checkPwdUpperCase()));

    public ChangePasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangePasswordModel = new ViewModelProvider(getActivity())
                .get(ChangePasswordViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.imgbuttonBackToForgotPassword.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        ChangePasswordFragmentDirections.actionChangePasswordFragmentToForgotPasswordFragment()
                ));*/
        binding.buttonSavePassword.setOnClickListener(this::attemptPasswordChange);
        mChangePasswordModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Attempt and start the registration process by validating the first name.
     * @param button the button that is used to register
     */
    private void attemptPasswordChange(final View button) {
        validatePasswordsMatch();
    }

    /**
     * Validate that the passwords match and the that they meet the password requirements.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.editChangePassword2.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(binding.editChangePassword1.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editChangePassword1.setError("Passwords do not match."));
    }

    /**
     * Validate the password meets the requirements and then put a connect request to the server.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editChangePassword1.getText().toString()),
                this::verifyPasswordWithServer,
                result -> binding.editChangePassword1.setError("Please enter a valid Password."));
    }

    /**
     * Put a connect request for registration.
     */
    private void verifyPasswordWithServer() {
        ChangePasswordFragmentArgs args = ChangePasswordFragmentArgs.fromBundle(getArguments());

        mChangePasswordModel.connect(args.getEmail(), binding.editVerificationCode.getText().toString(),
                binding.editChangePassword1.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Once registration is successful, navigate to the verification page.
     */
    private void navigateToSignIn() {
        ChangePasswordFragmentDirections.ActionChangePasswordFragmentToSignInFragment directions =
                ChangePasswordFragmentDirections.actionChangePasswordFragmentToSignInFragment();
        Navigation.findNavController(getView()).navigate(directions);

        Toast.makeText(getActivity(), "Your password has been updated.",
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

                    binding.editVerificationCode.setError("Error Authenticating: " + response.getJSONObject("data").getString("message"));

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToSignIn();
            }
        } else {
            Log.d("Change Password JSON Response", "No Response");
        }
    }
}
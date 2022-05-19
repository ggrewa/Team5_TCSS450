/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.forgotpass;

import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkPwdSpecialChar;

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

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentForgotPasswordBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInFragmentArgs;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInFragmentDirections;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInViewModel;
import edu.uw.tcss450.team5.holochat.utils.PasswordValidator;

/*
 * Class for the Forgot Password fragment.
 *
 * @author Charles Bryan
 * @author Gurleen Grewal
 * @version Spring 2022
 */
public class ForgotPasswordFragment extends Fragment {

    /** Binding view for the fragment.*/
    private FragmentForgotPasswordBinding binding;

    /** View model for the fragment. */
    private ForgotPasswordViewModel mForgotPasswordModel;

    /** Validator for the email. */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    public ForgotPasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForgotPasswordModel = new ViewModelProvider(getActivity())
                .get(ForgotPasswordViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgbuttonBackToSigin.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
                ));

        binding.buttonSendEmail.setOnClickListener(this::attemptSendingMail);

        mForgotPasswordModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    /**
     * Attempt and start the change password process by validating the email.
     * @param button the button that is used to start the change password process
     */
    private void attemptSendingMail(final View button) {
        validateEmail();
    }

    /**
     * Validate the email and then validate that the password.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmailForgotPassword.getText().toString().trim()),
                this::verifyEmailWithServer,
                result -> binding.editEmailForgotPassword.setError("Please enter a valid Email address."));
    }

    /**
     * Use connect to verify entered email.
     */
    private void verifyEmailWithServer() {
        mForgotPasswordModel.connect(
                binding.editEmailForgotPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Helper to abstract the navigation to the Change Password Fragment.
     */
    private void navigateToSuccess(final String email, final String jwt) {
        ForgotPasswordFragmentDirections.ActionForgotPasswordFragmentToChangePasswordFragment directions =
                ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToChangePasswordFragment();

        directions.setEmail(email);
        directions.setJwt(jwt);

        Navigation.findNavController(getView()).navigate(directions);
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to ForgotPasswordViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {

                    binding.editEmailForgotPassword.setError("Error Authenticating: " + response.getJSONObject("data").getString("message"));

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    navigateToSuccess(binding.editEmailForgotPassword.getText().toString()
                            ,response.getString("token"));
                    //Log.e("bug", binding.editEmailForgotPassword.getText().toString());
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("Forgot Password JSON Response", "No Response");
        }
    }
}
/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.register;

import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.*;
import static edu.uw.tcss450.team5.holochat.utils.PasswordValidator.checkClientPredicate;

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

import edu.uw.tcss450.team5.holochat.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.team5.holochat.utils.PasswordValidator;

/*
 * Class for the Register fragment.
 *
 * @author Charles Bryan
 * @author Gurleen Grewal
 * @version Spring 2022
 */
public class RegisterFragment extends Fragment {

    /** Binding view for the fragment. */
    private FragmentRegisterBinding binding;

    /** View model for the fragment. */
    private RegisterViewModel mRegisterModel;

    /** Validator for the name field. */
    private PasswordValidator mNameValidator = checkPwdLength(3)
            .and(checkPwdDoNotInclude("!@#$%^&*()_-+={}[]|:;<>,.?/~`"))
            .and(checkPwdDoNotInclude("1234567890"));

    /** Validator for the Nickname field. */
    private PasswordValidator mNicknameValidator = checkPwdLength(6);

    /** Validator for the email field. */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /** Validator for the password field. */
    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editPassword2.getText().toString()))
                    .and(checkPwdLength(6))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().and(checkPwdUpperCase()));

    /**
     * Empty constructor.
     */
    public RegisterFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);
        return binding.getRoot();
    }

   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonRegister.setOnClickListener(this::attemptRegister);
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Attempt and start the registration process by validating the first name.
     * @param button the button that is used to register
     */
    private void attemptRegister(final View button) {
        validateFirst();
    }

    /**
     * Validate the first name and then validate the last name.
     */
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editFirst.getText().toString().trim()),
                this::validateLast,
                result -> binding.editFirst.setError("Please enter a first name."));
    }

    /**
     * Validate the last name and then the nickname.
     */
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(binding.editLast.getText().toString().trim()),
                this::validateNickname,
                result -> binding.editLast.setError("Please enter a last name."));
    }

    /**
     * Validate the nickname and then the email.
     */
    private void validateNickname() {
        mNicknameValidator.processResult(
                mNicknameValidator.apply(binding.editNickname.getText().toString().trim()),
                this::validateEmail,
                result -> binding.editNickname.setError("Please enter a Nickname."));
    }

    /**
     * Validate the email and then validate that the passwords entered match.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * Validate that the passwords match and the that they meet the password requirements.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.editPassword2.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(binding.editPassword1.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editPassword1.setError("Passwords do not match."));
    }

    /**
     * Validate the password meets the requirements and then put a connect request to the server.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword1.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword1.setError("Please enter a valid Password."));
    }

    /**
     * Put a connect request for registration.
     */
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                binding.editFirst.getText().toString(),
                binding.editLast.getText().toString(),
                binding.editNickname.getText().toString(),
                binding.editEmail.getText().toString(),
                binding.editPassword1.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Once registration is successful, navigate to the verification page.
     */
    private void navigateToVerification() {
       RegisterFragmentDirections.ActionRegisterFragmentToVerificationFragment directions =
                RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment();

        directions.setEmail(binding.editEmail.getText().toString());
        directions.setPassword(binding.editPassword1.getText().toString());

        Navigation.findNavController(getView()).navigate(directions);


        /*Navigation.findNavController(getView()).navigate(
                RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment()
        );*/
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
                navigateToVerification();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui.auth.verification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentVerificationBinding;
import edu.uw.tcss450.team5.holochat.ui.auth.register.RegisterFragmentDirections;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInFragmentDirections;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInViewModel;

/*
 * Class for the Verification fragment.
 *
 * @author Gurleen Grewal
 * @version Spring 2022
 */
public class VerificationFragment extends Fragment {

    /** Binding view for the fragment. */
    private FragmentVerificationBinding binding;

    /**
     * Empty constructor.
     */
    public VerificationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerificationBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VerificationFragmentDirections.ActionVerificationFragmentToSignInFragment directions =
                VerificationFragmentDirections.actionVerificationFragmentToSignInFragment();

        binding.buttonBackToSigin.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(directions));
    }
}
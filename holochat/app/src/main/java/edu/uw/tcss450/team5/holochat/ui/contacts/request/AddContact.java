package edu.uw.tcss450.team5.holochat.ui.contacts.request;

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
import edu.uw.tcss450.team5.holochat.databinding.FragmentAddContactBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * @author Tarnveer
 */

public class AddContact extends Fragment {

    FragmentAddContactBinding binding;
    private AddContactViewModel mAddContactViewModel;
    private UserInfoViewModel mModel;
    public AddContact() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAddContactViewModel = new ViewModelProvider(getActivity()).get(AddContactViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddContactBinding.inflate(inflater);
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mModel = provider.get(UserInfoViewModel.class);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding.addContactButton.setOnClickListener(button->processAdd());


    }

    private void processAdd() {
        mAddContactViewModel.connectAddContact(mModel.getEmail(), binding.newContactEmail.getText().toString(), mModel.getJwt());
        //Navigation.findNavController(getView()).navigate(AddContactDirections.actionAddContactToNavigationHome());
    }
}

package edu.uw.tcss450.team5.holochat.ui.contacts.request;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactRequestBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * Fragment that holds all friend requests the user has recieved
 * @author Tarnveer
 */
public class ContactRequestListFragment extends Fragment {
    private ContactRequestListViewModel mModel;
    FragmentContactRequestBinding binding;

    public ContactRequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactRequestListViewModel.class);

        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        Log.i("CONTACT", model.getJwt());
        mModel.connectGet(model.getJwt());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_request, container, false);
    }
    /**
     * Creates an instance of the fragment for when the user returns to it.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentContactRequestBinding binding = FragmentContactRequestBinding.bind(getView());

        mModel.addContactRequestListObserver(getViewLifecycleOwner(), contactList -> {
            //if (!contactList.isEmpty()) {
            binding.listRoot.setAdapter(
                    new ContactRequestRecyclerViewAdapter(contactList, getActivity().getSupportFragmentManager())
            );
        });
    }
}
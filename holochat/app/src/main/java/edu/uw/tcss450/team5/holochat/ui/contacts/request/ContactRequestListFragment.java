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
 * @AUTHOR KEN
 */
public class ContactRequestListFragment extends Fragment {
    private ContactRequestListViewModel mRequestInModel;
    private ContactRequestOutListViewModel mRequestOutModel;
    FragmentContactRequestBinding binding;

    public ContactRequestListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestInModel = new ViewModelProvider(getActivity()).get(ContactRequestListViewModel.class);
        mRequestOutModel = new ViewModelProvider(getActivity()).get(ContactRequestOutListViewModel.class);

        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        Log.i("CONTACT_REQUEST", model.getJwt());
        mRequestInModel.connectGet(model.getJwt());
        mRequestOutModel.connectGet(model.getJwt());
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

        mRequestInModel.addContactRequestListObserver(getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                int size = contactList.size();
                binding.textContactRequestInLabel.setText("You have " + size + " incoming request(s) [̲̅$̲̅(̲̅ ͡° ͜ʖ ͡°̲̅)̲̅$̲̅]");
            } else {
                binding.textContactRequestInLabel.setText("You have no incoming requests ( ˃̣̣̥⌓˂̣̣̥)");
            }
            binding.recyclerRequestInList.setAdapter(
                    new ContactRequestRecyclerViewAdapter(contactList, getActivity().getSupportFragmentManager())
            );
        });

        mRequestOutModel.addContactRequestListObserver(getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                int size = contactList.size();
                binding.textContactRequestOutLabel.setText("You have " + size + " outgoing request(s)! (｢•-•)｢ ʷʱʸ?");
            } else {
                binding.textContactRequestOutLabel.setText("You have no outgoing requests ¯\\_(ツ)_/¯");
            }
            binding.recyclerRequestOutList.setAdapter(
                    new ContactRequestOutRecyclerViewAdapter(contactList, getActivity().getSupportFragmentManager())
            );
        });
    }
}
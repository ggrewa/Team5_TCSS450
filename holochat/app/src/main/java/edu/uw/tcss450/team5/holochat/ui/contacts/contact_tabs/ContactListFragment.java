package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.ContactListFragmentBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.Contact;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactlistCard;

/* from
* Fragment that contains a list to view all friends user currently has added
 */
public class ContactListFragment extends Fragment {

    private ContactListViewModel mContactListViewModel;
    private ArrayList<String> mContacts;
    private ContactListFragmentBinding binding;

    public ContactListFragment() {
        // Required empty public constructor
    }
    private ContactListViewModel mViewModel;

    /*Singleton contacts list*/
    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContacts = new ArrayList<>();
        mContactListViewModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mContactListViewModel.connect(model.getEmail(), model.getmJwt());

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ContactListFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContactListViewModel.addContactListObserver(getViewLifecycleOwner(),
                contactsList -> {
                    if(!contactsList.isEmpty()) {
                        binding.contactlistCard.setAdapter(new ContactListRecyclerViewAdapter(contactsList));
                    }
                });
        //binding.buttonAddContactlist.setOnClickListener();

    }




}
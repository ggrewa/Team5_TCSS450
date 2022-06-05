package edu.uw.tcss450.team5.holochat.ui.contacts.list;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactListBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.list.ContactListRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.contacts.list.ContactListViewModel;

/**
 * Fragment that contains a list to view all friends user currently has added
 * @author Ken
 * @author Tarnveer
 * */
public class ContactListFragment extends Fragment {


    //    //The chat ID for "global" chat
//    private ChatSendViewModel mSendModel;
    private ContactListViewModel mContactListModel;
    private UserInfoViewModel mUserModel;
    private ContactListRecyclerViewAdapter mAdapter;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactListModel = provider.get(ContactListViewModel.class);
        mContactListModel.getContacts(mUserModel.getJwt());

        // mSendModel = provider.get(ChatSendViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);
        mAdapter = new ContactListRecyclerViewAdapter(
                mContactListModel.getContactListByEmail(mUserModel.getEmail()));
        binding.recyclerContacts.setAdapter(mAdapter);

        //model and message refresh
        mContactListModel.addContactObserver(mUserModel.getEmail(), getViewLifecycleOwner(),
                list -> {
                    if (!list.isEmpty()) {
                        int size = list.size();
                        binding.textContactListLabel.setText("(☞ ͡° ͜ʖ ͡°)☞ You have "+ size + " contact(s):");
                    } else {
                        binding.textContactListLabel.setText("You have no contacts （◞‸◟）");
                    }
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    binding.recyclerContacts.getAdapter().notifyDataSetChanged();
                    binding.swipeContainer.setRefreshing(false);
                });

        //refresh the list with a swipe down
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeContainer.setRefreshing(true);
                mContactListModel.getContacts(mUserModel.getJwt());
//                mAdapter = new ContactListRecyclerViewAdapter(
//                        mContactListModel.getContactListByEmail(mUserModel.getEmail()));
//                binding.recyclerContacts.getAdapter().notifyDataSetChanged();
            }
        });

    }
}
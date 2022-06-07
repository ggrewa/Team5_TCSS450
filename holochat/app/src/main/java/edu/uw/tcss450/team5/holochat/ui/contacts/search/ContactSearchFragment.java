package edu.uw.tcss450.team5.holochat.ui.contacts.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import edu.uw.tcss450.team5.holochat.databinding.FragmentContactSearchBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactTabFragmentDirections;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactViewModel;

/**
 * Fragment that hold tools to search for a user of the app and send a friend request
 * @author Ken
 */
public class ContactSearchFragment extends Fragment {
    private UserInfoViewModel mUserModel;
    private ContactViewModel mContactViewModel;
    private ArrayList<String> mContacts;
    private FragmentContactSearchBinding binding;
    private AllMemberListViewModel mModel;
    private SearchViewModel mSearchModel;

    private boolean searchCheck = false;

    public ContactSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContacts = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContactSearchBinding.inflate(inflater);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelProvider provider= new ViewModelProvider(getActivity());
        //contact view models
        mContactViewModel = provider.get(ContactViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

        //contact view model of people that you can add
        binding.swipeContainer.setRefreshing(true);
        mContactViewModel.connect(mUserModel.getEmail(), mUserModel.getJwt());
        mContactViewModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeContacts);

        //model of members
        setViewAllMembers();

        //search bar, searching view model
        mSearchModel = provider.get(SearchViewModel.class);
        mSearchModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeContacts);
        binding.buttonFindContact.setOnClickListener(this::attemptToFind);

        //refresh, brings search back to default state
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetSearch();
            }
        });
        binding.buttonRefreshSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSearch();
            }
        });

    }

    /**
     * Initializes member model and recycler adapter
     */
    private void setViewAllMembers() {
        //model of members
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mModel = provider.get(AllMemberListViewModel.class);
        mModel.connectGet(mUserModel.getJwt(), mUserModel.getMemberID());
        mModel.addContactRequestListObserver(getViewLifecycleOwner(), contactList -> {
            binding.recyclerSearchList.setAdapter(
                    new AllMemberRecyclerViewAdapter(contactList, getActivity().getSupportFragmentManager())
            );
            binding.contactNames.setText("All Non-Contact-Members: x" + contactList.size() + " ( ͡° ᴥ ͡°)");
            binding.swipeContainer.setRefreshing(false);
        });
    }

    /**
     * Get the search bar input, parse it and send to webservice
     * On callback have it navigate to the ContactFound fragment
     *
     * @param view
     */
    private void attemptToFind(View view) {
        //take the current input and parse it
        String input = String.valueOf(binding.connectionsSearchEditText.getText()).replaceAll("\\s", "");;
        Log.i("CONTACT_SEARCH_FRAG", "input:" + input);
        System.out.println("user tried to find: " + input);

        //attempt connection to webservice to find
        mSearchModel.connectGet(mUserModel.getJwt(), input);
        //set an observer and replace the adapter after user tries to search
        mSearchModel.addContactSearchListObserver(getViewLifecycleOwner(), contactList -> {
            binding.recyclerSearchList.setAdapter(
                    new AllMemberRecyclerViewAdapter(contactList, getActivity().getSupportFragmentManager())
            );

            if(contactList.isEmpty()) binding.contactNames.setText("No results... ¯\\_(ツ)_/¯");
            else binding.contactNames.setText("Results found: x" + contactList.size() + " ᕙ(▀̿ĺ̯▀̿ ̿)ᕗ");
            binding.swipeContainer.setRefreshing(false);
        });

    }

    /**
     * Reinitialzies the viewmodel and adapter. Returns fragment to default state.
     */
    private void resetSearch() {
        binding.connectionsSearchEditText.clearFocus();
        binding.connectionsSearchEditText.getText().clear();
        binding.swipeContainer.setRefreshing(true);
        setViewAllMembers();
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SearchViewModel
     *
     * @param response the Response from the server
     */
    private void observeSearchResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                binding.connectionsSearchEditText.setError(
                        "Can't find this user.");
            } else { //something in the response no
                try {
                    String email = response.getString("searchemail");
                    String fullName = response.getString("firstname") + " " + response.getString("lastname");;
                    String username = response.getString("username");
                    int memberID = response.getInt("memberid");
                    //navigateToUserFound(email,username,memberID,fullName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("something went wrong in the user search response");
                }
                //navigateToUserFound("kenahren@gmail.com","Kenpai",26);
            }
        }
    }

    /**
     * Successfully searched now navigate to the user fragment
     */
    private void navigateToUserFound(String email, String username, int memberid, String fullName) {
        Navigation.findNavController(getView())
                .navigate(ContactTabFragmentDirections.
                        actionNavigationTabbedContactsToContactFoundFragment(
                                email,username, memberid, fullName));
    }

    private void observeContacts(JSONObject response) {
        Log.i("user", response.toString());
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.contactNames.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                try {
                    getContacts(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void getContacts(JSONObject response) throws JSONException {
        Log.i("contacts", response.toString());
        ArrayList<String> contacts = new ArrayList<String>();

        JSONArray jsonUsers = response.getJSONArray("values");
        for(int i = 0; i < jsonUsers.length(); i++) {
            JSONObject user = (new JSONObject(jsonUsers.getString(i)));
            contacts.add(user.getString("username"));
        }

        StringBuilder builder = new StringBuilder();
        for(int j = 0; j < contacts.size(); j++) {
            mContacts.add(contacts.get(j));
            builder.append(contacts.get(j) + "\n\n");
        }
        Log.i("contacts", builder.toString());
        binding.contactNames.setText(builder.toString());
    }
}

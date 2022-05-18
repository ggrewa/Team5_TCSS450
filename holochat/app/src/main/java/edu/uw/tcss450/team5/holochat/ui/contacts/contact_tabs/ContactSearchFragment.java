package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

/**
 * Fragment that hold tools to search for a user of the app and send a friend request
 */
public class ContactSearchFragment extends Fragment {
    private UserInfoViewModel mUserModel;
    private ContactViewModel mContactViewModel;
    private ArrayList<String> mContacts;
    private FragmentContactSearchBinding binding;



    public ContactSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContacts = new ArrayList<>();
        ViewModelProvider provider= new ViewModelProvider(getActivity());
        mContactViewModel = provider.get(ContactViewModel.class);
        mUserModel = provider.get(UserInfoViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contact_search, container, false);
        binding = FragmentContactSearchBinding.inflate(inflater);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactViewModel.connect(mUserModel.getEmail(), mUserModel.getmJwt());
        mContactViewModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeContacts);
        binding.buttonAddContact.setOnClickListener(this::attemptToAdd);
        binding.buttonDeleteContact.setOnClickListener(this::attemptToDelete);

    }

    private void attemptToAdd(View view) {
    }

    private void attemptToDelete(View view) {
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
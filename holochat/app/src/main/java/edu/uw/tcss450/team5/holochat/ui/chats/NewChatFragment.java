package edu.uw.tcss450.team5.holochat.ui.chats;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentNewChatBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.HomeFragmentDirections;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListSingle;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListViewModel;

/**
* Fragment to create a new chatroom
 *
 * @author Ken
 */
public class NewChatFragment extends Fragment {

    private ContactListViewModel mContactListModel;
    private NewChatRecyclerViewAdapter mAdapter;
    private NewChatViewModel mChatModel;
    private UserInfoViewModel mUserInfoModel;
    private FragmentNewChatBinding binding;
    private int mNewChatID;

    public NewChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(NewChatViewModel.class);
        mUserInfoModel = provider.get(UserInfoViewModel.class);
        mContactListModel = provider.get(ContactListViewModel.class);
        mContactListModel.getContacts(mUserInfoModel.getJwt());

        Log.i("MAKECHAT", "Prompted to try to make a new chatroom");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding = FragmentNewChatBinding.bind(getView());

        final RecyclerView rv = binding.recyclerMakeChatContacts;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
/*        ContactListRecyclerViewAdapter adap = new ContactListRecyclerViewAdapter(
                mContactListModel.getContactListByEmail(mUserInfoModel.getEmail()));*/

        List<ContactListSingle> listOfContacts = mContactListModel.getContactListByEmail(mUserInfoModel.getEmail());
        if(listOfContacts.isEmpty()) { //put in the contacts if it was empty

        }
        NewChatRecyclerViewAdapter adap = new NewChatRecyclerViewAdapter(listOfContacts);
        rv.setAdapter(adap);


/*        mContactModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            mAdapter = new NewChatRecyclerViewAdapter(contactList);
            binding.listRoot.setAdapter(mAdapter);
        });*/

        mChatModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
        binding.editChatName.setTextColor(Color.BLACK);


        binding.buttonCancel.setOnClickListener(button -> Navigation.findNavController(getView()).
                navigate(NewChatFragmentDirections.actionNewChatFragmentToNavigationMessages()));

        binding.buttonCreate.setOnClickListener(button -> handleCreateChatRoom());
    }

    /**
     * HAndles creating a chat room
     */
    private void handleCreateChatRoom(){
        String name = binding.editChatName.getText().toString().trim();
        if(name.length() < 1){
            binding.editChatName.setError("Please enter a valid chat room name");
        }else{
            mChatModel.createChat(mUserInfoModel.getJwt(), name);
        }
    }

    /**
     * HAndles adding a contact to the chat room
     * @throws JSONException
     */
    private void handleAddContacts() throws JSONException {
        ArrayList<Integer> selectedContacts = mAdapter.getSelectedList();
        selectedContacts.add(mUserInfoModel.getMemberID());
        int[] temp = new int[selectedContacts.size()];

        for(int i = 0 ; i < selectedContacts.size(); i++){
            temp[i] = selectedContacts.get(i);
        }

        mChatModel.putMembers(mUserInfoModel.getJwt(), temp, mNewChatID);
        mAdapter.notifyDataSetChanged();
        Navigation.findNavController(getView())
                .navigate(NewChatFragmentDirections.actionNewChatFragmentToNavigationMessages());
    }

    /**
     * Observes the response from the server.
     *
     * @param response the response.
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                Log.e("CHAT", "Failed to create chat room");
            } else {
                try {
                    if(response.has("chatID")){
                        System.out.println("Created a chat room");
                        mNewChatID = response.getInt("chatID");
                        System.out.println("moving on to populate chat room");
                        handleAddContacts();
                    }
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
package edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactListSingle;
import edu.uw.tcss450.team5.holochat.ui.contacts.list.ContactListViewModel;

/**
* Fragment to create a new chatroom
 *
 * @author Ken
 */
public class NewChatFragment extends Fragment {

    private ContactListViewModel mContactListModel;
    private NewChatRecyclerViewAdapter mChatAdapter;
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

        //SetRefreshing shows the internal Swiper view progress bar. Show this until contactlist show
        binding.swipeContainer.setRefreshing(true);

        //initialize recycler view
        final RecyclerView rv = binding.recyclerMakeChatContacts;
        List<ContactListSingle> listOfContacts = mContactListModel.getContactListByEmail(mUserInfoModel.getEmail());
        if(listOfContacts.isEmpty()) { //fill contacts list if it was empty
            mContactListModel.getContacts(mUserInfoModel.getJwt());
            listOfContacts = mContactListModel.getContactListByEmail(mUserInfoModel.getEmail());
        }
        mChatAdapter = new NewChatRecyclerViewAdapter(listOfContacts);
        rv.setAdapter(mChatAdapter);

        //observer that resets the recycler view if the dataset changed
        mContactListModel.addContactObserver(mUserInfoModel.getEmail(), getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    binding.swipeContainer.setRefreshing(false);

                });

        mChatModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);

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
        ArrayList<Integer> selectedContacts = mChatAdapter.getSelectedList();
        selectedContacts.add(mUserInfoModel.getMemberID());
        int[] temp = new int[selectedContacts.size()];

        for(int i = 0 ; i < selectedContacts.size(); i++){
            temp[i] = selectedContacts.get(i);
        }

        mChatModel.putMembers(mUserInfoModel.getJwt(), temp, mNewChatID);
        mChatAdapter.notifyDataSetChanged();
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
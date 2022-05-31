package edu.uw.tcss450.team5.holochat.ui.chats.chatroom;

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

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentChatMembersBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom.NewChatRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom.NewChatViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListSingle;
import edu.uw.tcss450.team5.holochat.ui.contacts.ContactListViewModel;

/**
 * View all members in a chatroom
 *
 * @author Ken
 */
public class ChatMembersFragment extends Fragment {


    private ChatMembersViewModel mChatMembersModel;
    private NewChatRecyclerViewAdapter mChatAdapter;
    private NewChatViewModel mChatModel;
    private UserInfoViewModel mUserInfoModel;
    private FragmentChatMembersBinding binding;
    private int mChatID;
    private String mTitle;

    public ChatMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatMembersFragmentArgs args = ChatMembersFragmentArgs.fromBundle(getArguments());
        mChatID = args.getChatID();
        mTitle = args.getName();

        ((MainActivity) getActivity())
                .setTitle("Members in " + mTitle);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChatModel = provider.get(NewChatViewModel.class);
        mUserInfoModel = provider.get(UserInfoViewModel.class);
        mChatMembersModel = provider.get(ChatMembersViewModel.class);
        mChatMembersModel.getMembers(mUserInfoModel.getJwt(), mChatID);

        Log.i("ADDCHAT", "Prompted to try to add members to chatroom");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        binding = FragmentChatMembersBinding.bind(getView());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until contactlist show
        binding.swipeContainer.setRefreshing(true);

        //initialize recycler view
        final RecyclerView rv = binding.recyclerAddChatContacts;
        List<ContactListSingle> listOfContacts = mChatMembersModel.getContactListByChatID(mChatID);
        if(listOfContacts.isEmpty()) { //fill contacts list if it was empty
            mChatMembersModel.getMembers(mUserInfoModel.getJwt(), mChatID);
            listOfContacts = mChatMembersModel.getContactListByChatID(mChatID);
        }
        mChatAdapter = new NewChatRecyclerViewAdapter(listOfContacts);
        rv.setAdapter(mChatAdapter);

        //observer that resets the recycler view if the dataset changed
        mChatMembersModel.addMemberObserver(mChatID, getViewLifecycleOwner(),
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
    }

    /**
     * Handles adding a contact to the chat room
     * @throws JSONException
     */
    private void handleAddContacts() throws JSONException {
        ArrayList<Integer> selectedContacts = mChatAdapter.getSelectedList();
        selectedContacts.add(mUserInfoModel.getMemberID());
        int[] temp = new int[selectedContacts.size()];

        for(int i = 0 ; i < selectedContacts.size(); i++){
            temp[i] = selectedContacts.get(i);
        }

        mChatModel.putMembers(mUserInfoModel.getJwt(), temp, mChatID);
        mChatAdapter.notifyDataSetChanged();
        Navigation.findNavController(getView())
                .navigate(AddContactToChatFragmentDirections.
                        actionAddContactToChatFragmentToChatRoomFragment(mChatID, mTitle));
    }

    /**
     * Observes the response from the server.
     *
     * @param response the response.
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                Log.e("CHAT", "Failed to add users to chat room");
            } else {
                try {
                    if(response.has("chatID")){
                        System.out.println("Created a chat room");
                        mChatID = response.getInt("chatID");
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
package edu.uw.tcss450.team5.holochat.ui.chats.chatroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentChatRoomBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {
    private ChatSendViewModel mSendModel;
    //The chat ID for "global" chat
    private ChatRoomFragmentArgs mArgs;
    private int mChatId;
    private String mName;
    private ChatViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = ChatRoomFragmentArgs.fromBundle(getArguments());
        mChatId = mArgs.getChatID();
        mName = mArgs.getName();

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatViewModel.class);
        mChatModel.getFirstMessages(mChatId, mUserModel.getJwt());
        mSendModel = provider.get(ChatSendViewModel.class);
    }

    /**
     * Create custom toolbar with chatroom features such as adding and viewing people
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Override actionbar option menu for use with chatrooms
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_add_chat_contact:
                Log.d("CHAT", "Clicked to add a contact");
                navigateAddToChat();
                return true;
            case R.id.action_view_chat_contacts:
                Log.d("CHAT", "Clicked to view chat contacts");
                navigateViewChatMembers();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Navigate to add to chat
     */
    private void navigateAddToChat() {
        Navigation.findNavController(getView())
                .navigate(ChatRoomFragmentDirections.actionChatRoomFragmentToAddContactToChatFragment(mChatId, mName));
    }

    /**
     * Navigate to add to view chat members
     */
    private void navigateViewChatMembers() {
        Navigation.findNavController(getView())
                .navigate(ChatRoomFragmentDirections.actionChatRoomFragmentToViewChatMembersFragment(mChatId, mName));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentChatRoomBinding binding = FragmentChatRoomBinding.bind(getView());

        //set chatroom title in the actionbar
        ((MainActivity)getActivity()).setTitle(mName);

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerMessages;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        rv.setAdapter(new ChatRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(mChatId),
                mUserModel.getEmail()));


        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(mChatId, mUserModel.getJwt());
        });

        mChatModel.addMessageObserver(mChatId, getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });
        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatId,
                    mUserModel.getJwt(),
                    binding.editMessage.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));
    }
}

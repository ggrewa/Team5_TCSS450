package edu.uw.tcss450.team5.holochat.ui.chats.chatroom_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentMessageListBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.new_chatroom.MessageListViewModel;

/**
 * Fragment contains a list of chatrooms that the user is in
 *
 * @author Ken
 */
public class MessageListFragment extends Fragment {

    private MessageListViewModel mMessagesModel;
    private UserInfoViewModel mUserInfoModel;
    private MessageListRecyclerViewAdapter mAdapter;

    public MessageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mMessagesModel = provider.get(MessageListViewModel.class);
        mUserInfoModel = provider.get(UserInfoViewModel.class);

        mMessagesModel.connectGet(mUserInfoModel.getJwt());


    }


    /**
     * Inflates the container for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_list, container, false);
    }

    /**
     * Creates an instance of the fragment for when the user returns to it.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentMessageListBinding binding = FragmentMessageListBinding.bind(getView());

        //FAB navigate to make a new chatroom
        binding.buttonAddChat.setOnClickListener(button -> Navigation.findNavController(getView())
                .navigate(MessageListFragmentDirections.actionNavigationMessagesToNewChatFragment()));

        updateMessagesModel();

        //refresh the list with a swipe down
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMessagesModel();
            }
        });
    }

    private void updateMessagesModel() {
        FragmentMessageListBinding binding = FragmentMessageListBinding.bind(getView());
        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);
        //may need to change this to be similar to chat fragment on view created.
        //add observer for getting messages
        mMessagesModel.addMessageListObserver(getViewLifecycleOwner(), messageList -> {
            mAdapter = new MessageListRecyclerViewAdapter(messageList, getActivity().getSupportFragmentManager());
            binding.listRoot.setAdapter(mAdapter);
            if(messageList.isEmpty()) binding.textMessageListLabel.setText("┏(-_-)┛ Error 404: No chatrooms found ");
            else binding.textMessageListLabel.setText("You are in " + messageList.size() + " chatrooms (͠≖ ͜ʖ͠≖)");

            binding.swipeContainer.setRefreshing(false);
        });
    }

}

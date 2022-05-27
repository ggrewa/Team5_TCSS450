package edu.uw.tcss450.team5.holochat.ui.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentMessageListBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * Fragment contains a list of chatrooms that the user is in
 *
 * @author Ken
 */
public class MessageListFragment extends Fragment {

    private MessageListViewModel mModel;
    private MessageListRecyclerViewAdapter mAdapter;

    public MessageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new ViewModelProvider(getActivity()).get(MessageListViewModel.class);
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mModel.connectGet(model.getJwt());


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

        //may need to change this to be similar to chat fragment on view created.
        //add observer for getting messages
        mModel.addMessageListObserver(getViewLifecycleOwner(), messageList -> {
            mAdapter = new MessageListRecyclerViewAdapter(messageList, getActivity().getSupportFragmentManager());
            binding.listRoot.setAdapter(mAdapter);
        });
    }

}

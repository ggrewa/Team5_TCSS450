package edu.uw.tcss450.team5.holochat.ui.chats;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentMessageListBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.HomeFragmentDirections;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageListFragment extends Fragment {

    private MessageListViewModel mModel;
    private MessagesRecyclerViewAdapter mAdapter;

    public MessageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //FAB naviagate to make a new chatroom
        binding.buttonAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMakeChat(view);
            }
        });

    }

    private void navigateToMakeChat(View view){
        //note this needs to be passed with user info
        @NonNull NavDirections directions = MessageListFragmentDirections.actionNavigationMessagesToNewChatFragment();
        Navigation.findNavController(view).navigate(directions);
    }
}

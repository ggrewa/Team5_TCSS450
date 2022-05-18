package edu.uw.tcss450.team5.holochat.ui.chats;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentRecentsBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentSignInBinding;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInFragmentArgs;
import edu.uw.tcss450.team5.holochat.ui.auth.signin.SignInFragmentDirections;
public class RecentsFragment extends Fragment {

    private FragmentRecentsBinding binding;

    //title, description
    String s1[], s2[];
    //picture for chatroom, can implement later if desired.
    int icons[] = {R.drawable.ic_chat_black_24dp,R.drawable.ic_chat_black_24dp,R.drawable.ic_chat_black_24dp};
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}
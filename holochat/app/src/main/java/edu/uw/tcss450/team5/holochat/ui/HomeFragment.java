/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.jwt.JWT;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentHomeBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.chats.MyAdapter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserInfoViewModel mUserModel;
    //title, description
    String s1[], s2[];
    //picture for chatroom, can implement later if desired.
    int icons[] = {R.drawable.ic_chat_black_24dp,R.drawable.ic_chat_black_24dp,R.drawable.ic_chat_black_24dp};
    RecyclerView recyclerView;


    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        s1=getResources().getStringArray(R.array.chat_titles);
        s2=getResources().getStringArray(R.array.chat_preview);
        MyAdapter adapter = new MyAdapter(view.getContext(),s1,s2,icons);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //welcome the user
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        JWT jwt = new JWT( mUserModel.getJwt());
        binding.textHomeWelcomeUser.setText("Welcome, " + jwt.getClaim("first").asString() + " " +
                jwt.getClaim("last").asString() + "!");

        binding.homeWeatherTemp.setText("its cold!");


    }



}


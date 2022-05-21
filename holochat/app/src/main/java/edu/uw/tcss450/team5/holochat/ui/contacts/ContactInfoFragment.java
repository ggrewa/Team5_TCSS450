package edu.uw.tcss450.team5.holochat.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactInfoBinding;

/**
 * Fragment that shows given Contact's username and email, also can start a new chat with given
 * contact in this fragment
 */
public class ContactInfoFragment extends Fragment {

    private FragmentContactInfoBinding mBinding;
    private String mUserName;
    private String mContactEmail;

    public ContactInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContactInfoFragmentArgs args = ContactInfoFragmentArgs.fromBundle(getArguments());
        mContactEmail = args.getContactEmail();
        mUserName = args.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }
    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Use a Lamda expression to add the OnClickListener
        mBinding.startChatButton.setOnClickListener(button -> startChat());
        TextView nameView = (TextView)mBinding.contactName;
        TextView emailView = (TextView)mBinding.contactEmail;
        nameView.setText(mUserName);
        emailView.setText(mContactEmail);

    }

    /**
     * startChat called when wanting to start a new chat with current contact
     *
     * Empty for now - still need to work on it
     */
    public void startChat() {

    }
}
package edu.uw.tcss450.team5.holochat.ui.contacts.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactFoundBinding;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactInfoBinding;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.Contact;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactInfoFragmentArgs;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.ContactViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.request.DeleteContactDialog;

/**
 * Fragment that shows given Contact's username and email, also can start a new chat with given
 * contact in this fragment
 */
public class ContactFoundFragment extends Fragment {

    private @NonNull FragmentContactFoundBinding mBinding;

    private String mContactEmail;
    private String mContactUsername;
    private int mMemberID;

    public ContactFoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        ContactFoundFragmentArgs args = ContactFoundFragmentArgs.fromBundle(getArguments());
        mMemberID = args.getMemberid();
        mContactEmail = args.getEmail();
        mContactUsername = args.getUsername();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactFoundBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactFoundBinding binding = FragmentContactFoundBinding.bind(getView());

        //Use a Lamda expression to add the OnClickListener
        TextView nickNameView = (TextView) mBinding.contactNickname;
        TextView emailView = (TextView) mBinding.contactEmail;
        TextView realName = mBinding.contactRealName;
        realName.setText(mContactUsername);
        nickNameView.setText(mContactUsername);
        emailView.setText(mContactEmail);

        //set title to username
        ((MainActivity) getActivity()).setTitle("Found " + mContactEmail);

        mBinding.buttonActionAddContact.setOnClickListener(button -> handleAddContact());
    }

    private void handleAddContact() {
        Log.i("ADD_SEARCH", "ADDING A USER FROM A SEARCH");
        openDialog();
    }

    /**
     * navigates to a contacts profile to add
     */
    private void openDialog() {

    }
}
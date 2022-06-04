package edu.uw.tcss450.team5.holochat.ui.contacts.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.team5.holochat.MainActivity;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactInfoBinding;
import edu.uw.tcss450.team5.holochat.ui.dialog.DeleteContactRequestDialog;

/**
 * Fragment that shows given Contact's username and email, also can start a new chat with given
 * contact in this fragment
 */
public class ContactInfoFragment extends Fragment {

    private FragmentContactInfoBinding mBinding;
    private String mUserName;
    private String mContactEmail;
    private String mRealName;
    private int mContactID;
    private FragmentManager mFragmMan;

    public ContactInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContactInfoFragmentArgs args = ContactInfoFragmentArgs.fromBundle(getArguments());
        mContactEmail = args.getContactEmail();
        mUserName = args.getContactUsername();
        mRealName = args.getContactRealName();
        mContactID = args.getContactMemberID();
        mFragmMan = getActivity().getSupportFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Use a Lamda expression to add the OnClickListener
        TextView nickNameView = (TextView) mBinding.contactNickname;
        TextView emailView = (TextView) mBinding.contactEmail;
        TextView realName = mBinding.contactRealName;
        realName.setText(mRealName);
        nickNameView.setText(mUserName);
        emailView.setText(mContactEmail);

        //set title to username
        ((MainActivity) getActivity()).setTitle("Contact: " + mUserName);

        mBinding.buttonActionDeleteContact.setOnClickListener(button -> handleDeleteContact());

    }

    private void handleDeleteContact() {
        openDialog();
    }

    /**
     * navigates to a contacts profile.
     */
    private void openDialog() {
        DeleteContactRequestDialog dialog = new DeleteContactRequestDialog(mUserName,
                mContactID);
        dialog.show(mFragmMan, "maybe?");
    }
}
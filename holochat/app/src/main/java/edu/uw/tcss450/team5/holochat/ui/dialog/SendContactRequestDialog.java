package edu.uw.tcss450.team5.holochat.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.search.AllMemberListViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.search.AllMemberRecyclerViewAdapter;

/**
 * A Dialog for sending a friend request
 *
 * @author Ken
 */
public class SendContactRequestDialog extends DialogFragment {

    private final String mContactName;
    private final int mMemberID;
    private UserInfoViewModel mUserModel;
    private AllMemberListViewModel mContactRequestModel;
    private final AllMemberRecyclerViewAdapter.ContactRequestViewHolder mUpdater;

    /**
     * Constructor for the accept dialog
     *  @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     * @param testing
     */
    public SendContactRequestDialog(String name, int memberID,
                                    AllMemberRecyclerViewAdapter.ContactRequestViewHolder testing){

        this.mContactName = name;
        this.mMemberID = memberID;
        mUpdater = testing;
    }

    /**
     * Constructor for the send dialog
     *  @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     */
    public SendContactRequestDialog(String name, int memberID) {

        this.mContactName = name;
        this.mMemberID = memberID;
        mUpdater = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mContactRequestModel = new ViewModelProvider(getActivity())
                .get(AllMemberListViewModel.class);
    }

    /**
     * The view created for send dialog
     *
     * @param view the view
     * @param savedInstanceState the saved instance state.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactRequestModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_send_friend_request_dialog, null);
        TextView name = (TextView)view.findViewById(R.id.textContactName);
        name.setText(mContactName);
        builder.setView(view)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mContactRequestModel.postFriendRequest(mUserModel.getJwt(), mMemberID);
                        if(mUpdater != null) mUpdater.deleteRequest();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                System.out.println("Failed to send contact");
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}

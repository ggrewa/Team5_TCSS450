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
import edu.uw.tcss450.team5.holochat.ui.contacts.request.ContactRequestListViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.request.ContactRequestOutRecyclerViewAdapter;

/**
 * A Dialog for canceling your own outgoing friend request
 *
 * @author Tarnveer
 * @author Ken
 */
public class CancelRequestContactDialog extends DialogFragment {

    private final String mContactName;
    private final int mMemberID;
    private UserInfoViewModel mUserModel;
    private ContactRequestListViewModel mContactRequestModel;
    private final ContactRequestOutRecyclerViewAdapter.ContactRequestViewHolder mUpdater;

    /**
     * Constructor for the accept dialog
     *
     * @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     */
    public CancelRequestContactDialog(String name, int memberID,
                                      ContactRequestOutRecyclerViewAdapter.ContactRequestViewHolder testing){

        this.mContactName = name;
        this.mMemberID = memberID;
        mUpdater = testing;
    }

    /**
     * Constructor for the accept dialog
     *
     * @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     */
    public CancelRequestContactDialog(String name, int memberID) {
        this.mContactName = name;
        this.mMemberID = memberID;
        this.mUpdater = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mContactRequestModel = new ViewModelProvider(getActivity())
                .get(ContactRequestListViewModel.class);
    }

    /**
     * The view created for cancel dialog
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
        View view = inflater.inflate(R.layout.fragment_delete_contact_dialog, null);
        TextView name = (TextView)view.findViewById(R.id.textContactName);
        name.setText("Cancel your friend request to " + mContactName );
        builder.setView(view)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mContactRequestModel.deleteContact(mUserModel.getJwt(), mMemberID);
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
                System.out.println("Failed to cancel contact");
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}

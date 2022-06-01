package edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;

/**
 * A Dialog for accepting a request.
 *
 * @author Tarnveer
 */
public class AcceptContactDialog extends DialogFragment {

    private final String mContactName;
    private final int mMemberID;
    private UserInfoViewModel mUserModel;
    private ContactRequestListViewModel mContactRequestModel;
    private final ContactRequestRecyclerViewAdapter.ContactRequestViewHolder mUpdater;


    /**
     * Constructor for the accept dialog
     *
     * @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     */
    public AcceptContactDialog(String name, int memberID,
                               ContactRequestRecyclerViewAdapter.ContactRequestViewHolder testing){

        this.mContactName = name;
        this.mMemberID = memberID;
        mUpdater = testing;
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
     * The view created for  Accept dialog.
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
        View view = inflater.inflate(R.layout.fragment_accept_contact_dialog, null);
        TextView name = (TextView)view.findViewById(R.id.textContactName);
        name.setText(mContactName);
        builder.setView(view)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mContactRequestModel.sendVerify(mUserModel.getJwt(), mMemberID);
                        mUpdater.deleteRequest();
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
                System.out.println("Failed to add contact");
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}

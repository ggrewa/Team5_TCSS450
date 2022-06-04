package edu.uw.tcss450.team5.holochat.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONObject;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.model.UserInfoViewModel;
import edu.uw.tcss450.team5.holochat.ui.contacts.list.ContactListRecyclerViewAdapter;
import edu.uw.tcss450.team5.holochat.ui.contacts.list.ContactListViewModel;

/**
 * A Dialog to delete a contact
 *
 * @author Ken
 */
public class DeleteContactRequestDialog extends DialogFragment {

    private final String mContactName;
    private final int mMemberID;
    private UserInfoViewModel mUserModel;
    private ContactListViewModel mContactListModel;

    /**
     * Constructor for the accept dialog
     *
     * @param name A String representing a contacts name
     * @param memberID an integer representing the contact ID
     */
    public DeleteContactRequestDialog(String name, int memberID){

        this.mContactName = name;
        this.mMemberID = memberID;
        //mUpdater = testing;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactListModel = provider.get(ContactListViewModel.class);
        mContactListModel.getContacts(mUserModel.getJwt());
    }

    /**
     * The view created for delete dialog.
     *
     * @param view the view
     * @param savedInstanceState the saved instance state.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mContactRequestModel.addResponseObserver(getViewLifecycleOwner(),
//                this::observeResponse);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_delete_contact_dialog, null);
        TextView name = (TextView)view.findViewById(R.id.textContactName);
        name.setText(mContactName);
        builder.setView(view)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mContactListModel.deleteContact(mUserModel.getJwt(), mMemberID);
                        //temp fix to refresh the contacts after a delete
                        restartActivity();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    private void restartActivity() {
        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
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
                System.out.println("Failed to delete contact");
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}

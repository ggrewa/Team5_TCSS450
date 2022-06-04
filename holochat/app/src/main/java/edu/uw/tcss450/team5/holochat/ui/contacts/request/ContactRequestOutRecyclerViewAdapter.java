package edu.uw.tcss450.team5.holochat.ui.contacts.request;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactOutgoingRequestCardBinding;
import edu.uw.tcss450.team5.holochat.ui.contacts.info.Contact;
import edu.uw.tcss450.team5.holochat.ui.dialog.CancelRequestContactDialog;

/**
 * Presents information on a contact request
 * On click it should accept the friend request
 *
 * @author Tarnveer
 */
public class ContactRequestOutRecyclerViewAdapter extends RecyclerView.Adapter<ContactRequestOutRecyclerViewAdapter.ContactRequestViewHolder> {

    private final FragmentManager mFragmMan;
    private final List<Contact> mContactRequests;

    /**
     * A constructor for teh contact request recycler view.
     *
     * @param items a list of contacts.
     */
    public ContactRequestOutRecyclerViewAdapter(List<Contact> items, FragmentManager fm) {
        this.mContactRequests = items;
        this.mFragmMan = fm;
    }

    /**
     * Creates a view holder.
     *
     * @param parent   the parent.
     * @param viewType the view type
     * @return a contact view holder.
     */
    public ContactRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactRequestViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_outgoing_request_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactRequestViewHolder holder, int position) {
        holder.setContact(mContactRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactRequests.size();
    }


    /**
     * An inner class which hold the view for a contact.
     */
    public class ContactRequestViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public @NonNull FragmentContactOutgoingRequestCardBinding binding;
        public Contact mContact;

        /**
         * Constructore for teh contact view holder.
         *
         * @param view the view.
         */
        public ContactRequestViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactOutgoingRequestCardBinding.bind(view);
        }

        /**
         * navigates to a contacts profile.
         */
        private void openRejectDialog() {
            String name = mContact.getContactFirstName() + " " + mContact.getContactLastName();
            CancelRequestContactDialog dialog = new CancelRequestContactDialog(name,
                    mContact.getContactMemberID(), this);
            dialog.show(mFragmMan, "maybe?");
        }


        /**
         * Sets the contact.
         *
         * @param contact the contact
         */
        void setContact(final Contact contact) {
            binding.textUsername.setText(contact.getContactUsername());
            binding.textEmail.setText(contact.getContactEmail());
            mContact = contact;

            binding.buttonCancel.setOnClickListener(button -> {
                openRejectDialog();
            });
        }

        public void deleteRequest() {
            mContactRequests.remove(mContact);
            notifyDataSetChanged();
        }
    }
}



package edu.uw.tcss450.team5.holochat.ui.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Observable;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentContactlistCardBinding;

/**
 * Adapts a ContactListViewModel into a RecyclerView
 * so the data can be displayed on the app
 */

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactListViewHolder> {
    List<Contact> mContacts;

    public ContactListRecyclerViewAdapter(List<Contact> contacts) {
        this.mContacts=contacts;
    }
    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_contactlist_card, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));



    }

    /**
     * helper method to check the size of the contact list
     * @author Tarnveer Mangat
     * @return the size of the contact list
     */
    @Override
    public int getItemCount() {
        return mContacts.size();

    }

    /**
     * Inner class to create the card view for each contact
     * @author Tarnveer Mangat
     */
    public class ContactListViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactlistCardBinding binding;
        public ContactListViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactlistCardBinding.bind(view);


        }
        void setContact(final Contact contact) {
            //binding.deleteContactButton.setOnClickListener(button->progressDeletion(contact));
            binding.contactlistTextView.setText(""+ contact.getContactUsername());

        }

        //private void progressDeletion(Contact contact) {
            //Navigation.findNavController(mView)
                    //navigate(ContactsListFragmentDirections.actionNavigationConnectionsToDeleteContact(contact));
            // mContacts.remove(contact);
            //  notifyDataSetChanged();


        //}
    }

}

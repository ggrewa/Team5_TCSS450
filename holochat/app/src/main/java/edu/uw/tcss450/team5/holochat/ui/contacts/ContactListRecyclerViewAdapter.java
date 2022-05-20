package edu.uw.tcss450.team5.holochat.ui.contacts;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.team5.holochat.databinding.FragmentContactListSingleBinding;
import edu.uw.tcss450.team5.holochat.ui.contacts.contact_tabs.ContactListFragmentDirections;
import edu.uw.tcss450.team5.holochat.R;

/**
 * Adapts a ContactListViewModel into a RecyclerView
 * so the data can be displayed on the app
 */

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactViewHolder> {

    private final List<ContactListSingle> mContactList;

    public ContactListRecyclerViewAdapter(List<ContactListSingle> contacts) {
        this.mContactList = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_list_single, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContactList.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentContactListSingleBinding binding;

        public ContactViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentContactListSingleBinding.bind(view);
        }

        void setContact(final ContactListSingle contact) {
            final Resources res = mView.getContext().getResources();
            final CardView card = binding.contactListRoot;



            int standard = (int) res.getDimension(R.dimen.room_margin);



            String contactText = contact.getContact();
            if(contact.getContact().length() > 25) {
                contactText = contactText.substring(0,25) + "...";
            }
            binding.contactListRoot.setOnClickListener(view -> {
                Navigation.findNavController(mView).navigate(

                        ContactListFragmentDirections
                                .actionNavigationContactsToContactInfo(contact.getContact(), contact.getContactEmail(), contact.getContact())); //TODO: navigate to contact fragment
            });
            binding.contactName.setText(contactText);
            binding.contactName.setPadding(10,50,0,0);
            binding.contactName.setTextSize(25);
            card.setBackgroundColor(res.getColor(R.color.colorPrimaryLight));
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            //Set the left margin
            layoutParams.setMargins(standard, standard, standard, standard);

            binding.contactName.setTextColor(
                    res.getColor(R.color.colorPrimaryDark, null));

            card.requestLayout();

        }
    }
}